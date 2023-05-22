package com.github.clemerjunior.controleponto.repository;

import com.github.clemerjunior.controleponto.utils.Constants;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Momento {

    @Valid
    @NotNull(message = Constants.DATA_HORA_NULL_VAZIA)
    private LocalDateTime dataHora;
}
