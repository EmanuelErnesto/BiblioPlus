package com.emanuel.BiblioPlus.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@EnableWebSecurity
@Configuration
public class SpringSecurityConfig {

    @Autowired
    private SpringSecurityFilter securityFilter;

    private static final String[] DOCUMENTATION_OPENAPI = {
            "/docs/index.html",
            "/biblioplus.html", "/biblioplus/**",
            "/v3/api-docs/**",
            "/swagger-ui-custom.html", "/swagger-ui.html", "/swagger-ui/**",
            "/**.html", "/webjars/**", "/configuration/**", "/swagger-resources/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                                .requestMatchers(HttpMethod.POST, "/auth/refresh-token").permitAll()
                                .requestMatchers(HttpMethod.POST, "/users").permitAll()
                                .requestMatchers(HttpMethod.GET, "/users/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.PATCH, "/orders/**").hasRole("ADMIN")
                                .requestMatchers( "/books/**").hasRole("ADMIN")
                                .requestMatchers(HttpMethod.GET, "/books").authenticated()
                                .requestMatchers(HttpMethod.GET,"/ping").permitAll()
                                .requestMatchers(DOCUMENTATION_OPENAPI).permitAll()
                                .anyRequest().authenticated()
                        )
                .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
