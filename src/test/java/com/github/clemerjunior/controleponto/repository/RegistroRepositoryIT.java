package com.github.clemerjunior.controleponto.repository;

import com.github.clemerjunior.controleponto.config.AbstractIntegrationTestConfig;
import com.github.clemerjunior.controleponto.domain.Registro;
import com.github.clemerjunior.controleponto.repositories.RegistroRepository;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class RegistroRepositoryIT extends AbstractIntegrationTestConfig {

    @Autowired
    private RegistroRepository registroRepository;

    @Test
    @DisplayName("Deve salva um novo registro corretamente")
    @Order(1)
    void deveSalvarUmNovoRegistro() throws Exception{
        var registro = new Registro(LocalDate.now(), new ArrayList<>());
        registro.addHorario(LocalTime.of(8,0,0));

        registroRepository.save(registro);

        var registroSalvo = registroRepository.findAll().get(0);
        assertThat(registroSalvo).isNotNull();
    }

    @Test
    @DisplayName("Deve consultar um registro pelo dia")
    @Order(2)
    void deveConsultarUmRegistroPeloDia() throws Exception{
        var registroSalvo = registroRepository
                .findRegistroByDia(LocalDate.now()).orElse(null);

        assertThat(registroSalvo).isNotNull();
        assertThat(LocalDate.now()).isEqualTo(registroSalvo.getDia());
    }

    @Test
    @DisplayName("Deve adicionar um horario num dia já existente")
    @Order(3)
    void deveAdicionarHorario() {
        var registroRecuperado = registroRepository.findRegistroByDia(LocalDate.now()).orElse(null);

        assertThat(registroRecuperado).isNotNull();

        var novoHorario = LocalTime.of(12,0,0);
        registroRecuperado.addHorario(novoHorario);
        var registroSalvo = registroRepository.save(registroRecuperado);

        assertThat(registroSalvo).isNotNull();
        assertThat(registroSalvo.getHorarios()).hasSize(2);
        assertThat(registroSalvo.getHorarios()).contains(novoHorario);
    }

    @Test
    @DisplayName("Deve verificar se um registro existe com dado dia e horario")
    @Order(4)
    void deveVerificarRegistroExistePorDiaHorario() {
        var registroExiste = registroRepository
                .existsByDiaAndHorariosContains(LocalDate.now(), LocalTime.of(8,0,0));

        assertThat(registroExiste).isTrue();
    }

    @Test
    @DisplayName("Deve verificar se um registro nao existe com dado dia e horario")
    @Order(5)
    void deveVerificarRegistroNaoExistePorDiaHorario() {
        var registroExiste = registroRepository
                .existsByDiaAndHorariosContains(LocalDate.now(), LocalTime.of(13,0,0));

        assertThat(registroExiste).isFalse();
    }

    @Test
    @DisplayName("Deve recuperar os registros do mes")
    @Order(6)
    void deveRecuperarOsRegistrosDoMes() {
        var yearMonth = YearMonth.now();
        var inicioProximoMes = yearMonth.atDay(1).plusMonths(1);
        var fimMesAnterio = yearMonth.atEndOfMonth().minusMonths(1);

        var registroInicioMes = new Registro(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()), new ArrayList<>());
        registroRepository.save(registroInicioMes);

        var registroFimMes = new Registro(LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()), new ArrayList<>());
        registroRepository.save(registroFimMes);

        var registroOutroMes = new Registro(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).plusMonths(1), new ArrayList<>());
        registroRepository.save(registroOutroMes);


        var todosRegistros = registroRepository.findAll();
        var registrosDoMes = registroRepository.findByDiaBetweenOrderByDia(fimMesAnterio, inicioProximoMes)
                .orElse(new ArrayList<>());

        assertThat(todosRegistros).isNotEmpty().hasSize(4).containsAll(registrosDoMes);
        assertThat(registrosDoMes).isNotEmpty().hasSize(3).doesNotContain(registroOutroMes);
    }
}
