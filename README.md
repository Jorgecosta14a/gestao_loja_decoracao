# GestãoLojaDecoração (Nordic Curator)

Aplicação Java multiplataforma desenvolvida para apoio à gestão de uma loja de móveis e artigos de decoração. O sistema centraliza as regras de negócio e a base de dados, oferecendo duas interfaces de acesso distintas (Desktop e Web).

---

##  Estado Atual e Arquitetura

* **Interface Desktop:** JavaFX/FXML estruturado para edição no SceneBuilder.
* **Interface Web:** Spring Boot com motor de templates Thymeleaf.
* **Persistência:** JPA/Hibernate com base de dados PostgreSQL.
* **Arquitetura:** Camadas separadas em `model`, `dao`, `bll`, `ui` (Desktop) e `web` (Spring).

---

##  Funcionalidades 

A aplicação encontra-se totalmente funcional e com a interface otimizada nas duas plataformas:

* **Segurança:** Login de entrada obrigatório para acesso à aplicação.
* **Dashboard:** Visão geral diária com métricas de vendas, total de produtos e alertas de stock baixo.
* **Gestão de Clientes:** Registo de clientes com histórico de compras integrado.
* **Gestão de Produtos e Stock:** Catálogo completo de artigos (com **suporte a upload de imagens** na versão Web).
* **Ponto de Venda (POS):** Registo de faturas com validação de stock, opção de "venda ao balcão" e **remoção individual de itens no carrinho com recálculo instantâneo**.
* **Pós-Venda:** Registo, triagem e atualização de estados de pedidos de garantia.
* **API REST:** Endpoints JSON disponíveis para integração de serviços externos.

---

## Como Correr o Projeto

### 1. Requisitos da Base de Dados
Certifique-se de que o servidor PostgreSQL está a correr com as seguintes credenciais:
* **Host:** `localhost:5432`
* **Base de dados:** `ProjII`
* **Utilizador:** `postgres`

### 2. Aplicação Desktop (JavaFX)
Para contornar restrições de módulos do JavaFX, utilize a classe `AppMain` (Wrapper) ou o Maven.
* **No IntelliJ:** Correr a classe `pt.ipvc.estg.ui.AppMain`
* **Via Maven (Terminal):** `mvn javafx:run`

### 3. Aplicação Web (Spring Boot)
* **No IntelliJ:** Correr a classe principal `web.GestaoLojaWebApplication`
* **Acesso:** Abrir o navegador em [http://localhost:8080](http://localhost:8080) *(Utilizar as credenciais padrão configuradas no controlador de Login)*.

---

## Estrutura de Ecrãs e Rotas

### Ecrãs FXML (Desktop)
Localizados em `src/main/resources/`:
* `Login.fxml`
* `ui/dashboard.fxml`
* `ui/sales.fxml`
* `ui/clients.fxml`
* `ui/products.fxml`
* `ui/warranties.fxml`

### Páginas Web (Spring/Thymeleaf)
* `/login`
* `/dashboard`
* `/clientes`
* `/produtos`
* `/vendas`
* `/garantias`

---

## API REST
O sistema disponibiliza os seguintes *endpoints*:

**Estatísticas e Entidades Principais:**
* `GET /api/dashboard`
* `GET /api/clientes` | `POST /api/clientes`
* `GET /api/produtos` | `POST /api/produtos`

**Vendas e Pós-Venda:**
* `GET /api/vendas` | `POST /api/vendas`
* `GET /api/garantias` | `POST /api/garantias`
* `PATCH /api/garantias/{id}/estado`
