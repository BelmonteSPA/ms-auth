package com.belmonte.ms_auth.controller;

import com.belmonte.ms_auth.dto.UsuarioRequestDTO;
import com.belmonte.ms_auth.dto.UsuarioResponseDTO;
import com.belmonte.ms_auth.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//todos los endpoints requieren rol ADMIN (se define en SecurityConfig)
@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    //GET /usuarios
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> getTodos() {
        return ResponseEntity.ok(usuarioService.mostrarUsuarios());
    }

    //GET /usuarios/id  (404 si no existe)
    @GetMapping("{id}")
    public ResponseEntity<UsuarioResponseDTO> getPorId(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.mostrarPorId(id));
    }

    //POST /usuarios  (400 si falta idReferencia en PASAJERO/CONDUCTOR, 409 si el email ya existe)
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> postUsuario(@Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(usuarioService.save(dto));
    }

    //PATCH /usuarios/id/estado
    //alterna el estado: si esta activo (1) pasa a inactivo (0) y viceversa
    @PatchMapping("{id}/estado")
    public ResponseEntity<UsuarioResponseDTO> patchEstado(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.cambiarEstado(id));
    }
}
