package pt.ipvc.estg.ui;

import pt.ipvc.estg.bll.DashboardService;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import pt.ipvc.estg.model.Produto;
import pt.ipvc.estg.model.Venda;

import java.io.IOException;

public class DashboardController {

    private final DashboardService dashboardService = new DashboardService();

    @FXML private Label totalVendasHojeLabel;
    @FXML private Label totalProdutosLabel;
    @FXML private Label produtosStockBaixoLabel;
    @FXML private TableView<Venda> vendasTable;
    @FXML private TableColumn<Venda, Integer> colVendaId;
    @FXML private TableColumn<Venda, String> colVendaCliente;
    @FXML private TableColumn<Venda, String> colVendaData;
    @FXML private TableColumn<Venda, String> colVendaTotal;
    @FXML private TableView<Produto> stockBaixoTable;
    @FXML private TableColumn<Produto, String> colStockProduto;
    @FXML private TableColumn<Produto, Integer> colStockQtd;

    @FXML
    private void initialize() {
        colVendaId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colVendaCliente.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getCliente().getNome()));
        colVendaData.setCellValueFactory(cell -> new ReadOnlyStringWrapper(Formatador.dataHora(cell.getValue().getDataVenda())));
        colVendaTotal.setCellValueFactory(cell -> new ReadOnlyStringWrapper(Formatador.moeda(cell.getValue().getTotal())));

        colStockProduto.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colStockQtd.setCellValueFactory(new PropertyValueFactory<>("quantidadeStock"));
        vendasTable.setRowFactory(table -> {
            TableRow<Venda> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    abrirFatura(row.getItem());
                }
            });
            return row;
        });

        carregarDados();
    }

    @FXML
    private void carregarDados() {
        try {
            totalVendasHojeLabel.setText(Formatador.moeda(dashboardService.totalVendasHoje()));
            totalProdutosLabel.setText(String.valueOf(dashboardService.totalProdutos()));
            produtosStockBaixoLabel.setText(String.valueOf(dashboardService.produtosComStockBaixo().size()));
            vendasTable.setItems(FXCollections.observableArrayList(dashboardService.ultimasVendas()));
            stockBaixoTable.setItems(FXCollections.observableArrayList(dashboardService.produtosComStockBaixo()));
        } catch (RuntimeException e) {
            totalVendasHojeLabel.setText("BD indisponivel");
            vendasTable.setItems(FXCollections.observableArrayList());
            stockBaixoTable.setItems(FXCollections.observableArrayList());
        }
    }

    @FXML
    private void goSales() {
        UiNavigator.showSales();
    }

    @FXML
    private void goClients() {
        UiNavigator.showClients();
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
}
