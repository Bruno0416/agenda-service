package com.mariluz.agenda.exceptions;

public class UnauthenticatedException extends RuntimeException {

    public UnauthenticatedException() {
        super("No hay un usuario autenticado.");
    }

    public UnauthenticatedException(String message) {
        super(message);
    }
}
