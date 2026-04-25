# Testes - VendeMais CRM Backend

## Objetivo

Documentar a suíte de testes automatizados do backend do VendeMais CRM, cobrindo regras de negócio, segurança, controllers HTTP, configuração e entidades de domínio.

## Stack de testes

- **JUnit 5**: execução dos testes.
- **Mockito**: mocks de services, repositories e dependências.
- **AssertJ**: assertions fluentes nos testes unitários.
- **MockMvc**: simulação de requisições HTTP nos controllers.
- **Spring Security Test**: simulação de usuários autenticados e perfis de acesso.
- **Spring Boot Test**: validação de contexto da aplicação.
- **H2 Database**: banco local usado no profile de demonstração/testes.

## Como executar

```bash
mvn clean test
```

Com Maven Wrapper:

```bash
./mvnw clean test
```

## Estrutura dos testes

```text
src/test/java/br/com/vendemais
├── config
├── controller
├── domain/entity
├── service
└── support
```

## Quantidade atual

A suíte atual possui **55 testes automatizados** distribuídos entre testes de configuração, controllers, services, entidades e suporte de segurança.

## Tipos de teste

### 1. Testes de configuração

| Classe | Objetivo |
|---|---|
| `DemoApplicationTests` | Verifica se o contexto Spring carrega corretamente. |
| `InstanciacaoBDDemoTest` | Verifica se a inicialização demo delega para o `DbSeeder`. |
| `SecurityConfigTest` | Valida CORS, métodos HTTP permitidos, origem permitida e ausência de credenciais por cookie. |

### 2. Testes de controllers HTTP

| Classe | Cenários cobertos |
|---|---|
| `AuthControllerHttpTest` | `/auth/me`, usuário autenticado, não autenticado e usuário não encontrado. |
| `LeadControllerTest` | Criação de lead, validação de payload e busca inexistente. |
| `OpportunityControllerTest` | Criação de oportunidade, payload inválido, regra de negócio e checagem de oportunidade aberta. |
| `PipelineControllerHttpTest` | Permissões ADMIN/USER para pipelines e stages. |
| `TaskControllerTest` | Criação de tasks por lead ou oportunidade, validação de vínculo único, payload inválido e exclusão. |
| `UserControllerHttpTest` | Criação, atualização, exclusão e bloqueios por autenticação/permissão. |

### 3. Testes de services

| Classe | Cenários cobertos |
|---|---|
| `OpportunityServiceTest` | Criação válida, stage fora do pipeline, fallback para primeira stage e verificação de oportunidade aberta. |
| `PipelineServiceTest` | Criação, título duplicado e conflito ao excluir pipeline em uso. |
| `StageServiceTest` | Criação, pipeline inexistente, stage fora do pipeline e bloqueio de alteração do código técnico. |

### 4. Testes de domínio

| Classe | Cenários cobertos |
|---|---|
| `UserTest` | Role padrão `USER`, adição de `ADMIN` e mapeamento para authorities do Spring Security. |
| `TaskServiceTest` | Comportamento da entidade `Task`: vínculo com lead, vínculo com oportunidade e alteração de status/dados básicos. |

> Observação: apesar do nome `TaskServiceTest`, essa classe testa a entidade `Task`. Caso seja renomeada futuramente, o nome recomendado é `TaskTest`.

### 5. Suporte de testes

| Classe | Função |
|---|---|
| `ControllerTestSupport` | Montagem de `MockMvc` com handlers globais e suporte JSON. |
| `MockMvcSecurityConfig` | Configuração auxiliar para testes HTTP com Spring Security. |
| `TestAuthentications` | Criação de usuários autenticados fake com roles `USER` e `ADMIN`. |
| `TestReflectionUtils` | Utilitário para definir IDs em entidades durante testes unitários. |

## Contrato de erros validado

| Cenário | Status esperado |
|---|---:|
| Payload inválido | `400 Bad Request` |
| Token ausente ou inválido | `401 Unauthorized` |
| Usuário autenticado sem permissão | `403 Forbidden` |
| Recurso inexistente | `404 Not Found` |
| Duplicidade ou recurso em uso | `409 Conflict` |
| Violação de regra de negócio | `422 Unprocessable Entity` |

## Regras de negócio cobertas

- Login/autenticação via JWT e recuperação do usuário autenticado.
- Diferenciação entre usuário autenticado e usuário com role `ADMIN`.
- Criação de leads com validação de campos obrigatórios.
- Criação de oportunidades com validação de pipeline e stage.
- Rejeição de stage que não pertence ao pipeline selecionado.
- Inicialização automática da oportunidade na primeira stage quando `currentStageId` é nulo.
- Verificação de oportunidades abertas usando `won = false` e `closedAt = null`.
- Criação de tasks vinculadas a exatamente um registro comercial: lead ou opportunity.
- Rejeição de task sem vínculo comercial.
- Rejeição de task vinculada simultaneamente a lead e opportunity.
- Proteção de endpoints administrativos por role `ADMIN`.
- Bloqueio de alteração do código técnico de uma stage.
- Tratamento de conflitos de dados e regras de negócio com status REST adequados.

## Convenções adotadas

- Testes de controller validam status HTTP, payload de erro e headers relevantes.
- Testes de service focam regra de negócio e interação com repositories mockados.
- Testes de domínio validam comportamento interno das entidades.
- Helpers de autenticação evitam repetição de configuração de segurança nos testes HTTP.
- O profile de demonstração usa H2 para simplificar avaliação e execução local.

## Próximos pontos de melhoria

- Renomear `TaskServiceTest` para `TaskTest`, pois o escopo atual é entidade.
- Expandir testes unitários para `LeadService`, `TaskService`, `UserService` e `MarketingLeadIntegrationService`.
- Adicionar testes específicos para `ResourceExceptionHandler`.
- Adicionar testes para importação de leads via API externa usando mock do client HTTP.
