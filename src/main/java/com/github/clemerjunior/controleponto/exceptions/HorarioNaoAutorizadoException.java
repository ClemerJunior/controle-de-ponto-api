package com.github.clemerjunior.controleponto.exceptions;

public class HorarioNaoAutorizadoException extends RuntimeException{

    public HorarioNaoAutorizadoException(String message) {
        super(message);
    }

    public HorarioNaoAutorizadoException(String message, Throwable cause) {
        super(message, cause);
    }
}
