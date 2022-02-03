/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.controladores;
import com.bibliotheque.demo.entidades.Usuario;
import com.bibliotheque.demo.excepciones.ErrorServicio;
import com.bibliotheque.demo.repositorios.UsuarioRepositorio;
import com.bibliotheque.demo.servicios.AdministradorServicio;
import java.util.List;
import java.util.Optional;
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
@RequestMapping("/administradores")
public class AdministradorControlador {

@Autowired
AdministradorServicio adminServ;
@Autowired
UsuarioRepositorio usuarioRepo;


    @PreAuthorize("hasAnyRole('ROLE_ADMIN_REGISTRADO')")
    @GetMapping("/administrador")
    public String administradores(HttpSession session, @RequestParam String id, ModelMap modelo) throws ErrorServicio {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/inicio";
        }
//        Usuario solicitantes = adminServ.listarSolicitantes();
        Usuario solicitantes = null;
        
        Optional <Usuario> rta = usuarioRepo.findById(id);
        if (rta.isPresent()) {
            solicitantes = rta.get();
        }
        
        modelo.put("solicitantes", solicitantes);    
        modelo.put("pen", "La cuenta se encuentra penalizada para realizar préstamos");
        return "administrador.html";
    }
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN_REGISTRADO')")
    @PostMapping("/proceso-generar-orden")
    public String generarOrden() {
        String html = "html";
        return html;
    }
    
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN_REGISTRADO')")
//    @PostMapping("/proceso-generar")
//    public String generarPrestamo( ModelMap modelo, @RequestParam String id , HttpSession session, @RequestParam String libroId) throws ErrorServicio {
//        
//        Usuario login = (Usuario) session.getAttribute("usuariosession");
//        if (login == null || !login.getId().equals(id)) {
//            return "redirect:/inicio";
//        }
//        modelo.put("pen", "La cuenta se encuentra penalizada para realizar préstamos");
//        
//        try {                       
//            String usuarioId= login.getId();
//            prestamoServ.crearPrestamo(usuarioId, libroId);
//            modelo.put("success", "La solicitud fue envíada. Si desea realizar otra, seleccione un texto");
//            listas(modelo, id);
//
//
//            return "prestamos.html";
//        } catch (ErrorServicio e) {
//            listas(modelo, id);
//            modelo.put("error", e.getMessage());
//            modelo.put("libroId", libroId);
//            return "prestamos.html";
//        }
//    }
//    
//    //genera las lista que formaran parte del html
//    private void listas (ModelMap modelo, String id) {
//        List <Libro> libros = libroRepo.listarLibrosActivos();
//        modelo.put("libros", libros);
//        
//        List <Prestamo> solicitudes = null;
//        Optional <List <Prestamo>> rta = prestamoRepo.buscaPrestamoSolicitUsuarioID(id);
//        if (rta.isPresent()) {
//            solicitudes = rta.get();
//        }
//        modelo.put("solicitudes", solicitudes);
//        if (solicitudes.isEmpty()) {
//            modelo.put("mes1","La cuenta no tiene solicitudes pendientes");
//        }
//        
//        List <Prestamo> prestamos = null;
//        Optional <List <Prestamo>> rta1 = prestamoRepo.buscaPrestamoActivosUsuarioID(id);
//        if (rta.isPresent()) {
//            prestamos = rta1.get();
//        }
//        modelo.put("prestamos", prestamos);
//        if (prestamos.isEmpty()) {
//            modelo.put("mes2","La cuenta no tiene préstamos vigentes");
//        }
//    }
    
}