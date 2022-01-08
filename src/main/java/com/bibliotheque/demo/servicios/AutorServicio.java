/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.servicios;


import com.bibliotheque.demo.entidades.Autor;
import com.bibliotheque.demo.entidades.Foto;
import com.bibliotheque.demo.excepciones.ErrorServicio;
import com.bibliotheque.demo.repositorios.AutorRepositorio;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


/**
 *
 * @author Gonzalo
 */
@Service
public class AutorServicio {
    @Autowired
    AutorRepositorio wrRepo;
    @Autowired
    FotoServicio picServ;
    
    @Transactional
    public Autor crearAutor(String name, MultipartFile archivo) throws ErrorServicio {
        if (name == null || name.isEmpty()) {
            throw new ErrorServicio("Falta el nombre del usuario");
        }
        Autor buk = new Autor();
        buk.setNombre(name);
        buk.setAlta(true);
        Foto foto = picServ.guardar(archivo);
        buk.setRetrato(foto);
        return wrRepo.save(buk);
    }
    
    @Transactional
    public void modificarAutor(String nombre, MultipartFile archivo) throws ErrorServicio {
        Autor buk = null;
        Optional <Autor> rta = wrRepo.buscaAutorNom(nombre);
        
        if (rta.isPresent()) {
            buk = rta.get();
        } else {
            throw new ErrorServicio("El autor seleccionado no está en la base de datos");
        }
        
        if (nombre == null) {
            buk.setNombre(buk.getNombre());
        } else {
            buk.setNombre(nombre);
        }
        
        if (archivo == null) {
            buk.setRetrato(buk.getRetrato());
        }
        
        String idFoto = null;
        if (buk.getRetrato() != null){
            idFoto = buk.getRetrato().getId();
        }

        Foto foto = picServ.actualizar(idFoto, archivo);
        buk.setRetrato(foto);
        wrRepo.save(buk);
    }

    @Transactional
    public void bajaAutor(String nombre) throws ErrorServicio{
        Autor buk = null;
        Optional <Autor> rta = wrRepo.buscaAutorNom(nombre);
        if (rta.isPresent()) {
            buk = rta.get();
        } else {
            throw new ErrorServicio("El autor seleccionado no está en la base de datos");
        }
        
        if (buk.getAlta().equals(true)) {
            buk.setAlta(false);
            wrRepo.save(buk);
        } else {
            System.out.println("El autor seleccionado ya se encuenstra dado de baja.");
        }
    }

    @Transactional
    public void altaAutor(String nombre) throws ErrorServicio {
        Autor buk = null;
        Optional <Autor> rta = wrRepo.buscaAutorNom(nombre);
        if (rta.isPresent()) {
            buk = rta.get();
        } else {
            throw new ErrorServicio("El autor seleccionado no está en la base de datos");
        }
        
        if (buk.getAlta().equals(false)) {
            buk.setAlta(true);
            wrRepo.save(buk);
        } else {
           throw new ErrorServicio("El autor seleccionado ya se encuenstra dado de baja.");
        }
    }
    
    
    public Autor consultaAutor(String nombre) throws ErrorServicio {
        Autor buk = null;
        Optional <Autor> rta = wrRepo.buscaAutorNom(nombre);
        if (rta.isPresent()) {
            buk = rta.get();
        } else {
            throw new ErrorServicio("El nombre seleccionado no pertenece a un autor listado en la base de datos");
        }
        return buk;
    }
    
    @Transactional(readOnly = true)
    public List<Autor> listarAutor() {
        List<Autor>wrs = wrRepo.listarAutor();
        return wrs;
    }
}
