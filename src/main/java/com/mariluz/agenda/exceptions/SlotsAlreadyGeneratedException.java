package com.mariluz.agenda.exceptions;

public class SlotsAlreadyGeneratedException extends RuntimeException {

    public SlotsAlreadyGeneratedException(String message) {
        super(message);
    }
}
