package com.github.clemerjunior.controleponto.handlers;

import com.github.clemerjunior.controleponto.exceptions.HorarioNaoAutorizadoException;
import com.github.clemerjunior.controleponto.exceptions.HorarioRegistradoException;
import com.github.clemerjunior.controleponto.utils.Constants;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
public class ExceptionHandle {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseError HandleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        var msg = Objects.nonNull(ex.getFieldError()) ? ex.getFieldError().getDefaultMessage() : Constants.DATA_HORA_NULL_VAZIA;
        return returnResponseError(msg);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseError handleNotReadableException(HttpMessageNotReadableException ex) {
        return returnResponseError(Constants.DATA_HORA_INVALIDO);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(HorarioNaoAutorizadoException.class)
    public ResponseError handleHorarioNaoAutorizadoException(HorarioNaoAutorizadoException ex) {
        return returnResponseError(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(HorarioRegistradoException.class)
    public ResponseError handleHorarioRegistradoException(HorarioRegistradoException ex) {
        return returnResponseError(ex.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public ResponseError handleException(Exception ex) {
        return returnResponseError(Constants.ERRO_INTERNO);
    }

    private ResponseError returnResponseError(String mensagem) {
        return new ResponseError(mensagem);
    }
}