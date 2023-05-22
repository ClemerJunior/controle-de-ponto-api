package com.github.clemerjunior.controleponto.repository;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Document
public class Registro {

    @Id
    String id;

    @Getter
    @Setter
    LocalDate dia;

    @Getter
    List<LocalTime> horarios;


    public Registro(LocalDate dia) {
        this.dia = dia;
        this.horarios = new ArrayList<>(4);
    }

    @PersistenceCreator
    public Registro(LocalDate dia, List<LocalTime> horarios) {
        this.dia = dia;
        this.horarios = Objects.requireNonNullElse(horarios, new ArrayList<>(4));
    }

    public void addHorario(LocalTime horario) {
       this.horarios.add(horario);
    }
}
