/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.servicios;


import com.bibliotheque.demo.entidades.Foto;
import com.bibliotheque.demo.excepciones.ErrorServicio;
import com.bibliotheque.demo.repositorios.FotoRepositorio;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Gonzalo
 */
@Service
public class FotoServicio {
    @Autowired
    private FotoRepositorio picRepo;

    @Transactional
    public Foto guardar(MultipartFile archivo) throws ErrorServicio {
        if (archivo != null && !archivo.isEmpty()) {
            try {
            Foto foto = new Foto();
            foto.setMime(archivo.getContentType());
            foto.setNombre(archivo.getName());
            foto.setContenido(archivo.getBytes());
            return picRepo.save(foto);
            } catch(Exception e) {
                System.err.println(e.getMessage());
            }        
        }
        return null;
    }
    
    @Transactional
    public Foto actualizar(String idFoto, MultipartFile archivo) throws ErrorServicio{
        if (archivo != null) {
            try {
                Foto foto = new Foto();
                if (idFoto != null) {
                    Optional<Foto> rta = picRepo.findById(idFoto);
                    if(rta.isPresent()) {
                        foto = rta.get();
                    }
                }
                    foto.setMime(archivo.getContentType());
                    foto.setNombre(archivo.getName());
                    foto.setContenido(archivo.getBytes());
                    return picRepo.save(foto);
            } catch(Exception e) {
                System.out.println(e.getMessage());
            }
        }
        return null;
    } 
}  