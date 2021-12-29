/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.servicios;


import com.bibliotheque.demo.entidades.Foto;
import com.bibliotheque.demo.entidades.Genero;
import com.bibliotheque.demo.entidades.Socio;
import com.bibliotheque.demo.excepciones.ErrorServicio;
import com.bibliotheque.demo.repositorios.GeneroRepositorio;
import com.bibliotheque.demo.repositorios.SocioRepositorio;
import com.bibliotheque.demo.repositorios.SocioRepositorio;
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
public class SocioServicio implements UserDetailsService {
    @Autowired
    private SocioRepositorio socioRepo;
    @Autowired
    private FotoServicio picServ;
    @Autowired
    private GeneroRepositorio genRepo;
    @Autowired
    private NotificacionServicio notServ;
//-----------------------------------------------Socio y Socio
    @Transactional
    public Socio registrarSocio(String nombre, String pass1, String pass2, byte sexoId, MultipartFile archivo, String mail) throws ErrorServicio {
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
    
        Socio socio = new Socio();
        socio.setNombre(nombre);
        String passCrypt = new BCryptPasswordEncoder().encode(pass);
        socio.setPass(passCrypt);
        socio.setSexo(genRepo.buscaGenId(sexoId));
        Foto foto = picServ.guardar(archivo);
        socio.setFoto(foto);
        socio.setAlta(true);
        socio.setMail(validarMail(mail));
        //notServ.enviar("Bienvenido a Bookdepository", "Bookdepository", socio.getMail());
        return socioRepo.save(socio);
    }
    
    @Transactional
    public void modificar(String id, String name, String pass1 , String pass2, byte sexoId, String mail, MultipartFile archivo) throws ErrorServicio {
        
        Socio socio = null;
        Optional<Socio> rta1 = socioRepo.findById(id);
        if(rta1.isPresent()) {
            socio = rta1.get();
            if (name == null || name.isEmpty()) {
                socio.setNombre(socio.getNombre());
            } else {
                socio.setNombre(name);
            }
            
            String pass = null;
            
            if (pass1 != null && !pass1.isEmpty() && pass2 != null && !pass2.isEmpty()) {
                if (pass1.equals(pass2)) {
                    pass = pass1;
                    if (pass.length() > 3) {
                        String passCrypt = new BCryptPasswordEncoder().encode(pass);
                        socio.setPass(passCrypt);
                    } else {
                        throw new ErrorServicio("El password ingresado posee menos de 4 caracteres");
                    }
                } else {
                    throw new ErrorServicio("No coinciden los passwords ingresados.");
                }
            }
                      
            if (sexoId != 1 && sexoId != 2 && sexoId != 3) {
                socio.setSexo(socio.getSexo());
            } else {
                socio.setSexo(genRepo.buscaGenId(sexoId));
            }

            if (mail == null || mail.isEmpty()) {
                socio.setMail(socio.getMail());
            } else {
                socio.setMail(validarMail(mail));
            }

            if (archivo == null || archivo.isEmpty()) {
                socio.setFoto(socio.getFoto());
            } else {
                String idFoto = null;
                if (socio.getFoto() != null){
                    idFoto = socio.getFoto().getId();
                }
                Foto foto = picServ.actualizar(idFoto, archivo);
                socio.setFoto(foto);
            }
        } else {
            throw new ErrorServicio("No hay un socio registrado con ese nombre.");
        }
        socioRepo.save(socio);
        //notServ.enviar("LA modificación ha sido realizada", "Bookdepository", socio.getMail());
    }
    
    @Transactional
    public void BajaDeSocio(String name, String pass) throws ErrorServicio {
        Socio memberLoan = null;
        Optional<Socio> rta1 = socioRepo.buscaSocioNom(name);
        if(rta1.isPresent()) {
            memberLoan = rta1.get();
            if (memberLoan.getAlta().equals(false)) {
                throw new ErrorServicio("El usuario se encuentra dado de baja.");
            } else {
                 memberLoan.setAlta(Boolean.TRUE);
                 socioRepo.save(memberLoan);
            }
        } else {
            throw new ErrorServicio ("El nombre de usuario que ingresó no se encuentra en la base de datos");
        }
    }
    
    @Transactional
    public void AltaDeSocio(String name, String pass) throws ErrorServicio {
        Optional<Socio> rta = socioRepo.buscaSocioNom(name);
        Socio socio = rta.get();
        if (rta.isPresent()) {
            if (socio.getAlta().equals(true)) {
                throw new ErrorServicio("El usuario se encuentra dado de alta.");
            } else {
            socio.setAlta(Boolean.TRUE);
            socioRepo.save(socio);
            }
        } else {
            throw new ErrorServicio ("El nombre de usuario que ingresó no se encuentra en la base de datos");
        }
    }
    
    //--------------------------------------Solo Socio
    @Transactional(readOnly = true)
    public Socio consultaSocio(String nom) throws ErrorServicio {
        Optional<Socio> rta = socioRepo.buscaSocioNom(nom);
        if (rta.isPresent()) {
            Socio socio = rta.get();
            return socio;
        } else {
            throw new ErrorServicio ("El nombre del usuario que ingresó no se encuentra en la base de datos");
        }
    }
    
    @Transactional(readOnly = true)
    public List<Socio> consultaListaSocios() throws ErrorServicio {
        List<Socio> socios = socioRepo.listarSocios();
        return socios;
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
        Socio socio = null;
        Optional <Socio> rta = socioRepo.buscaSocioMail(mail);
        if (rta.isPresent()) {
            socio = rta.get();
            List<GrantedAuthority> permisos = new ArrayList();
            GrantedAuthority p1 = new SimpleGrantedAuthority("ROLE_ADMIN_REGISTRADO");
            permisos.add(p1);
            
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpSession session = attr.getRequest().getSession(true);
            session.setAttribute("sociosession", socio);
            
            User user = new User(socio.getMail(), socio.getPass(), permisos);
            return user;
            
        } else {
            return null;
        }
    }
    
    @Transactional(readOnly=true)
    public Socio buscarPorId(String id) throws ErrorServicio {

        Optional<Socio> respuesta = socioRepo.findById(id);
        if (respuesta.isPresent()) {

            Socio socio = respuesta.get();
            return socio;
        } else {
            throw new ErrorServicio("No se encontró el usuario solicitado");
        }
    }
}