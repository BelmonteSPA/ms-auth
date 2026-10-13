package com.belmonte.ms_auth.dto;

import com.belmonte.ms_auth.model.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//creacion de usuarios por un ADMIN (conductores, otros administradores o pasajeros ya existentes)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioRequestDTO {

    //id y activo no se incluyen: la base de datos genera el id y la cuenta nace activa

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato valido")
    @Size(max = 150, message = "El email no puede superar los 150 caracteres")
    private String email;

    @NotBlank(message = "La contrasena es obligatoria")
    @Size(min = 8, max = 72, message = "La contrasena debe tener entre 8 y 72 caracteres")
    private String password;

    @NotNull(message = "El rol es obligatorio (ADMIN, PASAJERO o CONDUCTOR)")
    private Rol rol;

    //obligatorio para PASAJERO y CONDUCTOR: id de su ficha en ms-pasajeros / ms-conductores
    private Long idReferencia;
}
