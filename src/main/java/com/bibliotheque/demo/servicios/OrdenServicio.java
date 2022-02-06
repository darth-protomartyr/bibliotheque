/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.servicios;


import com.bibliotheque.demo.entidades.Libro;
import com.bibliotheque.demo.entidades.Prestamo;
import com.bibliotheque.demo.entidades.Usuario;
import com.bibliotheque.demo.entidades.Orden;
import com.bibliotheque.demo.excepciones.ErrorServicio;
import com.bibliotheque.demo.repositorios.LibroRepositorio;
import com.bibliotheque.demo.repositorios.UsuarioRepositorio;
import com.bibliotheque.demo.repositorios.OrdenRepositorio;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bibliotheque.demo.repositorios.PrestamoRepositorio;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;

/**
 *
 * @author Gonzalo
 */
@Service
public class OrdenServicio {
    @Autowired
    private PrestamoRepositorio prestamoRepo;
    @Autowired
    private LibroRepositorio libroRepo;
    @Autowired
    private LibroServicio libroServ;
    @Autowired
    private UsuarioRepositorio usuarioRepo;
    @Autowired
    private OrdenRepositorio ordenRepo;
    @Autowired
    private PrestamoServicio prestamoServ;
    
    @Transactional
    public Orden iniciarOrden(Usuario usuario) {
        Orden orden = new Orden();
        orden.setUsuario(usuario);
        orden.setAlta(true);
        return ordenRepo.save(orden);
    }
    
    
    @Transactional
    public Orden generarOrden(List <Prestamo> prestamos, String usuarioId) throws ErrorServicio {
        Orden orden = null;
        
        if (usuarioId == null || usuarioId.isEmpty()) {
            throw new ErrorServicio("Falta ingresar el número de socio");
        }
        
        if (prestamos.size() == 0) {
            throw new ErrorServicio("No ha ingresado los pedidos de prestamo");
        }
        
        Usuario usuario = null;
        Optional<Usuario> rta1 = usuarioRepo.buscaUsuarioIdAlta(usuarioId);
        if(rta1.isPresent()) {
            usuario = rta1.get();
        } else {
            throw new ErrorServicio("No es un socio registrado.");
        }
        
        for (Prestamo prestamo : prestamos) {
            prestamo.setAlta(Boolean.TRUE);
            prestamo.setFechaAlta(new Date());
            
            Calendar date = Calendar.getInstance();
            date.setTime(prestamo.getFechaAlta());
            
            date.add(Calendar.HOUR, 180);
            java.util.Date fechaVencimiento = date.getTime();
            prestamo.setFechaDevolucion(fechaVencimiento);
        }

        
        if (usuario.getPenalidad() == true) {
            throw new ErrorServicio("El Socio no puede realizar el pedido por encontrarse penalizado");
        }
        
        orden.setUsuario(usuario);
        orden.setAlta(Boolean.TRUE);
        orden.setPrestamos(prestamos);
        return ordenRepo.save(orden);
    }
    
    public void darBajaOrden(String ordenId) throws ErrorServicio, ParseException {
        Orden orden = null;
        Optional <Orden> rta = ordenRepo.buscaOrdenIdAlta(ordenId);
        if (rta.isPresent()) {
            orden = rta.get();
        }
        
        List <Prestamo> prestamos = orden.getPrestamos();
        
        for (Prestamo prestamo : prestamos) {
            String idPrestamo = prestamo.getId();
            prestamoServ.bajaPrestamo(idPrestamo);
            prestamos.remove(prestamo);
        }
        
        orden.setAlta(Boolean.FALSE);
    }

    
    public List<Prestamo> listaPrestamoAlta(String ordenId, String prestamoId) {
        List <Prestamo> alta = new ArrayList();
        Orden orden = null;
        Optional <Orden> rta = ordenRepo.findById(ordenId);
        if(rta.isPresent()) {
            orden = rta.get();
        }
        
        List<Prestamo>prestamos = orden.getPrestamos();
        for (Prestamo prestamo : prestamos) {
            alta.add(prestamo);
        }

        Prestamo actual = null;
        Optional<Prestamo>rta1 = prestamoRepo.findById(prestamoId);
        if(rta1.isPresent()) {
            actual = rta1.get();
        }
        alta.add(actual);   
        return alta;
    }
}
