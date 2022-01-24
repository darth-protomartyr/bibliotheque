/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.servicios;


import com.bibliotheque.demo.entidades.Libro;
import com.bibliotheque.demo.entidades.Prestamo;
import com.bibliotheque.demo.entidades.Admin;
import com.bibliotheque.demo.entidades.Orden;
import com.bibliotheque.demo.excepciones.ErrorServicio;
import com.bibliotheque.demo.repositorios.LibroRepositorio;
import com.bibliotheque.demo.repositorios.AdminRepositorio;
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
    private AdminRepositorio adminRepo;
    @Autowired
    private OrdenRepositorio ordenRepo;
    @Autowired
    private PrestamoServicio prestamoServ;
    
    @Transactional
    public Orden generarOrden(List <Prestamo> prestamos, String adminId) throws ErrorServicio {
        Orden orden = null;
        
        if (adminId == null || adminId.isEmpty()) {
            throw new ErrorServicio("Falta ingresar el número de socio");
        }
        
        if (prestamos.size() == 0) {
            throw new ErrorServicio("No ha ingresado los pedidos de prestamo");
        }
        
        Admin admin = null;
        Optional<Admin> rta1 = adminRepo.buscaAdminIdAlta(adminId);
        if(rta1.isPresent()) {
            admin = rta1.get();
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

        
        if (admin.getPenalidad() == true) {
            throw new ErrorServicio("El Socio no puede realizar el pedido por encontrarse penalizado");
        }
        
        orden.setAdmin(admin);
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
    
}
