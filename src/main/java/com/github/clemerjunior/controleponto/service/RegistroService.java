package com.github.clemerjunior.controleponto.service;

import com.github.clemerjunior.controleponto.domain.RegistroDTO;

import java.time.LocalDateTime;

public interface RegistroService {

    RegistroDTO baterPonto(LocalDateTime momento);
}
