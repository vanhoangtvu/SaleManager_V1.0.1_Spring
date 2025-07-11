package com.example.salem2025.config;

import com.example.salem2025.repository.entity.UserAccountEntity;
import com.example.salem2025.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        try {
            System.out.println("Creating default admin account...");
            
            // Kiểm tra xem admin đã tồn tại chưa
            UserAccountEntity existingAdmin = userAccountService.getByUsername("admin");
            
            if (existingAdmin == null) {
                // Tạo admin account
                UserAccountEntity adminUser = new UserAccountEntity();
                adminUser.setUsername("admin");
                adminUser.setPassword(passwordEncoder.encode("hoangadmin"));
                adminUser.setRole("ADMIN");
                adminUser.setFullName("Administrator");
                
                userAccountService.save(adminUser);
                System.out.println("Admin account created: username=admin, password=hoangadmin");
            } else {
                System.out.println("Admin account already exists");
            }
        } catch (Exception e) {
            System.err.println("Error creating admin account: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
