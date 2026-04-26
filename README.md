<details open>
<summary>🇧🇷 Português</summary>

# VendeMais CRM

Backend de CRM comercial desenvolvido com Spring Boot para gerenciar usuários, leads, oportunidades, funis, etapas e tarefas, com autenticação JWT, documentação OpenAPI e ambiente de demonstração com H2.

## Visão geral da arquitetura

O projeto segue uma arquitetura monolítica em camadas, com responsabilidades separadas entre:

- `controller`: exposição dos endpoints REST e contratos HTTP
- `service`: regras de negócio e orquestração dos fluxos
- `repository`: acesso a dados com Spring Data JPA
- `domain/entity`: modelo de domínio persistido
- `domain/dtos`: contratos de entrada, saída e filtros
- `security`: autenticação, autorização e filtros JWT
- `config`: configuração de segurança, OpenAPI e carga demo
- `infrastructure`: integração externa para importação de leads

## Stack principal

| Camada | Tecnologias |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 3 |
| Segurança | Spring Security, JWT, BCrypt |
| Persistência | Spring Data JPA, H2 Database |
| Documentação | Swagger / OpenAPI (`springdoc-openapi`) |
| Testes | JUnit 5, Mockito, MockMvc, Spring Security Test |

## Principais módulos

- `Users`: gestão de contas e perfis de acesso
- `Leads`: cadastro e consulta de potenciais clientes
- `Opportunities`: acompanhamento de negociações comerciais
- `Pipelines`: definição de funis comerciais
- `Stages`: etapas pertencentes aos pipelines
- `Tasks`: atividades de follow-up ligadas a leads ou oportunidades
- `Marketing Lead Integration`: importação simulada de leads externos

## Regras de negócio importantes

- Oportunidade aberta: `won = false` e `closedAt = null`
- Oportunidade ganha: `won = true`
- Oportunidade perdida: `won = false` e `closedAt != null`
- Oportunidade fechada não pode ser atualizada pelo fluxo geral de atualização
- Task deve pertencer a exatamente um vínculo comercial: `Lead` ou `Opportunity`
- O usuário autenticado da task é obtido do JWT/contexto de segurança, não do payload
- Usuário não pode excluir a própria conta
- Ao criar uma oportunidade sem `currentStageId`, o sistema usa a primeira etapa do pipeline
- Integração de marketing ignora leads duplicados por e-mail

## Segurança

- Autenticação stateless com JWT
- Login via endpoint público `POST /login`
- Token retornado no header `Authorization` no formato `Bearer <token>`
- Senhas armazenadas com `BCrypt`
- Perfis de acesso: `USER` e `ADMIN`
- `401 Unauthorized`: token ausente, inválido ou expirado, ou credenciais inválidas
- `403 Forbidden`: usuário autenticado sem permissão para o recurso
- Endpoints administrativos e de integração são protegidos por roles

## Tratamento de erros REST

| Status | Quando ocorre |
|---|---|
| `400 Bad Request` | payload inválido ou parâmetro inválido |
| `401 Unauthorized` | requisição não autenticada ou token inválido/expirado |
| `403 Forbidden` | usuário sem permissão |
| `404 Not Found` | recurso não encontrado |
| `409 Conflict` | duplicidade, conflito de dados ou recurso em uso |
| `422 Unprocessable Entity` | regra de negócio violada |

## Como rodar o projeto localmente

### Pré-requisitos

- Java 21
- Maven 3.9+ ou Maven Wrapper

### 1. Defina as variáveis de ambiente

Use valores seguros de exemplo. Não reutilize segredos reais em ambiente local ou compartilhado.

```bash
SPRING_PROFILES_ACTIVE=demo
JWT_SECRET=your-base64-secret-here
JWT_EXPIRATION=86400000
DB_URL=jdbc:h2:mem:vendemais;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
DB_USERNAME=sa
DB_PASSWORD=change-me
RANDOM_USER_API_URL=https://randomuser.me
RANDOM_USER_API_PATH=/api/
```

### 2. Execute a aplicação

Com Maven:

```bash
mvn spring-boot:run
```

Com Maven Wrapper:

```bash
./mvnw spring-boot:run
```

No Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

### 3. Acesse os recursos locais

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- H2 Console: `http://localhost:8080/h2-console`

### Observações sobre o profile `demo`

- O profile `demo` é o perfil esperado para avaliação local
- Nesse perfil, a base é populada automaticamente com dados de demonstração
- O banco configurado para demonstração é H2

## Variáveis de ambiente esperadas

| Variável | Obrigatória | Exemplo seguro | Finalidade |
|---|---|---|---|
| `SPRING_PROFILES_ACTIVE` | Sim | `demo` | seleciona o profile ativo |
| `JWT_SECRET` | Sim | `your-base64-secret-here` | segredo JWT em Base64 |
| `JWT_EXPIRATION` | Sim | `86400000` | tempo de expiração do token em ms |
| `DB_URL` | Sim | `jdbc:h2:mem:vendemais;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE` | URL do banco |
| `DB_USERNAME` | Sim | `sa` | usuário do banco |
| `DB_PASSWORD` | Sim | `change-me` | senha do banco |
| `RANDOM_USER_API_URL` | Não | `https://randomuser.me` | URL base da API externa |
| `RANDOM_USER_API_PATH` | Não | `/api/` | path da API externa |

## Como rodar os testes

```bash
mvn clean test
```

Com Maven Wrapper:

```bash
./mvnw clean test
```

## Documentação adicional

- Guia da suíte de testes: [docs/TESTING.md](docs/TESTING.md)

## Observação sobre o uso do H2

O H2 foi escolhido para facilitar avaliação técnica, demonstração local e execução rápida sem dependências externas. Em um ambiente de produção real, o banco pode ser substituído por PostgreSQL ou MySQL, mantendo a mesma organização em camadas e a estratégia de persistência com JPA.

</details>

<details>
<summary>🇺🇸 English</summary>

# VendeMais CRM

A Spring Boot CRM backend for managing users, leads, opportunities, pipelines, stages, and tasks, with JWT authentication, OpenAPI documentation, and an H2-based demo environment.

## Architecture overview

The project follows a layered monolithic architecture with responsibilities split across:

- `controller`: REST endpoints and HTTP contracts
- `service`: business rules and application flows
- `repository`: data access with Spring Data JPA
- `domain/entity`: persisted domain model
- `domain/dtos`: input, output, and filter contracts
- `security`: authentication, authorization, and JWT filters
- `config`: security, OpenAPI, and demo bootstrap configuration
- `infrastructure`: external integration used for lead import simulation

## Main stack

| Layer | Technologies |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3 |
| Security | Spring Security, JWT, BCrypt |
| Persistence | Spring Data JPA, H2 Database |
| Documentation | Swagger / OpenAPI (`springdoc-openapi`) |
| Testing | JUnit 5, Mockito, MockMvc, Spring Security Test |

## Core modules

- `Users`: account and access profile management
- `Leads`: prospect registration and lookup
- `Opportunities`: commercial negotiation tracking
- `Pipelines`: sales funnel definitions
- `Stages`: pipeline stages
- `Tasks`: follow-up activities linked to leads or opportunities
- `Marketing Lead Integration`: simulated external lead import

## Important business rules

- Open opportunity: `won = false` and `closedAt = null`
- Won opportunity: `won = true`
- Lost opportunity: `won = false` and `closedAt != null`
- A closed opportunity cannot be updated through the general update flow
- A task must belong to exactly one commercial link: `Lead` or `Opportunity`
- The task owner comes from the authenticated JWT/security context, not from the payload
- A user cannot delete their own account
- When an opportunity is created without `currentStageId`, the system falls back to the pipeline's first stage
- Marketing integration skips duplicated leads by email

## Security

- Stateless JWT authentication
- Public login endpoint: `POST /login`
- Token returned in the `Authorization` header as `Bearer <token>`
- Passwords are stored with `BCrypt`
- Access roles: `USER` and `ADMIN`
- `401 Unauthorized`: missing, invalid, or expired token, or invalid credentials
- `403 Forbidden`: authenticated user without permission
- Administrative and integration endpoints are role-protected

## REST error handling

| Status | Typical meaning |
|---|---|
| `400 Bad Request` | invalid payload or invalid parameter |
| `401 Unauthorized` | unauthenticated request or invalid/expired token |
| `403 Forbidden` | insufficient permission |
| `404 Not Found` | resource not found |
| `409 Conflict` | duplicated data, data conflict, or resource in use |
| `422 Unprocessable Entity` | business rule violation |

## Running locally

### Prerequisites

- Java 21
- Maven 3.9+ or Maven Wrapper

### 1. Define environment variables

Use safe placeholder values. Do not reuse real secrets in local or shared environments.

```bash
SPRING_PROFILES_ACTIVE=demo
JWT_SECRET=your-base64-secret-here
JWT_EXPIRATION=86400000
DB_URL=jdbc:h2:mem:vendemais;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
DB_USERNAME=sa
DB_PASSWORD=change-me
RANDOM_USER_API_URL=https://randomuser.me
RANDOM_USER_API_PATH=/api/
```

### 2. Start the application

With Maven:

```bash
mvn spring-boot:run
```

With Maven Wrapper:

```bash
./mvnw spring-boot:run
```

On Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

### 3. Open local resources

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- H2 Console: `http://localhost:8080/h2-console`

### Notes about the `demo` profile

- `demo` is the expected profile for local evaluation
- In this profile, the database is automatically seeded with demo data
- The demo database uses H2

## Expected environment variables

| Variable | Required | Safe example | Purpose |
|---|---|---|---|
| `SPRING_PROFILES_ACTIVE` | Yes | `demo` | selects the active profile |
| `JWT_SECRET` | Yes | `your-base64-secret-here` | Base64-encoded JWT secret |
| `JWT_EXPIRATION` | Yes | `86400000` | token expiration in ms |
| `DB_URL` | Yes | `jdbc:h2:mem:vendemais;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE` | database URL |
| `DB_USERNAME` | Yes | `sa` | database username |
| `DB_PASSWORD` | Yes | `change-me` | database password |
| `RANDOM_USER_API_URL` | No | `https://randomuser.me` | external API base URL |
| `RANDOM_USER_API_PATH` | No | `/api/` | external API path |

## Running tests

```bash
mvn clean test
```

With Maven Wrapper:

```bash
./mvnw clean test
```

## Additional documentation

- Test suite guide: [docs/TESTING.md](docs/TESTING.md)

## Note about H2

H2 was chosen to make technical evaluation, local demos, and quick startup easier without external infrastructure. In a real production environment, it could be replaced by PostgreSQL or MySQL while keeping the same layered design and JPA-based persistence approach.

</details>
