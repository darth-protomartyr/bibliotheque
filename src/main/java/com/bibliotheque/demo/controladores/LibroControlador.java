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
    private LibroRepositorio bookRepo;
    @Autowired
    private LibroServicio bookServ;
    @Autowired
    private AutorRepositorio wrRepo;
    @Autowired
    private EditorialRepositorio ediRepo;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN_REGISTRADO')")
    @GetMapping("/libro")
    public String libros(HttpSession session, @RequestParam String id, ModelMap modelo) throws ErrorServicio {
        List<Libro> textos = bookRepo.findAll();
        modelo.put("textos", textos);
        Admin login = (Admin) session.getAttribute("adminsession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/inicio";
        }
        try {
            Admin admin = adminServ.buscarPorId(id);
            modelo.addAttribute("perfil", admin);
        } catch (ErrorServicio e) {
            modelo.addAttribute("error", e.getMessage());
        }
        return "libros.html";
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN_REGISTRADO')")
    @GetMapping("/ingresar")
    public String ingresar(HttpSession session, @RequestParam String id, ModelMap modelo){
        List<Autor> writers = wrRepo.findAll();
        List<Editorial> pubs = ediRepo.findAll();
        Admin login = (Admin) session.getAttribute("adminsession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/inicio";
        }
        modelo.put("writers", writers);
        modelo.put("pubs", pubs);
        return "libro-ingresar.html";
    }
    
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN_REGISTRADO')")
    @PostMapping("/proceso-ingresar")
    public String procesoIngresar( ModelMap modelo, HttpSession session, String titulo, Long isbn, Integer ejemplaresTotales, String autor, String editorial, MultipartFile archivo) throws ErrorServicio {
        try {
            bookServ.crearLibro(isbn, titulo, ejemplaresTotales, autor, editorial, archivo);
        } catch (ErrorServicio e) {
            modelo.put("error", e.getMessage());
            modelo.put("titulo", titulo);
            modelo.put("isbn", isbn);
            modelo.put("ejemplaresTotales", ejemplaresTotales);
            modelo.put("autor", autor);
            modelo.put("archivo", archivo);

            return "libro-ingresar.html";
        }
        modelo.put("tit", "Operación Exitosa");
        modelo.put("subTit", "La información fue ingresada a la base de datos correctamente.");

        return "succes.html";
    }
    
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN_REGISTRADO')")
    @GetMapping("/modificar")
    public String modificar(HttpSession session, @RequestParam String id, @RequestParam String idLibro,  ModelMap modelo){
        List<Autor> writers = wrRepo.findAll();
        List<Editorial> pubs = ediRepo.findAll();
        Admin login = (Admin) session.getAttribute("adminsession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/inicio";
        }
        modelo.put("writers", writers);
        modelo.put("pubs", pubs);
        return "modificar-libro.html";
    }

    
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN_REGISTRADO')")
    @PostMapping("/proceso-modificar")
    public String procesoModificar(ModelMap modelo, HttpSession session, @RequestParam String id, @RequestParam String libroId, String titulo, Long isbn, Integer ejemplaresTotales, String autor, String editorial, MultipartFile archivo) throws ErrorServicio {
        Libro book = null;
        try {

            Admin login = (Admin) session.getAttribute("adminsession");
            if (login == null || !login.getId().equals(id)) {
                return "redirect:/inicio";
            }
            

            book = bookServ.buscarLibroId(libroId);
            bookServ.modificar(libroId, isbn, titulo, ejemplaresTotales, autor, editorial, archivo);
            //session.setAttribute("adminsession", admin);
            modelo.put("tit", "Operación Exitosa");
            modelo.put("subTit", "La información fue ingresada al base de datos correctamente.");
            return "succes.html";
            
            
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());
            modelo.put("perfil", book);
            return "modificar-libro.html";
        }
    }

    
    
    
    
//
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN_REGISTRADO')")
//    @GetMapping("/editar-perfil")
//    public String editarPerfil(HttpSession session, @RequestParam String id, ModelMap modelo) {
//        List<Genero> sexos = genRepo.findAll();
//        modelo.put("sexos", sexos);
//        
//        Admin login = (Admin) session.getAttribute("adminsession");
//        if (login == null || !login.getId().equals(id)) {
//            return "redirect:/inicio";
//        }
//
//        try {
//            Admin admin = adminServ.buscarPorId(id);
//            modelo.addAttribute("perfil", admin);
//        } catch (ErrorServicio e) {
//            modelo.addAttribute("error", e.getMessage());
//        }
//        return "libros.html";
//    }
//
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN_REGISTRADO')")
//    @PostMapping("/proceso-actualizar-perfil")
//    public String modificarAdmin(ModelMap modelo, HttpSession session, @RequestParam String id, String name, String pass1, String pass2, byte sexoId, String mail, MultipartFile archivo) {
//        
//        Admin admin = null;
//        try {
//
//            Admin login = (Admin) session.getAttribute("adminsession");
//            if (login == null || !login.getId().equals(id)) {
//                return "redirect:/inicio";
//            }
//
//            admin = adminServ.buscarPorId(id);
//            adminServ.modificar(id, name, pass1, pass2, sexoId, mail, archivo);
//            session.setAttribute("adminsession", admin);
//            return "redirect:/inicio";
//        } catch (ErrorServicio ex) {
//            modelo.put("error", ex.getMessage());
//            modelo.put("perfil", admin);
//
//            return "perfil.html";
//        }
//   }        
}