package com.amani.amaniapirest.controllers.login;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utilidad de linea de comandos para generar hashes BCrypt de contrasenas.
 *
 * <p>No forma parte del flujo REST de la aplicacion; se ejecuta de forma
 * independiente mediante {@code java HasearContrasenia} para obtener el hash
 * que se insertara manualmente en la base de datos (p.ej. para el usuario admin).</p>
 */
public class HasearContrasenia {

    /**
     * Punto de entrada que codifica una contrasena en texto plano con BCrypt
     * e imprime el hash resultante por consola.
     *
     * @param args argumentos de linea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "1234"; // ← la contraseña que quieres para el admin
        String hashed = encoder.encode(password);
        System.out.println("Hash generado: " + hashed);
    }
}
