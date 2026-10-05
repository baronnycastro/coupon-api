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
├── domain            Regras de negócio. Java puro, sem framework.
│   ├── Coupon              agregado: create(...) e delete(...)
│   ├── CouponCode          VO: alfanumérico, 6 chars, remove caracteres especiais
│   ├── DiscountValue       VO: mínimo 0,5, sem máximo
│   └── exceptions          InvalidCoupon / CouponAlreadyDeleted
├── application       Orquestração. Só depende de interfaces (portas).
│   ├── port/CouponRepository   porta de saída
│   └── Create/Get/DeleteCouponUseCase   um único método público: execute
└── infra             Detalhes técnicos (pode importar Spring/JPA)
    ├── web             controller fino, DTOs, tradução de exceções em HTTP
    ├── persistence     entidade JPA (≠ domínio), mapper, adapter da porta
    └── config          montagem dos beans, Clock, OpenAPI
```

- O **UseCase não tem `if` de negócio**: busca, chama o domínio e salva.
- A **entidade JPA é separada do domínio** (`CouponJpaEntity` ↔ `Coupon`).
- A camada `application` não tem anotações Spring; os beans são montados em `infra/config`.
- `ArchitectureTest` (ArchUnit) garante que `domain`/`application` não importam Spring, JPA ou `infra`, e que cada UseCase expõe só `execute`.

## Regras de negócio → testes

| Regra | Onde é testada |
|-------|----------------|
| Obrigatórios: code, description, discountValue, expirationDate | `CouponTest`, `CouponApiIntegrationTest#rejectsMissingRequiredFields` |
| Código com 6 caracteres; especiais removidos antes de salvar e retornar | `CouponTest`, `CreateCouponUseCaseTest`, `CouponApiIntegrationTest` |
| Desconto mínimo 0,5, sem máximo | `CouponTest`, `CouponApiIntegrationTest` |
| Nunca criar com expiração no passado | `CouponTest`, `CreateCouponUseCaseTest`, `CouponApiIntegrationTest` |
| Pode ser criado já publicado | `CouponTest`, `CreateCouponUseCaseTest`, `CouponApiIntegrationTest` |
| Delete a qualquer momento (soft delete, dados preservados) | `CouponTest`, `DeleteCouponUseCaseTest`, `CouponApiIntegrationTest#deleteReturns204AndKeepsTheDataInTheDatabase` |
| Não deletar duas vezes | `CouponTest`, `DeleteCouponUseCaseTest`, `CouponApiIntegrationTest#cannotDeleteTheSameCouponTwice` |

Os testes de integração usam H2 real (sem mocks); os de UseCase usam um *fake* em memória da porta, não um mock.

## Decisões

- Violação de regra de negócio na criação → `400`; deletar cupom já deletado → `409`.
- `GET` de cupom deletado retorna `200` com `status: DELETED` (o soft delete preserva o registro).
- `discountValue` usa `BigDecimal` (sem arredondamento) e é serializado sem notação científica.
