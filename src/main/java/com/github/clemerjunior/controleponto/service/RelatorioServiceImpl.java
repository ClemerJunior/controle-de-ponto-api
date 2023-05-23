package com.github.clemerjunior.controleponto.service;

import com.github.clemerjunior.controleponto.domain.RegistroDTO;
import com.github.clemerjunior.controleponto.domain.RelatorioDTO;
import com.github.clemerjunior.controleponto.repositories.RegistroRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RelatorioServiceImpl implements RelatorioService{

    private final RegistroRepository registroRepository;

    @Override
    public RelatorioDTO gerarRelatorio(YearMonth mes) {
        var fimMesAnterior = mes.atEndOfMonth().minusMonths(1);
        var inicioProximoMes = mes.atDay(1).plusMonths(1);
        var registrosMes = registroRepository.findByDiaBetweenOrderByDia(fimMesAnterior, inicioProximoMes)
                .orElse(Collections.emptyList())
                .stream()
                .filter(Objects::nonNull)
                .map(RegistroDTO::new)
                .toList();

        var horasTrabalhadas = calcularHorasTrabalhadas(registrosMes);
        var horasExcedentes = calcularHorasExcedentes(registrosMes.size(), horasTrabalhadas);
        var horasDevidas = calcularHorasDevidas(registrosMes.size(), horasTrabalhadas);

        var relatorio = new RelatorioDTO();
        relatorio.setMes(mes);
        relatorio.setHorasTrabalhadas(horasTrabalhadas);
        relatorio.setHorasExcedentes(horasExcedentes);
        relatorio.setHorasDevidas(horasDevidas);
        relatorio.setRegistros(registrosMes);

        return relatorio;
    }

    private Duration calcularHorasTrabalhadas(List<RegistroDTO> registros) {

        Optional<Duration> horasTrabalhadas = registros
                .stream()
                .map(RegistroDTO::getHorarios)
                .map(this::calcularHoraTrabalhadasDia)
                .reduce(Duration::plus);

        return horasTrabalhadas.orElse(Duration.ZERO);
    }

    private Duration calcularHorasExcedentes(int diasTrabalhados, Duration horasTrabalhadas) {
        var horasNecessarias = Duration.ofHours(diasTrabalhados * 8L);
        var horasExcedentes = Duration.ZERO;

        if(horasTrabalhadas.compareTo(horasNecessarias) > 0) {
            horasExcedentes = horasTrabalhadas.minus(horasNecessarias);
        }

        return horasExcedentes;
    }

    private Duration calcularHorasDevidas(int diasTrabalhados, Duration horasTrabalhadas) {
        var horasNecessarias = Duration.ofHours(diasTrabalhados * 8L);
        var horasDevidas = Duration.ZERO;

        if(horasTrabalhadas.compareTo(horasNecessarias) <= 0) {
            horasDevidas = horasNecessarias.minus(horasTrabalhadas);
        }

        return horasDevidas;
    }

    private Duration calcularHoraTrabalhadasDia(List<LocalTime> horarios) {
        var horasTrabalhadasDia = Duration.ZERO;
        var qtdHorarios = horarios.size();

        if (qtdHorarios >= 2) {
            horarios.sort(LocalTime::compareTo);
            horasTrabalhadasDia = Duration.between(horarios.get(0), horarios.get(1));

            if(qtdHorarios >= 4) {
                horasTrabalhadasDia = horasTrabalhadasDia.plus(Duration.between(horarios.get(2), horarios.get(3)));
            }
        }

        return horasTrabalhadasDia;
    }

}
