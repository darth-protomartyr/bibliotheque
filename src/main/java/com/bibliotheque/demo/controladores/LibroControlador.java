/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.controladores;

import com.bibliotheque.demo.entidades.Admin;
import com.bibliotheque.demo.entidades.Autor;
import com.bibliotheque.demo.entidades.Editorial;
import com.bibliotheque.demo.entidades.Libro;
import com.bibliotheque.demo.excepciones.ErrorServicio;
import com.bibliotheque.demo.repositorios.AutorRepositorio;
import com.bibliotheque.demo.repositorios.EditorialRepositorio;
import com.bibliotheque.demo.repositorios.LibroRepositorio;
import com.bibliotheque.demo.servicios.AdminServicio;
import com.bibliotheque.demo.servicios.LibroServicio;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Gonzalo
 */
@Controller
@RequestMapping("/libros")
public class LibroControlador {

    @Autowired
    private AdminServicio adminServ;    
    @Autowired
    private LibroRepositorio libroRepo;
    @Autowired
    private LibroServicio libroServ;
    @Autowired
    private AutorRepositorio autorRepo;
    @Autowired
    private EditorialRepositorio ediRepo;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN_REGISTRADO')")
    @GetMapping("/libro")
    public String libros(HttpSession session, @RequestParam String id, ModelMap modelo) throws ErrorServicio {
        Admin login = (Admin) session.getAttribute("adminsession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/inicio";
        }
        return "libros.html";
    }
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN_REGISTRADO')")
    @PostMapping("/proceso-buscar")
    public String buscar(HttpSession session, @RequestParam String id, @RequestParam String qlibro, ModelMap modelo) throws ErrorServicio{
        Libro book= null;
        try {
            Admin login = (Admin) session.getAttribute("adminsession");
            if (login == null || !login.getId().equals(id)) {
                return "redirect:/inicio";
            }
            book = libroServ.buscarLibroTitCompl(qlibro);
            modelo.put("book", book);
            return "libro.html";
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());
            return "libros.html";
        }
    }
    


    @PreAuthorize("hasAnyRole('ROLE_ADMIN_REGISTRADO')")
    @GetMapping("/ingresar")
    public String ingresar(HttpSession session, @RequestParam String id, ModelMap modelo) {
        List<Autor> wris = autorRepo.findAll();
        List<Editorial> pubs = ediRepo.findAll();
        Admin login = (Admin) session.getAttribute("adminsession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/inicio";
        }
        modelo.put("wris", wris);
        modelo.put("pubs", pubs);
        return "libro-ingresar.html";
    }
    
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN_REGISTRADO')")
    @PostMapping("/proceso-ingresar")
    public String procesoIngresar( ModelMap modelo, @RequestParam String id, HttpSession session, String titulo, Long isbn, Integer ejemplaresTotales, String wriId, String pubId, MultipartFile archivo) throws ErrorServicio {
        List<Autor> wris = autorRepo.findAll();
        List<Editorial> pubs = ediRepo.findAll();
        try {
            Admin login = (Admin) session.getAttribute("adminsession");
            if (login == null || !login.getId().equals(id)) {
                return "redirect:/inicio";
            }
            libroServ.crearLibro(isbn, titulo, ejemplaresTotales, wriId, pubId, archivo);
            
            modelo.put("tit", "Operación Exitosa");
            modelo.put("subTit", "El Libro fue ingresado a la base de datos correctamente.");
            return "succes.html";
        } catch (ErrorServicio e) {
            modelo.put("error", e.getMessage());
            modelo.put("titulo", titulo);
            modelo.put("isbn", isbn);
            modelo.put("wris", wris);
            modelo.put("pubs", pubs);
            modelo.put("wriId", wriId);
            modelo.put("pubId", pubId);
            modelo.put("archivo", archivo);

            return "libro-ingresar.html";
        }
    }
    
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN_REGISTRADO')")
    @GetMapping("/modificar")
    public String modificar(HttpSession session, @RequestParam String id, @RequestParam String bookId,  ModelMap modelo){
        List<Autor> wris = autorRepo.findAll();
        List<Editorial> pubs = ediRepo.findAll();
        Libro book = libroRepo.getById(bookId);
        Admin login = (Admin) session.getAttribute("adminsession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/inicio";
        }
        modelo.put("book", book);
        modelo.put("wris", wris);
        modelo.put("pubs", pubs);
        return "libro-actualizar.html";
    }

    
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN_REGISTRADO')")
    @PostMapping("/proceso-modificar")
    public String procesoModificar(ModelMap modelo, HttpSession session, @RequestParam String id, @RequestParam String bookId, String titulo, Long isbn, Integer ejemplaresTotales, String wriId, String pubId, MultipartFile archivo) throws ErrorServicio {
        List<Autor> wris = autorRepo.findAll();
        List<Editorial> pubs = ediRepo.findAll();
        Libro book = null;
        try {

            Admin login = (Admin) session.getAttribute("adminsession");
            if (login == null || !login.getId().equals(id)) {
                return "redirect:/inicio";
            }
            

            book = libroServ.buscarLibroId(bookId);
            libroServ.modificar(bookId, isbn, titulo, ejemplaresTotales, wriId, pubId, archivo);
            //session.setAttribute("adminsession", admin);
            modelo.put("tit", "Operación Exitosa");
            modelo.put("subTit", "La información fue ingresada al base de datos correctamente.");
            return "succes.html";
            
            
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());
            modelo.put("book", book);
            modelo.put("wris", wris);
            modelo.put("pubs", pubs);
            modelo.put("wriId", wriId);
            modelo.put("pubId", pubId);
            return "libro-actualizar.html";
        }
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN_REGISTRADO')")
    @GetMapping("/listar-activas")
    public String ListarActiva(HttpSession session, @RequestParam String id, ModelMap modelo) throws ErrorServicio {
        List<Libro> books = libroServ.listarLibrosActivos();
        modelo.put("books", books);
        Admin login = (Admin) session.getAttribute("adminsession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/inicio";
        }

        return "libros-lista-activos.html";
    }
    
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN_REGISTRADO')")
    @GetMapping("/listar-todas")
    public String ListarTodas(HttpSession session, @RequestParam String id, ModelMap modelo) throws ErrorServicio {
        List<Libro> books = libroRepo.listarLibrosCompleta();
        modelo.put("books", books);
        Admin login = (Admin) session.getAttribute("adminsession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/inicio";
        }

        return "libros-lista-completa.html";
    }
    
    
        
    @PreAuthorize("hasAnyRole('ROLE_ADMIN_REGISTRADO')")
    @PostMapping("/proceso-baja")
    public String procesoBaja(ModelMap modelo, HttpSession session, @RequestParam String id, @RequestParam String bookId) throws ErrorServicio {
        try {
            Admin login = (Admin) session.getAttribute("adminsession");
            if (login == null || !login.getId().equals(id)) {
                return "redirect:/inicio";
            }       
            libroServ.darBajaLibro(bookId);
            modelo.put("tit", "Operación Exitosa");
            modelo.put("subTit", "La información fue modificada correctamente.");
            return "succes.html";
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());
            return "autores.html";
        }
    }
    
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN_REGISTRADO')")
    @PostMapping("/proceso-alta")
    public String procesoAlta(ModelMap modelo, HttpSession session, @RequestParam String id, @RequestParam String bookId) throws ErrorServicio {
        try {
            Admin login = (Admin) session.getAttribute("adminsession");
            if (login == null || !login.getId().equals(id)) {
                return "redirect:/inicio";
            }       
            libroServ.darAltaLibro(bookId);
            modelo.put("tit", "Operación Exitosa");
            modelo.put("subTit", "La información fue modificada correctamente.");
            return "succes.html";
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());
            return "autores.html";
        }
    }
}