package com.github.clemerjunior.controleponto.service;

import com.github.clemerjunior.controleponto.domain.RelatorioDTO;

import java.time.YearMonth;

public interface RelatorioService {

    RelatorioDTO gerarRelatorio(YearMonth mes);
}
