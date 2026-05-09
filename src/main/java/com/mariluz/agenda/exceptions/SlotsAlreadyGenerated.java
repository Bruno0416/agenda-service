package com.mariluz.agenda.exceptions;

public class SlotsAlreadyGenerated extends RuntimeException {

    public SlotsAlreadyGenerated(String message) {
        super(message);
    }
}
