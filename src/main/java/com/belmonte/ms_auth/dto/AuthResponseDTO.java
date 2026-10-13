package com.belmonte.ms_auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDTO {

    //se envia en cada peticion como: Authorization: Bearer <token>
    private String token;
    private String tipo;
    private long expiraEnMinutos;
    private UsuarioResponseDTO usuario;
}
