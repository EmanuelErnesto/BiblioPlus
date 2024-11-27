package com.emanuel.BiblioPlus.config.security;

import com.emanuel.BiblioPlus.modules.users.infra.database.repositories.UserRepository;

import com.emanuel.BiblioPlus.modules.users.services.TokenService;
import com.emanuel.BiblioPlus.shared.consts.AuthenticationExceptionConsts;
import com.emanuel.BiblioPlus.shared.exceptions.ApplicationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SpringSecurityFilter extends OncePerRequestFilter {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
       try {
           String token = readToken(request);

           if(token != null) {
               String subject = tokenService.validateToken(token);
               tokenService.validateAccessToken(token);
               UserDetails user = userRepository.findByEmail(subject);

               var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
               SecurityContextHolder.getContext().setAuthentication(authentication);
           }
           filterChain.doFilter(request, response);
       }  catch (NullPointerException ex) {
           response.setStatus(HttpStatus.NOT_FOUND.value());
           response.setContentType(MediaType.APPLICATION_JSON_VALUE);

           ApplicationException applicationException = new ApplicationException();
           applicationException.setPath(request.getRequestURI());
           applicationException.setMethod(request.getMethod());
           applicationException.setCode(HttpStatus.NOT_FOUND.value());
           applicationException.setStatus(HttpStatus.NOT_FOUND.getReasonPhrase());
           applicationException.setMessage(AuthenticationExceptionConsts.INVALID_USER_CREDENTIALS);
           ObjectMapper objectMapper = new ObjectMapper();
           response.getWriter().write(objectMapper.writeValueAsString(applicationException));
       }
       }

    private String readToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if(authHeader == null) {
            return null;
        }
        return authHeader.replace("Bearer ", "");
    }
}
