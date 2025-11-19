package com.eventos.eventos.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    
    @GetMapping("/")
    public String home() {
        return "🚀 Back-end RiseUp está online!";
    }
    
    @GetMapping("/api/test")
    public String test() {
        return "✅ API funcionando corretamente!";
    }
    
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
}