/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.controladores;

import com.bibliotheque.demo.entidades.Usuario;
import com.bibliotheque.demo.excepciones.ErrorServicio;
import com.bibliotheque.demo.servicios.EmailSenderService;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Gonzalo
 */
@Controller
@RequestMapping("/contactos")
public class ContactoControlador {

    @Autowired
    private EmailSenderService mailService;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR', 'ROLE_USUARIO')")
    @GetMapping("/contacto")
    public String contacto(HttpSession session, @RequestParam String id, ModelMap modelo) throws ErrorServicio {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/login";
        }
        modelo.put("pen", "La cuenta se encuentra penalizada para realizar préstamos");
        String role = login.getRol().toString();
        modelo.put("role", role);


        return "contacto.html";
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR', 'ROLE_USUARIO')")
    @PostMapping("/enviar-mail")
    public String enviarMail (HttpSession session, ModelMap modelo, @RequestParam String id, @RequestParam String name, @RequestParam String mail, @RequestParam String subject, @RequestParam String body) throws ErrorServicio{
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/login";
        }
        modelo.put("pen", "La cuenta se encuentra penalizada para realizar préstamos");
        String role = login.getRol().toString();
        modelo.put("role", role);
        
        String message = body +"\n\n Datos de contacto: " + "\nNombre: " + name + "\nE-mail: " + mail;
        mailService.sendMail( mail, subject, message);
        
        modelo.put("tit", "Operación Exitosa");
        modelo.put("subTit", "El mail fue enviado");
        return "succes.html";
    }
}