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
    
//    public Orden booleanOrden(String ordenIn) {
//        
//    }

    public void limpiezaOrden() {
        List <Orden> ordenes = new ArrayList();
        Optional <List<Orden>> rta = ordenRepo.listaOrdenListEmpty();
        if (rta.isPresent()) {
            ordenes = rta.get();
        }
        
        if (ordenes.size()>0) {
            for (Orden orden : ordenes) {
                ordenRepo.delete(orden);
            }
        }
    }
}
