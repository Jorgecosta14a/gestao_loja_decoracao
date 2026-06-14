package pt.ipvc.estg.ui;

import pt.ipvc.estg.bll.ProdutoService;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import pt.ipvc.estg.model.Produto;

public class ProductsController {

    private final ProdutoService produtoService = new ProdutoService();
    private final ObservableList<Produto> produtos = FXCollections.observableArrayList();

    // Esta variável guarda o produto que está a ser editado
    private Produto produtoSelecionado = null;

    @FXML private TextField pesquisaField;
    @FXML private TextField nomeField;
    @FXML private TextField descricaoField;
    @FXML private TextField precoField;
    @FXML private TextField stockField;

    @FXML private Label totalPecasLabel;
    @FXML private Label totalStockLabel;
    @FXML private Label stockBaixoLabel;
    @FXML private Label mensagemLabel;

    @FXML private TableView<Produto> produtosTable;
    @FXML private TableColumn<Produto, Integer> colId;
    @FXML private TableColumn<Produto, String> colNome;
    @FXML private TableColumn<Produto, String> colDescricao;
    @FXML private TableColumn<Produto, String> colPreco;
    @FXML private TableColumn<Produto, Integer> colStock;

    // Novos botões para a lógica de UI
    @FXML private Button btnGuardar;
    @FXML private Button btnEliminar;
    @FXML private Button btnCancelar;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colPreco.setCellValueFactory(cell -> new ReadOnlyStringWrapper(Formatador.moeda(cell.getValue().getPreco())));
        colStock.setCellValueFactory(new PropertyValueFactory<>("quantidadeStock"));

        FilteredList<Produto> filtrados = new FilteredList<>(produtos, produto -> true);
        pesquisaField.textProperty().addListener((obs, antigo, novo) -> {
            String termo = novo == null ? "" : novo.trim().toLowerCase();
            filtrados.setPredicate(produto -> termo.isEmpty()
                    || produto.getNome().toLowerCase().contains(termo)
                    || (produto.getDescricao() != null && produto.getDescricao().toLowerCase().contains(termo)));
        });
        produtosTable.setItems(filtrados);

        // A MAGIA ACONTECE AQUI: Deteta cliques na tabela
        produtosTable.getSelectionModel().selectedItemProperty().addListener((obs, antigo, novo) -> {
            if (novo != null) {
                preencherFormulario(novo);
            }
        });

        carregarProdutos();
        estadoInicialBotoes();
    }

    @FXML
    private void carregarProdutos() {
        try {
            produtos.setAll(produtoService.listarTodos());
            totalPecasLabel.setText(String.valueOf(produtoService.contarProdutos()));
            totalStockLabel.setText(String.valueOf(produtoService.totalStock()));
            stockBaixoLabel.setText(String.valueOf(produtoService.listarStockBaixo().size()));
            mensagemLabel.setText("");
        } catch (RuntimeException e) {
            mensagemLabel.setText("Nao foi possivel carregar produtos.");
        }
    }

    // Este método substitui o teu antigo adicionarProduto() e faz as duas coisas!
    @FXML
    private void adicionarProduto() {
        try {
            String nome = nomeField.getText().trim();
            String descricao = descricaoField.getText().trim();
            double preco = Double.parseDouble(precoField.getText().trim().replace(",", "."));
            int stock = Integer.parseInt(stockField.getText().trim());

            if (nome.isEmpty() || preco <= 0 || stock < 0) {
                mensagemLabel.setText("Preencha os campos do produto corretamente.");
                return;
            }

            if (produtoSelecionado == null) {
                // MODO CRIAR: Não há produto selecionado
                produtoService.adicionarProduto(new Produto(nome, descricao, preco, stock));
                mensagemLabel.setText("Produto guardado com sucesso.");
            } else {
                // MODO ATUALIZAR: Já existe um produto selecionado
                produtoSelecionado.setNome(nome);
                produtoSelecionado.setDescricao(descricao);
                produtoSelecionado.setPreco(preco);
                produtoSelecionado.setQuantidadeStock(stock);
                produtoService.atualizarProduto(produtoSelecionado); // Precisas de garantir que tens isto na BLL
                mensagemLabel.setText("Produto atualizado com sucesso.");
            }

            limparFormulario();
            carregarProdutos();
        } catch (NumberFormatException e) {
            mensagemLabel.setText("Preco ou stock com formato invalido.");
        } catch (RuntimeException e) {
            mensagemLabel.setText("Erro ao guardar produto: " + e.getMessage());
        }
    }

    @FXML
    private void removerProdutoSelecionado() {
        if (produtoSelecionado == null) {
            mensagemLabel.setText("Selecione um produto para eliminar.");
            return;
        }

        try {
            produtoService.removerProduto(produtoSelecionado.getId());
            mensagemLabel.setText("Produto eliminado com sucesso.");
            limparFormulario();
            carregarProdutos();
        } catch (RuntimeException e) {
            mensagemLabel.setText("Erro: Este produto já tem vendas associadas e não pode ser eliminado.");
        }
    }

    private void preencherFormulario(Produto p) {
        produtoSelecionado = p;
        nomeField.setText(p.getNome());
        descricaoField.setText(p.getDescricao() != null ? p.getDescricao() : "");
        precoField.setText(String.valueOf(p.getPreco()));
        stockField.setText(String.valueOf(p.getQuantidadeStock()));

        // Atualiza a UI para modo Edição
        if (btnGuardar != null) btnGuardar.setText("Atualizar Produto");
        if (btnEliminar != null) btnEliminar.setVisible(true);
        if (btnCancelar != null) btnCancelar.setVisible(true);
    }

    @FXML
    private void limparFormulario() {
        produtoSelecionado = null;
        nomeField.clear();
        descricaoField.clear();
        precoField.clear();
        stockField.clear();
        produtosTable.getSelectionModel().clearSelection();
        estadoInicialBotoes();
    }

    private void estadoInicialBotoes() {
        if (btnGuardar != null) btnGuardar.setText("Guardar Produto");
        if (btnEliminar != null) btnEliminar.setVisible(false);
        if (btnCancelar != null) btnCancelar.setVisible(false);
    }

    @FXML private void goDashboard() { UiNavigator.showDashboard(); }
    @FXML private void goSales() { UiNavigator.showSales(); }
    @FXML private void goClients() { UiNavigator.showClients(); }
    @FXML private void goWarranties() { UiNavigator.showWarranties(); }
    @FXML private void logout() { UiNavigator.showLogin(); }
}
