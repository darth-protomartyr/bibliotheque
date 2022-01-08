/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.controladores;

import com.bibliotheque.demo.entidades.Admin;
import com.bibliotheque.demo.excepciones.ErrorServicio;
import com.bibliotheque.demo.servicios.AdminServicio;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/foto")
public class FotoControlador {
   
    @Autowired
    private AdminServicio adminServ;

    @GetMapping("/perfil/{id}")
    public ResponseEntity<byte[]> fotoPartner(@PathVariable String id) {
        try {
            Admin admin = adminServ.buscarPorId(id);
            if (admin.getFoto() == null) {
                throw new ErrorServicio("El usuario no tiene una foto asignada.");
            }
            byte[] foto = admin.getFoto().getContenido();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);

            return new ResponseEntity<>(foto, headers, HttpStatus.OK);
        } catch (ErrorServicio ex) {
            Logger.getLogger(FotoControlador.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    } 
}





