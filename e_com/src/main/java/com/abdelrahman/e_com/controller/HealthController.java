package com.abdelrahman.e_com.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/health")
    public String health(){
        System.out.println("the server is good");
        return "server is working";
    }
    
}
