/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.servicios;


import com.bibliotheque.demo.entidades.Editorial;
import com.bibliotheque.demo.excepciones.ErrorServicio;
import com.bibliotheque.demo.repositorios.EditorialRepositorio;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Gonzalo
 */
@Service
public class EditorialServicio {
    @Autowired
    EditorialRepositorio editorialRepo;

    @Transactional
    public Editorial crearEditorial(String nombre) throws ErrorServicio {
        if (nombre == null || nombre.isEmpty()) {
            throw new ErrorServicio("Falta el nombre de la Editorial");
        }
        
        Editorial buk = new Editorial();
        
        Optional<Editorial> rta = editorialRepo.buscaEditorialNomCompl(nombre);
        if (rta.isPresent()) {
            throw new ErrorServicio("La Editorial ya se encuentra registrado en la base de datos");
        }
        
        buk.setNombre(nombre);
        buk.setAlta(true);
        return editorialRepo.save(buk);
    }
    
    @Transactional
    public void modificarEditorial(String id, String nombre) throws ErrorServicio {
        Editorial buk = null;
        Optional <Editorial> rta = editorialRepo.buscaEditorialIdCompl(id);
        if (rta.isPresent()) {
            buk = rta.get();
        } else {
            throw new ErrorServicio("La editorial seleccionada no está en la base de datos");
        }
        
        Optional<Editorial> rta1 = editorialRepo.buscaEditorialNomCompl(nombre);
        if (rta1.isPresent() && !nombre.equals(buk.getNombre())) {
            throw new ErrorServicio("La Editorial ya se encuentra registrada en la base de datos");
        }
        
        buk.setNombre(nombre);
        editorialRepo.save(buk);
    }
   
    @Transactional
    public void bajaEditorial(String id) throws ErrorServicio{
        Editorial buk = null;
        Optional <Editorial> rta = editorialRepo.buscaEditorialIdCompl(id);
        if (rta.isPresent()) {
            buk = rta.get();
        } else {
            throw new ErrorServicio("La editorial seleccionada no está en la base de datos");
        }
        
        if (buk.getAlta().equals(true)) {
            buk.setAlta(false);
            editorialRepo.save(buk);
        } else {
            System.out.println("La editorial seleccionada ya se encuentra dada de baja.");
        }
    }
    
    @Transactional
    public void altaEditorial(String id) throws ErrorServicio {
        Editorial buk = null;
        Optional <Editorial> rta = editorialRepo.buscaEditorialIdCompl(id);
        if (rta.isPresent()) {
            buk = rta.get();
        } else {
            throw new ErrorServicio("La editorial seleccionada no está en la base de datos");
        }
        
        if (buk.getAlta().equals(false)) {
            buk.setAlta(true);
            editorialRepo.save(buk);
        } else {
           throw new ErrorServicio("La editorial seleccionada ya se encuentra dada de alta.");
        }
    }
    
    
    @Transactional(readOnly = true)
    public Editorial consultaEditorialId(String id) throws ErrorServicio {
        Editorial ed = null;
        Optional <Editorial> rta = editorialRepo.buscaEditorialId(id);
        if (rta.isPresent()) {
            ed = rta.get();
        } else {
            throw new ErrorServicio("La Editorial consultada no pertenece a una Editorial listada en la base de datos");
        }
        return ed;
    }
    
    @Transactional(readOnly = true)
    public Editorial consultaEditorialIdCompl(String id) throws ErrorServicio {
        Editorial ed = null;
        Optional <Editorial> rta = editorialRepo.buscaEditorialIdCompl(id);
        if (rta.isPresent()) {
            ed = rta.get();
        } else {
            throw new ErrorServicio("La Editorial consultada no pertenece a una Editorial listada en la base de datos");
        }
        return ed;
    }
    
    
    @Transactional(readOnly = true)
    public Editorial consultaEditorialNom(String nombre) throws ErrorServicio {
        Editorial ed = null;
        Optional <Editorial> rta = editorialRepo.buscaEditorialNom(nombre);
        if (rta.isPresent()) {
            ed = rta.get();
        } else {
            throw new ErrorServicio("El nombre seleccionado no pertenece a una Editorial listada en la base de datos");
        }
        return ed;
    }
    
    @Transactional(readOnly = true)
    public Editorial consultaEditorialNomCompl(String nombre) throws ErrorServicio {
        Editorial ed = null;
        Optional <Editorial> rta = editorialRepo.buscaEditorialNomCompl(nombre);
        if (rta.isPresent()) {
            ed = rta.get();
        } else {
            throw new ErrorServicio("El nombre seleccionado no pertenece a una Editorial listada en la base de datos");
        }
        return ed;
    }
    

    @Transactional(readOnly = true)
    public List<Editorial> listarEditorialesActivas() {
        List<Editorial>wrs = editorialRepo.listarEditorialActiva();
        return wrs;
    }
    
    @Transactional(readOnly = true)
    public List<Editorial> listarEditorialesCompletas() {
        List<Editorial>wrs = editorialRepo.listarEditorialCompleta();
        return wrs;
    }
}