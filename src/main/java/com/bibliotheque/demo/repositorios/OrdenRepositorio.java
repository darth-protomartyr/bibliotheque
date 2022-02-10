/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.repositorios;

import com.bibliotheque.demo.entidades.Orden;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdenRepositorio extends JpaRepository<Orden, String> {
    @Query("SELECT o FROM Orden o WHERE o.id = :ordenId AND o.alta = true")
    public Optional <Orden> buscaOrdenIdAlta (@Param("ordenId") String ordenId);

    @Query("SELECT o FROM Orden o WHERE o.alta = true")
    public Optional<List<Orden>> listarActivas();

    @Query("SELECT o FROM Orden o WHERE o.prestamos is empty")
    public Optional<List<Orden>> listaOrdenListEmpty();
}