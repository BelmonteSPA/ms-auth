package com.belmonte.ms_auth.dto;

import com.belmonte.ms_auth.model.Rol;
import com.belmonte.ms_auth.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//nunca incluye la contrasena
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponseDTO {

    private Long id;
    private String email;
    private Rol rol;
    private Long idReferencia;
    private boolean activo;

    public static UsuarioResponseDTO desde(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getIdUsuario(),
                usuario.getEmail(),
                usuario.getRol(),
                usuario.getIdReferencia(),
                usuario.isActivo()
        );
    }
}
