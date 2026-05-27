package com.mariluz.agenda.exceptions;

public class ReservationAlreadyCanceledException extends RuntimeException {

    public ReservationAlreadyCanceledException(String message) {
        super(message);
    }
}
