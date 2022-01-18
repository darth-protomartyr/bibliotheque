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
public enum Genero {
    HOMBRE("Hombre", 1), MUJER("Mujer", 2), OTRO("Otro", 3);
    String gen;
    int id;
    private Genero(String gen, int id) {
        this.gen = gen;
        this.id = id;
    }

    public String getGen() {
        return gen;
    }
    
    public int getId() {
        return id;
    }
}
