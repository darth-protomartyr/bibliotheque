/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.repositorios;
import com.bibliotheque.demo.entidades.Admin;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepositorio extends JpaRepository<Admin, String> {

    @Query("SELECT s FROM Admin s WHERE s.nombre = :nombre AND s.alta = true")
    public Optional<Admin> buscaAdminNom (@Param("nombre") String nombre);
    
    @Query("SELECT s FROM Admin s WHERE s.pass = :pass AND s.alta = true")
    public Admin buscaAdminPass (@Param("pass") String nombre);
    
    @Query("SELECT s FROM Admin s WHERE s.mail = :mail AND s.alta = true")
    public Optional<Admin> buscaAdminMail(String mail);
    
    @Query("SELECT s FROM Admin s WHERE s.alta = true")
    public List<Admin> listarAdmins();  
}