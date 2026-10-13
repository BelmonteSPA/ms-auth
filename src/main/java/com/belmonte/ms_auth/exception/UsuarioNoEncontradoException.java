package com.belmonte.ms_auth.exception;

public class UsuarioNoEncontradoException extends RuntimeException {

    public UsuarioNoEncontradoException(Long id) {
        super("El usuario con ID: " + id + " no existe");
    }

    public UsuarioNoEncontradoException(String email) {
        super("El usuario con email: " + email + " no existe");
    }
}
