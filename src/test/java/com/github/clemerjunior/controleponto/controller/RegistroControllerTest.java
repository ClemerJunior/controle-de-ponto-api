package com.github.clemerjunior.controleponto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.clemerjunior.controleponto.domain.Momento;
import com.github.clemerjunior.controleponto.domain.RegistroDTO;
import com.github.clemerjunior.controleponto.exceptions.HorarioNaoAutorizadoException;
import com.github.clemerjunior.controleponto.exceptions.HorarioRegistradoException;
import com.github.clemerjunior.controleponto.service.RegistroService;
import com.github.clemerjunior.controleponto.utils.Constants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(RegistroController.class)
class RegistroControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RegistroService registroService;

    private Momento momento;
    private RegistroDTO registroDTO;

    @BeforeEach
    void setUp() {
        var diaHora = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY))
                .atTime(8,0);

        momento = new Momento(diaHora);
        registroDTO = new RegistroDTO(diaHora.toLocalDate(),
                new ArrayList<>(Collections.singletonList(diaHora.toLocalTime())));
    }

    @Test
    @DisplayName("Deve retornar registrar o ponto e retornar 201")
    void deveRealizarBaterPontoSemErros() throws Exception{
        when(registroService.baterPonto(momento.getDataHora())).thenReturn(registroDTO);

        MvcResult mvcResult = mockMvc.perform(post("/v1/batidas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(momento)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        assertThat(responseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(registroDTO));
    }

    @Test
    @DisplayName("Deve retornar erro campo nao informado")
    void deveRetornarErroCampoNaoInformado() throws Exception{
        momento.setDataHora(null);

        mockMvc.perform(post("/v1/batidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(momento)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value(Constants.DATA_HORA_NULL_VAZIA));
    }

    @Test
    @DisplayName("Deve retornar erro data invalida")
    void deveRetornarErroDataInvalida() throws Exception{
        String requestBody = objectMapper.writeValueAsString(momento);
        String regex = "\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}";

        mockMvc.perform(post("/v1/batidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody.replaceAll(regex, "asdf"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value(Constants.DATA_HORA_INVALIDO));
    }

    @Test
    @DisplayName("Deve retornar erro horario já registrado")
    void deveRetornarErroHorarioJaRegistrado() throws Exception{
        when(registroService.baterPonto(any()))
                .thenThrow(new HorarioRegistradoException(Constants.HORARIO_JA_REGISTRADO));

        mockMvc.perform(post("/v1/batidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(momento)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").value(Constants.HORARIO_JA_REGISTRADO));
    }

    @Test
    @DisplayName("Deve retornar erro fim de semana")
    void deveRetornarErroFimDeSemana() throws Exception{
        when(registroService.baterPonto(any()))
                .thenThrow(new HorarioNaoAutorizadoException(Constants.FIM_DE_SEMANA_NAO_VALIDO));

        mockMvc.perform(post("/v1/batidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(momento)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.mensagem").value(Constants.FIM_DE_SEMANA_NAO_VALIDO));
    }

    @Test
    @DisplayName("Deve retornar erro maximo 4 horarios")
    void deveRetornarErroMaxHorarios() throws Exception{
        when(registroService.baterPonto(any()))
                .thenThrow(new HorarioNaoAutorizadoException(Constants.MAX_HORARIOS_DIA));

        mockMvc.perform(post("/v1/batidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(momento)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.mensagem").value(Constants.MAX_HORARIOS_DIA));
    }

    @Test
    @DisplayName("Deve retornar erro duração almoco menor que 1h")
    void deveRetornarErroDurcaoAlmoco() throws Exception{
        when(registroService.baterPonto(any()))
                .thenThrow(new HorarioNaoAutorizadoException(Constants.DURACAO_ALMOCO_INVALIDA));

        mockMvc.perform(post("/v1/batidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(momento)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.mensagem").value(Constants.DURACAO_ALMOCO_INVALIDA));
    }
}

