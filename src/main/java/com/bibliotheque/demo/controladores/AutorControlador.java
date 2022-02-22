/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.controladores;

import com.bibliotheque.demo.entidades.Usuario;
import com.bibliotheque.demo.entidades.Autor;
import com.bibliotheque.demo.excepciones.ErrorServicio;
import com.bibliotheque.demo.repositorios.AutorRepositorio;
import com.bibliotheque.demo.servicios.AutorServicio;
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
@RequestMapping("/autores")
public class AutorControlador {

    
    @Autowired
    private AutorRepositorio autorRepo;
    @Autowired
    private AutorServicio autorServ;

    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR', 'ROLE_USUARIO')")
    @GetMapping("/autor")
    public String autores(HttpSession session, @RequestParam String id, ModelMap modelo) throws ErrorServicio {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/login";
        }
        modelo.put("pen", "La cuenta se encuentra penalizada para realizar préstamos");
        String role = login.getRol().toString();
        modelo.put("role", role);
        
        
        return "autores.html";
    }
    
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR', 'ROLE_USUARIO')")
    @PostMapping("/proceso-buscar")
    public String buscar(HttpSession session, @RequestParam String id, @RequestParam String qautor, ModelMap modelo) throws ErrorServicio{
        Autor autor= null;
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/login";
        }
        modelo.put("pen", "La cuenta se encuentra penalizada para realizar préstamos");
        String role = login.getRol().toString();
        modelo.put("role", role);
        
        
        try {         
            autor = autorServ.consultaAutorNomCompl(qautor);
            modelo.put("autor", autor);
            return "autor.html";
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());
            return "autores.html";
        }
    }
    

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR', 'ROLE_USUARIO')")
    @GetMapping("/ingresar")
    public String ingresar(HttpSession session, @RequestParam String id, ModelMap modelo){
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/login";
        }
        modelo.put("pen", "La cuenta se encuentra penalizada para realizar préstamos");
        String role = login.getRol().toString();
        modelo.put("role", role);
        return "autor-ingresar.html";
    }
    
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR')")
    @PostMapping("/proceso-ingresar")
    public String procesoIngresar(HttpSession session, @RequestParam String id, @RequestParam String nombre, @RequestParam MultipartFile archivo, ModelMap modelo) throws ErrorServicio {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/login";
        }       
        modelo.put("pen", "La cuenta se encuentra penalizada para realizar préstamos");
        String role = login.getRol().toString();
        modelo.put("role", role);
        try {
            autorServ.crearAutor(nombre, archivo);
            modelo.put("tit", "Operación Exitosa");
            modelo.put("subTit", "El Autor fue ingresado a la base de datos correctamente.");
            return "succes.html";
        } catch (ErrorServicio e) {
            modelo.put("error", e.getMessage());
            modelo.put("nombre", nombre);
            modelo.put("archivo", archivo);
            return "autor-ingresar.html";
        }
    }
   
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR', 'ROLE_USUARIO')")
    @GetMapping("/listar-activas")
    public String ListarActiva(HttpSession session, @RequestParam String id, ModelMap modelo) throws ErrorServicio {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/login";
        }
        modelo.put("pen", "La cuenta se encuentra penalizada para realizar préstamos");
        String role = login.getRol().toString();
        modelo.put("role", role);
        
        
        List<Autor> autores = autorRepo.listarAutorActiva();
        modelo.put("autores", autores);

        return "autores-lista-activos.html";
    }
    
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR')")
    @GetMapping("/listar-todas")
    public String ListarTodas(HttpSession session, @RequestParam String id, ModelMap modelo) throws ErrorServicio {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/login";
        }
        List<Autor> autores = autorRepo.listarAutorCompleta();
        modelo.put("autores", autores);
        modelo.put("pen", "La cuenta se encuentra penalizada para realizar préstamos");
        String role = login.getRol().toString();
        modelo.put("role", role);
        return "autores-lista-completa.html";
    }
    
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR')")
    @GetMapping("/modificar")
    public String modificar(HttpSession session, @RequestParam String id, @RequestParam String autorId,  ModelMap modelo) throws ErrorServicio{
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/login";
        }
        modelo.put("pen", "La cuenta se encuentra penalizada para realizar préstamos");
        String role = login.getRol().toString();
        modelo.put("role", role);
        Autor autor = autorServ.consultaAutorId(autorId);
        modelo.put("autor", autor);
        return "autor-actualizar.html";
    }
    
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR')")
    @PostMapping("/proceso-modificar")
    public String procesoModificar(ModelMap modelo, HttpSession session, @RequestParam String id, @RequestParam String autorId, @RequestParam String nombre, @RequestParam MultipartFile archivo) throws ErrorServicio {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/login";
        }
        modelo.put("pen", "La cuenta se encuentra penalizada para realizar préstamos");
        String role = login.getRol().toString();
        modelo.put("role", role);
        try {
            autorServ.modificarAutor(nombre, archivo, autorId);
            modelo.put("tit", "Operación Exitosa");
            modelo.put("subTit", "La información fue modificada correctamente.");
            return "succes.html";
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());
            return "autor-actualizar.html";
        }
    }
   
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR')")
    @PostMapping("/proceso-baja")
    public String procesoBaja(ModelMap modelo, HttpSession session, @RequestParam String id, @RequestParam String autorId) throws ErrorServicio {    
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/login";
        }
        modelo.put("pen", "La cuenta se encuentra penalizada para realizar préstamos");
        String role = login.getRol().toString();
        modelo.put("role", role);
        try {     
            autorServ.bajaAutor(autorId);
            modelo.put("tit", "Operación Exitosa");
            modelo.put("subTit", "La información fue modificada correctamente.");
            return "succes.html";
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());
            return "autores.html";
        }
    }
    
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR')")
    @PostMapping("/proceso-alta")
    public String procesoAlta(ModelMap modelo, HttpSession session, @RequestParam String id, @RequestParam String autorId) throws ErrorServicio {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/login";
        }
        modelo.put("pen", "La cuenta se encuentra penalizada para realizar préstamos");
        String role = login.getRol().toString();
        modelo.put("role", role);
        try {       
            autorServ.altaAutor(autorId);
            modelo.put("tit", "Operación Exitosa");
            modelo.put("subTit", "La información fue modificada correctamente.");
            return "succes.html";
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());
            return "autores.html";
        }
    }
}