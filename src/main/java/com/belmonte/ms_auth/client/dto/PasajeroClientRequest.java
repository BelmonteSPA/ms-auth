package com.belmonte.ms_auth.client.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//cuerpo del POST /api/v1/pasajeros de ms-pasajeros
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasajeroClientRequest {

    private String nombre;
    private String email;
    private String telefono;
    private String empresa;
}
