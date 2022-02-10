/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.servicios;



import com.bibliotheque.demo.entidades.Usuario;
import com.bibliotheque.demo.entidades.Orden;
import com.bibliotheque.demo.entidades.Prestamo;
import com.bibliotheque.demo.repositorios.OrdenRepositorio;
import com.bibliotheque.demo.repositorios.PrestamoRepositorio;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.Date;


@Service
public class OrdenServicio {
    
    @Autowired
    private OrdenRepositorio ordenRepo;
    @Autowired
    private PrestamoRepositorio prestamoRepo;
    
//Inicializa la orden pero no completa la lista de préstamos    
    @Transactional
    public Orden iniciarOrden(Usuario usuario) {
        Orden orden = new Orden();
        orden.setUsuario(usuario);
        orden.setAlta(true);
        return ordenRepo.save(orden);
    }


//Limpia eventuales oórdenes que se generen y luego no sean completadas con ningún préstamo
    public void limpiezaOrden() {
        List <Orden> ordenes = new ArrayList();
        Optional <List<Orden>> rta = ordenRepo.listaOrdenListEmpty();
        if (rta.isPresent()) {
            ordenes = rta.get();
        }
        
        if (ordenes.size()>0) {
            for (Orden orden : ordenes) {
                ordenRepo.delete(orden);
            }
        }
    }

    public void seteaOrden(String ordenId) {
        Orden orden = null;
        Optional <Orden> rta = ordenRepo.buscaOrdenIdAlta(ordenId);
        if (rta.isPresent()) {
            orden = rta.get();
        }
        orden.setAlta(false);
        ordenRepo.save(orden);
    }

    public void seteaFechaPrestamos(String ordenId) {
        Orden orden = null;
        Optional <Orden> rta = ordenRepo.buscaOrdenIdAlta(ordenId);
        if (rta.isPresent()) {
            orden = rta.get();
        }

        List <Prestamo> prestamos = orden.getPrestamos();
        for (Prestamo prestamo : prestamos) {
            Date vencimiento = orden.getFechaDevolucion();
            prestamo.setFechaDevolucion(vencimiento);
            prestamoRepo.save(prestamo);
        }
    }
}
