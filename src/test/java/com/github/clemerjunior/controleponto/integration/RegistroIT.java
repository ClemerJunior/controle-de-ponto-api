package com.github.clemerjunior.controleponto.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.clemerjunior.controleponto.config.AbstractIntegrationTestConfig;
import com.github.clemerjunior.controleponto.repository.Momento;
import com.github.clemerjunior.controleponto.repository.RegistroDTO;
import com.github.clemerjunior.controleponto.repositories.RegistroRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RegistroIT extends AbstractIntegrationTestConfig {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RegistroRepository registroRepository;

    @Test
    @DisplayName("Deve registrar o ponto e salvar no banco sem erros")
    void deveRealizarRegistroSemErro() throws Exception{
        var momento = new Momento(LocalDate.now()
                .with(TemporalAdjusters.nextOrSame(DayOfWeek.MONDAY))
                .atTime(8,0));

        var registroDTO = new RegistroDTO(momento.getDataHora().toLocalDate(),
                new ArrayList<>(Collections.singletonList(momento.getDataHora().toLocalTime())));

        MvcResult mvcResult = mockMvc.perform(post("/v1/batidas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(momento)))
                .andExpect(status().isCreated())
                .andReturn();

        String responseBody = mvcResult.getResponse().getContentAsString();

        assertThat(responseBody)
                .isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(registroDTO));

        var registro = registroRepository.findRegistroByDia(registroDTO.getDia()).orElse(null);

        assertThat(registro).isNotNull();
        assertThat(registro.getHorarios().size()).isEqualTo(1);
        assertThat(registro.getHorarios().get(0)).isEqualTo(momento.getDataHora().toLocalTime());
    }
}
