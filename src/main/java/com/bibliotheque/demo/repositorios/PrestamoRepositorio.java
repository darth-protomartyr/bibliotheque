/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.repositorios;


import com.bibliotheque.demo.entidades.Prestamo;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PrestamoRepositorio extends JpaRepository<Prestamo, String> {
    
    @Query("SELECT p FROM Prestamo p WHERE p.admin.nombre = :nombre AND p.alta = true")
    public Optional<List<Prestamo>> buscaPrestamoNom (@Param("nombre") String nombre);
    
    @Query("SELECT p FROM Prestamo p WHERE p.libro.titulo = :titulo AND p.alta = true")
    public Optional<List<Prestamo>> buscaPrestamoBook (@Param("titulo") String titulo);
    
    @Query("SELECT s FROM Prestamo s WHERE s.alta = true")
    public List<Prestamo> listarPrestamo(); 
}
