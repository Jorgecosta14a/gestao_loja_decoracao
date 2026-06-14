GestaoLojaDecoracao

Aplicacao Java para apoio a gestao de uma loja de moveis e artigos de decoracao.

Estado atual

JavaFX/FXML para a aplicacao desktop.
Spring Boot/Thymeleaf para a aplicacao Web.
SceneBuilder pode abrir os ficheiros em src/main/resources.
JPA/Hibernate com PostgreSQL para persistencia.
Camadas separadas em model, dao, bll, ui e web.

Funcionalidades ja ligadas

Login de entrada para a aplicacao nas duas versoes.
Dashboard com vendas, produtos e stock baixo.
Gestao de clientes com historico de compras.
Gestao de produtos e stock com suporte a upload de imagens na versao Web.
Registo de venda com validacao de stock, opcao de venda ao balcao e remocao individual de itens no carrinho com recalculo imediato.
Registo, aprovacao, rejeicao e atualizacao de pedidos de garantia.
Versao Web e Desktop com interface limpa e otimizada.
API REST com endpoints JSON para clientes, produtos, vendas, garantias e dashboard.

Como correr

Base de dados esperada:
PostgreSQL em localhost:5432
Base de dados: ProjII
Utilizador: postgres

Aplicacao Desktop

No IntelliJ, correr a classe:
ui.AppMain

Tambem pode correr por Maven:
mvn javafx:run

Aplicacao Web

No IntelliJ, correr a classe:
web.GestaoLojaWebApplication

Depois abrir:
http://localhost:8080

Ecras FXML

src/main/resources/Login.fxml
src/main/resources/ui/dashboard.fxml
src/main/resources/ui/sales.fxml
src/main/resources/ui/clients.fxml
src/main/resources/ui/products.fxml
src/main/resources/ui/warranties.fxml

Paginas Web

/login
/dashboard
/clientes
/produtos
/vendas
/garantias

API REST

GET /api/dashboard
GET /api/clientes
POST /api/clientes
GET /api/produtos
POST /api/produtos
GET /api/vendas
POST /api/vendas
GET /api/garantias
POST /api/garantias
PATCH /api/garantias/{id}/estado
