package com.github.clemerjunior.controleponto.domain;

import com.github.clemerjunior.controleponto.repositories.RegistroRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RegistroTest {

    @Autowired
    private RegistroRepository registroRepository;

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @DynamicPropertySource
    static void mongoProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeAll
    public static void init() {
        mongoDBContainer.start();
    }

    @AfterAll
    public static void tearDown() {
        mongoDBContainer.stop();
    }

    @Test
    @DisplayName("Deve salva um novo registro corretamente")
    @Order(1)
    void deveSalvarUmNovoRegistro() throws Exception{
        var registro = new Registro(LocalDate.now(), new ArrayList<>());
        registro.addHorario(LocalTime.now());

        registroRepository.save(registro);

        var registroSalvo = registroRepository.findAll().get(0);
        Assertions.assertNotNull(registroSalvo);
    }

    @Test
    @DisplayName("Deve consultar um registro pelo dia")
    @Order(2)
    void deveConsultarUmRegistroPeloDia() throws Exception{
        var registroSalvo = registroRepository
                .findRegistroByDia(LocalDate.now()).orElse(null);

        Assertions.assertNotNull(registroSalvo);
        Assertions.assertEquals(LocalDate.now(), registroSalvo.getDia());
    }
}
