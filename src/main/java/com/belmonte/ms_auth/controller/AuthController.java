package com.belmonte.ms_auth.controller;

import com.belmonte.ms_auth.dto.AuthResponseDTO;
import com.belmonte.ms_auth.dto.LoginRequestDTO;
import com.belmonte.ms_auth.dto.RegistroRequestDTO;
import com.belmonte.ms_auth.dto.UsuarioResponseDTO;
import com.belmonte.ms_auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    //POST /auth/register  (publico: crea cuenta PASAJERO; 409 si el email ya existe)
    @PostMapping("register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegistroRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(authService.registrar(dto));
    }

    //POST /auth/login  (publico: 401 si las credenciales son incorrectas)
    @PostMapping("login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    //GET /auth/me  (requiere token: devuelve el usuario autenticado)
    @GetMapping("me")
    public ResponseEntity<UsuarioResponseDTO> me(Authentication authentication) {
        return ResponseEntity.ok(authService.mostrarPorEmail(authentication.getName()));
    }
}
