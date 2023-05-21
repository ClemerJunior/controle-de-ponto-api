package com.github.clemerjunior.controleponto.service;

import com.github.clemerjunior.controleponto.domain.Registro;
import com.github.clemerjunior.controleponto.domain.RegistroDTO;
import com.github.clemerjunior.controleponto.exceptions.HorarioNaoAutorizadoException;
import com.github.clemerjunior.controleponto.exceptions.HorarioRegistradoException;
import com.github.clemerjunior.controleponto.repositories.RegistroRepository;
import com.github.clemerjunior.controleponto.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;


@Service
@RequiredArgsConstructor
public class RegistroServiceImpl implements RegistroService{

    private final static int HORARIOS_MAX_SIZE = 4;

    private final RegistroRepository repository;

    @Override
    public RegistroDTO baterPonto(LocalDateTime momento) {
        var dia = momento.toLocalDate();
        var horario = momento.toLocalTime();

        validarFimSemana(dia);
        validarHorarioJaRegistrado(dia, horario);

        var registro = repository.findRegistroByDia(dia).orElse(new Registro(dia));

        validarQuantidadeBatidasMax(registro);
        validarHorarioAlmoco(registro, horario);

        registro.addHorario(horario);

        var registroSalvo = repository.save(registro);

        return new RegistroDTO(registroSalvo.getDia(), registroSalvo.getHorarios());
    }

    private void validarFimSemana(LocalDate dia) {
        if(dia.getDayOfWeek().equals(DayOfWeek.SATURDAY) || dia.getDayOfWeek().equals(DayOfWeek.SUNDAY))
            throw new HorarioNaoAutorizadoException(Constants.FIM_DE_SEMANA_NAO_VALIDO);
    }

    private void validarHorarioJaRegistrado(LocalDate dia, LocalTime horario) {
        if (repository.existsByDiaAndHorariosContains(dia, horario))
            throw new HorarioRegistradoException(Constants.HORARIO_JA_REGISTRADO);
    }

    private void validarQuantidadeBatidasMax(Registro registro) {
        if(registro.getHorarios().size() >= HORARIOS_MAX_SIZE)
            throw new HorarioNaoAutorizadoException(Constants.MAX_BATIDAS_DIA);
    }

    private void validarHorarioAlmoco(Registro registro, LocalTime horario) {
        if(registro.getHorarios().size() == 2) {
            LocalTime saidaAlmoco = registro.getHorarios().stream().max(LocalTime::compareTo).orElseThrow();

            if(Duration.between(saidaAlmoco, horario).toHours() < 1)
                throw new HorarioNaoAutorizadoException(Constants.HORARIO_ALMOCO_INVALIDO);

        }
    }
}
