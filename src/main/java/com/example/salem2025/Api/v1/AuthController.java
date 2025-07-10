package com.example.salem2025.Api.v1;

import com.example.salem2025.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        // Tạm hardcode, Hoàng có thể thay bằng logic từ DB
        if ("admin".equals(username) && "123".equals(password)) {
            return jwtUtil.generateToken(username);
        } else {
            return "Invalid credentials";
        }
    }
}
