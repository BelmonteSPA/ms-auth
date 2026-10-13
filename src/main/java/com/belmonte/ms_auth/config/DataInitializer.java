package com.belmonte.ms_auth.config;

import com.belmonte.ms_auth.model.Rol;
import com.belmonte.ms_auth.model.Usuario;
import com.belmonte.ms_auth.repository.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

/**
 * Carga usuarios 100% sintéticos (el anexo prohíbe usar datos reales).
 * Solo corre en el perfil "dev" y únicamente si la tabla está vacía.
 *
 * Crea 1 administrador, un usuario por cada pasajero de ms-pasajeros (ids 1..100)
 * y uno por cada conductor de ms-conductores (ids 1..20). Los emails coinciden con los
 * de los datos de prueba de esos servicios (pasajeroN@ejemplo.test, conductorN@ejemplo.test).
 */
@Slf4j
@Configuration
@Profile("dev")
public class DataInitializer {

    private static final int TOTAL_PASAJEROS = 100;
    private static final int TOTAL_CONDUCTORES = 20;

    //contrasenas de prueba solo para desarrollo
    private static final String PASSWORD_ADMIN = "Admin1234!";
    private static final String PASSWORD_PASAJERO = "Pasajero1234!";
    private static final String PASSWORD_CONDUCTOR = "Conductor1234!";

    @Bean
    CommandLineRunner cargarUsuarios(UsuarioRepository repository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (repository.count() > 0) {
                return;
            }

            //BCrypt es lento a proposito: se calcula una sola vez por rol y se reutiliza
            String hashAdmin = passwordEncoder.encode(PASSWORD_ADMIN);
            String hashPasajero = passwordEncoder.encode(PASSWORD_PASAJERO);
            String hashConductor = passwordEncoder.encode(PASSWORD_CONDUCTOR);

            List<Usuario> usuarios = new ArrayList<>();
            usuarios.add(new Usuario(null, "admin@belmonte.test", hashAdmin, Rol.ADMIN, null, true));

            for (int i = 1; i <= TOTAL_PASAJEROS; i++) {
                usuarios.add(new Usuario(
                        null, "pasajero" + i + "@ejemplo.test", hashPasajero, Rol.PASAJERO, (long) i, true));
            }
            for (int i = 1; i <= TOTAL_CONDUCTORES; i++) {
                usuarios.add(new Usuario(
                        null, "conductor" + i + "@ejemplo.test", hashConductor, Rol.CONDUCTOR, (long) i, true));
            }

            repository.saveAll(usuarios);
            log.info(">>> {} usuarios sintéticos cargados (1 ADMIN, {} PASAJERO, {} CONDUCTOR)",
                    repository.count(), TOTAL_PASAJEROS, TOTAL_CONDUCTORES);
        };
    }
}
