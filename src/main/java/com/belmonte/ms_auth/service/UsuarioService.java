package com.belmonte.ms_auth.service;

import com.belmonte.ms_auth.dto.UsuarioRequestDTO;
import com.belmonte.ms_auth.dto.UsuarioResponseDTO;
import com.belmonte.ms_auth.exception.EmailDuplicadoException;
import com.belmonte.ms_auth.exception.UsuarioNoEncontradoException;
import com.belmonte.ms_auth.model.Rol;
import com.belmonte.ms_auth.model.Usuario;
import com.belmonte.ms_auth.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

//gestion de usuarios, solo para el rol ADMIN
@Slf4j
@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private Usuario buscarOFallar(Long id) {
        return usuarioRepository
                .findById(id)
                .orElseThrow(() -> new UsuarioNoEncontradoException(id));
    }

    //GET /usuarios
    public List<UsuarioResponseDTO> mostrarUsuarios() {
        return usuarioRepository
                .findAll()
                .stream()
                .map(UsuarioResponseDTO::desde)
                .collect(Collectors.toList());
    }

    //GET /usuarios/id
    public UsuarioResponseDTO mostrarPorId(Long id) {
        return UsuarioResponseDTO.desde(buscarOFallar(id));
    }

    //POST /usuarios
    public UsuarioResponseDTO save(UsuarioRequestDTO dto) {
        String email = dto.getEmail().toLowerCase();
        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailDuplicadoException(email);
        }

        //PASAJERO y CONDUCTOR deben apuntar a su ficha; ADMIN no tiene ficha
        Long idReferencia = null;
        if (dto.getRol() != Rol.ADMIN) {
            if (dto.getIdReferencia() == null) {
                throw new IllegalArgumentException(
                        "El idReferencia es obligatorio para el rol " + dto.getRol());
            }
            idReferencia = dto.getIdReferencia();
        }

        Usuario guardado = usuarioRepository.save(new Usuario(
                null,
                email,
                passwordEncoder.encode(dto.getPassword()),
                dto.getRol(),
                idReferencia,
                true
        ));
        log.info(">>> Usuario {} creado con rol {}", guardado.getIdUsuario(), guardado.getRol());
        return UsuarioResponseDTO.desde(guardado);
    }

    //PATCH /usuarios/id/estado  (alterna activo <-> inactivo)
    public UsuarioResponseDTO cambiarEstado(Long id) {
        Usuario usuario = buscarOFallar(id);
        usuario.setActivo(!usuario.isActivo());
        return UsuarioResponseDTO.desde(usuarioRepository.save(usuario));
    }
}
