/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.entidades;


import com.bibliotheque.demo.enumeraciones.Genero;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
/**
 *
 * @author Gonzalo
 */
@Entity
public class Usuario {
   @Id
   @GeneratedValue(generator = "uuid")
   @GenericGenerator(name = "uuid", strategy = "uuid2")
   private String id;
   private String nombre;
   private String pass;
   @Column(unique = true)
   private String mail;
   @Enumerated(EnumType.ORDINAL)
   private Genero genero;
   private Boolean alta;
   @Temporal(TemporalType.TIMESTAMP)
    private Date fechaPenalidad;
   private Boolean penalidad;
   @OneToOne
   private Foto foto;

   public Usuario() {
   
   }

    public Usuario(String id, String nombre, String pass, Boolean alta, Genero genero, String mail, Date fechaPenalidad, Boolean penalidad, Foto foto) {
        this.id = id;
        this.nombre = nombre;
        this.pass = pass;
        this.alta = alta;
        this.genero = genero;
        this.mail = mail;
        this.fechaPenalidad = fechaPenalidad;
        this.penalidad = penalidad;
        this.foto = foto;
    }

    public Date getFechaPenalidad() {
        return fechaPenalidad;
    }

    public void setFechaPenalidad(Date fechaPenalidad) {
        this.fechaPenalidad = fechaPenalidad;
    }

    public Boolean getPenalidad() {
        return penalidad;
    }

    public void setPenalidad(Boolean penalidad) {
        this.penalidad = penalidad;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getPass() {
        return pass;
    }
    
    public void setPass(String pass) {
        this.pass = pass;
    }

    public Boolean getAlta() {
        return alta;
    }

    public void setAlta(Boolean alta) {
        this.alta = alta;
    }
    
    public Genero getGenero() {
        return genero;
    }

    public void setGenero(Genero genero) {
        this.genero = genero;
    }

    @Override
    public String toString() {
        return "Usuario:\n"
                + "     Id: " + id + "\n"
                + "     Nombre: " + nombre + "\n"
                + "     Sexo: " + genero + "\n"
                + "     Password: " + pass + "\n"
                + "     Mail: " + mail + "\n"
                + "     Alta: " + alta;
    }

    /**
     * @return the foto
     */
    public Foto getFoto() {
        return foto;
    }

    /**
     * @param foto the foto to set
     */
    public void setFoto(Foto foto) {
        this.foto = foto;
    }

    /**
     * @return the mail
     */
    public String getMail() {
        return mail;
    }

    /**
     * @param mail the mail to set
     */
    public void setMail(String mail) {
        this.mail = mail;
    }
}
