/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.controladores;




import com.bibliotheque.demo.entidades.Usuario;
import com.bibliotheque.demo.enumeraciones.Genero;
import com.bibliotheque.demo.excepciones.ErrorServicio;
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
@RequestMapping("/")
public class PortalControlador {
    @Autowired
    private UsuarioServicio usuarioServ;

    @GetMapping("/")
    public String index(ModelMap modelo) throws ErrorServicio{    
        List<Usuario> usuariosActivos = usuarioServ.consultaListaUsuarios();
        modelo.addAttribute("usuarios", usuariosActivos);
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
        Usuario login = (Usuario) session.getAttribute("usuariosession");
        String id = login.getId();
        Usuario usuario = usuarioServ.buscarPorId(id);
        modelo.put("pen", "La cuenta se encuentra penalizada para realizar préstamos");

        modelo.addAttribute("perfil", usuario);
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
            usuarioServ.registrarUsuario(nom, pass1, pass2, generoId, archivo, mail);
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
        return "succes_1.html";
    }
}