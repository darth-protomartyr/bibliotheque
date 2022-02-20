/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.controladores;
import com.bibliotheque.demo.entidades.Usuario;
import com.bibliotheque.demo.entidades.Libro;
import com.bibliotheque.demo.entidades.Prestamo;
import com.bibliotheque.demo.excepciones.ErrorServicio;
import com.bibliotheque.demo.repositorios.LibroRepositorio;
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
import com.bibliotheque.demo.repositorios.PrestamoRepositorio;
import com.bibliotheque.demo.servicios.PrestamoServicio;
import java.util.Optional;


@Controller
@RequestMapping("/prestamos")
public class PrestamoControlador {

       
    @Autowired
    private PrestamoRepositorio prestamoRepo;
    @Autowired
    private PrestamoServicio prestamoServ;
    @Autowired
    private LibroRepositorio libroRepo;

    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR', 'ROLE_USUARIO')")
    @GetMapping("/prestamo")
    public String prestamos(HttpSession session, @RequestParam String id, ModelMap modelo) throws ErrorServicio {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/login";
        }
        listas(modelo,id);
        modelo.put("pen", "La cuenta se encuentra penalizada para realizar préstamos");
        return "prestamos.html";
    }
    
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR', 'ROLE_USUARIO')")
    @PostMapping("/proceso-generar-solicitud")
    public String generarPrestamo( ModelMap modelo, @RequestParam String id , HttpSession session, @RequestParam String libroId) throws ErrorServicio {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/login";
        }
        modelo.put("pen", "La cuenta se encuentra penalizada para realizar préstamos");
        try {                       
            String usuarioId= login.getId();
            prestamoServ.iniciarPrestamo(usuarioId, libroId);
            modelo.put("success", "La solicitud fue envíada. Si desea realizar otra, seleccione un texto");
            listas(modelo, id);
            return "prestamos.html";
        } catch (ErrorServicio e) {
            listas(modelo, id);
            modelo.put("error", e.getMessage());
            modelo.put("libroId", libroId);
            return "prestamos.html";
        }
    }
    
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR', 'ROLE_USUARIO')")
    @PostMapping("/proceso-eliminar-solicitud")
    public String eliminarPrestamo( ModelMap modelo, @RequestParam String id , HttpSession session, @RequestParam String solicitId) throws ErrorServicio {   
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/login";
        }
        modelo.put("pen", "La cuenta se encuentra penalizada para realizar préstamos");
        prestamoServ.bajaSolicitud(solicitId);
        modelo.put("success", "La solicitud fue eliminada");
        listas(modelo, id);
        return "prestamos.html";
    }
    
    
    //genera las lista que formaran parte del html
    private void listas (ModelMap modelo, String id) {
        List <Libro> libros = libroRepo.listarLibrosActivos();
        modelo.put("libros", libros);
        List <Prestamo> solicitudes = null;
        Optional <List <Prestamo>> rta = prestamoRepo.listarPrestamoSolicitadosUsuarioID(id);
        if (rta.isPresent()) {
            solicitudes = rta.get();
        }
        modelo.put("solicitudes", solicitudes);
        if (solicitudes.isEmpty()) {
            modelo.put("mes1","La cuenta no tiene solicitudes pendientes");
        }
        List <Prestamo> prestamos = null;
        Optional <List <Prestamo>> rta1 = prestamoRepo.buscaPrestamoActivosUsuarioID(id);
        if (rta.isPresent()) {
            prestamos = rta1.get();
        }
        modelo.put("prestamos", prestamos);
        if (prestamos.isEmpty()) {
            modelo.put("mes2","La cuenta no tiene préstamos vigentes");
        }
    }
}