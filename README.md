# Pedágio Audit System

Sistema de auditoria de planilhas de pedágio composto por dois serviços e um banco PostgreSQL.

## Arquitetura

```
Cliente --(multipart: file)--> audit-system (Spring Boot, :8080)
                                  |  1. grava Report (PENDING) no PostgreSQL
                                  |  2. POST /audit (multipart) --> python-service (FastAPI, :8000)
                                  |  3. grava resultado (PROCESSED) ou erro (ERROR)
                                  v
                              PostgreSQL (:5432)
```

- `audit-system/`: API Java (Spring Boot, JPA, WebClient). Recebe o arquivo, persiste o relatório e delega a análise ao serviço Python.
- `python-service/`: motor de auditoria (FastAPI + pandas). Lê a planilha e compara `valor_cobrado` com `valor_esperado`; se as colunas existirem, retorna total de linhas, inconsistências, valor divergente total e detalhes. Caso contrário, retorna apenas a contagem.
- `database/`: `docker-compose.yml` com PostgreSQL 15.

## Pré-requisitos

Java 17+ (Maven wrapper incluso), Python 3.10+, Docker e Docker Compose.

## Como subir

1. Banco de dados:
   ```bash
   cd database
   docker compose up -d
   ```
2. Serviço Python:
   ```bash
   cd python-service
   python -m venv venv && source venv/bin/activate   # Windows: venv\Scripts\activate
   pip install -r requirements.txt
   uvicorn main:app --port 8000
   ```
3. Serviço Java:
   ```bash
   cd audit-system
   ./mvnw spring-boot:run
   ```

## Endpoints

| Serviço | Método e rota | Descrição |
|---|---|---|
| audit-system | `POST /api/reports/upload` (campo `file`) | Cria o relatório, aciona a auditoria e devolve o `Report` (`id`, `fileName`, `uploadDate`, `status`, `analysisResult`) |
| python-service | `POST /audit` (campo `file`) | Audita `.xlsx`/`.xls`/`.csv`; responde 400 para outros formatos |

Status do relatório: `PENDING`, `PROCESSED`, `ERROR`.

Exemplo: `curl -F "file=@planilha.xlsx" http://localhost:8080/api/reports/upload`

## Configuração

As propriedades de `audit-system/src/main/resources/application.properties` e o `docker-compose.yml` leem variáveis de ambiente, com valores padrão apenas para desenvolvimento local:

| Variável | Padrão | Uso |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/audit_system` | URL JDBC (Spring) |
| `DB_USER` | `user_audit` | Usuário do banco (Spring e Compose) |
| `DB_PASSWORD` | `dev-password` (somente dev) | Senha do banco (Spring e Compose) |
| `DB_NAME` | `audit_system` | Nome do banco (Compose) |
| `PYTHON_SERVICE_URL` | `http://127.0.0.1:8000` | URL do serviço Python |

Defina as mesmas `DB_USER`/`DB_PASSWORD` ao subir o Compose e o Spring (use um `.env`, ignorado pelo git). Em ambientes reais nunca use o padrão de desenvolvimento. Se o volume `postgres_data` já existir, a senha antiga continua valendo até recriá-lo (`docker compose down -v`).

## Limitações

- A auditoria Python usa `read_excel`; arquivos `.csv` passam na validação, mas falham no processamento.
- Sem autenticação nos endpoints; `ddl-auto=update` e `show-sql=true` são adequados só para desenvolvimento.
- A chamada ao Python é síncrona (bloqueante).
