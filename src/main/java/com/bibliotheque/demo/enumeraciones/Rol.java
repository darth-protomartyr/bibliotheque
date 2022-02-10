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
    ADMIN(1), EDITOR(2), USUARIO(3);
    int id;
    private Rol(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    
}
