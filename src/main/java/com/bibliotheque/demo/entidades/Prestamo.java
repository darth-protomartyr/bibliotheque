/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.entidades;


import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
    private Admin admin;
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaSolicitud;
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaAlta;
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaDevolucion;
    @Temporal(TemporalType.TIMESTAMP)
    private Date fechaBaja;
    private Boolean alta;

    public Prestamo() {
    }

    public Prestamo(String id, Libro libro, Admin admin, Date fechaAlta, Date fechaDevolucion, Date fechaBaja, Date fechaSolicitud, Boolean alta) {
        this.id = id;
        this.libro = libro;
        this.admin = admin;
        this.fechaSolicitud = fechaSolicitud;
        this.fechaAlta = fechaAlta;
        this.fechaDevolucion = fechaDevolucion;
        this.fechaBaja = fechaBaja;
        this.alta = alta;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Libro getLibro() {
        return libro;
    }

    public void setLibro(Libro libro) {
        this.libro = libro;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
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
        return fechaAlta;
    }

    public void setFechaDevolucion(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
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
                + "     Admin:" + admin + "\n"
                + "     Fecha de Entraga: " + fechaAlta + "\n"
                + "     Fecha de devolución" + fechaBaja + "\n"
                + "     Alta: " + alta;
    }
}
