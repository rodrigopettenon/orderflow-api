# OrderFlow API – Sistema de Gestão de Clientes, Produtos, Pedidos e Itens de Pedido

API RESTful desenvolvida em Java com Spring Boot para realizar cadastro, consulta, filtros dinâmicos, paginação e ordenação de clientes, produtos, pedidos e itens de pedido.

O projeto adota uma arquitetura em camadas, utilizando EntityManager com consultas nativas, boas práticas de desenvolvimento, tratamento centralizado de erros e logs personalizados.

---

## Recursos Disponíveis

- CRUD completo de Produtos
- CRUD completo de Clientes
- Cadastro e consulta de Pedidos
- Cadastro e consulta de Itens de Pedido
- Consultas com filtros dinâmicos
- Paginação e ordenação
- Consultas com INNER JOIN trazendo detalhes completos dos itens, pedidos, produtos e clientes
- Validações manuais com retorno de erros personalizados
- Tratamento centralizado de exceções
- Logs personalizados

---

## Tecnologias Utilizadas

- Linguagem: Java 8
- Framework: Spring Boot 3.x
- Persistência: JPA com uso de EntityManager (consultas nativas SQL)
- Banco de Dados: MySQL (compatível com SQL Server)
- Build: Maven
- Ferramentas: IntelliJ IDEA, Git, Postman, MySQL Workbench

---

## Como Executar o Projeto Localmente

### Pré-requisitos

- Java JDK 8 instalado
- Maven instalado (ou utilização do Maven Wrapper `./mvnw`)
- MySQL instalado e em execução localmente
- IDE de sua preferência (IntelliJ IDEA recomendado)

### Configuração

1. Criar um banco de dados no MySQL, por exemplo: `cadastro_db`.

2. Configurar o arquivo de propriedades localizado em:

```
src/main/resources/application.properties
```

Exemplo de configuração:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/cadastro_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=none
```

Observação: As tabelas devem ser criadas manualmente no banco de dados.

### Execução

- Executar pela IDE: Rodar a classe principal `CadastroEConsultaApplication.java`.
- Executar pelo terminal:

Linux/macOS:
```
./mvnw spring-boot:run
```
Windows:
```
mvnw.cmd spring-boot:run
```

A aplicação estará disponível em:
```
http://localhost:8080
```

---

## Endpoints Disponíveis

Clientes (/clients)
POST /save
Cadastra um novo cliente.

GET /email/{email}
Busca cliente por e-mail.

GET /cpf/{cpf}
Busca cliente por CPF.

GET /all
Retorna todos os clientes com paginação e ordenação.
Parâmetros: page, linesPerPage, direction, orderBy

GET /filter
Busca clientes por filtros.
Parâmetros: name, email, cpf, birthStart, birthEnd, page, linesPerPage, direction, orderBy

PUT /update/{cpf}
Atualiza os dados de um cliente com base no CPF.

DELETE /delete/{cpf}
Deleta um cliente com base no CPF.

Produtos (/products)
POST /save
Cadastra um novo produto.

GET /sku?sku={sku}
Busca produto por SKU.

GET /all
Retorna todos os produtos com paginação e ordenação.
Parâmetros: page, linesPerPage, direction, orderBy

GET /filter
Busca produtos por filtros.
Parâmetros: name, sku, minPrice, maxPrice, page, linesPerPage, direction, orderBy

PUT /update?sku={sku}
Atualiza os dados de um produto com base no SKU.

DELETE /delete?sku={sku}
Deleta um produto com base no SKU.

Pedidos (/orders)
POST /save
Cadastra um novo pedido.

GET /id?id={id}
Busca um pedido por ID.

GET /filter
Busca pedidos com base em filtros.
Parâmetros: id, clientId, dateTimeStart, dateTimeEnd, status, page, linesPerPage, direction, orderBy

GET /details
Busca pedidos com detalhes (quantidade mínima/máxima de itens, status etc).
Parâmetros: orderId, clientId, dateTimeStart, dateTimeEnd, minQuantity, maxQuantity, status, page, linesPerPage, direction, orderBy

PUT /update?id={id}&status={status}
Atualiza o status de um pedido pelo ID.

Itens de Pedido (/item-orders)
POST /save
Cadastra um novo item de pedido.

GET /filter
Busca itens de pedido com base em filtros.
Parâmetros: id, orderId, productId, minQuantity, maxQuantity, page, linesPerPage, direction, orderBy

GET /full-details
Busca itens de pedido com detalhes (cliente, pedido, produto etc).
Parâmetros: itemOrderId, productId, orderId, clientId, page, linesPerPage, direction, orderBy

---

## Arquitetura e Boas Práticas

- Organização do código em camadas: Controller, Service, Repository, DTOs e Exceptions
- Persistência de dados utilizando EntityManager e consultas nativas SQL
- Validações manuais aplicadas nos serviços, com mensagens de erro personalizadas
- Implementação de filtros dinâmicos, paginação e ordenação
- Tratamento global de exceções através de um Global Exception Handler

---

## Melhorias Futuras

- Implementação de testes unitários e de integração
- Documentação interativa utilizando Swagger/OpenAPI
- Implementação de autenticação e controle de acesso
- Geração de relatórios e exportação de dados
- Consultas agregadas e estatísticas (ex.: total de pedidos por cliente)

---

## Repositório

Acesse o projeto no GitHub:
[github.com/rodrigopettenon/orderflow-api](https://github.com/rodrigopettenon/orderflow-api)
