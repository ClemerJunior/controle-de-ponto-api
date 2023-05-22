package com.github.clemerjunior.controleponto.service;


import com.github.clemerjunior.controleponto.repository.Registro;
import com.github.clemerjunior.controleponto.exceptions.HorarioNaoAutorizadoException;
import com.github.clemerjunior.controleponto.exceptions.HorarioRegistradoException;
import com.github.clemerjunior.controleponto.repositories.RegistroRepository;
import com.github.clemerjunior.controleponto.utils.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RegistroServiceTest {

    private RegistroService registroService;

    @Mock
    private RegistroRepository registroRepository;

    private Registro registro;

    @BeforeEach
    void setup() {
        registroService = new RegistroServiceImpl(registroRepository);
        registro = new Registro(LocalDate.now()
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY)));
        registro.addHorario(LocalTime.of(8,0, 0));
    }

    @Test
    @DisplayName("Deve registrar primeira batida de ponto do dia")
    void deveRegistrarPrimeiraBatidaDoDia() {
        when(registroRepository.save(any())).thenReturn(registro);

        var registroSalvo = registroService.baterPonto(LocalDateTime.now()
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY)));

        assertThat(registroSalvo.getDia()).isEqualTo(registro.getDia());
        assertThat(registroSalvo.getHorarios().size()).isEqualTo(1);
        assertThat(registroSalvo.getHorarios().get(0)).isEqualTo(LocalTime.of(8,0, 0));

        verify(registroRepository, times(1)).findRegistroByDia(any());
        verify(registroRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve registrar nova batida de ponto no dia")
    void deveRegistrarNovaBatidaDePonto() {

        when(registroRepository.findRegistroByDia(any())).thenReturn(Optional.of(registro));
        when(registroRepository.save(any())).thenReturn(registro);

        var registroSalvo = registroService.baterPonto(LocalDate.now()
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY))
                .atTime(12,0));

        assertThat(registroSalvo.getDia()).isEqualTo(registro.getDia());
        assertThat(registroSalvo.getHorarios().size()).isEqualTo(2);
        assertThatCollection(registroSalvo.getHorarios()).isEqualTo(registro.getHorarios());

        verify(registroRepository, times(1)).findRegistroByDia(any());
        verify(registroRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("Deve retornar erro quantidade maxima de batidas")
    void deveRetornarErroBatidasMaximasDia() {
        registro.addHorario(LocalTime.now());
        registro.addHorario(LocalTime.now());
        registro.addHorario(LocalTime.now());

        when(registroRepository.findRegistroByDia(any())).thenReturn(Optional.of(registro));

        assertThatThrownBy(() -> registroService.baterPonto(LocalDateTime.now()
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY))))
                .isInstanceOf(HorarioNaoAutorizadoException.class)
                .hasMessage(Constants.MAX_HORARIOS_DIA);

        verify(registroRepository, times(1)).findRegistroByDia(any());
        verify(registroRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("Deve retornar erro fim de semana")
    void deveRetornarErroFimDeSemana() {
        assertThatThrownBy(() -> registroService.baterPonto(LocalDateTime.now()
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))))
                .isInstanceOf(HorarioNaoAutorizadoException.class)
                .hasMessage(Constants.FIM_DE_SEMANA_NAO_VALIDO);

        verify(registroRepository, times(0)).findRegistroByDia(any());
        verify(registroRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("Deve retornar erro se o horario já foi registrado")
    void deveRetornarErroHorarioJaRegistrado() {
        when(registroRepository.existsByDiaAndHorariosContains(any(),any())).thenReturn(Boolean.TRUE);

        assertThatThrownBy(() -> registroService.baterPonto(LocalDateTime.now()
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY))))
                .isInstanceOf(HorarioRegistradoException.class)
                .hasMessage(Constants.HORARIO_JA_REGISTRADO);

        verify(registroRepository, times(1)).existsByDiaAndHorariosContains(any(),any());
        verify(registroRepository, times(0)).findRegistroByDia(any());
        verify(registroRepository, times(0)).save(any());
    }

    @Test
    @DisplayName("Deve retornar erro horario de almoço inválido")
    void deveRetornarAlmocoInvalido() {
        registro.addHorario(LocalTime.of(12,0,0));

        when(registroRepository.findRegistroByDia(any())).thenReturn(Optional.of(registro));

        assertThatThrownBy(() -> registroService.baterPonto(LocalDate.now()
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY))
                .atTime(12,40)))
                .isInstanceOf(HorarioNaoAutorizadoException.class)
                .hasMessage(Constants.DURACAO_ALMOCO_INVALIDA);

        verify(registroRepository, times(1)).existsByDiaAndHorariosContains(any(), any());
        verify(registroRepository, times(1)).findRegistroByDia(any());
        verify(registroRepository, times(0)).save(any());
    }
}
