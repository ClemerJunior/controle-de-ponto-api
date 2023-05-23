package com.github.clemerjunior.controleponto.service;

import com.github.clemerjunior.controleponto.domain.Registro;
import com.github.clemerjunior.controleponto.repositories.RegistroRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RelatorioServiceTest {

    private RelatorioService relatorioService;

    @Mock
    private RegistroRepository registroRepository;

    @BeforeEach
    void setup() {
        this.relatorioService = new RelatorioServiceImpl(registroRepository);
    }

    @Test
    @DisplayName("Deve Gerar um relatorio corretamente")
    void deveGerarUmRelatorio() {
        Optional<List<Registro>> registros = Optional.of(gerarRegistros());
        var mes = YearMonth.now();

        when(registroRepository.findByDiaBetweenOrderByDia(any(), any())).thenReturn(registros);

        var relatorio = relatorioService.gerarRelatorio(mes);

        assertThat(relatorio).isNotNull();
        assertThat(relatorio.getMes()).isEqualTo(mes);
        assertThat(relatorio.getHorasTrabalhadas()).isEqualTo(Duration.ofHours(45));
        assertThat(relatorio.getHorasExcedentes()).isEqualTo(Duration.ofHours(5));
        assertThat(relatorio.getHorasDevidas()).isEqualTo(Duration.ofHours(0));
        assertThat(relatorio.getRegistros()).isNotEmpty();
        assertThat(relatorio.getRegistros().size()).isEqualTo(5);

    }

    private List<Registro> gerarRegistros() {
        var registros = new ArrayList<Registro>();

        for(int i = 0; i < 5; i++) {
            var registro = new Registro(LocalDate.now().plusDays(i));
            registro.addHorario(LocalTime.of(8,0,0));
            registro.addHorario(LocalTime.of(12,0,0));
            registro.addHorario(LocalTime.of(13,0,0));
            registro.addHorario(LocalTime.of(18,0,0));
            registros.add(registro);
        }

        return registros;
    }
}
