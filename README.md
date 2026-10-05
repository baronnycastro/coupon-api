# Coupon API

API REST para cadastro (**create**) e remoção (**soft delete**) de cupons — Java 17, Spring Boot 3, H2 em memória, Swagger e Docker.

## Como rodar

```bash
# local (Java 17+ e Maven)
mvn spring-boot:run

# testes + relatório de cobertura (target/site/jacoco/index.html)
mvn verify

# docker
docker compose up --build
```

- Swagger UI: http://localhost:8080/swagger-ui.html
- H2 console: http://localhost:8080/h2-console (`jdbc:h2:mem:coupondb`, user `sa`, sem senha)

## Endpoints

| Método | Rota            | Sucesso            | Erros                                              |
|--------|-----------------|--------------------|----------------------------------------------------|
| POST   | `/coupon`       | `201` + cupom      | `400` regra de negócio violada / body inválido     |
| GET    | `/coupon/{id}`  | `200` + cupom      | `404` não encontrado, `400` id inválido            |
| DELETE | `/coupon/{id}`  | `204` sem corpo    | `404` não encontrado, `409` já deletado            |

## Arquitetura (hexagonal / orientada a objetos)

```
com.coupon
├── domain                    Regras de negócio, sem dependências de framework.
│   ├── model                  Agregado Coupon e enum CouponStatus
│   ├── valueobject            CouponCode e DiscountValue
│   └── exception              Exceções de domínio
├── application               Orquestração dos casos de uso.
│   ├── port/in                Contratos de entrada: CreateCoupon, GetCoupon, DeleteCoupon
│   ├── port/out               Contrato de saída: CouponRepository
│   ├── usecase                Implementações dos casos de uso
│   ├── dto                    Commands e outputs da aplicação
│   └── exception              Exceções da aplicação
├── adapter                   Integrações com entrada e saída.
│   ├── in/web                 API HTTP, requests/responses e tratamento de erros
│   └── out/persistence        Adapter JPA, entidade, mapper e Spring Data
└── bootstrap                 Configuração Spring e montagem dos beans, incluindo Clock
```

- O adapter HTTP depende das **portas de entrada**, não das implementações dos casos de uso.
- Os casos de uso dependem da porta de saída `CouponRepository`; o adapter JPA a implementa.
- O fluxo de criação/deleção orquestra domínio e persistência; as regras de negócio ficam no agregado e nos *value objects*.
- A entidade JPA é separada do modelo de domínio (`CouponJpaEntity` ↔ `Coupon`) e a conversão é feita por `CouponPersistenceMapper`.
- A camada `application` não tem anotações Spring; os beans e o `Clock` são montados em `bootstrap/UseCaseConfig`.
- `ArchitectureTest` (ArchUnit) verifica o isolamento de `domain` e `application`, a dependência do adapter web nas portas de entrada e que cada UseCase expõe apenas `execute`.

## Regras de negócio → testes

| Regra | Onde é testada |
|-------|----------------|
| Obrigatórios: code, description, discountValue, expirationDate | `CouponTest`, `CouponApiIntegrationTest#rejectsMissingRequiredFields`, `#rejectsNullRequiredFields` e `#rejectsBlankRequiredTextFields` |
| Código com 6 caracteres; especiais removidos antes de salvar e retornar | `CouponTest`, `CouponApiIntegrationTest#sanitizesCodeBeforeReturningAndPersisting` |
| Desconto mínimo 0,5, sem máximo | `CouponTest`, `CouponApiIntegrationTest#acceptsMinimumDiscount` e `#acceptsLargeDiscountWithoutBusinessMaximum` |
| Nunca criar com expiração no passado | `CouponTest`, `CreateCouponUseCaseTest`, `CouponApiIntegrationTest#rejectsExpirationDateInThePast` |
| Pode ser criado já publicado | `CouponTest`, `CreateCouponUseCaseTest`, `CouponApiIntegrationTest#createsAlreadyPublishedCoupon` |
| Delete a qualquer momento (soft delete, dados preservados) | `CouponTest`, `DeleteCouponUseCaseTest`, `CouponApiIntegrationTest#deleteReturns204AndKeepsTheDataInTheDatabase` e `#canDeleteAnExpiredCouponThroughEndpoint` |
| Não deletar duas vezes | `CouponTest`, `DeleteCouponUseCaseTest`, `CouponApiIntegrationTest#cannotDeleteTheSameCouponTwice` |

Os testes de integração usam H2 real (sem mocks); os de UseCase usam um *fake* em memória da porta, não um mock.

## Decisões

- Violação de regra de negócio na criação → `400`; deletar cupom já deletado → `409`.
- `GET` de cupom deletado retorna `200` com `status: DELETED` (o soft delete preserva o registro).
- `discountValue` usa `BigDecimal` (sem arredondamento) e é serializado sem notação científica.
