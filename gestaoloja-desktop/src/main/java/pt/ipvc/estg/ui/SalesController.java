package pt.ipvc.estg.ui;

import pt.ipvc.estg.bll.ClienteService;
import pt.ipvc.estg.bll.ProdutoService;
import pt.ipvc.estg.bll.VendaService;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import pt.ipvc.estg.model.Cliente;
import pt.ipvc.estg.model.Produto;

import java.util.LinkedHashMap;
import java.util.Map;

public class SalesController {

    private final ClienteService clienteService = new ClienteService();
    private final ProdutoService produtoService = new ProdutoService();
    private final VendaService vendaService = new VendaService();
    private final ObservableList<ItemCarrinho> itensCarrinho = FXCollections.observableArrayList();
    private final Cliente vendaAoBalcao = new Cliente("Venda ao balcao / sem cliente", "", "");

    @FXML private ComboBox<Cliente> clienteCombo;
    @FXML private ComboBox<Produto> produtoCombo;
    @FXML private Spinner<Integer> quantidadeSpinner;
    @FXML private Label clienteSelecionadoLabel;
    @FXML private Label subtotalLabel;
    @FXML private Label impostosLabel;
    @FXML private Label totalLabel;
    @FXML private Label mensagemLabel;
    @FXML private TableView<ItemCarrinho> carrinhoTable;
    @FXML private TableColumn<ItemCarrinho, String> colProduto;
    @FXML private TableColumn<ItemCarrinho, Integer> colQuantidade;
    @FXML private TableColumn<ItemCarrinho, String> colPreco;
    @FXML private TableColumn<ItemCarrinho, String> colSubtotal;

    @FXML
    private void initialize() {
        quantidadeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, 1));
        quantidadeSpinner.setEditable(true);

        colProduto.setCellValueFactory(new PropertyValueFactory<>("produtoNome"));
        colQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colPreco.setCellValueFactory(cell -> new ReadOnlyStringWrapper(Formatador.moeda(cell.getValue().getPrecoUnitario())));
        colSubtotal.setCellValueFactory(cell -> new ReadOnlyStringWrapper(Formatador.moeda(cell.getValue().getSubtotal())));
        carrinhoTable.setItems(itensCarrinho);

        clienteCombo.valueProperty().addListener((obs, antigo, cliente) ->
                clienteSelecionadoLabel.setText(cliente == null ? "Venda ao balcao" : cliente.getNome()));

        carregarCombos();
        atualizarTotais();
    }

    private void carregarCombos() {
        try {
            ObservableList<Cliente> clientes = FXCollections.observableArrayList();
            clientes.add(vendaAoBalcao);
            clientes.addAll(clienteService.listarIdentificados());
            clienteCombo.setItems(clientes);
            clienteCombo.getSelectionModel().selectFirst();
            produtoCombo.setItems(FXCollections.observableArrayList(produtoService.listarTodos()));
        } catch (RuntimeException e) {
            mensagemLabel.setText("Nao foi possivel carregar dados da BD.");
        }
    }

    @FXML
    private void adicionarAoCarrinho() {
        Produto produto = produtoCombo.getValue();
        int quantidade = quantidadeSpinner.getValue();

        if (produto == null) {
            mensagemLabel.setText("Selecione um produto.");
            return;
        }

        ItemCarrinho existente = procurarItem(produto);
        int quantidadeAtual = existente == null ? 0 : existente.getQuantidade();
        if (quantidadeAtual + quantidade > produto.getQuantidadeStock()) {
            mensagemLabel.setText("Stock insuficiente para " + produto.getNome() + ".");
            return;
        }

        if (existente == null) {
            itensCarrinho.add(new ItemCarrinho(produto, quantidade));
        } else {
            existente.somarQuantidade(quantidade);
            carrinhoTable.refresh();
        }

        mensagemLabel.setText("Produto adicionado ao carrinho.");
        atualizarTotais();
    }

    @FXML
    private void processarVenda() {
        if (itensCarrinho.isEmpty()) {
            mensagemLabel.setText("Adicione produtos ao carrinho.");
            return;
        }

        Map<Produto, Integer> itens = new LinkedHashMap<>();
        for (ItemCarrinho item : itensCarrinho) {
            itens.put(item.getProduto(), item.getQuantidade());
        }

        String resultado = vendaService.realizarVenda(clienteCombo.getValue(), itens);
        mensagemLabel.setText(resultado);
        if (resultado.startsWith("Venda")) {
            limparCarrinho();
            carregarCombos();
        }
    }

    @FXML
    private void limparCarrinho() {
        itensCarrinho.clear();
        mensagemLabel.setText("");
        atualizarTotais();
    }

    @FXML
    private void removerItemCarrinho() {
        ItemCarrinho itemSelecionado = carrinhoTable.getSelectionModel().getSelectedItem();
        if (itemSelecionado == null) {
            mensagemLabel.setText("Selecione um item do carrinho.");
            return;
        }

        itensCarrinho.remove(itemSelecionado);
        carrinhoTable.refresh();
        mensagemLabel.setText("Item removido do carrinho.");
        atualizarTotais();
    }

    private ItemCarrinho procurarItem(Produto produto) {
        for (ItemCarrinho item : itensCarrinho) {
            if (item.getProduto().getId() == produto.getId()) {
                return item;
            }
        }
        return null;
    }

    private void atualizarTotais() {
        double total = 0.0;
        for (ItemCarrinho item : itensCarrinho) {
            total += item.getPrecoUnitario() * item.getQuantidade();
        }

        double impostos = total * 0.23;
        subtotalLabel.setText(Formatador.moeda(total));
        impostosLabel.setText(Formatador.moeda(impostos));
        totalLabel.setText(Formatador.moeda(total));
    }

    @FXML
    private void goDashboard() {
        UiNavigator.showDashboard();
    }

    @FXML
    private void goProducts() {
        UiNavigator.showProducts();
    }

    @FXML
    private void goClients() {
        UiNavigator.showClients();
    }

    @FXML
    private void criarClienteRapido() {
        UiNavigator.showClients();
    }

    @FXML
    private void goWarranties() {
        UiNavigator.showWarranties();
    }

    @FXML
    private void logout() {
        UiNavigator.showLogin();
    }
}
