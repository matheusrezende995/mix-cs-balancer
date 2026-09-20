# 🎯 Mix CS Balancer

> Aplicação web desenvolvida com **Spring Boot** para gerenciar lobbies de partidas customizadas (Mix / PCW de CS:GO / CS2), automatizar votações de nível (*tier*) entre amigos e realizar o **balanceamento inteligente e justo de 2 times (5v5)**.

---

## 📌 Sobre o Projeto

O **Mix CS Balancer** nasceu para resolver o problema clássico das partidas entre amigos: criar times desequilibrados e perder tempo decidindo quem joga com quem. 

A aplicação permite que os jogadores entrem no lobby, atribuam notas/tiers uns aos outros de forma justa e, em seguida, gera dois times perfeitamente equilibrados com base nas médias de habilidade calculadas.

---

## 🛠️ Tecnologias e Ferramentas

* **Linguagem:** Java 17
* **Framework Backend:** Spring Boot 3 (Spring Web, Spring Data JPA)
* **Gerenciador de Dependências:** Maven (`pom.xml`)
* **Banco de Dados Relacional:** MySQL / MariaDB
* **Frontend:** HTML5, CSS3, JavaScript (Fetch API / Async Web)
* **Containerização:** Docker (`Dockerfile`)
* **Hospedagem / Nuvem:** Render Dashboard

---

## 📋 Status do Desenvolvimento

### ✅ Concluído & Implementado

- [x] **Configuração do Projeto e Build System**
  - Definição da estrutura Spring Boot e dependências no `pom.xml`.
- [x] **Algoritmo de Balanceamento Inteligente**
  - Implementação do `BalanceadorService` e lógica de combinação para equilibrar a soma/média de MMR/tier de dois times (5v5).
- [x] **Endpoints da API REST (`MixController`)**
  - Rotas para gerenciar entrada de jogadores, registro de votos, matriz de notas, cálculo de times e reset do lobby.
- [x] **Interface Web (Frontend)**
  - Criação da página intuitiva (`index.html`, `style.css`) consumindo a API via JavaScript.
- [x] **Containerização com Docker**
  - Criação do `Dockerfile` multi-stage para compilar e empacotar a aplicação em um container isolado.
- [x] **Deploy Continuo na Nuvem**
  - Integração e publicação automática no dashboard do **Render**.
- [x] **Persistência de Dados Relacional (JPA + MySQL)**
  - Transição da memória (`List<Jogador>`) para o **Spring Data JPA**.
  - Mapeamento das entidades `@Entity` (`Jogador` e `Voto`).
  - Criação dos repositórios Spring Data (`JogadorRepository` e `VotoRepository`).
  - Configuração da conexão no `application.properties`.

---

### 🚀 Próximas Funcionalidades (Roadmap)

- [ ] **Sistema de Escolha de Mapas (Veto / Pick & Ban)**
  - Votação interativa ou sistema de eliminação de mapas da pool ativa do CS2 (Mirage, Inferno, Nuke, Anubis, Ancient, Dust II, Vertigo).
- [ ] **Histórico e Registro de Partidas**
  - Tabela para salvar os confrontos realizados, placares (ex: 13x11) e data da partida.
- [ ] **Estatísticas e KDR por Jogador**
  - Módulo para registrar abate, mortes e assistências (K/D/A), taxa de vitória (*winrate %*) e ranking do servidor.
- [ ] **Perfis Permanentes & Autenticação (Steam / Login)**
  - Cadastro fixo dos amigos para manter o histórico acumulado ao longo da temporada.
- [ ] **Integração com Discord (Bot)**
  - Bot no Discord para notificar quando o lobby estiver cheio e mover automaticamente os jogadores para as salas de voz dos seus respectivos times (Time A / Time B).
- [ ] **Definição de Capitães e Reroll de Times**
  - Opção para os 2 melhores jogadores serem capitães e escolherem em formato de *Draft* ou forçar um recálculo com novos critérios.

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
