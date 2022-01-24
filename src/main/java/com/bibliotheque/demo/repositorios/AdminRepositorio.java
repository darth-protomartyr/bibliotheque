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
    //Busca admin de alta por nombre
    @Query("SELECT s FROM Admin s WHERE s.nombre = :nombre AND s.alta = true")
    public Optional<Admin> buscaAdminNom (@Param("nombre") String nombre);
    
    //Busca admin de alta por id
    @Query("SELECT s FROM Admin s WHERE s.id = :adminId AND s.alta = true")
    public Optional<Admin> buscaAdminIdAlta (@Param("adminId") String adminId);
    
    //Busca admin de alta por id que esté penalizado
    @Query("SELECT s FROM Admin s WHERE s.id = :adminId AND s.alta = true AND s.penalidad = true")
    public Optional<Admin> buscaAdminIdAltaPenAlta (@Param("adminId") String adminId);
    
    //Lista admins de alta que estén penalizados
    @Query("SELECT s FROM Admin s WHERE s.alta = true AND s.penalidad = true")
    public List <Optional<Admin>> ListarAdminIdAltaPenAlta ();
    
//    @Query("SELECT s FROM Admin s WHERE s.pass = :pass AND s.alta = true")
//    public Admin buscaAdminPass (@Param("pass") String nombre);
    
    //busca admins por mail
    @Query("SELECT s FROM Admin s WHERE s.mail = :mail AND s.alta = true")
    public Optional<Admin> buscaAdminMail(String mail);
    
    //Lista admins de alta
    @Query("SELECT s FROM Admin s WHERE s.alta = true")
    public List<Admin> listarAdmins();  
}