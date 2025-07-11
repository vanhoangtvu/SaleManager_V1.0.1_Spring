package com.example.salem2025.Api.v1;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping("/")
    @ResponseBody
    public String home() {
        return """
        <html>
        <head><title>SaleM-2025 API</title></head>
        <body>
            <h1>SaleM-2025 API</h1>
            <h2>Đăng ký tài khoản</h2>
            <form action="/v1/auth/register" method="post">
                <input type="text" name="username" placeholder="Username" required><br><br>
                <input type="password" name="password" placeholder="Password" required><br><br>
                <select name="role">
                    <option value="USER">USER</option>
                    <option value="ADMIN">ADMIN</option>
                </select><br><br>
                <button type="submit">Đăng ký</button>
            </form>
            
            <h2>Đăng nhập</h2>
            <form action="/v1/auth/login" method="post">
                <input type="text" name="username" placeholder="Username" required><br><br>
                <input type="password" name="password" placeholder="Password" required><br><br>
                <button type="submit">Đăng nhập</button>
            </form>
            
            <h2>API Endpoints</h2>
            <ul>
                <li><a href="/v1/products">Products API</a></li>
                <li><a href="/v1/orders">Orders API</a></li>
                <li><a href="/v1/auth/test">Auth Test</a></li>
                <li><a href="/swagger-ui.html">Swagger UI</a></li>
                <li><a href="/actuator/health">Health Check</a></li>
            </ul>
        </body>
        </html>
        """;
    }
}
