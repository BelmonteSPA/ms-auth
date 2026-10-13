package com.belmonte.ms_auth.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @Column(nullable = false, length = 150, unique = true)
    private String email;

    //siempre el hash BCrypt, nunca la contrasena en texto plano
    @ToString.Exclude
    @Column(nullable = false, length = 100)
    private String password;

    //se guarda como texto (VARCHAR) para que Flyway y Hibernate coincidan en MySQL
    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(nullable = false, length = 20)
    private Rol rol;

    //id de la ficha en ms-pasajeros (PASAJERO) o ms-conductores (CONDUCTOR); null para ADMIN
    @Column(name = "id_referencia")
    private Long idReferencia;

    @Column(nullable = false)
    private boolean activo = true;
}
