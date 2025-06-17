# Cadastro e Consulta - Projeto Spring Boot

## Descrição
API RESTful para cadastro e consulta de produtos desenvolvida com Spring Boot.  
Recursos disponíveis:
- CRUD completo de produtos
- Consulta paginada
- Validação de dados

## Tecnologias utilizadas
- **Linguagem**: Java 8
- **Framework**: Spring Boot 3.x
- **Build**: Maven
- **Banco de dados**: MySQL
- **ORM**: JPA/Hibernate

## Como rodar o projeto localmente

### Pré-requisitos
- Java JDK 8 instalado
- Maven instalado ou usar o wrapper (`./mvnw`)
- Banco de dados configurado e rodando
- IDE (recomendado: IntelliJ ou VS Code)

## Configuração e Execução

1. **Configure o banco de dados**:
   - Crie um banco MySQL (ex: `cadastro_db`)
   - Edite `src/main/resources/application.properties`:
     ```properties
     spring.datasource.url=jdbc:mysql://localhost:3306/cadastro_db?useSSL=false&serverTimezone=UTC
     spring.datasource.username=seu_usuario
     spring.datasource.password=sua_senha
     spring.jpa.hibernate.ddl-auto=update
     ```

2. **Execute o projeto**:
   - Via IDE: Execute a classe principal `Application`
   - Via terminal:
     ```bash
     ./mvnw spring-boot:run
     # Ou no Windows:
     mvnw.cmd spring-boot:run
     ```

3. **Acesse a aplicação**:
   - A API estará disponível em: http://localhost:8080
   - Endpoints disponíveis:
     - GET /produtos - Lista todos os produtos
     - POST /produtos - Cria novo produto
     - GET /produtos/{id} - Busca produto por ID
     - PUT /produtos/{id} - Atualiza produto
     - DELETE /produtos/{id} - Remove produto
