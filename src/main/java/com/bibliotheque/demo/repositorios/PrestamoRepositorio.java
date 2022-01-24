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
    
    @Query("SELECT p FROM Prestamo p WHERE p.id = :prestamoId AND p.alta = true")
    public Optional<Prestamo> buscaPrestamoId (@Param("prestamoId") String prestamoId);

    @Query("SELECT p FROM Prestamo p WHERE p.admin.id = :adminId AND p.alta = true")
    public Optional<List<Prestamo>> buscaPrestamoAdminID (@Param("adminId") String adminId);

    @Query("SELECT p FROM Prestamo p WHERE p.libro.titulo = :titulo AND p.alta = true")
    public Optional<List<Prestamo>> buscaPrestamoLibro (@Param("titulo") String titulo);

    @Query("SELECT s FROM Prestamo s WHERE s.alta = true")
    public Optional <List<Prestamo>> listarPrestamo();
    
    @Query("SELECT s FROM Prestamo s WHERE s.alta = false AND s.fechaAlta is null")
    public Optional <List<Prestamo>> listarPrestamoSolicitados();
}