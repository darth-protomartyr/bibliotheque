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
    EditorialRepositorio edRepo;

    @Transactional
    public Editorial crearEditorial(String name) throws ErrorServicio {
        if (name == null || name.isEmpty()) {
            throw new ErrorServicio("Falta el nombre de la Editorial");
        }
        
        Editorial buk = new Editorial();
        buk.setNombre(name);
        buk.setAlta(true);
        return edRepo.save(buk);
    }
    
    @Transactional
    public void modificarEditorial(String nombre) throws ErrorServicio {
        Editorial buk = null;
        Optional <Editorial> rta = edRepo.buscaEditorialNom(nombre);
        if (rta.isPresent()) {
            buk = rta.get();
        } else {
            throw new ErrorServicio("El autor seleccionado no está en la base de datos");
        }
        buk.setNombre(nombre);
        edRepo.save(buk);
    }
   
    @Transactional
    public void bajaEditorial(String nombre) throws ErrorServicio{
        Editorial buk = null;
        Optional <Editorial> rta = edRepo.buscaEditorialNom(nombre);
        if (rta.isPresent()) {
            buk = rta.get();
        } else {
            throw new ErrorServicio("El autor seleccionado no está en la base de datos");
        }
        
        if (buk.getAlta().equals(true)) {
            buk.setAlta(false);
            edRepo.save(buk);
        } else {
            System.out.println("El autor seleccionado ya se encuenstra dado de baja.");
        }
    }
    
    @Transactional
    public void altaEditorial(String nombre) throws ErrorServicio {
        Editorial buk = null;
        Optional <Editorial> rta = edRepo.buscaEditorialNom(nombre);
        if (rta.isPresent()) {
            buk = rta.get();
        } else {
            throw new ErrorServicio("El autor seleccionado no está en la base de datos");
        }
        
        if (buk.getAlta().equals(false)) {
            buk.setAlta(true);
            edRepo.save(buk);
        } else {
           throw new ErrorServicio("El autor seleccionado ya se encuenstra dado de baja.");
        }
    }
    
    @Transactional(readOnly = true)
    public Editorial consultaEditorial(String nombre) throws ErrorServicio {
        Editorial ed = null;
        Optional <Editorial> rta = edRepo.buscaEditorialNom(nombre);
        if (rta.isPresent()) {
            ed = rta.get();
        } else {
            throw new ErrorServicio("El nombre seleccionado no pertenece a una Editorial listada en la base de datos");
        }
        return ed;
    }

    @Transactional(readOnly = true)
    public List<Editorial> ListarEditorial() {
        List<Editorial>wrs = edRepo.listarEditorial();
        return wrs;
    }
}