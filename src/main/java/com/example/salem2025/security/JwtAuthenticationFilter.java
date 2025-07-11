package com.example.salem2025.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        String authHeader = request.getHeader("Authorization");
        
        System.out.println("=== JWT Filter Debug ===");
        System.out.println("Request: " + method + " " + requestURI);
        System.out.println("Auth Header: " + authHeader);
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            System.out.println("Token found, length: " + token.length());
            System.out.println("Token preview: " + token.substring(0, Math.min(50, token.length())) + "...");
            
            try {
                if (jwtUtil.validateToken(token)) {
                    String username = jwtUtil.extractUsername(token);
                    String role = jwtUtil.extractRole(token);
                    
                    System.out.println("Token VALID - Username: " + username + ", Role: " + role);

                    if (username != null && role != null) {
                        // Tạo authority từ role (Spring Security yêu cầu prefix ROLE_)
                        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
                        System.out.println("Authority created: " + authority.getAuthority());

                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(
                                        new User(username, "", Collections.singletonList(authority)),
                                        null,
                                        Collections.singletonList(authority));

                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        
                        System.out.println("Authentication set successfully!");
                        System.out.println("Current authorities: " + SecurityContextHolder.getContext().getAuthentication().getAuthorities());
                    } else {
                        System.out.println("Username or role is null");
                    }
                } else {
                    System.out.println("Token validation FAILED");
                }
            } catch (Exception e) {
                System.out.println("Exception in JWT processing: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("No Bearer token found");
        }
        
        System.out.println("Current authentication: " + SecurityContextHolder.getContext().getAuthentication());
        System.out.println("=== End JWT Filter Debug ===");
        
        filterChain.doFilter(request, response);
    }
}
