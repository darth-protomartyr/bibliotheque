/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.servicios;


import com.bibliotheque.demo.entidades.Autor;
import com.bibliotheque.demo.entidades.Editorial;
import com.bibliotheque.demo.entidades.Libro;
import com.bibliotheque.demo.excepciones.ErrorServicio;
import com.bibliotheque.demo.repositorios.AutorRepositorio;
import com.bibliotheque.demo.repositorios.EditorialRepositorio;
import com.bibliotheque.demo.repositorios.LibroRepositorio;
import java.util.ArrayList;
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
public class LibroServicio {
    @Autowired
    private LibroRepositorio bookRepo;
    @Autowired
    private AutorRepositorio wrRepo;
    @Autowired
    private EditorialRepositorio edRepo;
    @Autowired
    private AutorServicio wrServ;
    @Autowired
    private EditorialServicio edServ;

    @Transactional
    public Libro crearLibro(Long ISBN, String titulo, Integer ejemplaresTotales, String autor, String editorial) throws ErrorServicio, NullPointerException {
        
        if (ISBN == null) {
            throw new ErrorServicio("Falta ingresar el ISBN");
        }
        
        if (titulo == null || titulo.isEmpty()) {
            throw new ErrorServicio("Falta ingresar el Título");
        }
        
        if (ejemplaresTotales == null) {
            throw new ErrorServicio("Falta ingresar la cantidad de Ejemplares");
        }
        
        if (autor == null || autor.isEmpty()) {
            throw new ErrorServicio("Falta ingresar el autor");
        }
        
        if (editorial == null || editorial.isEmpty()) {
            throw new ErrorServicio("Falta ingresar la editorial");
        }
        
        
        Libro book = new Libro();
        book.setAlta(Boolean.TRUE);
        book.setIsbn(ISBN);
        book.setTitulo(titulo);
        book.setEjemplaresTotales(ejemplaresTotales);
        book.setEjemplaresRestantes(ejemplaresTotales);
        book.setEjemplaresPrestados(0);
        book.setAutor(wrServ.consultaAutor(autor));
        book.setEditorial(edServ.consultaEditorial(editorial));
        return bookRepo.save(book);
    }
        
    @Transactional
    public void darBajaLibro(String tit) throws ErrorServicio {
        Libro book = null;
        Optional<Libro> rta = bookRepo.buscaLibroNom(tit);
        if (rta.isPresent()) {
            book = rta.get();
        } else {
            throw new ErrorServicio("El titulo ingresado no pertenece a un libro listado en la base de datos");
        }
        
        if (book.getAlta().equals(true)) {
            book.setAlta(Boolean.FALSE);
        } else {
            throw new ErrorServicio("El libro consultado ya está dado de baja.");    
        }
    }
    
    @Transactional
    public void darAltaLibro(String tit) throws ErrorServicio {
        Libro book = null;
        Optional<Libro> rta = bookRepo.buscaLibroNom(tit);
        if (rta.isPresent()) {
            book = rta.get();
        } else {
            throw new ErrorServicio("El titulo ingresado no pertenece a un libro listado en la base de datos");
        }
        
        if (book.getAlta().equals(false)) {
            book.setAlta(Boolean.TRUE);
        } else {
            throw new ErrorServicio("El libro consultado ya está dado de alta.");    
        }
    }
    
    @Transactional
    public void modificarISBN(String tit, long ISBN) throws ErrorServicio {
        Libro book = null;
        Optional<Libro> rta = bookRepo.buscaLibroNom(tit);
        if (rta.isPresent()) {
            book = rta.get();
        } else {
            throw new ErrorServicio("El titulo ingresado no pertenece a un libro listado en la base de datos");
        }
        
        book.setIsbn(ISBN);
    }
    
    @Transactional
    public void modificarTitulo(String tit, String newTit) throws ErrorServicio {
        Libro book = null;
        Optional<Libro> rta = bookRepo.buscaLibroNom(tit);
        if (rta.isPresent()) {
            book = rta.get();
        } else {
            throw new ErrorServicio("El titulo ingresado no pertenece a un libro listado en la base de datos");
        }
        book.setTitulo(newTit);
    }
    
    @Transactional
    public void modificarEjemplaresTotales(String tit, int updEjempl) throws ErrorServicio {
        Libro book = null;
        Optional<Libro> rta = bookRepo.buscaLibroNom(tit);
        if (rta.isPresent()) {
            book = rta.get();
        } else {
            throw new ErrorServicio("El titulo ingresado no pertenece a un libro listado en la base de datos");
        }
        book.setEjemplaresTotales(book.getEjemplaresTotales() + updEjempl);
        book.setEjemplaresRestantes(book.getEjemplaresRestantes() + updEjempl);
    }
    
    @Transactional
    public void modificarAutor(String tit, String aut) throws ErrorServicio {
        Libro book = null;
        Optional<Libro> rta = bookRepo.buscaLibroNom(tit);
        if (rta.isPresent()) {
            book = rta.get();
        } else {
            throw new ErrorServicio("El titulo ingresado no pertenece a un libro listado en la base de datos");
        }
        
        Autor wr = null;
        Optional<Autor> rta1 = wrRepo.buscaAutorNom(aut);
        if (rta.isPresent()) {
            wr = rta1.get();
        } else {
            throw new ErrorServicio("El nombre ingresado no pertenece a un Autor listado en la base de datos");
        }
        
        book.setAutor(wr);
    }
    
    @Transactional
    public void modificarEditorial(String tit, String ed) throws ErrorServicio {
        Libro book = null;
        Optional<Libro> rta = bookRepo.buscaLibroNom(tit);
        if (rta.isPresent()) {
            book = rta.get();
        } else {
            throw new ErrorServicio("El titulo ingresado no pertenece a un libro listado en la base de datos");
        }
        
        Editorial edi = null;
        Optional<Editorial> rta1 = edRepo.buscaEditorialNom(ed);
        if (rta.isPresent()) {
            edi = rta1.get();
        } else {
            throw new ErrorServicio("El nombre ingresado no pertenece a una Editorial listada en la base de datos");
        }
        
        book.setEditorial(edi);
    }
    
    @Transactional(readOnly = true)
    public Libro buscarLibroISBN(long ISBN) throws ErrorServicio {
        Libro book = new Libro();
        Optional<Libro> rta = bookRepo.buscaLibroISBN(ISBN);
        if (rta.isPresent()){
            book = rta.get();
        } else {
            throw new ErrorServicio("El código ingresado no pertenece a un libro listado en la base de datos.");
        }
        return book;
    }

    @Transactional(readOnly = true)
    public Libro buscarLibroTit(String tit) throws ErrorServicio {
        Libro book = new Libro();
        Optional<Libro> rta = bookRepo.buscaLibroNom(tit);
        if (rta.isPresent()){
            book = rta.get();
        } else {
            throw new ErrorServicio("El nombre ingresado no pertenece a un libro listado en la base de datos.");
        }
        return book;
    }
    
    @Transactional(readOnly = true)    
    public List <Libro> buscarLibroAut(String aut) throws ErrorServicio {
        List <Libro> books = new ArrayList();
        Optional<List<Libro>> rta = bookRepo.listarLibrosAutor(aut);
        if (rta.isPresent()){
            books = rta.get();
        } else {
            throw new ErrorServicio("El nombre ingresado no pertenece a un libro listado en la base de datos.");
        }
        return books;
    }
    
    @Transactional(readOnly = true)
    public List <Libro> buscarLibroEd(String ed) throws ErrorServicio {
        List <Libro> books = new ArrayList();
        Optional<List<Libro>> rta = bookRepo.listarLibrosEditorial(ed);
        if (rta.isPresent()){
            books = rta.get();
        } else {
            throw new ErrorServicio("El nombre ingresado no pertenece a un libro listado en la base de datos.");
        }
        return books;
    }
    
    @Transactional(readOnly = true)
    public List <Libro> listarLibroEd(String ed) throws ErrorServicio {
        List <Libro> books = bookRepo.listarLibros();
        return books;
    }
    
    
    @Transactional
    public void modEjemplaresRet(Libro book) throws ErrorServicio {
        if (book.getEjemplaresRestantes()>0) {
            book.setEjemplaresRestantes(book.getEjemplaresRestantes() - 1);
            book.setEjemplaresPrestados(book.getEjemplaresPrestados() + 1);
        } else {
            System.out.println("No hay ejemplares para prestar");
        }
        bookRepo.save(book);
    }
    
    @Transactional
    public void modEjemplaresDev(Libro book) throws ErrorServicio {
        if (book.getEjemplaresTotales()>0) {
            book.setEjemplaresRestantes(book.getEjemplaresRestantes() + 1);
            book.setEjemplaresPrestados(book.getEjemplaresPrestados() - 1);
        } else {
            System.out.println("No hay ejemplares para prestar");
        }
        bookRepo.save(book);
    }
}