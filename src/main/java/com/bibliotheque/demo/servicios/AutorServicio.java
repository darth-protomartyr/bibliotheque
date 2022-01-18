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
    AutorRepositorio autorRepo;
    @Autowired
    FotoServicio picServ;
    
    @Transactional
    public Autor crearAutor(String nombre, MultipartFile archivo) throws ErrorServicio {
        if (nombre == null || nombre.isEmpty()) {
            throw new ErrorServicio("Falta el nombre del autor");
        }
        
        if (archivo == null || archivo.isEmpty()) {
            throw new ErrorServicio("Falta la imagen del autor");
        }
        
        Autor buk = new Autor();
        
        Optional<Autor> rta = autorRepo.buscaAutorNomCompl(nombre);
        if (rta.isPresent()) {
            throw new ErrorServicio("El autor ya se encuentra registrado en la base de datos");
        }
        
        buk.setNombre(nombre);
        buk.setAlta(true);
        Foto foto = picServ.guardar(archivo);
        buk.setFoto(foto);
        return autorRepo.save(buk);
    }
    
    @Transactional
    public void modificarAutor(String nombre, MultipartFile archivo, String id) throws ErrorServicio {
        Autor buk = null;
        Optional <Autor> rta = autorRepo.buscaAutorId(id);
        
        if (rta.isPresent()) {
            buk = rta.get();
        } else {
            throw new ErrorServicio("El autor seleccionado no está en la base de datos");
        }
        
        Optional<Autor> rta1 = autorRepo.buscaAutorNomCompl(nombre);
        if (rta1.isPresent() && !nombre.equals(buk.getNombre())) {
            throw new ErrorServicio("El autor ya se encuentra registrado en la base de datos");
        }
        
        if (nombre == null) {
            buk.setNombre(buk.getNombre());
        } else {
            buk.setNombre(nombre);
        }
        
        if (archivo == null) {
            buk.setFoto(buk.getFoto());
        }
        
        String idFoto = null;
        if (buk.getFoto() != null){
            idFoto = buk.getFoto().getId();
        }

        Foto foto = picServ.actualizar(idFoto, archivo);
        buk.setFoto(foto);
        autorRepo.save(buk);
    }

    @Transactional
    public void bajaAutor(String id) throws ErrorServicio{
        Autor buk = null;
        Optional <Autor> rta = autorRepo.buscaAutorIdCompl(id);
        if (rta.isPresent()) {
            buk = rta.get();
        } else {
            throw new ErrorServicio("El autor seleccionado no está en la base de datos");
        }
        
        if (buk.getAlta().equals(true)) {
            buk.setAlta(false);
            autorRepo.save(buk);
        } else {
            System.out.println("El autor seleccionado ya se encuenstra dado de baja.");
        }
    }

    @Transactional
    public void altaAutor(String id) throws ErrorServicio {
        Autor buk = null;
        Optional <Autor> rta = autorRepo.buscaAutorIdCompl(id);
        if (rta.isPresent()) {
            buk = rta.get();
        } else {
            throw new ErrorServicio("El autor seleccionado no está en la base de datos");
        }
        
        if (buk.getAlta().equals(false)) {
            buk.setAlta(true);
            autorRepo.save(buk);
        } else {
           throw new ErrorServicio("El autor seleccionado ya se encuenstra dado de baja.");
        }
    }
    
    
    
    
    
    
    @Transactional(readOnly = true)
    public Autor consultaAutorId(String id) throws ErrorServicio {
        Autor buk = null;
        Optional <Autor> rta = autorRepo.buscaAutorId(id);
        if (rta.isPresent()) {
            buk = rta.get();
        } else {
            throw new ErrorServicio("El Autor consultado no pertenece a una Autor listado en la base de datos");
        }
        return buk;
    }
    
    @Transactional(readOnly = true)
    public Autor consultaAutorIdCompl(String id) throws ErrorServicio {
        Autor buk = null;
        Optional <Autor> rta = autorRepo.buscaAutorIdCompl(id);
        if (rta.isPresent()) {
            buk = rta.get();
        } else {
            throw new ErrorServicio("El Autor consultado no pertenece a una Autor listado en la base de datos");
        }
        return buk;
    }
    
    
    @Transactional(readOnly = true)
    public Autor consultaAutorNom(String nombre) throws ErrorServicio {
        Autor buk = null;
        Optional <Autor> rta = autorRepo.buscaAutorNom(nombre);
        if (rta.isPresent()) {
            buk = rta.get();
        } else {
            throw new ErrorServicio("El nombre seleccionado no pertenece a un Autor listado en la base de datos");
        }
        return buk;
    }
    
    @Transactional(readOnly = true)
    public Autor consultaAutorNomCompl(String nombre) throws ErrorServicio {
        Autor buk = null;
        Optional <Autor> rta = autorRepo.buscaAutorNomCompl(nombre);
        if (rta.isPresent()) {
            buk = rta.get();
        } else {
            throw new ErrorServicio("El nombre seleccionado no pertenece a un Autor listado en la base de datos");
        }
        return buk;
    }
    
    
    @Transactional(readOnly = true)
    public List<Autor> listarAutoresActivas() {
        List<Autor>wrs = autorRepo.listarAutorActiva();
        return wrs;
    }
    
    @Transactional(readOnly = true)
    public List<Autor> listarAutoresCompletas() {
        List<Autor>wrs = autorRepo.listarAutorCompleta();
        return wrs;
    }
}
