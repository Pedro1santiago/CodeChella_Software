# 🎭 CodeChella - Sistema de Gerenciamento de Eventos

<div align="center">

[![Deploy](https://img.shields.io/badge/Deploy-Live-success?style=for-the-badge)](https://codechella-five-sigma.vercel.app)
[![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.0+-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![React](https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)](https://reactjs.org/)

**🌐 [Acesse a Aplicação ao Vivo](https://codechella-five-sigma.vercel.app)**

Sistema completo de gerenciamento de eventos com controle de permissões em três níveis, desenvolvido com Spring WebFlux para máxima performance e escalabilidade.

### 🚀 Hospedagem

![Render](https://img.shields.io/badge/Backend-Render-46E3B7?style=for-the-badge&logo=render&logoColor=white)
![Vercel](https://img.shields.io/badge/Frontend-Vercel-000000?style=for-the-badge&logo=vercel&logoColor=white)
![Neon](https://img.shields.io/badge/Database-Neon-00E699?style=for-the-badge&logo=postgresql&logoColor=white)

**⚡ Keep Alive ativo** - Aplicação mantida sempre online para resposta imediata!

</div>

---

## 📋 Sobre o Projeto

**CodeChella** é uma plataforma robusta para gerenciamento de eventos e ingressos, inspirada em grandes festivais de música. O sistema implementa um controle sofisticado de permissões com três níveis hierárquicos de usuários, cada um com responsabilidades e acessos específicos.

### 🎯 Principais Funcionalidades

- ✅ **Sistema de Permissões Multi-nível** - 3 tipos de usuários com diferentes privilégios
- ✅ **Gerenciamento de Eventos** - Criação, edição e exclusão de eventos
- ✅ **Sistema de Ingressos** - Compra e cancelamento de ingressos
- ✅ **Isolamento de Dados** - Cada admin gerencia apenas seus próprios eventos
- ✅ **Fluxo de Aprovação** - Solicitação e aprovação para escalação de privilégios
- ✅ **API Reativa** - Construída com Spring WebFlux para alta performance
- ✅ **Real-time Updates** - Server-Sent Events (SSE) para atualizações em tempo real

---

## 🛠️ Stack Tecnológica

### Backend
![Java](https://img.shields.io/badge/Java_17-ED8B00?style=flat-square&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=flat-square&logo=spring&logoColor=white)
![Spring WebFlux](https://img.shields.io/badge/Spring_WebFlux-6DB33F?style=flat-square&logo=spring&logoColor=white)
![R2DBC](https://img.shields.io/badge/R2DBC-6DB33F?style=flat-square&logo=spring&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=flat-square&logo=postgresql&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-CC0200?style=flat-square&logo=flyway&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=flat-square&logo=apache-maven&logoColor=white)

### Frontend
![React](https://img.shields.io/badge/React-20232A?style=flat-square&logo=react&logoColor=61DAFB)
![TypeScript](https://img.shields.io/badge/TypeScript-007ACC?style=flat-square&logo=typescript&logoColor=white)

### Hospedagem & Infraestrutura
![Render](https://img.shields.io/badge/Render-46E3B7?style=flat-square&logo=render&logoColor=white)
![Vercel](https://img.shields.io/badge/Vercel-000000?style=flat-square&logo=vercel&logoColor=white)
![Neon](https://img.shields.io/badge/Neon-00E699?style=flat-square&logo=postgresql&logoColor=white)

### Arquitetura
![REST API](https://img.shields.io/badge/REST_API-FF6C37?style=flat-square&logo=postman&logoColor=white)
![Reactive](https://img.shields.io/badge/Reactive-6DB33F?style=flat-square&logo=spring&logoColor=white)
![SSE](https://img.shields.io/badge/Server_Sent_Events-FF6C37?style=flat-square&logo=html5&logoColor=white)

---

## 👥 Hierarquia de Usuários

### 🎫 Usuário Normal (USER)

O nível básico de acesso, perfeito para participantes de eventos.

**Permissões:**
- 👀 Visualizar todos os eventos disponíveis
- 🔍 Filtrar eventos por categoria (Shows, Teatro, Esportes, etc)
- 🎟️ Comprar ingressos para eventos
- ❌ Cancelar ingressos já comprados
- 📝 Solicitar permissão para se tornar Administrador

**Restrições:**
- ❌ Não pode criar eventos
- ❌ Não pode gerenciar outros usuários
- ❌ Não pode aprovar permissões

---

### 🎭 Administrador (ADMIN)

Gestores de eventos com controle sobre suas próprias criações.

**Permissões:**
- ✅ Todas as permissões do Usuário Normal
- 🎨 Criar novos eventos
- 📊 Definir quantidade de ingressos disponíveis
- ✏️ Atualizar seus próprios eventos
- 🗑️ Excluir apenas eventos que criou
- 👥 Pesquisar e visualizar usuários
- 📈 Gerenciar status dos eventos (aberto/fechado)

**Isolamento de Dados:**
- 🔒 **Cada admin só pode modificar ou excluir seus próprios eventos**
- 🏢 **Exemplo:** Se "Nike" cria um evento, "Adidas" não pode excluí-lo
- 🛡️ **Proteção total contra interferência entre administradores**

**Restrições:**
- ❌ Não pode excluir eventos de outros admins
- ❌ Não pode aprovar solicitações de permissão
- ❌ Não pode promover/rebaixar usuários

---

### 👑 Super Admin (SUPER)

Controle total sobre toda a plataforma.

**Permissões:**
- ✅ Todas as permissões do Administrador
- 🔓 Excluir QUALQUER evento (sem restrições)
- ✅ Aprovar solicitações de permissão
- ❌ Negar solicitações de permissão (com justificativa)
- ⬆️ Promover usuários normais para Administrador
- ⬇️ Rebaixar administradores para usuário normal
- 🗑️ Remover usuários do sistema
- 🗑️ Remover administradores do sistema
- 👨‍💼 Criar novos administradores diretamente
- 📋 Visualizar todas as solicitações de permissão
- 🎯 Acesso irrestrito a todas as funcionalidades

---

## 🔐 Fluxo de Autorização

### 📈 De Usuário Normal → Administrador

```
┌─────────────────────────────────────────────┐
│  1. Usuário faz login                       │
│     POST /auth/usuario/login                │
└──────────────────┬──────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────┐
│  2. Solicita permissão para Admin           │
│     POST /permissoes/solicitar              │
│     Header: usuario-id: 1                   │
│     Status: PENDENTE                        │
└──────────────────┬──────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────┐
│  3. Super Admin visualiza solicitação       │
│     GET /permissoes/pendentes               │
│     Header: super-admin-id: 10              │
└──────────────────┬──────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────┐
│  4. Super Admin aprova                      │
│     PUT /permissoes/1/aprovar               │
│     Status: APROVADO                        │
└──────────────────┬──────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────┐
│  5. Usuário automaticamente promovido       │
│     tipoUsuario: USER → ADMIN               │
│     Agora pode criar e gerenciar eventos!   │
└─────────────────────────────────────────────┘
```

---

## 🎯 Casos de Uso Práticos

### 💼 Cenário 1: Produtor de Eventos
**Maria é produtora da Nike Events**

1. 📝 Maria cria conta como usuária normal
2. 📨 Solicita permissão para administrador
3. ✅ Super Admin aprova
4. 🎭 Maria cria evento "Nike Music Festival"
5. 🎟️ Define 500 ingressos disponíveis
6. 👥 Usuários compram ingressos
7. ✏️ Maria atualiza descrição do evento
8. 🗑️ Se necessário, Maria pode excluir seu evento

**Proteção:** Outros admins (Adidas, Puma) não podem excluir ou modificar o evento da Nike.

---

### 🎫 Cenário 2: Fã de Música
**João quer ir a um festival**

1. 📝 João cria conta como usuário normal
2. 👀 Navega pelos eventos disponíveis
3. 🔍 Filtra por "SHOW"
4. 🎟️ Compra 2 ingressos para "Nike Music Festival"
5. ❌ Se necessário, pode cancelar os ingressos

**Restrição:** João não pode criar eventos ou modificar dados da plataforma.

---

### 👑 Cenário 3: Gestão da Plataforma
**Carlos é Super Admin**

1. 📋 Visualiza todas as solicitações de permissão pendentes
2. ✅ Aprova Maria (Nike) para ser admin
3. ❌ Nega Pedro (sem justificativa clara)
4. 🗑️ Remove evento inapropriado de qualquer admin
5. ⬇️ Rebaixa admin que violou regras
6. 👥 Gerencia todos os usuários da plataforma

**Poder Total:** Carlos tem acesso irrestrito a todas as funcionalidades.

---

## 📊 Matriz de Permissões Completa

| Funcionalidade | 🎫 USER | 🎭 ADMIN | 👑 SUPER |
|---|:---:|:---:|:---:|
| Ver eventos | ✅ | ✅ | ✅ |
| Filtrar eventos | ✅ | ✅ | ✅ |
| Comprar ingressos | ✅ | ❌ | ❌ |
| Cancelar ingressos | ✅ | ❌ | ❌ |
| Solicitar permissão | ✅ | ❌ | ❌ |
| Criar evento | ❌ | ✅ | ✅ |
| Editar seu evento | ❌ | ✅ | ✅ |
| Excluir seu evento | ❌ | ✅ | ✅ |
| Excluir evento alheio | ❌ | ❌ | ✅ |
| Pesquisar usuários | ❌ | ✅ | ✅ |
| Aprovar permissões | ❌ | ❌ | ✅ |
| Negar permissões | ❌ | ❌ | ✅ |
| Promover usuário | ❌ | ❌ | ✅ |
| Rebaixar admin | ❌ | ❌ | ✅ |
| Remover usuários | ❌ | ❌ | ✅ |

---

## 🚀 Como Executar

### Pré-requisitos

- Java 17+
- PostgreSQL 14+
- Maven 3.8+
- Node.js 18+ (para o frontend)

### Backend

```bash
# Clone o repositório
git clone https://github.com/Pedro1santiago/CodeChella_Software_PROJECT.git

# Entre na pasta do projeto
cd CodeChella_Software_PROJECT/codechella

# Configure o banco de dados no application.properties
# spring.r2dbc.url=r2dbc:postgresql://localhost:5432/codechella
# spring.r2dbc.username=seu_usuario
# spring.r2dbc.password=sua_senha

# Execute as migrations (Flyway fará automaticamente)
mvn clean install

# Execute a aplicação
mvn spring-boot:run
```

A API estará disponível em `http://localhost:8080`

### Frontend

Acesse a aplicação em produção: **[https://codechella-five-sigma.vercel.app](https://codechella-five-sigma.vercel.app)**

---

## 📡 Principais Endpoints

### Autenticação
```http
POST /auth/usuario/register        # Registrar usuário
POST /auth/usuario/login           # Login usuário
POST /auth/super-admin/login       # Login super admin
```

### Eventos
```http
GET    /eventos                    # Listar todos eventos
GET    /eventos/{id}               # Buscar evento por ID
GET    /eventos/categoria/{tipo}   # Filtrar por categoria
POST   /eventos                    # Criar evento (Admin/Super)
PUT    /eventos/{id}               # Atualizar evento
DELETE /eventos/{id}               # Excluir evento (seu próprio)
```

### Ingressos
```http
GET  /ingressos                    # Listar ingressos
POST /ingressos/comprar            # Comprar ingressos (User)
PUT  /ingressos/cancelar/{id}      # Cancelar ingresso
```

### Permissões
```http
POST /permissoes/solicitar              # Solicitar Admin (User)
GET  /permissoes/minhas-solicitacoes    # Ver suas solicitações
GET  /permissoes/pendentes              # Listar pendentes (Super)
PUT  /permissoes/{id}/aprovar           # Aprovar (Super)
PUT  /permissoes/{id}/negar             # Negar (Super)
```

### Super Admin
```http
GET    /super-admin/listar/admins       # Listar admins
GET    /super-admin/listar/usuarios     # Listar usuários
DELETE /super-admin/eventos/{id}        # Excluir qualquer evento
PUT    /super-admin/promover/admin/{id} # Promover para admin
PUT    /super-admin/rebaixar/user/{id}  # Rebaixar para user
```

---

## 💾 Estrutura do Banco de Dados

### Principais Tabelas

- **usuario** - Usuários normais do sistema
- **usuario_admin** - Administradores de eventos
- **super_admin** - Super administradores
- **eventos** - Eventos cadastrados (com id_admin_criador)
- **ingressos** - Ingressos disponíveis/vendidos
- **solicitacao_permissao** - Histórico de solicitações

### Migrations

O projeto usa Flyway para versionamento do banco:

- `V001` - Criação da tabela eventos
- `V002` - Inserção de eventos iniciais
- `V003` - Criação da tabela ingressos
- `V004` - Criação da tabela usuario_admin
- `V005` - Modificações na tabela eventos
- `V006` - Criação da tabela usuario
- `V007` - Criação da tabela super_admin
- `V008` - Atualização usuario_admin
- `V009` - Rastreamento de criador em eventos
- `V010` - Tabela de solicitações de permissão
- `V011` - Inserção de super admin de teste

---

## 🔒 Segurança e Isolamento

### Validações Implementadas

✅ **Verificação de Tipo de Usuário** - Cada endpoint valida o tipo de usuário
✅ **Isolamento de Dados** - Admin só gerencia seus eventos
✅ **Headers Personalizados** - usuario-id, admin-id, super-admin-id
✅ **Validação de Propriedade** - Verifica se admin é dono do evento
✅ **Fluxo de Aprovação** - Solicitações devem ser aprovadas
✅ **Status de Permissão** - PENDENTE → APROVADO/NEGADO

### Exemplo de Isolamento

```java
// Admin só pode excluir seus próprios eventos
if (userAdmin.getTipoUsuario() == TipoUsuario.ADMIN 
    && !evento.getIdAdminCriador().equals(userAdmin.getIdUsuario())) {
    return Mono.error(new ResponseStatusException(
        HttpStatus.FORBIDDEN,
        "Você só pode excluir eventos que criou"
    ));
}
```

---

## 🎨 Tipos de Eventos Suportados

- 🎸 **SHOW** - Shows e festivais de música
- 🎭 **TEATRO** - Peças teatrais e apresentações
- ⚽ **ESPORTE** - Eventos esportivos
- 🎬 **CINEMA** - Sessões especiais e estreias
- 🎤 **STAND_UP** - Shows de comédia
- 🎨 **EXPOSICAO** - Exposições e galerias

---

## 📈 Diferenciais Técnicos

### 🚀 Programação Reativa
- **Spring WebFlux** - Processamento não-bloqueante
- **R2DBC** - Acesso reativo ao banco de dados
- **Mono e Flux** - Streams reativos assíncronos
- **Backpressure** - Controle de fluxo de dados

### ⚡ Performance
- **Server-Sent Events (SSE)** - Updates em tempo real
- **Connection Pooling** - Gerenciamento eficiente de conexões
- **Non-blocking I/O** - Máxima concorrência
- **Async Processing** - Operações assíncronas

### 🏗️ Arquitetura
- **RESTful API** - Padrões REST consolidados
- **DTO Pattern** - Separação de camadas
- **Repository Pattern** - Abstração de dados
- **Service Layer** - Lógica de negócio isolada

---

## 🤝 Contribuindo

Contribuições são bem-vindas! Sinta-se à vontade para:

1. 🍴 Fork o projeto
2. 🌿 Crie uma branch para sua feature (`git checkout -b feature/MinhaFeature`)
3. 💾 Commit suas mudanças (`git commit -m 'Adiciona MinhaFeature'`)
4. 📤 Push para a branch (`git push origin feature/MinhaFeature`)
5. 🎉 Abra um Pull Request

---

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo `LICENSE` para mais detalhes.

---

## 👨‍💻 Autor

**Pedro Santiago**

[![GitHub](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/Pedro1santiago)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/pedro-santiago)

---

<div align="center">



**[⬆ Voltar ao topo](#-codechella---sistema-de-gerenciamento-de-eventos)**

</div>
