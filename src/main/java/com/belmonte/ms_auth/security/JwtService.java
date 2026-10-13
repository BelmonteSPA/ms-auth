package com.belmonte.ms_auth.security;

import com.belmonte.ms_auth.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {
    private final SecretKey clave;
    private final long expiracionMinutos;

    public JwtService(
            @Value("${jwt.secret}") String secreto,
            @Value("${jwt.expiration-minutes:480}") long expiracionMinutos
    ) {
        //HS256 exige una clave de al menos 32 bytes
        this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
        this.expiracionMinutos = expiracionMinutos;
    }

    public long getExpiracionMinutos() {
        return expiracionMinutos;
    }

    //el token lleva email (subject), id de usuario, rol e id de su ficha (pasajero / conductor)
    public String generarToken(Usuario usuario) {
        long ahora = System.currentTimeMillis();
        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("userId", usuario.getIdUsuario())
                .claim("rol", usuario.getRol().name())
                .claim("idReferencia", usuario.getIdReferencia())
                .issuedAt(new Date(ahora))
                .expiration(new Date(ahora + expiracionMinutos * 60_000))
                .signWith(clave)
                .compact();
    }

    //lanza JwtException si el token esta vencido, alterado o mal formado
    public Claims leerToken(String token) {
        return Jwts.parser()
                .verifyWith(clave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
