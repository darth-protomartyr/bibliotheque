/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.repositorios;


import com.bibliotheque.demo.entidades.Genero;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
//import org.springframework.data.jpa.repository.Query;
//import java.util.List;

/**
 *
 * @author Gonzalo
 */
@Repository
public interface GeneroRepositorio extends JpaRepository<Genero, String> {
    
    @Query("SELECT g FROM Genero g WHERE g.id = :id")
    public Genero buscaGenId (@Param("id") byte id);

//    @Query("SELECT s FROM sexo s")
//    public List<Genero> listarSexo();
}