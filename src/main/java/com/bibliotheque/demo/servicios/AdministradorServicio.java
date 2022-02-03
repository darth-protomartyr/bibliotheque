package com.bibliotheque.demo.servicios;


import com.bibliotheque.demo.entidades.Libro;
import com.bibliotheque.demo.entidades.Prestamo;
import com.bibliotheque.demo.entidades.Usuario;
import com.bibliotheque.demo.excepciones.ErrorServicio;
import com.bibliotheque.demo.repositorios.LibroRepositorio;
import com.bibliotheque.demo.repositorios.UsuarioRepositorio;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bibliotheque.demo.repositorios.PrestamoRepositorio;
import java.util.ArrayList;
import java.util.HashSet;


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
    
    @Transactional
    public Usuario listarSolicitantes() throws ErrorServicio {
        Usuario usuario = null;
        List<Prestamo> solicitantes = null;
        Optional <List <Prestamo>> rta = prestamoRepo.listarPrestamo();
        if (rta.isPresent()) {
            solicitantes = rta.get();
        }
        
        for (Prestamo solicitante : solicitantes) {
            usuario = solicitante.getUsuario();
        }     
        return usuario ;
    }    
}