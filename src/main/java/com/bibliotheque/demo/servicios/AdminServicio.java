/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.servicios;


import com.bibliotheque.demo.entidades.Foto;
import com.bibliotheque.demo.entidades.Genero;
import com.bibliotheque.demo.entidades.Admin;
import com.bibliotheque.demo.excepciones.ErrorServicio;
import com.bibliotheque.demo.repositorios.GeneroRepositorio;
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
    private GeneroRepositorio genRepo;
    @Autowired
    private NotificacionServicio notServ;
//-----------------------------------------------Admin y Admin
    @Transactional
    public Admin registrarAdmin(String nombre, String pass1, String pass2, byte sexoId, MultipartFile archivo, String mail) throws ErrorServicio {
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
        
        if (sexoId != 1 && sexoId != 2 && sexoId != 3) {
            throw new ErrorServicio("Falta ingresar el sexo del usuario");
        }
        
        if (mail == null || mail.isEmpty()) {
            throw new ErrorServicio("Falta ingresar el sexo del usuario");
        }
    
        Admin admin = new Admin();
        admin.setNombre(nombre);
        String passCrypt = new BCryptPasswordEncoder().encode(pass);
        admin.setPass(passCrypt);
        admin.setSexo(genRepo.buscaGenId(sexoId));
        Foto foto = picServ.guardar(archivo);
        admin.setFoto(foto);
        admin.setAlta(true);
        admin.setMail(validarMail(mail));
        //notServ.enviar("Bienvenido a Bookdepository", "Bookdepository", admin.getMail());
        return adminRepo.save(admin);
    }
    
    @Transactional
    public void modificar(String id, String name, String pass1 , String pass2, byte sexoId, String mail, MultipartFile archivo) throws ErrorServicio {
        
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
                      
            if (sexoId != 1 && sexoId != 2 && sexoId != 3) {
                admin.setSexo(admin.getSexo());
            } else {
                admin.setSexo(genRepo.buscaGenId(sexoId));
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
        //notServ.enviar("LA modificación ha sido realizada", "Bookdepository", admin.getMail());
    }
    
    @Transactional
    public void BajaDeAdmin(String name, String pass) throws ErrorServicio {
        Admin memberLoan = null;
        Optional<Admin> rta1 = adminRepo.buscaAdminNom(name);
        if(rta1.isPresent()) {
            memberLoan = rta1.get();
            if (memberLoan.getAlta().equals(false)) {
                throw new ErrorServicio("El usuario se encuentra dado de baja.");
            } else {
                 memberLoan.setAlta(Boolean.TRUE);
                 adminRepo.save(memberLoan);
            }
        } else {
            throw new ErrorServicio ("El nombre de usuario que ingresó no se encuentra en la base de datos");
        }
    }
    
    @Transactional
    public void AltaDeAdmin(String name, String pass) throws ErrorServicio {
        Optional<Admin> rta = adminRepo.buscaAdminNom(name);
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

    static Genero validateSex(byte sexoId) throws ErrorServicio {
        
        Genero sex = null;
        switch (sexoId) {
            case 1:
                sex.setId(sexoId);
                sex.setGen("hombre");
                break;
            case 2:
                sex.setId(sexoId);
                sex.setGen("mujer");
                break;
            case 3:
                sex.setId(sexoId);
                sex.setGen("otro");
                break;
            default:
                throw new ErrorServicio("No ingresó un dato válido en la categoria género");
        }
        return sex;
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