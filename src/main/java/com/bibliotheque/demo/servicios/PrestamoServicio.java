/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.servicios;


import com.bibliotheque.demo.entidades.Libro;
import com.bibliotheque.demo.entidades.Prestamo;
import com.bibliotheque.demo.entidades.Admin;
import com.bibliotheque.demo.excepciones.ErrorServicio;
import com.bibliotheque.demo.repositorios.LibroRepositorio;
import com.bibliotheque.demo.repositorios.AdminRepositorio;
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

/**
 *
 * @author Gonzalo
 */
@Service
public class PrestamoServicio {
    @Autowired
    private PrestamoRepositorio prestamoRepo;
    @Autowired
    private LibroRepositorio libroRepo;
    @Autowired
    private LibroServicio libroServ;
    @Autowired
    private AdminRepositorio adminRepo;
    
    @Transactional
    public Prestamo altaPrestamo(String libro, String idAdmin, String sexo) throws ErrorServicio {
        if (libro == null || libro.isEmpty()) {
            throw new ErrorServicio("Falta el nombre del usuario");
        }
        
        if (idAdmin == null || idAdmin.isEmpty() || idAdmin.length() < 6) {
            throw new ErrorServicio("Falta ingresar el password del usuario");
        }
        
        if (sexo == null || sexo.isEmpty()) {
            throw new ErrorServicio("Falta ingresar el sexo del usuario");
        }
        
        Libro libroPrestamo = null; 
        Optional<Libro> rta = libroRepo.buscaLibroNom(libro);
        if(rta.isPresent()) {
            libroPrestamo = rta.get();
        } else {
            throw new ErrorServicio("No hay un libro registrado con ese Título.");
        }
        
        Admin adminPrestamo = null;
        Optional<Admin> rta1 = adminRepo.findById(idAdmin);
        if(rta1.isPresent()) {
            adminPrestamo = rta1.get();
        } else {
            throw new ErrorServicio("No hay un socio registrado con ese nombre.");
        }
        
        Prestamo prestamo = new Prestamo();
        
        prestamo.setAlta(Boolean.TRUE);
        prestamo.setLibro(libroPrestamo);
        prestamo.setAdmin(adminPrestamo);
        prestamo.setFechaAlta(new Date());
        libroServ.modEjemplaresRet(libroPrestamo);
        return prestamoRepo.save(prestamo);
    }
    
    /*
    @Transactional
    public void bajaPrestamo(String admin, String libro ) throws ErrorServicio {
        Admin adminPrestamo = null;
        Optional<Admin> rta = adminRepo.buscaAdminNom(admin);
        if (rta.isPresent()) {
            adminPrestamo = rta.get();
        } else {
            throw new ErrorServicio("No hay un socio registrado con ese nombre.");
        }
        
        Libro libroPrestamo = null;
        Optional<Libro> rta1 = libroRepo.buscaLibroNom(libro);
        if (rta1.isPresent()) {
            libroPrestamo = rta1.get();
        } else {
            throw new ErrorServicio("No hay un libro registrado con ese título.");
        }
        
        Optional<List<Prestamo>> prestamoOp1 = prestamoRepo.buscaPrestamoLibro(libroPrestamo.getTitulo());
        Optional<List<Prestamo>> prestamoOp2 = prestamoRepo.buscaPrestamoNom(adminPrestamo.getNombre());
        List<Prestamo> prestamo1 = null;
        List<Prestamo> prestamo2 = null;
        
        if (prestamoOp1.isPresent()) {
            prestamo1=prestamoOp1.get();
        }
        
        if (prestamoOp2.isPresent()) {
            prestamo2=prestamoOp2.get();
        }
        
        if(prestamo1.getId().equals(prestamo2.getId())) {
            prestamo1.setFechaBaja(new Date());
            prestamo1.setAlta(Boolean.FALSE);
            libroServ.modEjemplaresDev(libroPrestamo);
            prestamoRepo.save(prestamo1);
        } else {
            throw new ErrorServicio("No hay ningún préstamo activo con esos datos.");
        }
    }
    */
    
    @Transactional(readOnly = true)
    public List<Prestamo> listarPrestamos(){
        List<Prestamo> prestamos = prestamoRepo.listarPrestamo();
        return prestamos;
    }
    
    static LocalDate convertToLocalDateViaMilisecond(Date dateToConvert) {
        return Instant.ofEpochMilli(dateToConvert.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }
}