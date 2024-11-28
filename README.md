<h1 align="center">BiblioPlus</h1>

Este projeto consiste em uma API desenvolvida como requisito para pontuação na disciplina de backend framework. A API é proposta como sendo para uma biblioteca, possuindo como entidades principais a entidade de livros(books), usuários(users) e pedidos (orders), cada uma com suas respectivas regras de negócio e validações.

# Participantes

- Emanuel Ernesto de Andrade Rêgo - 01614951
- Wesley Ruan de Lima Silva - 01555915
- Felipe Farias Galvão - 01608490
- Antônio Henrique Leitão Barros - 01647043
- Adam Caldas Lopes - 01640350
- José Carlos Moura Silva - 01530501
- Thiago Oliveira Lima Pessoa - 01089643


## Pré-requisitos

Antes de começar, verifique se sua máquina possui os seguintes requisitos:

- **Jdk**: versão 17 ou superior. Se você ainda possui a JDK instalada, siga o tutorial de instalação [aqui](https://techexpert.tips/pt-br/windows-pt-br/instalar-java-jdk-no-windows/).

- **Docker**: necessário para executar tanto o Postgres quanto o Pgadmin e o sonarqube localmente. Instruções de instalação estão disponíveis [aqui](https://docs.docker.com/get-docker/).

- **Git**: essencial para clonar o repositório. Baixe-o [aqui](https://www.git-scm.com/downloads).

## Instalação e Configuração

1. **Clone o repositório**:

   ```bash
   git clone https://github.com/EmanuelErnesto/BiblioPlus.git


2. **Navegue Até a pasta do projeto**

  ```bash
  cd BiblioPlus

  ```


3. **Configuração do ambiente**


  ```bash
  cp .env.example .env
  ```

⚠️ Preencha as variáveis de ambiente com os respectivos valores.

⚠️ Certifique-se que a porta `8082` esteja livre para o correto funcionamento da API.

<h1>🔧 Executando a API</h1>

Você deverá acessar o docker desktop (caso esteja no windows), após isto acessar o terminal e rodar o comando:

```bash
docker-compose up -d
```

Feito isto, o docker irá subir os contêineres necessários para rodar a aplicação.

Ainda no terminal, rode o comando

```bash
./mvnw spring-boot:run
```

Este comando irá inicializar a aplicação e permitir com que seja feito acesso aos seus endpoints.

Para visualizar as rotas e a documentação (swagger) da API, acesse:

http://localhost:8082/api/v1/swagger-ui/index.html#/

<h1>🪢Testes</h1>

Para rodar os testes, você pode inserir no terminal o comando:

```bash 
./mvnw clean test
```

Assim, todos os testes irão rodar e você verá em algum tempo no terminal o resultado deles.

<h1>Tecnologias utilizadas</h1>

- Java/Springboot
- Postgres como banco de dados
- Documentação com swagger.
- Validação dos dados da requisição com o Bean Validation
- Contêinerização com docker.

