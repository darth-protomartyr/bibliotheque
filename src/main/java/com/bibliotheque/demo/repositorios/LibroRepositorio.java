/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.repositorios;


import com.bibliotheque.demo.entidades.Libro;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LibroRepositorio extends JpaRepository<Libro, String> {

    @Query("SELECT l FROM Libro l WHERE l.titulo = :titulo AND l.alta = true")
    public Optional<Libro> buscaLibroNom (@Param("titulo") String titulo);
    
    @Query("SELECT l FROM Libro l WHERE l.isbn = :isbn AND l.alta = true")
    public Optional<Libro> buscaLibroISBN (@Param("isbn") Long isbn);

    @Query("SELECT l FROM Libro l WHERE l.alta = true")
    public List<Libro> listarLibros();
    
    @Query("SELECT l FROM Libro l WHERE l.autor = :autor AND l.alta = true")
    public Optional<List<Libro>> listarLibrosAutor(@Param("autor") String autor);
    
    @Query("SELECT l FROM Libro l WHERE l.editorial = :editorial AND l.alta = true")
    public Optional<List<Libro>> listarLibrosEditorial(@Param("editorial") String editorial);
}
