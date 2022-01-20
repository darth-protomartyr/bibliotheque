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
    public Libro crearLibro(Long isbn, String titulo, Integer ejemplaresTotales, String autorId, String idEditorial, MultipartFile archivo) throws ErrorServicio, NullPointerException {
        
        if (isbn == null) {
            throw new ErrorServicio("Falta ingresar el isbn");
        }
        
        if (titulo == null || titulo.isEmpty()) {
            throw new ErrorServicio("Falta ingresar el Título");
        }
        
        if (ejemplaresTotales == null) {
            throw new ErrorServicio("Falta ingresar la cantidad de Ejemplares");
        }
        
        if (autorId == null || autorId.isEmpty()) {
            throw new ErrorServicio("Falta ingresar el autor");
        }
        
        if (idEditorial == null || idEditorial.isEmpty()) {
            throw new ErrorServicio("Falta ingresar la editorial");
        }
        
        
        Libro libro = new Libro();
        libro.setAlta(Boolean.TRUE);
        Optional<Libro> rta = libroRepo.buscaLibroIsbnCompl(isbn);
        if (rta.isPresent()) {
            throw new ErrorServicio("El título ya se encuentra registrado en la base de datos");
        }
        libro.setIsbn(isbn);
        libro.setTitulo(titulo);
        libro.setEjemplaresTotales(ejemplaresTotales);
        libro.setEjemplaresRestantes(ejemplaresTotales);
        libro.setEjemplaresPrestados(0);
        libro.setAutor(wrServ.consultaAutorIdCompl(autorId));
        libro.setEditorial(edServ.consultaEditorialIdCompl(idEditorial));
        Foto foto = picServ.guardar(archivo);
        libro.setFoto(foto);
        return libroRepo.save(libro);
    }
    
    @Transactional
    public void modificar(String id, Long isbn, String titulo, Integer ejemplaresTotales, String autorId, String editorialId, MultipartFile archivo) throws ErrorServicio {
        
            Libro libro = null;
                Optional<Libro> rta1 = libroRepo.findById(id);
            if(rta1.isPresent()) {
            libro = rta1.get();
            
            Optional<Libro> rta = libroRepo.buscaLibroIsbnCompl(isbn);
            if (rta.isPresent() && !isbn.equals(libro.getIsbn())) {
                throw new ErrorServicio("El ISBN del libro ya se encuentra registrado en la base de datos");
            }
            
            if (isbn == null) {
                libro.setIsbn(libro.getIsbn());
            } else {
                libro.setIsbn(isbn);
            }
            
            if (titulo == null || titulo.isEmpty()) {
                libro.setTitulo(libro.getTitulo());
            } else {
                libro.setTitulo(titulo);
            }
            
            if (ejemplaresTotales == null) {
                libro.setEjemplaresTotales(libro.getEjemplaresTotales());
            } else {
                libro.setEjemplaresTotales(ejemplaresTotales);
            }
            
            if (autorId == null || autorId.isEmpty()) {
                libro.setAutor(libro.getAutor());
            } else {
                Autor autor= null;
                Optional<Autor> rta2 = wrRepo.buscaAutorId(autorId);
                if (rta1.isPresent()) {
                    autor = rta2.get();
                    libro.setAutor(autor);
                } else {
                    throw new ErrorServicio("El Autor ingresado no se encuentra listado en la base de datos");
                }
            }
            
            if (editorialId == null || editorialId.isEmpty()) {
                libro.setEditorial(libro.getEditorial());
            } else {
                Editorial ed= null;
                Optional<Editorial> edito = edRepo.buscaEditorialId(editorialId);
                if (edito.isPresent()) {
                    ed = edito.get();
                    libro.setEditorial(ed);
                } else {
                    throw new ErrorServicio("La editorial ingresada no se encuentra listada en la base de datos");
                }
            }
            
            if (archivo == null || archivo.isEmpty()) {
                libro.setFoto(libro.getFoto());
            } else {
                String idFoto = null;
                if (libro.getFoto() != null){
                    idFoto = libro.getFoto().getId();
                }
                Foto foto = picServ.actualizar(idFoto, archivo);
                libro.setFoto(foto);
            }
        } else {
            throw new ErrorServicio("No hay un socio registrado con ese nombre.");
        }
    }
    
        
    @Transactional
    public void darBajaLibro(String id) throws ErrorServicio {
        Libro libro = null;
        Optional<Libro> rta = libroRepo.buscaLibroId(id);
        if (rta.isPresent()) {
            libro = rta.get();
        } else {
            throw new ErrorServicio("El titulo ingresado no pertenece a un libro listado en la base de datos");
        }
        
        if (libro.getAlta().equals(true)) {
            libro.setAlta(Boolean.FALSE);
        } else {
            throw new ErrorServicio("El libro consultado ya se encuentra dado de baja.");    
        }
    }
    
    @Transactional
    public void darAltaLibro(String id) throws ErrorServicio {
        Libro libro = null;
        Optional<Libro> rta = libroRepo.buscaLibroIdCompl(id);
        if (rta.isPresent()) {
            libro = rta.get();
        } else {
            throw new ErrorServicio("El titulo ingresado no pertenece a un libro listado en la base de datos");
        }
        
        if (libro.getAlta().equals(false)) {
            libro.setAlta(Boolean.TRUE);
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
        Libro libro = new Libro();
        Optional<Libro> rta = libroRepo.buscaLibroId(id);
        if (rta.isPresent()){
            libro = rta.get();
        } else {
            throw new ErrorServicio("El libro solicitado no se encuentra listado en la base de datos.");
        }
        return libro;
    }

    
    @Transactional(readOnly = true)
    public Libro buscarLibroTit(String tit) throws ErrorServicio {
        Libro libro = new Libro();
        Optional<Libro> rta = libroRepo.buscaLibroNom(tit);
        if (rta.isPresent()){
            libro = rta.get();
        } else {
            throw new ErrorServicio("El nombre ingresado no pertenece a un libro listado en la base de datos.");
        }
        return libro;
    }
    
    
    @Transactional(readOnly = true)
    public Libro buscarLibroTitCompl(String tit) throws ErrorServicio {
        Libro libro = new Libro();
        Optional<Libro> rta = libroRepo.buscaLibroNomCompl(tit);
        if (rta.isPresent()){
            libro = rta.get();
        } else {
            throw new ErrorServicio("El nombre ingresado no pertenece a un libro listado en la base de datos.");
        }
        return libro;
    }
    
    
    @Transactional(readOnly = true)    
    public List <Libro> buscarLibroAut(String aut) throws ErrorServicio {
        List <Libro> libros = new ArrayList();
        Optional<List<Libro>> rta = libroRepo.listarLibrosAutor(aut);
        if (rta.isPresent()){
            libros = rta.get();
        } else {
            throw new ErrorServicio("El nombre ingresado no pertenece a un libro listado en la base de datos.");
        }
        return libros;
    }
    
    @Transactional(readOnly = true)
    public List <Libro> buscarLibroEd(String ed) throws ErrorServicio {
        List <Libro> libros = new ArrayList();
        Optional<List<Libro>> rta = libroRepo.listarLibrosEditorial(ed);
        if (rta.isPresent()){
            libros = rta.get();
        } else {
            throw new ErrorServicio("El nombre ingresado no pertenece a un libro listado en la base de datos.");
        }
        return libros;
    }
    
    @Transactional(readOnly = true)
    public List <Libro> listarLibroEd(String ed) throws ErrorServicio {
        
        List <Libro> libros = null;
        Optional<List<Libro>> rta = libroRepo.listarLibrosEditorial(ed);
        if (rta.isPresent()) {
            libros = rta.get();
        }
        return libros;
    }
    
    
    @Transactional
    public void modEjemplaresRet(Libro libro) throws ErrorServicio {
        if (libro.getEjemplaresRestantes()>0) {
            libro.setEjemplaresRestantes(libro.getEjemplaresRestantes() - 1);
            libro.setEjemplaresPrestados(libro.getEjemplaresPrestados() + 1);
        } else {
            System.out.println("No hay ejemplares para prestar");
        }
        libroRepo.save(libro);
    }
    
    @Transactional
    public void modEjemplaresDev(Libro libro) throws ErrorServicio {
        if (libro.getEjemplaresTotales()>0) {
            libro.setEjemplaresRestantes(libro.getEjemplaresRestantes() + 1);
            libro.setEjemplaresPrestados(libro.getEjemplaresPrestados() - 1);
        } else {
            System.out.println("No hay ejemplares para prestar");
        }
        libroRepo.save(libro);
    }

}