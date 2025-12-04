# 🚀 Liferay - Sistema de Gerenciamento de Eventos

Sistema completo de gerenciamento de eventos desenvolvido com Spring Boot, oferecendo APIs REST para criação de eventos, autenticação de usuários, gerenciamento de perfis e inscrições.

## 📋 Índice

- [Tecnologias](#-tecnologias)
- [Funcionalidades](#-funcionalidades)
- [Configuração](#-configuração)
- [Endpoints da API](#-endpoints-da-api)
- [Segurança](#-segurança)
- [Estrutura do Projeto](#-estrutura-do-projeto)
- [Deploy](#-deploy)

## 🛠️ Tecnologias

- **Java 17**
- **Spring Boot 3.3.5**
- **Spring Security** - Autenticação JWT
- **Spring Data JPA** - Persistência de dados
- **PostgreSQL** - Banco de dados em produção
- **JWT (JSON Web Token)** - Autenticação stateless
- **Maven** - Gerenciamento de dependências
- **Hibernate** - ORM

## ✨ Funcionalidades

### 🔐 Autenticação e Usuários
- ✅ Login com JWT (usuário/email + senha)
- ✅ Registro de novos usuários
- ✅ Tokens com expiração de 5 horas
- ✅ Senhas criptografadas com BCrypt
- ✅ Criação automática de perfil ao registrar

### 👤 Perfis de Usuário
- ✅ Visualizar perfil próprio
- ✅ Atualizar informações do perfil
- ✅ Upload de foto de perfil
- ✅ Gerenciamento de habilidades
- ✅ Visualizar perfis públicos de outros usuários
- ✅ Busca global por nome, título e habilidades

### 🎉 Eventos
- ✅ Criação de eventos (autenticado)
- ✅ Listagem de eventos futuros
- ✅ Busca de evento por ID
- ✅ Busca por nome, descrição ou categoria
- ✅ Visualização de eventos criados pelo usuário
- ✅ Exclusão de eventos (apenas criador)
- ✅ Campos: nome, descrição, data, hora, local, categoria e vagas

### 📝 Inscrições
- ✅ Inscrição de usuários em eventos
- ✅ Validação de vagas disponíveis
- ✅ Verificação de inscrições duplicadas
- ✅ Cancelamento de inscrições
- ✅ Listagem de minhas inscrições
- ✅ Histórico de eventos passados
- ✅ Atualização automática de vagas

### 📧 Contato
- ✅ Formulário de contato corporativo
- ✅ Envio de mensagens (público)

## 🔧 Configuração

### Pré-requisitos
- JDK 17 ou superior
- PostgreSQL 12+ (produção) ou MySQL 8.0+ (desenvolvimento)
- Maven 3.6+

### Variáveis de Ambiente

Configure as seguintes variáveis de ambiente:

```bash
# Banco de Dados
DB_URL=jdbc:postgresql://seu-host:5432/seu-banco
DB_USER=seu_usuario
DB_PASSWORD=sua_senha

# JWT (opcional - tem valor padrão)
JWT_SECRET=sua_chave_secreta_aqui

# Porta (opcional)
PORT=8080
```

### Instalação Local

1. Clone o repositório:
```bash
git clone https://github.com/seu-usuario/eventos-liferay.git
cd eventos-liferay/eventos
```

2. Configure o banco de dados em `application.properties` (para desenvolvimento local):
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/liferay
spring.datasource.username=seu_usuario
spring.datasource.password=sua_senha
```

3. Compile o projeto:
```bash
./mvnw clean install
```

4. Execute a aplicação:
```bash
./mvnw spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`

## 📡 Endpoints da API

### 🔓 Públicos (Sem autenticação)

#### Autenticação

**Login**
```http
POST /api/auth/login
Content-Type: application/json

{
  "login": "teste@email.com",
  "senha": "123456"
}
```

**Resposta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tipo": "Bearer",
  "id": 1,
  "nomeUsuario": "testuser",
  "email": "teste@email.com"
}
```

**Registro**
```http
POST /api/auth/register
Content-Type: application/json

{
  "nomeUsuario": "novousuario",
  "email": "novo@email.com",
  "senha": "senha123",
  "nomeCompleto": "Novo Usuário",
  "titulo": "Desenvolvedor",
  "sobreMim": "Apaixonado por tecnologia",
  "habilidades": ["Java", "Spring Boot"]
}
```

#### Eventos

**Listar Eventos Futuros**
```http
GET /api/eventos
```

**Buscar Evento por ID**
```http
GET /api/eventos/{id}
```

#### Perfis

**Buscar Perfis Globalmente**
```http
GET /api/perfis/buscar?q=java&filtro=habilidades
```

Filtros disponíveis:
- `todos` - Busca em nome, título e habilidades (padrão)
- `usuarios` - Busca apenas em nome e título
- `habilidades` - Busca apenas em habilidades
- `eventos` - Busca em eventos

**Ver Perfil Público**
```http
GET /api/perfis/usuario/{usuarioId}
```

#### Contato

**Enviar Mensagem**
```http
POST /api/contato/enviar
Content-Type: application/json

{
  "nome": "João",
  "sobrenome": "Silva",
  "email": "joao@empresa.com",
  "telefone": "+5581999999999",
  "pais": "Brasil",
  "areaTrabalho": "Tecnologia",
  "motivo": "Interesse em parceria"
}
```

### 🔒 Protegidos (Requer autenticação)

**Header obrigatório:**
```http
Authorization: Bearer {seu_token_jwt}
```

#### Perfil do Usuário

**Meu Perfil**
```http
GET /api/perfis/me
```

**Atualizar Perfil**
```http
PUT /api/perfis/me
Content-Type: application/json

{
  "nomeCompleto": "Nome Completo Atualizado",
  "titulo": "Desenvolvedor Senior",
  "sobreMim": "Descrição atualizada",
  "habilidades": ["Java", "Spring", "Docker"]
}
```

**Upload de Foto**
```http
POST /api/perfis/foto
Content-Type: multipart/form-data

file: [arquivo de imagem]
```

#### Eventos

**Criar Evento**
```http
POST /api/eventos/criar
Content-Type: application/json

{
  "nome": "Workshop de Spring Boot",
  "descricao": "Aprenda Spring Boot na prática",
  "data": "2025-12-15",
  "hora": "14:00:00",
  "local": "Auditório Principal",
  "categoria": "Tecnologia",
  "vagas": 50
}
```

**Meus Eventos**
```http
GET /api/eventos/meus
```

**Deletar Evento**
```http
DELETE /api/eventos/{id}
```

#### Inscrições

**Inscrever-se em Evento**
```http
POST /api/inscricoes/eventos/{id}/inscrever
```

**Cancelar Inscrição**
```http
DELETE /api/inscricoes/eventos/{id}/cancelar
```

**Verificar Status**
```http
GET /api/inscricoes/eventos/{id}/status
```

**Minhas Inscrições Ativas**
```http
GET /api/inscricoes/minhas-inscricoes
```

**Histórico de Eventos**
```http
GET /api/inscricoes/historico
```

## 🔐 Segurança

### CORS
Configurado para aceitar requisições de qualquer origem (`*`) para facilitar integração com front-ends.

### JWT
- **Algoritmo:** HS256
- **Validade:** 5 horas (18000000 ms)
- **Chave secreta:** Configurável via variável de ambiente

### Endpoints Públicos
- `/` - Health check
- `/api/test` - Teste de API
- `/health` - Status da aplicação
- `/api/auth/**` - Autenticação
- `/api/contato/**` - Formulário de contato
- `GET /api/eventos/**` - Listagem de eventos
- `GET /api/perfis/buscar` - Busca global
- `GET /api/perfis/usuario/{id}` - Perfis públicos
- `GET /fotos/**` - Arquivos de imagem

### Endpoints Protegidos
Todos os demais endpoints requerem token JWT válido no header `Authorization: Bearer {token}`.

## 👥 Usuários de Teste

A aplicação cria automaticamente dois usuários ao iniciar:

| Usuário | Email | Senha | Nome Completo |
|---------|-------|-------|---------------|
| testuser | teste@email.com | 123456 | Usuário de Teste |
| jorgeuser | jorge@email.com | 12345 | Jorge da Silva |

## 📁 Estrutura do Projeto

```
eventos/
├── src/main/java/com/eventos/eventos/
│   ├── config/
│   │   ├── JwtRequestFilter.java      # Filtro de autenticação JWT
│   │   ├── JwtTokenUtil.java          # Utilitário para tokens
│   │   ├── MvcConfig.java             # Configuração de recursos estáticos
│   │   └── SecurityConfig.java        # Configuração de segurança
│   ├── controller/
│   │   ├── AuthController.java        # Login e registro
│   │   ├── ContatoController.java     # Formulário de contato
│   │   ├── EventoController.java      # CRUD de eventos
│   │   ├── InscricaoPerfilController.java  # Gerenciamento de inscrições
│   │   ├── PerfilController.java      # Gerenciamento de perfis
│   │   └── TestController.java        # Endpoints de teste
│   ├── dto/
│   │   ├── LoginRequest.java
│   │   ├── LoginResponse.java
│   │   ├── RegisterDto.java
│   │   ├── PerfilUpdateDto.java
│   │   ├── MensagemDTO.java
│   │   └── ResultadoBuscaDTO.java
│   ├── model/
│   │   ├── Usuario.java               # Entidade de usuário
│   │   ├── Perfil.java                # Entidade de perfil
│   │   ├── Evento.java                # Entidade de evento
│   │   ├── Inscricao.java             # Entidade de inscrição
│   │   └── Contato.java               # Entidade de contato
│   ├── repository/
│   │   ├── UsuarioRepository.java
│   │   ├── PerfilRepository.java
│   │   ├── EventoRepository.java
│   │   ├── InscricaoRepository.java
│   │   └── ContatoRepository.java
│   ├── service/
│   │   ├── UserDetailsServiceImpl.java  # Serviço de autenticação
│   │   ├── FileStorageService.java      # Upload de arquivos
│   │   └── ContatoService.java          # Serviço de contato
│   └── EventosApplication.java        # Classe principal
├── src/main/resources/
│   └── application.properties         # Configurações da aplicação
└── pom.xml                            # Dependências Maven
```

## 🚀 Deploy

### Render (Recomendado)

1. Crie um novo Web Service no Render
2. Conecte seu repositório GitHub
3. Configure as variáveis de ambiente:
   - `DB_URL`
   - `DB_USER`
   - `DB_PASSWORD`
   - `JWT_SECRET` (opcional)
4. Build Command: `./mvnw clean install -DskipTests`
5. Start Command: `java -jar target/eventos-0.0.1-SNAPSHOT.jar`

### Heroku

1. Instale o Heroku CLI
2. Execute os comandos:
```bash
heroku create seu-app-eventos
heroku addons:create heroku-postgresql:hobby-dev
heroku config:set JWT_SECRET=sua_chave_secreta
git push heroku main
```

## 📝 Notas Importantes

- **Uploads de Arquivo:** Os arquivos são salvos localmente em `uploads/fotos/`. Para produção, considere usar um serviço de armazenamento em nuvem (AWS S3, Cloudinary, etc.)
- **CORS:** Configurado para aceitar qualquer origem (`*`). Para produção, especifique as origens permitidas.
- **JWT Secret:** Altere a chave secreta JWT antes de fazer deploy em produção.
- **Banco de Dados:** Use PostgreSQL em produção para melhor compatibilidade e performance.

## 🤝 Contribuindo

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

## 👨‍💻 Autor

Desenvolvido como projeto de sistema completo de gerenciamento de eventos e perfis profissionais.

## 📞 Suporte

Para dúvidas ou problemas, abra uma issue no GitHub ou entre em contato através do formulário de contato da aplicação.

---

**⚠️ Importante:** Lembre-se de alterar as credenciais do banco de dados e a chave secreta JWT antes de fazer deploy em produção!
