/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.controladores;
import com.bibliotheque.demo.entidades.Usuario;
import com.bibliotheque.demo.enumeraciones.Genero;
import com.bibliotheque.demo.enumeraciones.Rol;
import com.bibliotheque.demo.excepciones.ErrorServicio;
import com.bibliotheque.demo.servicios.PrestamoServicio;
import com.bibliotheque.demo.servicios.UsuarioServicio;
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
@RequestMapping("/perfiles")
public class PerfilControlador {

    @Autowired
    private UsuarioServicio usuarioServ;
    @Autowired
    private PrestamoServicio prestamoServ;


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR', 'ROLE_USUARIO')")
    @GetMapping("/perfil")
    public String perfil(HttpSession session, @RequestParam String id, ModelMap modelo) {
        List<Genero> generos = new ArrayList<Genero>(Arrays.asList(Genero.values()));
        modelo.put("generos", generos);
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/login";
        }
        modelo.put("pen", "La cuenta se encuentra penalizada para realizar préstamos");
        try {
            Usuario usuario = usuarioServ.buscarPorId(id);
            modelo.addAttribute("perfil", usuario);
        } catch (ErrorServicio e) {
            modelo.addAttribute("error", e.getMessage());
        }
        return "perfil.html";
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR', 'ROLE_USUARIO')")
    @PostMapping("/editar-perfil")
    public String modificarUsuario(ModelMap modelo, HttpSession session, @RequestParam String id, String name, String pass1, String pass2, int generoId, String mail, MultipartFile archivo) {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/login";
        }
        modelo.put("pen", "La cuenta se encuentra penalizada para realizar préstamos");
        List<Genero> generos = new ArrayList<Genero>(Arrays.asList(Genero.values()));
        Usuario usuario = null;
        modelo.addAttribute("perfil", login);        
        try {
            usuario = usuarioServ.buscarPorId(id);
            usuarioServ.modificar(id, name, pass1, pass2, generoId, mail, archivo);
            session.setAttribute("usuariosession", usuario);
            modelo.put("tit", "Operación Exitosa");
            modelo.put("subTit", "La información fue ingresada al base de datos correctamente.");
            return "succes.html";
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());
            modelo.put("perfil", usuario);
            modelo.put("generos", generos);
            return "perfil.html";
        }
    }
    
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR', 'ROLE_USUARIO')")
    @PostMapping("/iniciar-proceso-baja-cuenta")
    public String iniciarBajaCuenta(ModelMap modelo, HttpSession session, @RequestParam String id, @RequestParam String nombre) throws ErrorServicio {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/login";
        }
        modelo.put("pen", "La cuenta se encuentra penalizada para realizar préstamos");
        try {
            //Evita que se dé de baja un usuario con prestamos en curso.
            if (prestamoServ.verificarPrestamosEnCurso(login.getId())) {
                throw new ErrorServicio("Usted tiene préstamos pendientes y no puede solicitar la baja de su cuenta");
            }
            //Evita que el ADMIN SEA DADO DE BAJA
            if(login.getRol().equals(Rol.ADMIN)) {
                throw new ErrorServicio("El Administrador no puede ser dado de baja");
            }            
            if (login.getSolicitudBaja() == true) {
                throw new ErrorServicio("La solicitud de baja ya fue enviada");
            }
            String string1 = usuarioServ.cleanString(nombre);
            String string2 = usuarioServ.cleanString(login.getNombre());
            if (!string1.equals(string2)) {
                throw new ErrorServicio("El nombre ingresado no coincide con el del usuario");
            }
            modelo.addAttribute("perfil", login);
            usuarioServ.iniciarBajaDeUsuario(id);
            modelo.put("tit", "Operación Exitosa");
            modelo.put("subTit", "La Solicitud de baja fue enviada al administrador.");
            return "succes.html";
        } catch (ErrorServicio e) {
            modelo.put("perfil", login);
            modelo.put("error", e.getMessage());
            return "perfil.html";
        }
    }  
}