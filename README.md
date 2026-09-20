# 🎯 Mix CS Balancer

> Aplicação web full-stack desenvolvida com **Spring Boot** e **WebSockets** para gerenciar lobbies de partidas customizadas (Mix / PCW de CS2), automatizar o ecossistema de jogadores em tempo real e realizar o **balanceamento inteligente e justo de 2 times (5v5)**.

---

## 🚀 Sobre o Projeto

O **Mix CS Balancer** nasceu para resolver o problema clássico das partidas entre amigos: criar times desequilibrados, gerenciar o lobby manualmente e perder tempo decidindo quem joga com quem. 

A aplicação entrega uma experiência fluida e em tempo real (via WebSockets), onde os jogadores entram no lobby, o sistema processa os dados de forma assíncrona, valida as regras de negócio de ponta a ponta e gera dois times perfeitamente equilibrados com base em algoritmos de combinação.

---

## 🛠️ Tecnologias e Arquitetura

O projeto foi construído seguindo boas práticas de mercado e padrões de arquitetura corporativa:

* **Linguagem:** Java 17
* **Backend:** Spring Boot 3 (Spring Web, Spring Data JPA, Spring WebSocket)
* **Comunicação em Tempo Real:** STOMP Protocol, SockJS & WebSockets
* **Tratamento de Erros & Padrões:** DTOs (Data Transfer Objects), `@RestControllerAdvice` (Global Exception Handler)
* **Banco de Dados Relacional:** MySQL / MariaDB
* **Gerenciador de Dependências:** Maven
* **Frontend:** HTML5, CSS3, JavaScript (Vanilla JS, SockJS Client, STOMP Client, Fetch API)
* **Containerização:** Docker (`Dockerfile` multi-stage)
* **Hospedagem / Deploy:** Render / Nuvem

---

## 📋 Status do Desenvolvimento & Funcionalidades

### ✅ Concluído & Implementado

- [x] **Comunicação em Tempo Real (WebSockets + STOMP)**
  - Substituição completa de polling por *Server-Push* via WebSocket, atualizando o lobby e contadores instantaneamente para todos os clientes conectados.
- [x] **Padronização da API e Tratamento Global de Erros**
  - Implementação de `ErroResponseDTO` e um tratador global de exceções (`@RestControllerAdvice`), garantindo respostas JSON padronizadas e limpas para o front-end.
- [x] **Persistência de Dados Relacional (Spring Data JPA + MySQL)**
  - Mapeamento objeto-relacional (`@Entity`) de Jogadores e Votos, substituindo armazenamento em memória por um banco de dados relacional robusto.
- [x] **Algoritmo de Balanceamento Inteligente**
  - Implementação do `BalanceadorService` utilizando lógica combinatória para calcular e equilibrar a soma/média de tiers de dois times (5v5) com o mínimo de desvio.
- [x] **Endpoints RESTful (`MixController`)**
  - Rotas estruturadas para gerenciar entrada de jogadores, registro de votos, matriz de notas, cálculo de times e reset do lobby.
- [x] **Interface Web Responsiva (Frontend)**
  - Interface moderna consumindo a API e escutando eventos via WebSocket em tempo real.
- [x] **Containerização e Deploy (Docker)**
  - Configuração de `Dockerfile` otimizado para empacotar a aplicação pronta para ambientes em nuvem.

---

### 🚀 Próximas Funcionalidades (Roadmap)

- [ ] **Sistema de Escolha de Mapas (Veto / Pick & Ban)** — Votação interativa para eliminação de mapas da pool do CS2.
- [ ] **Histórico e Registro de Partidas** — Tabela para salvar confrontos realizados e placares.
- [ ] **Estatísticas e KDR por Jogador** — Módulo de winrate e desempenho individual.
- [ ] **Perfis Permanentes & Autenticação** — Cadastro fixo e histórico acumulado por temporada.
- [ ] **Integração com Discord (Bot)** — Notificações automáticas e movimentação para salas de voz.

---

## ⚙️ Como Executar o Projeto Localmente

### Pré-requisitos
* Java 17 instalado
* Maven instalado
* MySQL Server rodando localmente

### 1. Clonar o Repositório
```bash
git clone [https://github.com/SEU_USUARIO/mix-cs-balancer.git](https://github.com/SEU_USUARIO/mix-cs-balancer.git)
cd mix-cs-balancer
