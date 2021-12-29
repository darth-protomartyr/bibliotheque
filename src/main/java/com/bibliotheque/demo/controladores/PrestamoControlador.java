/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.controladores;

import com.bibliotheque.demo.entidades.Genero;
import com.bibliotheque.demo.entidades.Admin;
import com.bibliotheque.demo.entidades.Libro;
import com.bibliotheque.demo.entidades.Prestamo;
import com.bibliotheque.demo.excepciones.ErrorServicio;
import com.bibliotheque.demo.repositorios.GeneroRepositorio;
import com.bibliotheque.demo.repositorios.LibroRepositorio;
import com.bibliotheque.demo.repositorios.PrestamoRepositorio;
import com.bibliotheque.demo.servicios.AdminServicio;
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
@RequestMapping("/prestamos")
public class PrestamoControlador {

    @Autowired
    private AdminServicio adminServ;    
    @Autowired
    private PrestamoRepositorio loanRepo;
    

    @PreAuthorize("hasAnyRole('ROLE_ADMIN_REGISTRADO')")
    @GetMapping("/prestamo")
    public String prestamos(HttpSession session, @RequestParam String id, ModelMap modelo) throws ErrorServicio {
        List<Prestamo> loans = loanRepo.findAll();
        modelo.put("loans", loans);
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
        return "prestamos.html";
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