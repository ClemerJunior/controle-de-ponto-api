package com.github.clemerjunior.controleponto.domain;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.YearMonth;
import java.util.List;

@Getter
@Setter
public class RelatorioDTO {

    private YearMonth mes;
    private Duration horasTrabalhadas;
    private Duration horasExcedentes;
    private Duration horasDevidas;
    private List<RegistroDTO> registros;
}
