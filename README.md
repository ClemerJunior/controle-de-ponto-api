# controle-de-ponto-api

## Dependências do ambiente
[JDK 20](https://www.oracle.com/java/technologies/javase/20-relnote-issues.html)

[Apache Maven 3.6+](https://maven.apache.org/download.cgi)

## Setup e execução

Para executar o projeto a maneira mais fácil é, caso tenha o docker instalado,
executar o comando `docker-compose up` no diretório raiz do projeto.
Esse comando utiliza o arquivo docker-compose.yml presente no projeto para: 

1 - criar a imagem docker do projeto descrita no Dockerfile; 

2 - subir um container com a imagem do projeto, subir um container com imagem do MongoDB que o projeto utiliza como Banco de Dados.

Caso queira, o projeto também pode ser executado com o comando `./mvnw spring-boot:run`,
entretanto dessa forma será necessário uma instancia ou container do banco de dados em execução para a conexão.



## Utilização

A Interface do controle de ponto é totalmente REST, com as seguintes operações disponíveis.

### Registro de ponto

Para registrar uma nova batida, envie uma requisição POST para o endpoint `/v1/batidas` com um payload JSON contendo o momento da batida, como no exemplo abaixo:

```JSON
  {
    "dataHora": "2018-08-22T08:00:00"
  }
```

A resposta da requisição será um payload JSON contendo os dados do registro criado ou uma mensagem de erro informando o motivo do erro:

Possíveis retonros:
```JSON
  {
    "dia": "2023-05-22",
    "horarios": [
      "08:00:00"
    ]
  }
```

```JSON
  {
    "mensagem": "Data e hora em formato inválido"
  }
```

### Gerar relatorio mensal

Para autorizar uma transação, envie uma requisição GET para o endpoint `/v1/folhas-de-ponto/{mes}`.

A resposta da requisição será um payload JSON contendo os calculos de horas de trabalhos e os regitros do mes ou uma mensagem de erro informando o motivo do mesmo.

```JSON
  {
    "mes": "2018-08",
    "horasTrabalhadas": "PT69H35M5S",
    "horasExcedentes": "PT25M5S",
    "horasDevidas": "PT0S",
    "registros": [
      {
        "dia": "2023-05-23",
        "horarios": [
         "08:00:00",
         "12:00:00",
         "13:00:00",
         "18:00:00"
        ]
      }
    ]
  }
```

## Referências das principais tecnologias utilizadas na construção desta API
[SpringBoot 3+](https://spring.io/projects/spring-boot)

[SpringData MongoDB](https://spring.io/projects/spring-data-mongodb)

[Lombok 1.18+](https://projectlombok.org/features/all)

[MongoDB](https://www.mongodb.com/)

[Docker](https://www.docker.com/)