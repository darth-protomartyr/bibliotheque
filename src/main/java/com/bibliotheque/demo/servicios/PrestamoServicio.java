package com.bibliotheque.demo.servicios;


import com.bibliotheque.demo.entidades.Libro;
import com.bibliotheque.demo.entidades.Prestamo;
import com.bibliotheque.demo.entidades.Admin;
import com.bibliotheque.demo.excepciones.ErrorServicio;
import com.bibliotheque.demo.repositorios.LibroRepositorio;
import com.bibliotheque.demo.repositorios.AdminRepositorio;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.bibliotheque.demo.repositorios.PrestamoRepositorio;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Gonzalo
 */
@Service
public class PrestamoServicio {
    @Autowired
    private PrestamoRepositorio prestamoRepo;
    @Autowired
    private LibroRepositorio libroRepo;
    @Autowired
    private LibroServicio libroServ;
    @Autowired
    private AdminRepositorio adminRepo;
    
    @Transactional
    public Prestamo crearPrestamo(String adminId, String libroId) throws ErrorServicio {
        
        if (adminId == null || adminId.isEmpty()) {
            throw new ErrorServicio("Falta ingresar el número de socio");
        }
        
        if (libroId == null || libroId.isEmpty()) {
            throw new ErrorServicio("Falta ingresar el libro que desea pedir");
        }
        
        int limite = limitePrestamo(adminId);
        if (limite >= 5) {
            throw new ErrorServicio("Usted ha excedido el límite de préstamos permitidos.");
        }
        
        Admin adminPrestamo = null;
        Optional<Admin> rta1 = adminRepo.buscaAdminIdAlta(adminId);
        if(rta1.isPresent()) {
            adminPrestamo = rta1.get();
        } else {
            throw new ErrorServicio("No hay un socio registrado con ese nombre.");
        }
        
        if (adminPrestamo.getPenalidad() == true) {
            throw new ErrorServicio("Usted se encuentra penalizado para el préstamo de Libros");
        }
        
        Libro libroPrestamo = null; 
        Optional<Libro> rta = libroRepo.buscaLibroId(libroId);
        if(rta.isPresent()) {
            libroPrestamo = rta.get();
        } else {
            throw new ErrorServicio("No hay un libro registrado con ese Título.");
        }
        
        
        
        Prestamo prestamo = new Prestamo();
        
        prestamo.setAlta(Boolean.FALSE);
        
        if (libroPrestamo.getEjemplaresRestantes() > 0) {
            prestamo.setLibro(libroPrestamo);
            libroPrestamo.setEjemplaresRestantes(libroPrestamo.getEjemplaresRestantes() - 1);
            libroPrestamo.setEjemplaresPrestados(libroPrestamo.getEjemplaresPrestados() + 1);
            
        } else {
            throw new ErrorServicio("No hay ejemplares disponibles para préstamo");
        }
        prestamo.setAdmin(adminPrestamo);
        prestamo.setFechaSolicitud(new Date());
        libroServ.modEjemplaresRet(libroPrestamo);
        return prestamoRepo.save(prestamo);
    }
    
    
    
    @Transactional
    public void altaPrestamo(String idPrestamo) throws ErrorServicio, ParseException {
        Optional <Prestamo> rta = prestamoRepo.buscaPrestamoId(idPrestamo);
        Prestamo prestamo = null;
        if (rta.isPresent()) {
            prestamo = rta.get();
        }
        Admin admin = prestamo.getAdmin();
        Libro libro = prestamo.getLibro();
        
        prestamo.setAlta(true);
        prestamo.setFechaAlta(new Date());
        prestamo.setFechaDevolucion(generarFechaDevolucion(prestamo.getFechaAlta()));
        
        libro.setEjemplaresPrestados(libro.getEjemplaresPrestados() - 1);
        libro.setEjemplaresRestantes(libro.getEjemplaresRestantes() + 1);
    }
    
    
    
    @Transactional
    public void bajaPrestamo(String idPrestamo) throws ErrorServicio, ParseException {
        Optional <Prestamo> rta = prestamoRepo.buscaPrestamoId(idPrestamo);
        Prestamo prestamo = null;
        if (rta.isPresent()) {
            prestamo = rta.get();
        }
        Admin admin = prestamo.getAdmin();
        Libro libro = prestamo.getLibro();
        
        prestamo.setAlta(false);
        prestamo.setFechaBaja(new Date());
        
        Date dateBaja = prestamo.getFechaBaja();
        Date dateVenc = prestamo.getFechaDevolucion();
        Date datePen = admin.getFechaPenalidad();
        if (dateVenc.before(dateBaja)) {
            admin.setPenalidad(Boolean.TRUE);
            admin.setFechaPenalidad(diasPenalidad(dateBaja, dateVenc, datePen));
        }
        
        libro.setEjemplaresPrestados(libro.getEjemplaresPrestados() - 1);
        libro.setEjemplaresRestantes(libro.getEjemplaresRestantes() + 1);
    }
    
    

    
//    @Transactional(readOnly = true)
//    public List<Prestamo> listarPrestamos(){
//        List <Prestamo> prestamos = null;
//        Optional <List<Prestamo>> rta = prestamoRepo.listarPrestamo();
//        prestamos = rta.get();
//        return prestamos;
//    }

    static LocalDate convertToLocalDateViaMilisecond(Date dateToConvert) {
        return Instant.ofEpochMilli(dateToConvert.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }
    
    
    public List <Admin> listarSolicitantes() {
        HashSet<Admin> solicitantesHS = null;
        List<Admin> solicitantes = null;
        List <Prestamo> prestamos = null;
        Optional <List<Prestamo>> rta = prestamoRepo.listarPrestamoSolicitados();
        
        if (rta.isPresent()) {
            prestamos = rta.get();
        }

        for (Prestamo prestamo : prestamos) {
            solicitantesHS.add(prestamo.getAdmin());
        }
        
        for (Admin admin : solicitantes) {
            solicitantes.add(admin);
        }
        
        return solicitantes;
    }
    
    
    private int limitePrestamo(String adminId) {
        int limite = 0;
        List <Prestamo> prestamos = null;
        Optional <List <Prestamo>> rta = prestamoRepo.buscaPrestamoSolicitAdminID(adminId);
        if (rta.isPresent()) {
            prestamos = rta.get();
        }
        limite = prestamos.size();
        return limite;
    }
    
    
    static Date generarFechaDevolucion(Date dateAlta) throws ParseException {
        int horas = 176;
        Calendar calendar = Calendar.getInstance();	
        calendar.setTime(dateAlta); // Configuramos la fecha que se recibe
        calendar.add(Calendar.HOUR, horas);  // numero de horas a añadir, o restar en caso de horas<0
        return calendar.getTime(); // Devuelve el objeto Date con las nuevas horas añadidas
    }
    
    
    static Date diasPenalidad(Date dateBaja, Date dateVenc, Date datePen) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	String dBaja = dateFormat.format(dateBaja);
       	String dVenc = dateFormat.format(dateVenc);
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
        Date baja = sdf.parse(dBaja);
        Date venc = sdf.parse(dVenc);

        long diffInMillies = Math.abs(baja.getTime() - venc.getTime());
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        
        int days = (int) diff;
        
        Calendar calendar = Calendar.getInstance();
        if (datePen == null) {
            calendar.setTime(dateBaja);
            calendar.add(Calendar.DAY_OF_YEAR, days);  
            return calendar.getTime();
        } else {
            calendar.setTime(datePen);
            calendar.add(Calendar.DAY_OF_YEAR, days); 
            return calendar.getTime();
        }
    }    
}