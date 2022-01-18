/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.controladores;
import com.bibliotheque.demo.entidades.Admin;
import com.bibliotheque.demo.enumeraciones.Genero;
import com.bibliotheque.demo.excepciones.ErrorServicio;
import com.bibliotheque.demo.servicios.AdminServicio;
import java.util.ArrayList;
import java.util.Arrays;
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
@PreAuthorize("hasAnyRole('ROLE_ADMIN_REGISTRADO')")
@RequestMapping("/perfiles")
public class PerfilControlador {

    @Autowired
    private AdminServicio adminServ;
    


    @PreAuthorize("hasAnyRole('ROLE_ADMIN_REGISTRADO')")
    @GetMapping("/perfil")
    public String perfil(HttpSession session, @RequestParam String id, ModelMap modelo) {
        List<Genero> generos = new ArrayList<Genero>(Arrays.asList(Genero.values()));
        modelo.put("generos", generos);
        
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
        return "perfil.html";
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN_REGISTRADO')")
    @PostMapping("/editar-perfil")
    public String modificarAdmin(ModelMap modelo, HttpSession session, @RequestParam String id, String name, String pass1, String pass2, int generoId, String mail, MultipartFile archivo) {
        List<Genero> generos = new ArrayList<Genero>(Arrays.asList(Genero.values()));
        Admin admin = null;
        try {

            Admin login = (Admin) session.getAttribute("adminsession");
            if (login == null || !login.getId().equals(id)) {
                return "redirect:/inicio";
            }

            admin = adminServ.buscarPorId(id);
            adminServ.modificar(id, name, pass1, pass2, generoId, mail, archivo);
            session.setAttribute("adminsession", admin);
            modelo.put("tit", "Operación Exitosa");
            modelo.put("subTit", "La información fue ingresada al base de datos correctamente.");
            return "succes.html";
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());
            modelo.put("perfil", admin);
            modelo.put("generos", generos);
            return "perfil.html";
        }
    }        
}