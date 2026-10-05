---
title: "Como Rodar o Coupon API"
description: "Guia completo de build, execução e troubleshooting da aplicação Coupon API"
tags: ["build", "maven", "spring-boot", "java", "run", "execute"]
---

# Skill: Como Rodar o Coupon API

## Resumo

Este skill fornece instruções passo a passo para compilar, executar e acessar a Coupon API, um projeto Spring Boot que gerencia cupons com soft delete.

## Pré-requisitos

- ✅ **Java 17+** (recomendado: OpenJDK 21.0.2)
- ✅ **Maven 3.9.6+**
- ✅ **Conexão com internet** (para download de dependências)

Verificar versões:
```powershell
java -version
mvn -version
```

## Procedimento Completo

### 1️⃣ Build do Projeto

Execute na raiz do projeto:

```powershell
mvn clean package
```

**O que cada fase faz:**
- `clean` - Remove build anterior
- `package` - Compila, testa e cria JAR

**Tempo esperado:** 2-5 minutos (primeira vez pode levar mais)

**Arquivo gerado:** `target/coupon-api-0.0.1-SNAPSHOT.jar`

### 2️⃣ Executar a Aplicação

```powershell
java -jar target/coupon-api-0.0.1-SNAPSHOT.jar
```

**A aplicação está pronta quando você vê:**
```
Tomcat started on port 8080 (http) with context path '/'
Started CouponApplication in X.XX seconds
```

### 3️⃣ Acessar a API

A aplicação estará disponível em: **http://localhost:8080**

**Principais endpoints:**

| Recurso | URL | Descrição |
|---------|-----|-----------|
| Swagger UI | http://localhost:8080/swagger-ui.html | Interface interativa |
| OpenAPI JSON | http://localhost:8080/v3/api-docs | Especificação |
| H2 Console | http://localhost:8080/h2-console | Banco de dados |

## Operações Comuns

### ➕ Criar um cupom
```bash
curl -X POST http://localhost:8080/coupons \
  -H "Content-Type: application/json" \
  -d '{
    "code": "SUMMER20",
    "description": "20% off summer collection",
    "discountValue": 20.00,
    "expirationDate": "2025-09-30T23:59:59Z",
    "published": true
  }'
```

### 🔍 Buscar um cupom
```bash
curl http://localhost:8080/coupons/{id}
```

### 🗑️ Deletar um cupom (soft delete)
```bash
curl -X DELETE http://localhost:8080/coupons/{id}
```

## Configuração

Arquivo: `src/main/resources/application.yml`

**Mudar porta (padrão: 8080):**
```yaml
server:
  port: 8081
```

**Mudar banco de dados:**
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/coupondb
    username: root
    password: secret
```

## Troubleshooting

### ❌ "mvn is not recognized"

**Solução 1:** Instalar Maven globalmente
- Download: https://maven.apache.org/download.cgi
- Adicionar `\bin` ao PATH do Windows

**Solução 2:** Usar Maven local
```powershell
& "$env:USERPROFILE\maven\apache-maven-3.9.6\bin\mvn.cmd" clean package
```

### ❌ "Address already in use"

**Problema:** Porta 8080 já está em uso

**Solução:**
1. Matar processo na porta: `netstat -ano | findstr :8080`
2. Ou mudar porta em `application.yml`

### ❌ "Could not find or load main class"

**Problema:** JAR não encontrado

**Solução:**
1. Verificar se build completou: `ls target/coupon-api*.jar`
2. Usar caminho absoluto: `java -jar "C:\Users\Baronny\Downloads\coupon-api\coupon-api\target\coupon-api-0.0.1-SNAPSHOT.jar"`

### ❌ Testes falhando no build

**Solução:** Pular testes (não recomendado)
```powershell
mvn clean package -DskipTests
```

## Arquitetura do Projeto

O projeto segue **Clean Architecture**:

```
domain/          → Lógica de negócio pura (sem dependências)
application/     → Use cases e portas (interfaces)
infra/           → Implementações (controllers, persistência)
```

## Soft Delete

Ao deletar um cupom:
- Status muda de `ACTIVE` → `DELETED`
- Campo `deletedAt` registra o timestamp
- Dados são preservados no banco
- Operação é idempotente (não permite deletar 2x)

## Parar a Aplicação

Pressione: **`Ctrl + C`** no terminal

## Docker (opcional)

```powershell
# Build imagem
docker build -t coupon-api .

# Rodar container
docker run -p 8080:8080 coupon-api

# Ou usar docker-compose
docker-compose up
```

## Recursos Adicionais

- **Documentação OpenAPI**: Gerada automaticamente via SpringDoc
- **Banco H2**: Em memória, ideal para testes/desenvolvimento
- **Testes**: Execute `mvn test` para rodar suite de testes

## Checklist de Execução ✅

- [ ] Java 17+ instalado
- [ ] Maven 3.9.6+ instalado
- [ ] Build concluído com sucesso (`BUILD SUCCESS`)
- [ ] Aplicação iniciada (sem erros nos logs)
- [ ] Swagger acessível em http://localhost:8080/swagger-ui.html
- [ ] Conseguir fazer uma requisição à API

---

**Última atualização:** 2026-10-04
**Versão:** 1.0
