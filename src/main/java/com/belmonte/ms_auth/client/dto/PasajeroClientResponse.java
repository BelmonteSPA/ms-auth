package com.belmonte.ms_auth.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//respuesta de ms-pasajeros al crear un pasajero
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasajeroClientResponse {

    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private String empresa;
    private boolean activo;
}
