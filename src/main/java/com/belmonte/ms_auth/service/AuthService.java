package com.belmonte.ms_auth.service;

import com.belmonte.ms_auth.client.PasajeroClient;
import com.belmonte.ms_auth.client.dto.PasajeroClientRequest;
import com.belmonte.ms_auth.client.dto.PasajeroClientResponse;
import com.belmonte.ms_auth.dto.AuthResponseDTO;
import com.belmonte.ms_auth.dto.LoginRequestDTO;
import com.belmonte.ms_auth.dto.RegistroRequestDTO;
import com.belmonte.ms_auth.dto.UsuarioResponseDTO;
import com.belmonte.ms_auth.exception.CredencialesInvalidasException;
import com.belmonte.ms_auth.exception.EmailDuplicadoException;
import com.belmonte.ms_auth.exception.ServicioNoDisponibleException;
import com.belmonte.ms_auth.exception.UsuarioNoEncontradoException;
import com.belmonte.ms_auth.model.Rol;
import com.belmonte.ms_auth.model.Usuario;
import com.belmonte.ms_auth.repository.UsuarioRepository;
import com.belmonte.ms_auth.security.JwtService;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasajeroClient pasajeroClient;

    @Autowired
    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            PasajeroClient pasajeroClient
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.pasajeroClient = pasajeroClient;
    }

    private AuthResponseDTO construirRespuesta(Usuario usuario) {
        return new AuthResponseDTO(
                jwtService.generarToken(usuario),
                "Bearer",
                jwtService.getExpiracionMinutos(),
                UsuarioResponseDTO.desde(usuario)
        );
    }

    //crea la ficha del pasajero en ms-pasajeros y devuelve su id
    private Long crearFichaPasajero(RegistroRequestDTO dto, String email) {
        try {
            PasajeroClientResponse ficha = pasajeroClient.crear(new PasajeroClientRequest(
                    dto.getNombre(),
                    email,
                    dto.getTelefono(),
                    dto.getEmpresa()
            ));
            return ficha.getId();
        } catch (FeignException.Conflict e) {
            throw new EmailDuplicadoException(email);
        } catch (FeignException.BadRequest e) {
            throw new IllegalArgumentException("Los datos del pasajero no son validos (revisa nombre, telefono o empresa)");
        } catch (FeignException e) {
            log.error(">>> Error al consultar ms-pasajeros: {}", e.getMessage());
            throw new ServicioNoDisponibleException("ms-pasajeros");
        }
    }

    //POST /auth/register  (cuenta PASAJERO + ficha en ms-pasajeros; devuelve el token para entrar de inmediato)
    public AuthResponseDTO registrar(RegistroRequestDTO dto) {
        String email = dto.getEmail().toLowerCase();
        if (usuarioRepository.existsByEmail(email)) {
            throw new EmailDuplicadoException(email);
        }

        Long idPasajero = crearFichaPasajero(dto, email);

        Usuario guardado = usuarioRepository.save(new Usuario(
                null,
                email,
                passwordEncoder.encode(dto.getPassword()),
                Rol.PASAJERO,
                idPasajero,
                true
        ));
        log.info(">>> Usuario {} registrado como PASAJERO", guardado.getIdUsuario());
        return construirRespuesta(guardado);
    }

    //POST /auth/login
    public AuthResponseDTO login(LoginRequestDTO dto) {
        String email = dto.getEmail().toLowerCase();
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, dto.getPassword()));
        } catch (AuthenticationException e) {
            //email inexistente, contrasena incorrecta o cuenta inactiva: mismo mensaje en los tres casos
            throw new CredencialesInvalidasException();
        }

        Usuario usuario = usuarioRepository
                .findByEmail(email)
                .orElseThrow(CredencialesInvalidasException::new);
        return construirRespuesta(usuario);
    }

    //GET /auth/me  (datos del usuario del token)
    public UsuarioResponseDTO mostrarPorEmail(String email) {
        return UsuarioResponseDTO.desde(
                usuarioRepository
                        .findByEmail(email)
                        .orElseThrow(() -> new UsuarioNoEncontradoException(email))
        );
    }
}
