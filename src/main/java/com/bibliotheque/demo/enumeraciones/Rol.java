/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.enumeraciones;

/**
 *
 * @author Gonzalo
 */
public enum Rol {
    ADMIN("Admin", 1), EDITOR("Editor", 2), USUARIO("Usuario", 3);
    String cargo;
    int id;
    private Rol(String cargo, int id) {
        this.cargo = cargo; 
        this.id = id;
    }

    public String getCargo() {
        return cargo;
    }

    public int getId() {
        return id;
    }
    
}
