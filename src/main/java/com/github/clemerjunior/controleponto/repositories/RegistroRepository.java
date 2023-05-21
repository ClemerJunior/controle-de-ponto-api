package com.github.clemerjunior.controleponto.repositories;

import com.github.clemerjunior.controleponto.domain.Registro;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RegistroRepository extends MongoRepository<Registro, LocalDate> {

    Optional<Registro> findRegistroByDia(LocalDate dia);
}
