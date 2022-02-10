/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.entidades;


import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author Gonzalo
 */
@Entity
public class Prestamo {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    @OneToOne
    private Libro libro;
    @OneToOne
    private Usuario usuario;
    //Se crea al generar la solicitud de prestamo
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaSolicitud;
    //Se crea cuando se habilita el préstamo
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaAlta;
    //Se crea cuando se habilita el préstamo y corresponde a 7 días luego de la habilitación
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaDevolucion;
    //Se genera cuando los libros son devueltos
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaBaja;
    private Boolean alta;
    //Convierte en bidireccional la relación con orden
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="orden_id", nullable=true)
    private Orden orden;

    public Prestamo() {
    }

    public Prestamo(String id, Libro libro, Usuario usuario, Date fechaAlta, Date fechaDevolucion, Date fechaBaja, Date fechaSolicitud, Boolean alta, Orden orden) {
        this.id = id;
        this.libro = libro;
        this.usuario = usuario;
        this.fechaSolicitud = fechaSolicitud;
        this.fechaAlta = fechaAlta;
        this.fechaDevolucion = fechaDevolucion;
        this.fechaBaja = fechaBaja;
        this.alta = alta;
        this.orden = orden;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Orden getOrden() {
        return orden;
    }

    public void setOrden(Orden orden) {
        this.orden = orden;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    public Date getFechaSolicitud() {
        return fechaSolicitud;
    }

    public void setFechaSolicitud(Date fechaSolicitud) {
        this.fechaSolicitud = fechaSolicitud;
    }

    public Date getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }
    
    
    public Date getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(Date fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }
    

    public Date getFechaBaja() {
        return fechaBaja;
    }

    public void setFechaBaja(Date fechaBaja) {
        this.fechaBaja = fechaBaja;
    }
    
    

    public Boolean getAlta() {
        return alta;
    }

    public void setAlta(Boolean alta) {
        this.alta = alta;
    }

    @Override
    public String toString() {
        return "Préstamo:"
                + "     Id de la Préstamo: " + id + "\n"
                + "     Libro: " + libro + "\n"
                + "     Usuario:" + usuario + "\n"
                + "     Fecha de Entraga: " + fechaAlta + "\n"
                + "     Fecha de devolución" + fechaBaja + "\n"
                + "     Alta: " + alta;
    }
}
