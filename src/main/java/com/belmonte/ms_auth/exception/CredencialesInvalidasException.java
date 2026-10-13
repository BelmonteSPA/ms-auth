package com.belmonte.ms_auth.exception;

//mismo mensaje para email inexistente, contrasena incorrecta o cuenta inactiva (no revela cual fue)
public class CredencialesInvalidasException extends RuntimeException {

    public CredencialesInvalidasException() {
        super("Email o contrasena incorrectos");
    }
}
