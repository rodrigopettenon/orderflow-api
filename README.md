# OrderFlow API

Sistema completo de gestão de clientes, produtos, pedidos e itens de pedido, com filtros dinâmicos, paginação e ordenação.

API RESTful desenvolvida em Java com Spring Boot, utilizando EntityManager e consultas nativas, que permite realizar cadastro, consulta, atualização e remoção de clientes, produtos, pedidos e itens de pedido. Oferece suporte a filtros dinâmicos, paginação, ordenação e tratamento centralizado de exceções.

O projeto adota uma arquitetura em camadas, utilizando EntityManager com consultas nativas, boas práticas de desenvolvimento, tratamento centralizado de erros e logs personalizados.

---

## Recursos Disponíveis

- CRUD completo de Produtos
- CRUD completo de Clientes
- Cadastro e consulta de Pedidos
- Cadastro e consulta de Itens de Pedido
- Consultas com filtros dinâmicos, paginação, ordenação e JOINs para retornar dados completos dos relacionamentos
- Validações manuais com retorno de erros personalizados
- Tratamento centralizado de exceções
- Logs personalizados

---

### Tecnologias Utilizadas
- Java 17
- Spring Boot 3.x
- JPA (com consultas nativas via EntityManager)
- MySQL (compatível com SQL Server)
- Maven
- IntelliJ IDEA, Git, Postman, MySQL Workbench

---

### Como Executar o Projeto Localmente

#### Pré-requisitos
- Java 17
- Maven
- MySQL
- IDE (recomendado: IntelliJ IDEA)

#### Configuração
1. Crie o banco `cadastro_db` no MySQL.
2. Configure o arquivo `application.properties`:

Exemplo de configuração:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/cadastro_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=none
```


Observação: As tabelas devem ser criadas manualmente no banco de dados.


3. **Execute o projeto:**
- Via IDE: rode `OrderFlowApplication.java`

# Linux/macOS
./mvnw spring-boot:run

# Windows
mvnw.cmd spring-boot:run

A aplicação estará disponível em:
http://localhost:8080

---

### Endpoints Disponíveis

#### Clientes (`/clients`)

- **POST** `/save`  
  Cadastra um novo cliente.

- **GET** `/email/{email}`  
  Busca cliente por e-mail.

- **GET** `/cpf/{cpf}`  
  Busca cliente por CPF.

- **GET** `/all`  
  Retorna todos os clientes com paginação e ordenação.  
  **Parâmetros:** `page`, `linesPerPage`, `direction`, `orderBy`

- **GET** `/filter`  
  Busca clientes por filtros.  
  **Parâmetros:** `name`, `email`, `cpf`, `birthStart`, `birthEnd`, `page`, `linesPerPage`, `direction`, `orderBy`

- **GET** `/search-by-identifier`  
  Busca cliente de forma flexível por ID, nome, e-mail ou CPF.  
  **Parâmetros:** `identifier`, `page`, `linesPerPage`, `direction`, `orderBy`

- **PUT** `/update/{cpf}`  
  Atualiza os dados de um cliente com base no CPF.

- **DELETE** `/delete/{cpf}`  
  Deleta um cliente com base no CPF.

---

#### Produtos (`/products`)

- **POST** `/save`  
  Cadastra um novo produto.

- **GET** `/sku?sku={sku}`  
  Busca produto por SKU.

- **GET** `/all`  
  Retorna todos os produtos com paginação e ordenação.  
  **Parâmetros:** `page`, `linesPerPage`, `direction`, `orderBy`

- **GET** `/filter`  
  Busca produtos por filtros.  
  **Parâmetros:** `name`, `sku`, `minPrice`, `maxPrice`, `page`, `linesPerPage`, `direction`, `orderBy`

- **PUT** `/update?sku={sku}`  
  Atualiza os dados de um produto com base no SKU.

- **DELETE** `/delete?sku={sku}`  
  Deleta um produto com base no SKU.

---

#### Pedidos (`/orders`)

- **POST** `/save`  
  Cadastra um novo pedido.  
  **Body:** `OrderDto` (JSON com dados do pedido)

- **GET** `/id`  
  Busca um pedido por ID.  
  **Parâmetro:** `id` (UUID do pedido)

- **GET** `/filter`  
  Busca pedidos com filtros.  
  **Parâmetros:** `id`, `clientId`, `dateTimeStart`, `dateTimeEnd`, `status`, `page`, `linesPerPage`, `direction`, `orderBy`

- **GET** `/details`  
  Busca pedidos com detalhes (quantidade mínima/máxima de itens, status etc).  
  **Parâmetros:** `orderId`, `clientId`, `dateTimeStart`, `dateTimeEnd`, `minQuantity`, `maxQuantity`, `status`, `page`, `linesPerPage`, `direction`, `orderBy`

- **GET** `/relevant-data`  
  Busca dados relevantes de pedidos.  
  **Parâmetros:** `clientId`, `dateTimeStart`, `dateTimeEnd`, `status`, `page`, `linesPerPage`, `direction`, `orderBy`

- **GET** `/client-sales-report`  
  Gera relatório de vendas por cliente.  
  **Parâmetros:** `dateTimeStart`, `dateTimeEnd`, `minOrder`, `maxOrder`, `status`, `page`, `linesPerPage`, `direction`, `orderBy`

- **PUT** `/update`  
  Atualiza o status de um pedido.  
  **Parâmetros:** `id` (UUID do pedido), `status` (novo status)

---

#### Itens de Pedido (`/item-orders`)

- **POST** `/save`  
  Cadastra um novo item de pedido.

- **GET** `/filter`  
  Busca itens de pedido com base em filtros.  
  **Parâmetros:** `id`, `orderId`, `productId`, `minQuantity`, `maxQuantity`, `page`, `linesPerPage`, `direction`, `orderBy`

- **GET** `/full-details`  
  Busca itens de pedido com detalhes (cliente, pedido, produto etc).  
  **Parâmetros:** `itemOrderId`, `productId`, `orderId`, `clientId`, `page`, `linesPerPage`, `direction`, `orderBy`

---

## Arquitetura e Boas Práticas

- Organização do código em camadas: Controller, Service, Repository, DTOs e Exceptions
- Persistência de dados utilizando EntityManager e consultas nativas SQL
- Validações manuais aplicadas nos serviços, com mensagens de erro personalizadas
- Implementação de filtros dinâmicos, paginação e ordenação
- Tratamento global de exceções através de um Global Exception Handler
- Cobertura completa de testes unitários em todos os métodos das camadas Service e Repository
---

### Melhorias Futuras
- Documentação interativa com Swagger/OpenAPI
- Autenticação e controle de acesso (JWT ou OAuth2)
- Relatórios e exportação de dados
- Dashboards com estatísticas agregadas (ex.: pedidos por cliente, valor total vendido)

---

## Repositório

🔗 [Acesse o projeto no GitHub](https://github.com/rodrigopettenon/orderflow-api)
