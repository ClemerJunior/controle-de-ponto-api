package com.github.clemerjunior.controleponto.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
@Document
public class Registro {

    @Id
    String id;

    @Getter
    @Setter
    LocalDate dia;

    @Getter
    List<LocalTime> horarios;

    @PersistenceCreator
    public Registro(LocalDate dia, List<LocalTime> horarios) {
        this.dia = dia;
        this.horarios = horarios;
    }

    public void addHorario(LocalTime horario) throws Exception{
       this.horarios.add(horario);
    }
}
