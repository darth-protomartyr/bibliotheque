/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.repositorios;
import com.bibliotheque.demo.entidades.Socio;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SocioRepositorio extends JpaRepository<Socio, String> {

    @Query("SELECT s FROM Socio s WHERE s.nombre = :nombre AND s.alta = true")
    public Optional<Socio> buscaSocioNom (@Param("nombre") String nombre);
    
    @Query("SELECT s FROM Socio s WHERE s.pass = :pass AND s.alta = true")
    public Socio buscaSocioPass (@Param("pass") String nombre);
    
    @Query("SELECT s FROM Socio s WHERE s.mail = :mail AND s.alta = true")
    public Optional<Socio> buscaSocioMail(String mail);
    
    @Query("SELECT s FROM Socio s WHERE s.alta = true")
    public List<Socio> listarSocios();  
}