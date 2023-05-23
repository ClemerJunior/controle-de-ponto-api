package com.github.clemerjunior.controleponto.exceptions;

public class HorarioRegistradoException extends RuntimeException{

    public HorarioRegistradoException(String message) {
        super(message);
    }

    public HorarioRegistradoException(String message, Throwable cause) {
        super(message, cause);
    }
}
