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
import java.util.Collection;
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
}