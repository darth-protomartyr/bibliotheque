/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.entidades;


import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import org.hibernate.annotations.GenericGenerator;
import javax.persistence.OneToOne;
/**
 *
 * @author Gonzalo
 */
@Entity
public class Admin {
   @Id
   @GeneratedValue(generator = "uuid")
   @GenericGenerator(name = "uuid", strategy = "uuid2")
   private String id;
   private String nombre;
   private String pass;
   private String mail;
   @OneToOne
   private Genero sexo;
   private Boolean alta;
   @OneToOne
   private Foto foto;

   public Admin() {
   
   }

    public Admin(String id, String nombre, String pass, Boolean alta, Genero sexo, String mail, Foto foto) {
        this.id = id;
        this.nombre = nombre;
        this.pass = pass;
        this.alta = alta;
        this.sexo = sexo;
        this.mail = mail;
        this.foto = foto;
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
    
    public Genero getSexo() {
        return sexo;
    }

    public void setSexo(Genero sexo) {
        this.sexo = sexo;
    }

    @Override
    public String toString() {
        return "Admin:\n"
                + "     Id: " + id + "\n"
                + "     Nombre: " + nombre + "\n"
                + "     Sexo: " + sexo + "\n"
                + "     Password: " + pass + "\n"
                + "     Mail: " + mail + "\n"
                + "     Alta: " + alta;
    }

    /**
     * @return the retrato
     */
    public Foto getFoto() {
        return foto;
    }

    /**
     * @param retrato the retrato to set
     */
    public void setFoto(Foto retrato) {
        this.foto = retrato;
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
