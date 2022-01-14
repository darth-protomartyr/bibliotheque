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
    
    @Query("SELECT e FROM Autor e WHERE e.nombre = :nombre AND e.alta = true")
    public Optional<Autor> buscaAutorNom (@Param("nombre") String nombre);
    
    @Query("SELECT e FROM Autor e WHERE e.nombre = :nombre")
    public Optional<Autor> buscaAutorNomCompl (@Param("nombre") String nombre);
    
    @Query("SELECT e FROM Autor e WHERE e.id = :id AND e.alta = true")
    public Optional<Autor> buscaAutorId (@Param("id") String id);
    
    @Query("SELECT e FROM Autor e WHERE e.id = :id")
    public Optional<Autor> buscaAutorIdCompl (@Param("id") String id);
    
    @Query("SELECT e FROM Autor e WHERE e.alta = true")
    public List<Autor> listarAutorActiva();
    
    @Query("SELECT e FROM Autor e")
    public List<Autor> listarAutorCompleta();
}