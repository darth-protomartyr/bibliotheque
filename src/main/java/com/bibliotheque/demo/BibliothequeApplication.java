package com.bibliotheque.demo;

import com.bibliotheque.demo.servicios.AdminServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class BibliothequeApplication {
    @Autowired
    private AdminServicio adminServ;

	public static void main(String[] args) {
		SpringApplication.run(BibliothequeApplication.class, args);
	}
        
        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            auth
            .userDetailsService(adminServ)
            .passwordEncoder (new BCryptPasswordEncoder());        
        }

}
