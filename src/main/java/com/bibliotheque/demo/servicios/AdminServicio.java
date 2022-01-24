/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.servicios;


import com.bibliotheque.demo.entidades.Foto;
import com.bibliotheque.demo.entidades.Admin;
import com.bibliotheque.demo.enumeraciones.Genero;
import com.bibliotheque.demo.excepciones.ErrorServicio;
import com.bibliotheque.demo.repositorios.AdminRepositorio;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;


/**
 *
 * @author Gonzalo
 */

@Service
public class AdminServicio implements UserDetailsService {
    @Autowired
    private AdminRepositorio adminRepo;
    @Autowired
    private FotoServicio picServ;
    @Autowired
    private NotificacionServicio notServ;
//-----------------------------------------------Admin y Admin
    @Transactional
    public Admin registrarAdmin(String nombre, String pass1, String pass2, int generoId, MultipartFile archivo, String mail) throws ErrorServicio {
        if (nombre == null || nombre.isEmpty()) {
            throw new ErrorServicio("Falta el nombre del usuario");
        }
        
        if (pass1 == null || pass1.isEmpty()) {
            throw new ErrorServicio("Falta ingresar el password del usuario");
        }
        
        if (pass2 == null || pass2.isEmpty()) {
            throw new ErrorServicio("Falta ingresar la comprobacion del password del usuario");
        }
        
        String pass = new String();
        if (pass1.equals(pass2)) {
            pass = pass1;
        } else {
            throw new ErrorServicio("No coinciden los passwords ingresados.");
        }
        
        if (pass.length() < 3) {
            throw new ErrorServicio("El password ingresado posee menos de 4 caracteres");
        }
        
        if (generoId != 1 && generoId != 2 && generoId != 3) {
            throw new ErrorServicio("Falta ingresar el sexo del usuario");
        }
        
        Optional<Admin> rta = adminRepo.buscaAdminMail(mail);
        if (rta.isPresent()) {
            throw new ErrorServicio("El mail ya se encuentra registrado en la base de datos");
        }
        
        if (mail == null || mail.isEmpty()) {
            throw new ErrorServicio("Falta ingresar el sexo del usuario");
        }
    
        Admin admin = new Admin();
        admin.setNombre(nombre);
        String passCrypt = new BCryptPasswordEncoder().encode(pass);
        admin.setPass(passCrypt);
        admin.setGenero(validateGenero(generoId));
        Foto foto = picServ.guardar(archivo);
        admin.setFoto(foto);
        admin.setAlta(true);
        admin.setMail(validarMail(mail));
        //notServ.enviar("Bienvenido a Librodepository", "Librodepository", admin.getMail());
        return adminRepo.save(admin);
    }
    
    @Transactional
    public void modificar(String id, String name, String pass1 , String pass2, int generoId, String mail, MultipartFile archivo) throws ErrorServicio {
        
        Admin admin = null;
        Optional<Admin> rta1 = adminRepo.findById(id);
        if(rta1.isPresent()) {
            admin = rta1.get();
            if (name == null || name.isEmpty()) {
                admin.setNombre(admin.getNombre());
            } else {
                admin.setNombre(name);
            }
            
            String pass = null;
            
            if (pass1 != null && !pass1.isEmpty() && pass2 != null && !pass2.isEmpty()) {
                if (pass1.equals(pass2)) {
                    pass = pass1;
                    if (pass.length() > 3) {
                        String passCrypt = new BCryptPasswordEncoder().encode(pass);
                        admin.setPass(passCrypt);
                    } else {
                        throw new ErrorServicio("El password ingresado posee menos de 4 caracteres");
                    }
                } else {
                    throw new ErrorServicio("No coinciden los passwords ingresados.");
                }
            }
                      
            if (generoId != 1 && generoId != 2 && generoId != 3) {
                admin.setGenero(admin.getGenero());
            } else {
                admin.setGenero(validateGenero(generoId));
            }
            
            Optional<Admin> rta = adminRepo.buscaAdminMail(mail);
            if (rta.isPresent() && !mail.equals(admin.getMail())) {
                throw new ErrorServicio("El mail ya se encuentra registrado en la base de datos");
            }
            

            if (mail == null || mail.isEmpty()) {
                admin.setMail(admin.getMail());
            } else {
                admin.setMail(validarMail(mail));
            }

            if (archivo == null || archivo.isEmpty()) {
                admin.setFoto(admin.getFoto());
            } else {
                String idFoto = null;
                if (admin.getFoto() != null){
                    idFoto = admin.getFoto().getId();
                }
                Foto foto = picServ.actualizar(idFoto, archivo);
                admin.setFoto(foto);
            }
        } else {
            throw new ErrorServicio("No hay un socio registrado con ese nombre.");
        }
        adminRepo.save(admin);
        //notServ.enviar("LA modificación ha sido realizada", "Librodepository", admin.getMail());
    }
    
    @Transactional
    public void bajaDeAdmin(String id, String pass) throws ErrorServicio {
        Admin adminPrestamo = null;
        Optional<Admin> rta1 = adminRepo.buscaAdminIdAlta(id);
        if(rta1.isPresent()) {
            adminPrestamo = rta1.get();
            if (adminPrestamo.getAlta().equals(false)) {
                throw new ErrorServicio("El usuario se encuentra dado de baja.");
            } else {
                 adminPrestamo.setAlta(Boolean.TRUE);
                 adminRepo.save(adminPrestamo);
            }
        } else {
            throw new ErrorServicio ("El nombre de usuario que ingresó no se encuentra en la base de datos");
        }
    }
    
    @Transactional
    public void altaDeAdmin(String id, String pass) throws ErrorServicio {
        Optional<Admin> rta = adminRepo.buscaAdminIdAlta(id);
        Admin admin = rta.get();
        if (rta.isPresent()) {
            if (admin.getAlta().equals(true)) {
                throw new ErrorServicio("El usuario se encuentra dado de alta.");
            } else {
            admin.setAlta(Boolean.TRUE);
            adminRepo.save(admin);
            }
        } else {
            throw new ErrorServicio ("El nombre de usuario que ingresó no se encuentra en la base de datos");
        }
    }
    
//    @Transactional
//    public void modificarPenalidad(String id, int penalidad) throws ErrorServicio {
//        Optional<Admin> rta = adminRepo.buscaAdminIdAlta(id);
//        Admin admin = rta.get();
//        if (rta.isPresent()) {
//            if (admin.getAlta().equals(true)) {
//                throw new ErrorServicio("El usuario se encuentra dado de alta.");
//            } else {
//            admin.setAlta(Boolean.TRUE);
//            adminRepo.save(admin);
//            }
//        } else {
//            throw new ErrorServicio ("El nombre de usuario que ingresó no se encuentra en la base de datos");
//        }
//    }
    
    @Transactional
    public void eliminarPenalidad (String id) throws ErrorServicio {
        Admin admin = null;
        Optional <Admin> rta = adminRepo.buscaAdminIdAltaPenAlta(id);
        if (rta.isPresent()) {
            admin = rta.get();
            admin.setPenalidad(Boolean.FALSE);
            admin.setFechaPenalidad(null);
        }  else {
            throw new ErrorServicio ("El nombre del usuario que ingresó no se encuentra en los Registros o no está penalizado");
        }
    }
    
    //--------------------------------------Solo Admin
    @Transactional(readOnly = true)
    public Admin consultaAdmin(String nom) throws ErrorServicio {
        Optional<Admin> rta = adminRepo.buscaAdminNom(nom);
        if (rta.isPresent()) {
            Admin admin = rta.get();
            return admin;
        } else {
            throw new ErrorServicio ("El nombre del usuario que ingresó no se encuentra en la base de datos");
        }
    }
    
    @Transactional(readOnly = true)
    public List<Admin> consultaListaAdmins() throws ErrorServicio {
        List<Admin> admins = adminRepo.listarAdmins();
        return admins;
    }

    static Genero validateGenero(int generoId) throws ErrorServicio {
        switch (generoId) {
            case 1:
                return Genero.HOMBRE;
            case 2:
                return Genero.MUJER;
            case 3:
                return Genero.OTRO;
            default:
                throw new ErrorServicio("No ingresó un dato válido en la categoria género");
        }
    }
    
    private static String validarMail(String mail) throws ErrorServicio {
	if (mail.contains("@") && mail.contains(".com")) {
            return mail;
        } else {
            throw new ErrorServicio("No es una direccion e-mail válida");
        }
    }


    @Override
    public UserDetails loadUserByUsername(String mail) throws UsernameNotFoundException {
        Admin admin = null;
        Optional <Admin> rta = adminRepo.buscaAdminMail(mail);
        if (rta.isPresent()) {
            admin = rta.get();
            List<GrantedAuthority> permisos = new ArrayList();
            GrantedAuthority p1 = new SimpleGrantedAuthority("ROLE_ADMIN_REGISTRADO");
            permisos.add(p1);
            
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);
            session.setAttribute("adminsession", admin);
            
            User user = new User(admin.getMail(), admin.getPass(), permisos);
            return user;
            
        } else {
            return null;
        }
    }
    
    @Transactional(readOnly=true)
    public Admin buscarPorId(String id) throws ErrorServicio {

        Optional<Admin> respuesta = adminRepo.findById(id);
        if (respuesta.isPresent()) {

            Admin admin = respuesta.get();
            return admin;
        } else {
            throw new ErrorServicio("No se encontró el usuario solicitado");
        }
    }
}