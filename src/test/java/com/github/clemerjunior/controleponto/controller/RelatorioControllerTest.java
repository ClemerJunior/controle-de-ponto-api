package com.github.clemerjunior.controleponto.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.clemerjunior.controleponto.domain.RelatorioDTO;
import com.github.clemerjunior.controleponto.service.RelatorioService;
import com.github.clemerjunior.controleponto.utils.Constants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Duration;
import java.time.YearMonth;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RelatorioController.class)
class RelatorioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RelatorioService relatorioService;

    @Test
    @DisplayName("Deve retornar o relatorio gerado codigo 200")
    void deveRetornarRelatorioGerado200() throws Exception{
        var relatorio = new RelatorioDTO();
        relatorio.setMes(YearMonth.now());
        relatorio.setHorasTrabalhadas(Duration.ZERO);
        relatorio.setHorasExcedentes(Duration.ZERO);
        relatorio.setHorasDevidas(Duration.ZERO);
        relatorio.setRegistros(new ArrayList<>());

        when(relatorioService.gerarRelatorio(YearMonth.now())).thenReturn(relatorio);

        MvcResult mvcResult = mockMvc.perform(get("/v1/folhas-de-ponto/2023-05")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        var responseBody = mvcResult.getResponse().getContentAsString();

        assertThat(responseBody).isEqualToIgnoringWhitespace(objectMapper.writeValueAsString(relatorio));
        verify(relatorioService, times(1)).gerarRelatorio(YearMonth.now());
    }

    @Test
    @DisplayName("Deve retornar erro mes invalido")
    void deveRetornarErroMesInvalido() throws Exception{
        mockMvc.perform(get("/v1/folhas-de-ponto/2023-5")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value(Constants.MES_INVALIDO));
    }


}
