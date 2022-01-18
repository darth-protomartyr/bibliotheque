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
@RequestMapping("/")
public class PortalControlador {
    @Autowired
    private AdminServicio adminServ;

    @GetMapping("/")
    public String index(ModelMap modelo) throws ErrorServicio{    
        List<Admin> adminsActivos = adminServ.consultaListaAdmins();
        modelo.addAttribute("admins", adminsActivos);
        return "index.html";
    }

    
    @GetMapping("/login")
    public String login(@RequestParam(required=false) String error, @RequestParam(required = false) String logout, ModelMap modelo){
        if (error!= null) {
            modelo.put("error", "Nombre de usuario o clave incorrectos");
        }
        if (logout != null) {
            modelo.put("logout", "Ha salido correctamemnte de la plataforma");
        }
        return "login.html";
    }

    
    @PreAuthorize("hasAnyRole('ROLE_ADMIN_REGISTRADO')")
    @GetMapping("/inicio")
    public String inicio(ModelMap modelo, HttpSession session) throws ErrorServicio {
//-------------------------intento
        Admin login = (Admin) session.getAttribute("adminsession");
        String id = login.getId();
        Admin admin = adminServ.buscarPorId(id);
        modelo.addAttribute("perfil", admin);
//-------------------------intento
        return "inicio.html";
    }
    

    @GetMapping("/registrar")
    public String registrar(ModelMap modelo){
        List<Genero> generos = new ArrayList<Genero>(Arrays.asList(Genero.values()));
        modelo.put("generos", generos);
        return "registrar.html";
    }
    
    @PostMapping("/proceso-registro")
    public String registro(ModelMap modelo, @RequestParam String nom, @RequestParam String pass1, @RequestParam String pass2, @RequestParam int generoId, MultipartFile archivo,@RequestParam String mail) throws ErrorServicio {
        List<Genero> generos = new ArrayList<Genero>(Arrays.asList(Genero.values()));
        try {
            adminServ.registrarAdmin(nom, pass1, pass2, generoId, archivo, mail);
        } catch (ErrorServicio e) {
            modelo.put("error", e.getMessage());
            modelo.put("nom", nom);
            modelo.put("pass1", pass1);
            modelo.put("pass2", pass2);
            modelo.put("mail", mail);
            modelo.put("generos", generos);
            modelo.put("generoId", generoId);
            modelo.put("archivo", archivo);
            
            return "registrar.html";
        }
        modelo.put("tit", "Operación Exitosa");
        modelo.put("subTit", "La información fue ingresada al base de datos correctamente.");
        return "succes.html";
    }
}