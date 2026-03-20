package com.amani.amaniapirest.controllers.login;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HasearContrasenia {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123"; // ← la contraseña que quieres para el admin
        String hashed = encoder.encode(password);
        System.out.println("Hash generado: " + hashed);
    }
}
