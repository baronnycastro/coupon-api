# 🚀 Como Rodar o Coupon API

## ⚙️ Pré-requisitos

- **Java**: versão 17 ou superior (testado com OpenJDK 21.0.2)
- **Maven**: versão 3.9.6 ou superior

## 📦 Build da Aplicação

Compile o projeto Maven com o seguinte comando:

```powershell
# No diretório raiz do projeto
mvn clean package
```

Ou, se Maven não estiver no PATH:

```powershell
# Usando Maven instalado localmente
& "$env:USERPROFILE\maven\apache-maven-3.9.6\bin\mvn.cmd" clean package
```

**O que acontece:**
- ✅ Limpeza de builds anteriores
- ✅ Download de dependências
- ✅ Compilação do código-fonte
- ✅ Execução dos testes
- ✅ Empacotamento em JAR executável

**Output esperado:** `[INFO] BUILD SUCCESS`

**Arquivo gerado:** `target/coupon-api-0.0.1-SNAPSHOT.jar`

---

## ▶️ Executar a Aplicação

Inicie a aplicação com:

```powershell
java -jar target/coupon-api-0.0.1-SNAPSHOT.jar
```

Ou com caminho absoluto:

```powershell
java -jar "c:\Users\Baronny\Downloads\coupon-api\coupon-api\target\coupon-api-0.0.1-SNAPSHOT.jar"
```

**Indicadores de sucesso:**
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

 :: Spring Boot :: (v3.3.5)

... (logs de inicialização) ...

Started CouponApplication in X.XX seconds
```

---

## 🌐 Endpoints Disponíveis

A aplicação está pronta para uso em `http://localhost:8080`

| Recurso | URL |
|---------|-----|
| **Documentação Swagger/OpenAPI** | http://localhost:8080/swagger-ui.html |
| **Especificação OpenAPI (JSON)** | http://localhost:8080/v3/api-docs |
| **Console H2 Database** | http://localhost:8080/h2-console |

### Acessar Swagger
1. Abra o navegador
2. Acesse: `http://localhost:8080/swagger-ui.html`
3. Você verá todos os endpoints REST disponíveis
4. Pode testar diretamente da interface

---

## 🗄️ Banco de Dados

**Configuração:**
- **Tipo**: H2 (em memória)
- **JDBC URL**: `jdbc:h2:mem:coupondb`
- **Usuário**: `sa`
- **Senha**: (deixar em branco)
- **Console**: http://localhost:8080/h2-console

**Automático:**
- Tabelas criadas automaticamente na inicialização (Hibernate DDL)
- Dados limpos ao reiniciar a aplicação

---

## 🛑 Parar a Aplicação

Pressione **`Ctrl+C`** no terminal onde a aplicação está rodando.

---

## 🔧 Troubleshooting

### Maven não encontrado
```
mvn : The term 'mvn' is not recognized...
```
**Solução:**
- Instalar Maven: https://maven.apache.org/download.cgi
- Ou usar: `& "$env:USERPROFILE\maven\apache-maven-3.9.6\bin\mvn.cmd"`

### Porta 8080 já em uso
```
Address already in use
```
**Solução:** Mudar porta em `src/main/resources/application.yml`
```yaml
server:
  port: 8081  # Mudar para outra porta disponível
```

### Build falha
```
[ERROR] COMPILATION ERROR
```
**Solução:**
1. Verificar se Java 17+ está instalado: `java -version`
2. Limpar cache Maven: `mvn clean`
3. Verificar conexão com internet (download de dependências)

---

## 📋 Estrutura do Projeto

```
coupon-api/
├── src/
│   ├── main/
│   │   ├── java/com/coupon/
│   │   │   ├── domain/           # Lógica de negócio (aggregates, value objects)
│   │   │   ├── application/      # Use cases e portas
│   │   │   ├── infra/            # Persistência, controllers, configuração
│   │   │   └── CouponApplication.java
│   │   └── resources/
│   │       └── application.yml   # Configurações
│   └── test/                     # Testes unitários e integração
├── pom.xml                       # Dependências Maven
├── Dockerfile                    # Para executar em container
└── docker-compose.yml           # Orquestração de serviços
```

---

## 🐳 Executar com Docker (opcional)

```powershell
docker build -t coupon-api .
docker run -p 8080:8080 coupon-api
```

Ou com docker-compose:
```powershell
docker-compose up
```
