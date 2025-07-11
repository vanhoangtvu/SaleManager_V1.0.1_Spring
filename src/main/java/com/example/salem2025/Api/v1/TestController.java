package com.example.salem2025.Api.v1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello! API is working!";
    }

    @GetMapping("/info")
    public String info() {
        return "SaleM-2025 API - Test endpoint";
    }
}
