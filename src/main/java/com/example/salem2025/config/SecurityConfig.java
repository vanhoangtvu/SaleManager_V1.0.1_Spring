package com.example.salem2025.config;

import com.example.salem2025.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("=== Security Config Loading ===");
        http
                .csrf(csrf -> {
                    csrf.disable();
                    System.out.println("CSRF disabled");
                })
                .cors(cors -> {
                    cors.disable();
                    System.out.println("CORS disabled");
                })
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    System.out.println("Setting up authorization rules...");
                    auth
                        // Public endpoints (Spring Security sees paths without context-path)
                        .requestMatchers("/v1/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        
                        // Public home endpoint
                        .requestMatchers("/v1/home/**").permitAll()
                        
                        // Public product viewing endpoints  
                        .requestMatchers("/v1/products/view/**").permitAll()
                        
                        // Admin only endpoints
                        .requestMatchers("/v1/admin/**").hasRole("ADMIN")
                        .requestMatchers("/v1/products/**").hasRole("ADMIN")  // All product management requires ADMIN
                        
                        // User endpoints
                        .requestMatchers("/v1/user/**").hasAnyRole("ADMIN", "USER")
                        
                        // Order endpoints - Allow all authenticated users for testing
                        .requestMatchers("/v1/orders/**").hasAnyRole("ADMIN", "USER")
                        
                        // Temporary: Allow all for debugging
                        .anyRequest().permitAll();
                        //.anyRequest().authenticated();
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        System.out.println("=== Security Config Loaded ===");
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
