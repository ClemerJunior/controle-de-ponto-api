package com.github.clemerjunior.controleponto.controller;

import com.github.clemerjunior.controleponto.domain.Momento;
import com.github.clemerjunior.controleponto.domain.RegistroDTO;
import com.github.clemerjunior.controleponto.service.RegistroService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/batidas")
@RequiredArgsConstructor
@Validated
public class RegistroController {

    private final RegistroService registroService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
                 produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<RegistroDTO> baterPonto(@Valid @RequestBody Momento momento) {
        var registro = registroService.baterPonto(momento.getDataHora());
        return ResponseEntity.status(HttpStatus.CREATED).body(registro);
    }

}
