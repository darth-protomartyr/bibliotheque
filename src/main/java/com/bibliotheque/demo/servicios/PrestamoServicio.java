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
import com.bibliotheque.demo.repositorios.PrestamoRepositorio;
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

/**
 *
 * @author Gonzalo
 */
@Service
public class PrestamoServicio {
    @Autowired
    private PrestamoRepositorio loanRepo;
    @Autowired
    private LibroRepositorio bookRepo;
    @Autowired
    private LibroServicio bookServ;
    @Autowired
    private AdminRepositorio adminRepo;
    
    @Transactional
    public Prestamo altaPrestamo(String book, String idAdmin, String sexo) throws ErrorServicio {
        if (book == null || book.isEmpty()) {
            throw new ErrorServicio("Falta el nombre del usuario");
        }
        
        if (idAdmin == null || idAdmin.isEmpty() || idAdmin.length() < 6) {
            throw new ErrorServicio("Falta ingresar el password del usuario");
        }
        
        if (sexo == null || sexo.isEmpty()) {
            throw new ErrorServicio("Falta ingresar el sexo del usuario");
        }
        
        Libro bookLoan = null; 
        Optional<Libro> rta = bookRepo.buscaLibroNom(book);
        if(rta.isPresent()) {
            bookLoan = rta.get();
        } else {
            throw new ErrorServicio("No hay un libro registrado con ese Título.");
        }
        
        Admin memberLoan = null;
        Optional<Admin> rta1 = adminRepo.findById(idAdmin);
        if(rta1.isPresent()) {
            memberLoan = rta1.get();
        } else {
            throw new ErrorServicio("No hay un socio registrado con ese nombre.");
        }
        
        Prestamo loan = new Prestamo();
        
        loan.setAlta(Boolean.TRUE);
        loan.setLibro(bookLoan);
        loan.setAdmin(memberLoan);
        loan.setFechaAlta(new Date());
        bookServ.modEjemplaresRet(bookLoan);
        return loanRepo.save(loan);
    }
    
    /*
    @Transactional
    public void bajaPrestamo(String member, String book ) throws ErrorServicio {
        Admin memberLoan = null;
        Optional<Admin> rta = adminRepo.buscaAdminNom(member);
        if (rta.isPresent()) {
            memberLoan = rta.get();
        } else {
            throw new ErrorServicio("No hay un socio registrado con ese nombre.");
        }
        
        Libro bookLoan = null;
        Optional<Libro> rta1 = bookRepo.buscaLibroNom(book);
        if (rta1.isPresent()) {
            bookLoan = rta1.get();
        } else {
            throw new ErrorServicio("No hay un libro registrado con ese título.");
        }
        
        Optional<List<Prestamo>> loanOp1 = loanRepo.buscaPrestamoBook(bookLoan.getTitulo());
        Optional<List<Prestamo>> loanOp2 = loanRepo.buscaPrestamoNom(memberLoan.getNombre());
        List<Prestamo> loan1 = null;
        List<Prestamo> loan2 = null;
        
        if (loanOp1.isPresent()) {
            loan1=loanOp1.get();
        }
        
        if (loanOp2.isPresent()) {
            loan2=loanOp2.get();
        }
        
        if(loan1.getId().equals(loan2.getId())) {
            loan1.setFechaBaja(new Date());
            loan1.setAlta(Boolean.FALSE);
            bookServ.modEjemplaresDev(bookLoan);
            loanRepo.save(loan1);
        } else {
            throw new ErrorServicio("No hay ningún préstamo activo con esos datos.");
        }
    }
    */
    
    @Transactional(readOnly = true)
    public List<Prestamo> listarPrestamos(){
        List<Prestamo> prestamos = loanRepo.listarPrestamo();
        return prestamos;
    }
    
    static LocalDate convertToLocalDateViaMilisecond(Date dateToConvert) {
        return Instant.ofEpochMilli(dateToConvert.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }
}