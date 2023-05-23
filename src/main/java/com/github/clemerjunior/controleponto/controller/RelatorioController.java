package com.github.clemerjunior.controleponto.controller;

import com.github.clemerjunior.controleponto.domain.RelatorioDTO;
import com.github.clemerjunior.controleponto.service.RelatorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;

@RestController
@RequestMapping("/v1/folhas-de-ponto")
@RequiredArgsConstructor
public class RelatorioController {

    private final RelatorioService relatorioService;

    @GetMapping("/{mes}")
    public ResponseEntity<RelatorioDTO> gerarRelatorioMensal(@PathVariable
                                                             @DateTimeFormat(pattern = "yyyy-MM") YearMonth mes) {
        var relatorio = relatorioService.gerarRelatorio(mes);
        return ResponseEntity.ok(relatorio);
    }
}
