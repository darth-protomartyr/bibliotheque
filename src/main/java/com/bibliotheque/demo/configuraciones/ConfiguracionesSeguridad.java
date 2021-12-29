/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bibliotheque.demo.configuraciones;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
@Order(1)
public class ConfiguracionesSeguridad extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
        .authorizeRequests()
        .antMatchers("/css/*", "/js/*","/img/*", "/**").permitAll()
        .and().formLogin()
        .loginPage("/login") // Que formulario esta mi login
        .loginProcessingUrl("/logincheck")
        .usernameParameter("username") // Como viajan los datos del logueo
        .passwordParameter("password")// Como viajan los datos del logueo
        .defaultSuccessUrl("/inicio") // A que URL viaja
        .permitAll()
        .and().logout() // Aca configuro la salida
        .logoutUrl("/logout")
        .logoutSuccessUrl("/login?logout")   

        .permitAll().and().csrf().disable();
    }
}