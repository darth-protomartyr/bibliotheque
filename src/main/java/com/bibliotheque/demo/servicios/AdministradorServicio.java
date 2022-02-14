package com.bibliotheque.demo.servicios;


import com.bibliotheque.demo.entidades.Orden;
import com.bibliotheque.demo.entidades.Prestamo;
import com.bibliotheque.demo.entidades.Usuario;
import com.bibliotheque.demo.excepciones.ErrorServicio;
import com.bibliotheque.demo.repositorios.OrdenRepositorio;
import com.bibliotheque.demo.repositorios.UsuarioRepositorio;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bibliotheque.demo.repositorios.PrestamoRepositorio;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;


/**
 *
 * @author Gonzalo
 */
@Service
public class AdministradorServicio {
    @Autowired
    private PrestamoRepositorio prestamoRepo;
    @Autowired
    private UsuarioRepositorio usuarioRepo;
    @Autowired
    private UsuarioServicio usuarioServ;
    @Autowired
    private OrdenRepositorio ordenRepo;
    
    @Transactional(readOnly = true)
    public List<Usuario> listarSolicitantes() throws ErrorServicio {
        List<Prestamo> prestamos = new ArrayList();
        HashSet<Usuario> solicitantesHS = new HashSet();
        Optional<List<Prestamo>> rta = prestamoRepo.listarPrestamoSolicitados();
        if (rta.isPresent()) {
            prestamos = rta.get();
        }
        for (Prestamo prestamo : prestamos) {
            solicitantesHS.add(prestamo.getUsuario());
        }
        List<Usuario> solicitantes = new ArrayList(solicitantesHS);
        return solicitantes;
    }    

    @Transactional(readOnly = true)
    public List<Orden> listarActivas() {
        List<Orden> activos = new ArrayList();
        Optional <List<Orden>> rta = ordenRepo.listarActivas();
        if (rta.isPresent()) {
            activos= rta.get();
        }
        return activos;
    }
    
    @Transactional(readOnly = true)
    public List<Orden> listarVencidas() {
        List <Orden> vencidas = new ArrayList();
        List <Orden> todas = listarActivas();
        for (Orden toda : todas) {
            if (toda.getFechaDevolucion().before(new Date())) {
                vencidas.add(toda);
            }
        }
        return vencidas;
    }
    
    @Transactional
    public void completarBajaDeUsuario(String solicitId) {
        Usuario usuario = null;
        Optional <Usuario> rta = usuarioRepo.buscaUsuarioIdAlta(solicitId);
        if(rta.isPresent()) {
            usuario = rta.get();
        }
        usuario.setAlta(Boolean.FALSE);
        usuarioRepo.save(usuario);
    }
    
    
    @Transactional
    public List<Usuario> listarActualizarPenalidades() throws ErrorServicio {
        List <Usuario> usuarios = new ArrayList();
        Optional <List<Usuario>> rta = usuarioRepo.ListarUsuarioIdAltaPenAlta();
        if(rta.isPresent()) {
            usuarios = rta.get();
        }    
        
        //Actualiza el estado de la penalidad al momento presente
        
        

        Iterator<Usuario> it = usuarios.iterator();
        while(it.hasNext()) {
            Usuario usuario = it.next();
            if (usuario.getFechaPenalidad().before(new Date())) {
                usuario.setFechaPenalidad(null);
                usuario.setPenalidad(Boolean.FALSE);
                usuarioRepo.save(usuario);
//                usuarios.remove(usuario);        
            }
        }
        
        Optional <List<Usuario>> rta1 = usuarioRepo.ListarUsuarioIdAltaPenAlta();
        if(rta1.isPresent()) {
            usuarios = rta1.get();
        }
        
        
        
//        for (Usuario usuario : usuarios) {
//            
//            if (usuario.getFechaPenalidad().before(new Date())) {
//                usuario.setFechaPenalidad(null);
//                usuario.setPenalidad(Boolean.FALSE);
//                usuarioRepo.save(usuario);
////                usuarios.remove(usuario);        
//            }
//        }
        
        return usuarios;
    }
}