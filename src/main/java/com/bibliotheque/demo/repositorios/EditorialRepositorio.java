/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.repositorios;


import com.bibliotheque.demo.entidades.Editorial;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EditorialRepositorio extends JpaRepository<Editorial, String> {
   
    @Query("SELECT e FROM Editorial e WHERE e.nombre = :nombre AND e.alta = true")
    public Optional<Editorial> buscaEditorialNom (@Param("nombre") String nombre);
    
    @Query("SELECT e FROM Editorial e WHERE e.nombre = :nombre")
    public Optional<Editorial> buscaEditorialNomCompl (@Param("nombre") String nombre);
    
    @Query("SELECT e FROM Editorial e WHERE e.id = :id AND e.alta = true")
    public Optional<Editorial> buscaEditorialId (@Param("id") String id);
    
    @Query("SELECT e FROM Editorial e WHERE e.id = :id")
    public Optional<Editorial> buscaEditorialIdCompl (@Param("id") String id);
    
    @Query("SELECT e FROM Editorial e WHERE e.alta = true")
    public List<Editorial> listarEditorialActiva();
    
    @Query("SELECT e FROM Editorial e")
    public List<Editorial> listarEditorialCompleta();
    
}