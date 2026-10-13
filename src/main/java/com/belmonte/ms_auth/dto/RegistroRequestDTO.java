package com.belmonte.ms_auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//registro del solicitante: crea la cuenta (rol PASAJERO) y su ficha en ms-pasajeros
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre no puede superar los 150 caracteres")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato valido")
    @Size(max = 150, message = "El email no puede superar los 150 caracteres")
    private String email;

    //BCrypt solo usa los primeros 72 bytes, por eso el maximo
    @NotBlank(message = "La contrasena es obligatoria")
    @Size(min = 8, max = 72, message = "La contrasena debe tener entre 8 y 72 caracteres")
    private String password;

    @Pattern(
            regexp = "^$|^\\+?[0-9]{8,15}$",
            message = "El telefono debe tener entre 8 y 15 digitos (puede iniciar con +)"
    )
    private String telefono;

    @Size(max = 150, message = "La empresa no puede superar los 150 caracteres")
    private String empresa;
}
