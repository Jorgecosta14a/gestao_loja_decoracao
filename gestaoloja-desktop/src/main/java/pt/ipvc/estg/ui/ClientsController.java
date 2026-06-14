package pt.ipvc.estg.ui;

import pt.ipvc.estg.bll.ClienteService;
import pt.ipvc.estg.bll.VendaService;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import pt.ipvc.estg.model.Cliente;
import pt.ipvc.estg.model.Venda;

import java.io.IOException;

public class ClientsController {

    private final ClienteService clienteService = new ClienteService();
    private final VendaService vendaService = new VendaService();
    private final ObservableList<Cliente> clientes = FXCollections.observableArrayList();
    private Cliente clienteSelecionado;

    @FXML private TextField pesquisaField;
    @FXML private TextField nomeField;
    @FXML private TextField contactoField;
    @FXML private TextField nifField;
    @FXML private Label formTitleLabel;
    @FXML private Label totalClientesLabel;
    @FXML private Label mensagemLabel;
    @FXML private TableView<Cliente> clientesTable;
    @FXML private TableColumn<Cliente, Integer> colId;
    @FXML private TableColumn<Cliente, String> colNome;
    @FXML private TableColumn<Cliente, String> colContacto;
    @FXML private TableColumn<Cliente, String> colNif;
    @FXML private TableView<Venda> historicoComprasTable;
    @FXML private TableColumn<Venda, Integer> colHistoricoId;
    @FXML private TableColumn<Venda, String> colHistoricoData;
    @FXML private TableColumn<Venda, String> colHistoricoTotal;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colContacto.setCellValueFactory(cell -> new ReadOnlyStringWrapper(textoOuTraco(cell.getValue().getContacto())));
        colNif.setCellValueFactory(cell -> new ReadOnlyStringWrapper(textoOuTraco(cell.getValue().getNif())));

        colHistoricoId.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(cell.getValue().getId()));
        colHistoricoData.setCellValueFactory(cell -> new ReadOnlyStringWrapper(Formatador.dataHora(cell.getValue().getDataVenda())));
        colHistoricoTotal.setCellValueFactory(cell -> new ReadOnlyStringWrapper(Formatador.moeda(cell.getValue().getTotal())));

        FilteredList<Cliente> filtrados = new FilteredList<>(clientes, cliente -> true);
        pesquisaField.textProperty().addListener((obs, antigo, novo) -> {
            String termo = novo == null ? "" : novo.trim().toLowerCase();
            filtrados.setPredicate(cliente -> termo.isEmpty()
                    || cliente.getNome().toLowerCase().contains(termo)
                    || (cliente.getContacto() != null && cliente.getContacto().toLowerCase().contains(termo))
                    || (cliente.getNif() != null && cliente.getNif().contains(termo)));
        });

        clientesTable.setItems(filtrados);
        historicoComprasTable.setItems(FXCollections.observableArrayList());
        clientesTable.getSelectionModel().selectedItemProperty().addListener((obs, antigo, cliente) -> selecionarCliente(cliente));
        historicoComprasTable.setRowFactory(table -> {
            TableRow<Venda> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    abrirFatura(row.getItem());
                }
            });
            return row;
        });
        carregarClientes();
    }

    @FXML
    private void carregarClientes() {
        try {
            clienteService.garantirConsumidorFinal();
            clientes.setAll(clienteService.listarTodos());
            totalClientesLabel.setText(String.valueOf(clientes.size()));
            clientesTable.getSelectionModel().clearSelection();
            selecionarCliente(null);
            mensagemLabel.setText("");
        } catch (RuntimeException e) {
            mensagemLabel.setText("Nao foi possivel carregar clientes.");
        }
    }

    @FXML
    private void adicionarCliente() {
        try {
            String nome = nomeField.getText().trim();
            String contacto = contactoField.getText().trim();
            String nif = nifField.getText().trim();
            String mensagem;

            if (clienteSelecionado == null) {
                clienteService.registarCliente(new Cliente(nome, contacto, nif));
                mensagem = "Cliente guardado com sucesso.";
            } else {
                clienteSelecionado.setNome(nome);
                clienteSelecionado.setContacto(contacto);
                clienteSelecionado.setNif(nif);
                clienteService.editarCliente(clienteSelecionado);
                mensagem = "Cliente atualizado com sucesso.";
            }

            limparFormulario();
            carregarClientes();
            mensagemLabel.setText(mensagem);
        } catch (RuntimeException e) {
            mensagemLabel.setText(e.getMessage());
        }
    }

    private void selecionarCliente(Cliente cliente) {
        clienteSelecionado = cliente;
        historicoComprasTable.setItems(FXCollections.observableArrayList());

        if (cliente == null) {
            formTitleLabel.setText("Novo cliente");
            limparFormulario();
            return;
        }

        formTitleLabel.setText("Editar cliente");
        nomeField.setText(textoOuVazio(cliente.getNome()));
        contactoField.setText(textoOuVazio(cliente.getContacto()));
        nifField.setText(textoOuVazio(cliente.getNif()));
        carregarHistoricoCompras(cliente);
    }

    private void carregarHistoricoCompras(Cliente cliente) {
        try {
            historicoComprasTable.setItems(FXCollections.observableArrayList(
                    vendaService.listarVendas().stream()
                            .filter(venda -> venda.getCliente() != null
                                    && venda.getCliente().getId() == cliente.getId())
                            .toList()
            ));
        } catch (RuntimeException e) {
            mensagemLabel.setText("Nao foi possivel carregar o historico de compras.");
        }
    }

    private void abrirFatura(Venda venda) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/fatura.fxml"));
            Parent root = loader.load();
            FaturaController controller = loader.getController();
            controller.setVenda(venda);

            Stage stage = new Stage();
            stage.setTitle("Fatura #" + venda.getId());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Fatura");
            alert.setHeaderText("Nao foi possivel abrir a fatura.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private String textoOuTraco(String texto) {
        return texto == null || texto.isBlank() ? "-" : texto;
    }

    private String textoOuVazio(String texto) {
        return texto == null ? "" : texto;
    }

    private void limparFormulario() {
        nomeField.clear();
        contactoField.clear();
        nifField.clear();
    }

    @FXML
    private void goDashboard() {
        UiNavigator.showDashboard();
    }

    @FXML
    private void goSales() {
        UiNavigator.showSales();
    }

    @FXML
    private void goProducts() {
        UiNavigator.showProducts();
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
