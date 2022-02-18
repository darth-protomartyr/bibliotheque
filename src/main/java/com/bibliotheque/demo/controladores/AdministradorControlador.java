/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.controladores;
import com.bibliotheque.demo.entidades.Orden;
import com.bibliotheque.demo.entidades.Prestamo;
import com.bibliotheque.demo.entidades.Usuario;
import com.bibliotheque.demo.enumeraciones.Genero;
import com.bibliotheque.demo.enumeraciones.Rol;
import com.bibliotheque.demo.excepciones.ErrorServicio;
import com.bibliotheque.demo.repositorios.OrdenRepositorio;
import com.bibliotheque.demo.repositorios.PrestamoRepositorio;
import com.bibliotheque.demo.repositorios.UsuarioRepositorio;
import com.bibliotheque.demo.servicios.AdministradorServicio;
import com.bibliotheque.demo.servicios.OrdenServicio;
import com.bibliotheque.demo.servicios.PrestamoServicio;
import com.bibliotheque.demo.servicios.UsuarioServicio;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.springframework.web.multipart.MultipartFile;

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
UsuarioServicio usuarioServ;
@Autowired
OrdenRepositorio ordenRepo;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR')")
    @GetMapping("/administrador")
    public String administradores(HttpSession session, @RequestParam String id, ModelMap modelo) throws ErrorServicio {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/inicio";
        }

        ordenServ.limpiezaOrden();
        List <Usuario> solicitantes = adminServ.listarSolicitantes();
        modelo.put("solicitantes", solicitantes);
        List <Orden> activas = adminServ.listarActivas();
        modelo.put("activas", activas);
        List <Orden> vencidas = adminServ.listarVencidas();
        modelo.put("vencidas", vencidas);
        List <Usuario> bajados = usuarioServ.listarBajados();
        modelo.put("bajados", bajados);
        List <Usuario> penalizados = adminServ.listarActualizarPenalidades();
        modelo.put("penalizados", penalizados);
        modelo.put("pen", "La cuenta se encuentra penalizada para realizar préstamos");
        return "administrador.html";
    }


    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR')")
    @PostMapping("/proceso-iniciar-orden")
    public String iniciarOrden(HttpSession session, @RequestParam String id, @RequestParam String solicitId, ModelMap modelo) {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/inicio";
        }
        
        
       ordenServ.limpiezaOrden();

        
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
    
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR')")
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
            orden = rta.get();
        }
        
        modelo.put("order", orden);
        Prestamo prestamo = prestamoServ.completarPrestamo(prestamoId, ordenId);
        
        orden.agregarPrestamo(prestamo);
        
        
        
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
            if(error != null) {
                modelo.put("error", "No se pudo completar la orden");
            } else {
                modelo.put("succes", "El pedido fue ingresado a la orden de préstamos");
            }
            return "orden.html";
        } else {
            ordenServ.seteaFechaPrestamos(ordenId);
            modelo.put("tit", "Operación Exitosa");
            modelo.put("subTit", "La orden fue ingresada y los prestamos están en curso.");
            return "succes.html";
        }
    }
    
    
    
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR')")
    @PostMapping("/baja-orden")
    public String bajaOrden(@RequestParam(required=false) String error, HttpSession session, @RequestParam String id, @RequestParam String ordenId, ModelMap modelo) throws ErrorServicio, ParseException {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/inicio";
        }
        modelo.put("pen", "La cuenta se encuentra penalizada para realizar préstamos");

        Orden orden = null;
        Optional <Orden> rta = ordenRepo.findById(ordenId);
        if (rta.isPresent()) {
            orden = rta.get();
        }
        
        Usuario usuario = orden.getUsuario();
        
        List<Prestamo>activos = orden.getPrestamos();
        modelo.put("activos", activos);
        modelo.put("perfil", usuario);        
        modelo.put("order", orden);        

        return "orden-baja.html";

    }
    
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR')")
    @PostMapping("/proceso-baja-prestamo")
    public String ProcesoBajaPrestamo(@RequestParam(required=false) String error, HttpSession session, @RequestParam String id, @RequestParam String prestamoId, @RequestParam String ordenId, ModelMap modelo) throws ErrorServicio, ParseException {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/inicio";
        }
        modelo.put("pen", "La cuenta se encuentra penalizada para realizar préstamos");
        
        prestamoServ.bajaPrestamo(prestamoId, ordenId);
        
        
        Orden orden = null;
        Optional <Orden> rta = ordenRepo.buscaOrdenIdAlta(ordenId);
        if (rta.isPresent()) {
            orden = rta.get();
        }
        
        
        List<Prestamo> activos = new ArrayList();
        Optional <List<Prestamo>> rta2 = prestamoRepo.listarPrestamoByOrden(ordenId);
        if(rta2.isPresent()) {
            activos = rta2.get();
        }
        
        Usuario usuario = orden.getUsuario();
        
        if (activos.size() > 0) {
            modelo.put("perfil", usuario);
            modelo.put("activos", activos);
            modelo.put("order", orden);
            return "orden-baja.html";
        } else {
            ordenServ.seteaOrden(ordenId);
            modelo.put("tit", "Operación Exitosa");
            modelo.put("subTit", "Los prestamos y la orden fueron dados de baja.");
            return "succes.html";
        }
    }
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/finalizar-proceso-baja-cuenta")
    public String finalizarBajaCuenta(ModelMap modelo, HttpSession session, @RequestParam String id, @RequestParam String solicitId) throws ErrorServicio {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/inicio";
        }
        
        modelo.put("pen", "La cuenta se encuentra penalizada para realizar préstamos");

        modelo.addAttribute("perfil", login);
        
        adminServ.completarBajaDeUsuario(solicitId);
        modelo.put("tit", "Operación Exitosa");
        modelo.put("subTit", "La baja del usuario es efectiva.");
        return "succes.html";
    }
    
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/proceso-eliminar-penalidad")
    public String eliminarPenalidad(ModelMap modelo, HttpSession session, @RequestParam String id, @RequestParam String penalizadoId) throws ErrorServicio {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/inicio";
        }
        
        usuarioServ.eliminarPenalidad(penalizadoId);
        
        
        modelo.put("pen", "La cuenta se encuentra penalizada para realizar préstamos");
        modelo.addAttribute("perfil", login);
        modelo.put("tit", "Operación Exitosa");
        modelo.put("subTit", "La baja del usuario es efectiva.");
        return "succes.html";
    }



    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping("/proceso-modificar-penalidad")
    public String modificarPenalidad(ModelMap modelo, HttpSession session, String id, String penalizadoId, String newPen) throws ErrorServicio, Exception {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/inicio";
        }

        usuarioServ.modificarPenalidad(penalizadoId, newPen);

        modelo.put("pen", "La cuenta se encuentra penalizada para realizar préstamos");
        modelo.addAttribute("perfil", login);
        modelo.put("tit", "Operación Exitosa");
        modelo.put("subTit", "La baja del usuario es efectiva.");
        return "succes.html";
    }
    
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR')")
    @GetMapping("/usuarios")
    public String usuarios(ModelMap modelo, HttpSession session, @RequestParam String id) {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/inicio";
        }
        
        List <Usuario> usuarios = null;
        Optional <List<Usuario>> rta = usuarioRepo.listarUsuarios();
        if (rta.isPresent()) {
            usuarios = rta.get();
        }
        
        modelo.put("usuarios",usuarios);
        modelo.put("pen", "La cuenta se encuentra penalizada para realizar préstamos");
        return "usuarios.html";
    }
    
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR')")
    @PostMapping("/proceso-buscar-id")
    public String buscarId(ModelMap modelo, HttpSession session, @RequestParam String id, @RequestParam String user) {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/inicio";
        }
        
        Usuario usuario = null;
        Optional <Usuario> rta = usuarioRepo.findById(user);
        if (rta.isPresent()) {
            usuario = rta.get();
        }
        
        List <Usuario> usuarios = usuarioRepo.findAll();       
        List<Genero> generos = new ArrayList<Genero>(Arrays.asList(Genero.values()));
        List<Rol> roles = new ArrayList<Rol>(Arrays.asList(Rol.values()));
        roles.remove(Rol.ADMIN);
        
        modelo.put("roles", roles);
        modelo.put("generos", generos);
        modelo.put("perfil",usuario);
        modelo.put("usuarios",usuarios);
        modelo.put("pen", "La cuenta se encuentra penalizada para realizar préstamos");
        return "usuarios.html";
    }
    
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR')")
    @PostMapping("/proceso-buscar-nombre")
    public String buscarNom(ModelMap modelo, HttpSession session, @RequestParam String id, @RequestParam String qUsuario) throws ErrorServicio {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/inicio";
        }
        
        List <Usuario> usuarios = usuarioRepo.findAll();

        
        try {
            Usuario usuario = null;
            Optional <Usuario> rta = usuarioRepo.buscaUsuarioNomTot(qUsuario);
            if (rta.isPresent()) {
                usuario = rta.get();
            } else {
                throw new ErrorServicio("El Usuario no se encuantra en la base de datos");
            }
            
            
            List<Genero> generos = new ArrayList<Genero>(Arrays.asList(Genero.values()));
            List<Rol> roles = new ArrayList<Rol>(Arrays.asList(Rol.values()));
            roles.remove(Rol.ADMIN);
            modelo.put("roles", roles);
            modelo.put("generos", generos);
            modelo.put("perfil",usuario);
            modelo.put("usuarios",usuarios);
            modelo.put("pen", "La cuenta se encuentra penalizada para realizar préstamos");
            return "usuarios.html";
        } catch (ErrorServicio e) {
            modelo.put("error", e.getMessage());
            modelo.put("usuarios",usuarios);
            modelo.put("pen", "La cuenta se encuentra penalizada para realizar préstamos");
            return "usuarios.html";
        }
    }
    
    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR')")
    @PostMapping("/proceso-modificar-rol")
    public String modificarRol(ModelMap modelo, HttpSession session, @RequestParam String id, @RequestParam String usuarioId, @RequestParam String rol) throws ErrorServicio {
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/inicio";
        }
        
        modelo.put("pen", "La cuenta se encuentra penalizada para realizar préstamos");

        Usuario usuario = usuarioServ.modificarRol(usuarioId, rol);

        modelo.put("pen", "La cuenta se encuentra penalizada para realizar préstamos");
        modelo.put("tit", "Operación Exitosa");
        if (usuario.getRol().equals(Rol.EDITOR)) {
            modelo.put("subTit", "El usuario ahora tiene el rol de Editor");
        } else {
            modelo.put("subTit", "El usuario ahora tiene el rol de Usuario");
        }
        return "succes.html";
    }



    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EDITOR')")
    @PostMapping("/editar-perfil")
    public String modificarUsuario(ModelMap modelo, HttpSession session, @RequestParam String id, @RequestParam String usuarioId, String name, String pass1, String pass2, int generoId, String mail, MultipartFile archivo) {
        
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        if (login == null || !login.getId().equals(id)) {
            return "redirect:/inicio";
        }
        modelo.put("pen", "La cuenta se encuentra penalizada para realizar préstamos");

        
        List<Genero> generos = new ArrayList<Genero>(Arrays.asList(Genero.values()));
        Usuario usuario = null;

        
        try {
            usuario = usuarioServ.buscarPorId(usuarioId);
            usuarioServ.modificar(usuarioId, name, pass1, pass2, generoId, mail, archivo);
            modelo.put("tit", "Operación Exitosa");
            modelo.put("subTit", "La información fue ingresada al base de datos correctamente.");
            return "succes.html";
        } catch (ErrorServicio ex) {
            modelo.put("error", ex.getMessage());
            modelo.put("perfil", usuario);
            modelo.put("generos", generos);
            return "usuarios.html";
        }
    }
}