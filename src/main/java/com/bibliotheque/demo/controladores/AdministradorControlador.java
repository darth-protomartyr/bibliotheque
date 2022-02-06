/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.controladores;
import com.bibliotheque.demo.entidades.Orden;
import com.bibliotheque.demo.entidades.Prestamo;
import com.bibliotheque.demo.entidades.Usuario;
import com.bibliotheque.demo.excepciones.ErrorServicio;
import com.bibliotheque.demo.repositorios.OrdenRepositorio;
import com.bibliotheque.demo.repositorios.PrestamoRepositorio;
import com.bibliotheque.demo.repositorios.UsuarioRepositorio;
import com.bibliotheque.demo.servicios.AdministradorServicio;
import com.bibliotheque.demo.servicios.OrdenServicio;
import com.bibliotheque.demo.servicios.PrestamoServicio;
import java.text.ParseException;
import java.util.ArrayList;
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
OrdenServicio ordenServ;
@Autowired
UsuarioRepositorio usuarioRepo;
@Autowired
PrestamoRepositorio prestamoRepo;
@Autowired
PrestamoServicio prestamoServ;
@Autowired
OrdenRepositorio ordenRepo;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN_REGISTRADO')")
    @GetMapping("/administrador")
    public String administradores(HttpSession session, @RequestParam String id, ModelMap modelo) throws ErrorServicio {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/inicio";
        }

        List <Usuario> solicitantes = adminServ.listarSolicitantes();
        modelo.put("solicitantes", solicitantes);    
        modelo.put("pen", "La cuenta se encuentra penalizada para realizar préstamos");
        return "administrador.html";
    }
    
    
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN_REGISTRADO')")
    @PostMapping("/proceso-iniciar-orden")
    public String iniciarOrden(HttpSession session, @RequestParam String id, @RequestParam String solicitId, ModelMap modelo) {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/inicio";
        }
        Usuario solicit = null;
        Optional <Usuario> rta = usuarioRepo.findById(solicitId);
        if(rta.isPresent()) {
            solicit = rta.get();
        }
        modelo.put("perfil", solicit);
        modelo.put("pen", "La cuenta se encuentra penalizada para realizar préstamos");

        
        List<Prestamo> solicitados = new ArrayList();
        Optional <List<Prestamo>> rta1 = prestamoRepo.listarPrestamoSolicitadosUsuarioID(solicitId);
        if(rta1.isPresent()) {
            solicitados = rta1.get();
        }       
        modelo.put("solicitados", solicitados);
        
        Orden orden = ordenServ.iniciarOrden(solicit);       
        modelo.put("order", orden);

        return "orden.html";
    }
    
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN_REGISTRADO')")
    @PostMapping("/proceso-completar-orden")
    public String completarOrden(@RequestParam(required=false) String error, HttpSession session, @RequestParam String id, @RequestParam String ordenId, @RequestParam String prestamoId, ModelMap modelo) throws ErrorServicio, ParseException {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/inicio";
        }
        
        modelo.put("pen", "La cuenta se encuentra penalizada para realizar préstamos");
        
        Orden orden = null;
        Optional <Orden> rta = ordenRepo.buscaOrdenIdAlta(ordenId);
        if (rta.isPresent()) {
            orden=rta.get();
        }
        modelo.put("order", orden);
        
        Prestamo prestamo = prestamoServ.completarPrestamo(prestamoId);
        
        List <Prestamo> prestamos = ordenServ.listaPrestamoAlta(ordenId, prestamoId);


        orden.setPrestamos(prestamos);
        
        Usuario usuario = prestamo.getUsuario();
        String solicitId = usuario.getId();
        
        List<Prestamo> solicitados = new ArrayList();
        Optional <List<Prestamo>> rta2 = prestamoRepo.listarPrestamoSolicitadosUsuarioID(solicitId);
        if(rta2.isPresent()) {
            solicitados = rta2.get();
        }
        
        modelo.put("solicitados", solicitados);
        modelo.put("perfil", usuario);        
        
        int solicitInt = solicitados.size();
        
        if(solicitInt > 0) {
            modelo.put("solicitados",solicitados);
            if(error != null) {
                modelo.put("error", "No se pudo completar la orden");
            } else {
                modelo.put("succes", "El pedido fue ingresado a la orden de préstamos");
            }
            return "orden.html";
        } else {
            modelo.put("tit", "Operación Exitosa");
            modelo.put("subTit", "La orden fue ingresada y los prestamos están activos.");
            return "succes.html";
        }   
    }
}