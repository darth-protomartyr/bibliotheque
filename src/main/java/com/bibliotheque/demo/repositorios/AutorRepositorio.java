/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.repositorios;


import com.bibliotheque.demo.entidades.Autor;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AutorRepositorio extends JpaRepository<Autor, String>{
    
    @Query("SELECT a FROM Autor a WHERE a.nombre = :nombre AND a.alta = true")
    public Optional<Autor> buscaAutorNom (@Param("nombre") String nombre);
    
    @Query("SELECT s FROM Autor s WHERE s.alta = true")
    public List<Autor> listarAutor();
}