/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.servicios;


import com.bibliotheque.demo.entidades.Autor;
import com.bibliotheque.demo.entidades.Editorial;
import com.bibliotheque.demo.entidades.Foto;
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
import org.springframework.web.multipart.MultipartFile;


/**
 *
 * @author Gonzalo
 */
@Service
public class LibroServicio {
    @Autowired
    private LibroRepositorio libroRepo;
    @Autowired
    private AutorRepositorio wrRepo;
    @Autowired
    private EditorialRepositorio edRepo;
    @Autowired
    private AutorServicio wrServ;
    @Autowired
    private EditorialServicio edServ;
    @Autowired
    private FotoServicio picServ;

    @Transactional
    public Libro crearLibro(Long isbn, String titulo, Integer ejemplaresTotales, String idWri, String idPub, MultipartFile archivo) throws ErrorServicio, NullPointerException {
        
        if (isbn == null) {
            throw new ErrorServicio("Falta ingresar el isbn");
        }
        
        if (titulo == null || titulo.isEmpty()) {
            throw new ErrorServicio("Falta ingresar el Título");
        }
        
        if (ejemplaresTotales == null) {
            throw new ErrorServicio("Falta ingresar la cantidad de Ejemplares");
        }
        
        if (idWri == null || idWri.isEmpty()) {
            throw new ErrorServicio("Falta ingresar el autor");
        }
        
        if (idPub == null || idPub.isEmpty()) {
            throw new ErrorServicio("Falta ingresar la editorial");
        }
        
        
        Libro book = new Libro();
        book.setAlta(Boolean.TRUE);
        Optional<Libro> rta = libroRepo.buscaLibroIsbnCompl(isbn);
        if (rta.isPresent()) {
            throw new ErrorServicio("El título ya se encuentra registrado en la base de datos");
        }
        book.setIsbn(isbn);
        book.setTitulo(titulo);
        book.setEjemplaresTotales(ejemplaresTotales);
        book.setEjemplaresRestantes(ejemplaresTotales);
        book.setEjemplaresPrestados(0);
        book.setAutor(wrServ.consultaAutorIdCompl(idWri));
        book.setEditorial(edServ.consultaEditorialIdCompl(idPub));
        Foto foto = picServ.guardar(archivo);
        book.setFoto(foto);
        return libroRepo.save(book);
    }
    
    @Transactional
    public void modificar(String id, Long isbn, String titulo, Integer ejemplaresTotales, String wriId, String pubId, MultipartFile archivo) throws ErrorServicio {
        
            Libro book = null;
                Optional<Libro> rta1 = libroRepo.findById(id);
            if(rta1.isPresent()) {
            book = rta1.get();
            
            Optional<Libro> rta = libroRepo.buscaLibroIsbnCompl(isbn);
            if (rta.isPresent() && !isbn.equals(book.getIsbn())) {
                throw new ErrorServicio("El ISBN del libro ya se encuentra registrado en la base de datos");
            }
            
            if (isbn == null) {
                book.setIsbn(book.getIsbn());
            } else {
                book.setIsbn(isbn);
            }
            
            if (titulo == null || titulo.isEmpty()) {
                book.setTitulo(book.getTitulo());
            } else {
                book.setTitulo(titulo);
            }
            
            if (ejemplaresTotales == null) {
                book.setEjemplaresTotales(book.getEjemplaresTotales());
            } else {
                book.setEjemplaresTotales(ejemplaresTotales);
            }
            
            if (wriId == null || wriId.isEmpty()) {
                book.setAutor(book.getAutor());
            } else {
                Autor wri= null;
                Optional<Autor> writer = wrRepo.buscaAutorId(wriId);
                if (writer.isPresent()) {
                    wri = writer.get();
                    book.setAutor(wri);
                } else {
                    throw new ErrorServicio("El Autor ingresado no se encuentra listado en la base de datos");
                }
            }
            
            if (pubId == null || pubId.isEmpty()) {
                book.setEditorial(book.getEditorial());
            } else {
                Editorial ed= null;
                Optional<Editorial> edito = edRepo.buscaEditorialId(pubId);
                if (edito.isPresent()) {
                    ed = edito.get();
                    book.setEditorial(ed);
                } else {
                    throw new ErrorServicio("La editorial ingresada no se encuentra listada en la base de datos");
                }
            }
            
            if (archivo == null || archivo.isEmpty()) {
                book.setFoto(book.getFoto());
            } else {
                String idFoto = null;
                if (book.getFoto() != null){
                    idFoto = book.getFoto().getId();
                }
                Foto foto = picServ.actualizar(idFoto, archivo);
                book.setFoto(foto);
            }
        } else {
            throw new ErrorServicio("No hay un socio registrado con ese nombre.");
        }
    }
    
        
    @Transactional
    public void darBajaLibro(String id) throws ErrorServicio {
        Libro book = null;
        Optional<Libro> rta = libroRepo.buscaLibroId(id);
        if (rta.isPresent()) {
            book = rta.get();
        } else {
            throw new ErrorServicio("El titulo ingresado no pertenece a un libro listado en la base de datos");
        }
        
        if (book.getAlta().equals(true)) {
            book.setAlta(Boolean.FALSE);
        } else {
            throw new ErrorServicio("El libro consultado ya se encuentra dado de baja.");    
        }
    }
    
    @Transactional
    public void darAltaLibro(String id) throws ErrorServicio {
        Libro book = null;
        Optional<Libro> rta = libroRepo.buscaLibroIdCompl(id);
        if (rta.isPresent()) {
            book = rta.get();
        } else {
            throw new ErrorServicio("El titulo ingresado no pertenece a un libro listado en la base de datos");
        }
        
        if (book.getAlta().equals(false)) {
            book.setAlta(Boolean.TRUE);
        } else {
            throw new ErrorServicio("El libro consultado ya se encuentra dado de alta.");    
        }
    }
    
    
    @Transactional(readOnly = true)
    public List<Libro> listarLibrosActivos() {
        List<Libro>wrs = libroRepo.listarLibrosActivos();
        return wrs;
    }
    
    
    @Transactional(readOnly = true)
    public List<Libro> listarLibrosCompletas() {
        List<Libro>wrs = libroRepo.listarLibrosCompleta();
        return wrs;
    }
    
    
    @Transactional(readOnly = true)
    public Libro buscarLibroId(String id) throws ErrorServicio {
        Libro book = new Libro();
        Optional<Libro> rta = libroRepo.buscaLibroId(id);
        if (rta.isPresent()){
            book = rta.get();
        } else {
            throw new ErrorServicio("El libro solicitado no se encuentra listado en la base de datos.");
        }
        return book;
    }

    
    @Transactional(readOnly = true)
    public Libro buscarLibroTit(String tit) throws ErrorServicio {
        Libro book = new Libro();
        Optional<Libro> rta = libroRepo.buscaLibroNom(tit);
        if (rta.isPresent()){
            book = rta.get();
        } else {
            throw new ErrorServicio("El nombre ingresado no pertenece a un libro listado en la base de datos.");
        }
        return book;
    }
    
    
    @Transactional(readOnly = true)
    public Libro buscarLibroTitCompl(String tit) throws ErrorServicio {
        Libro book = new Libro();
        Optional<Libro> rta = libroRepo.buscaLibroNomCompl(tit);
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
        Optional<List<Libro>> rta = libroRepo.listarLibrosAutor(aut);
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
        Optional<List<Libro>> rta = libroRepo.listarLibrosEditorial(ed);
        if (rta.isPresent()){
            books = rta.get();
        } else {
            throw new ErrorServicio("El nombre ingresado no pertenece a un libro listado en la base de datos.");
        }
        return books;
    }
    
    @Transactional(readOnly = true)
    public List <Libro> listarLibroEd(String ed) throws ErrorServicio {
        
        List <Libro> books = null;
        Optional<List<Libro>> rta = libroRepo.listarLibrosEditorial(ed);
        if (rta.isPresent()) {
            books = rta.get();
        }
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
        libroRepo.save(book);
    }
    
    @Transactional
    public void modEjemplaresDev(Libro book) throws ErrorServicio {
        if (book.getEjemplaresTotales()>0) {
            book.setEjemplaresRestantes(book.getEjemplaresRestantes() + 1);
            book.setEjemplaresPrestados(book.getEjemplaresPrestados() - 1);
        } else {
            System.out.println("No hay ejemplares para prestar");
        }
        libroRepo.save(book);
    }

}