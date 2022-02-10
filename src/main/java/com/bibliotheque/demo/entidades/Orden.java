/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.entidades;


import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import org.hibernate.annotations.GenericGenerator;

/**
 *
 * @author Gonzalo
 */
@Entity
public class Orden {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;

//No se crea tabla intermedia por lo que anotaciones comentadas innecesarias.
    @OneToMany (mappedBy="orden")
//    @OneToMany
//    @JoinTable(name="orden_prestamos" //da nombre a la tabla
//        ,joinColumns=@JoinColumn(name="orden_id") //da nombre a la columna que incluye el id del objeto de origen
//        ,inverseJoinColumns=@JoinColumn(name="prestamo_id")) //danombre a la columna que incluye el id del objeto al que se dirige
    private List<Prestamo> prestamos;
    @ManyToOne
    private Usuario usuario;
    private boolean alta;

    public Orden() {
    }

    public Orden(String id, List<Prestamo> prestamos, Usuario usuario, boolean alta) {
        this.id = id;
        this.prestamos = prestamos;
        this.usuario = usuario;
        this.alta = alta;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Prestamo> getPrestamos() {
        return prestamos;
    }

    public void setPrestamos(List<Prestamo> prestamos) {
        this.prestamos = prestamos;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public boolean getAlta() {
        return alta;
    }

    public void setAlta(boolean alta) {
        this.alta = alta;
    }
    
    
    //Modifican la List que contiene los préstamos
    public void agregarPrestamo(Prestamo prestamo) {
        prestamos.add(prestamo);
        prestamo.setOrden(this);
    }
    
     public void quitarPrestamo(Prestamo prestamo) {
        prestamos.remove(prestamo);
//        prestamo.setOrden(this);
        
    }
    
}
