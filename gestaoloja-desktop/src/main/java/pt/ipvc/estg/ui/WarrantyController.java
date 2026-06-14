package pt.ipvc.estg.ui;

import pt.ipvc.estg.bll.GarantiaService;
import pt.ipvc.estg.bll.VendaService;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import pt.ipvc.estg.model.EstadoGarantia;
import pt.ipvc.estg.model.Garantia;
import pt.ipvc.estg.model.Venda;

import java.util.List;

public class WarrantyController {

    private final GarantiaService garantiaService = new GarantiaService();
    private final VendaService vendaService = new VendaService();

    @FXML private Label emAnaliseLabel;
    @FXML private Label aprovadasLabel;
    @FXML private Label rejeitadasLabel;
    @FXML private Label totalGarantiasLabel;
    @FXML private Label mensagemLabel;
    @FXML private ComboBox<Venda> vendaCombo;
    @FXML private TextArea motivoArea;
    @FXML private TableView<Garantia> garantiasTable;
    @FXML private TableColumn<Garantia, Integer> colId;
    @FXML private TableColumn<Garantia, String> colCliente;
    @FXML private TableColumn<Garantia, String> colVenda;
    @FXML private TableColumn<Garantia, String> colMotivo;
    @FXML private TableColumn<Garantia, String> colEstado;

    @FXML
    private void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCliente.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getCliente().getNome()));
        colVenda.setCellValueFactory(cell -> new ReadOnlyStringWrapper("#" + cell.getValue().getVenda().getId()));
        colMotivo.setCellValueFactory(new PropertyValueFactory<>("motivo"));
        colEstado.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getEstado().getDescricao()));
        carregarGarantias();
    }

    @FXML
    private void carregarGarantias() {
        try {
            List<Garantia> garantias = garantiaService.listarTodas();
            garantiasTable.setItems(FXCollections.observableArrayList(garantias));
            vendaCombo.setItems(FXCollections.observableArrayList(vendaService.listarVendas()));

            long emAnalise = garantiaService.contarPorEstado(EstadoGarantia.EM_ANALISE);
            long aprovadas = garantiaService.contarPorEstado(EstadoGarantia.APROVADA);
            long rejeitadas = garantiaService.contarPorEstado(EstadoGarantia.REJEITADA);
            emAnaliseLabel.setText(String.valueOf(emAnalise));
            aprovadasLabel.setText(String.valueOf(aprovadas));
            rejeitadasLabel.setText(String.valueOf(rejeitadas));
            totalGarantiasLabel.setText(String.valueOf(emAnalise + aprovadas + rejeitadas));
        } catch (RuntimeException e) {
            mensagemLabel.setText("Nao foi possivel carregar garantias.");
        }
    }

    @FXML
    private void registarGarantia() {
        try {
            garantiaService.registarGarantia(vendaCombo.getValue(), motivoArea.getText());
            motivoArea.clear();
            carregarGarantias();
            mensagemLabel.setText("Pedido de garantia registado.");
        } catch (RuntimeException e) {
            mensagemLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void aprovarSelecionada() {
        atualizarEstadoSelecionado(EstadoGarantia.APROVADA);
    }

    @FXML
    private void rejeitarSelecionada() {
        atualizarEstadoSelecionado(EstadoGarantia.REJEITADA);
    }

    private void atualizarEstadoSelecionado(EstadoGarantia novoEstado) {
        Garantia garantia = garantiasTable.getSelectionModel().getSelectedItem();
        if (garantia == null) {
            mensagemLabel.setText("Selecione um pedido.");
            return;
        }

        if (garantia.getEstado() != EstadoGarantia.EM_ANALISE) {
            mensagemLabel.setText("Apenas pedidos em analise podem ser atualizados.");
            return;
        }

        try {
            if (novoEstado == EstadoGarantia.APROVADA) {
                garantiaService.aprovar(garantia.getId());
                mensagemLabel.setText("Garantia aprovada.");
            } else if (novoEstado == EstadoGarantia.REJEITADA) {
                garantiaService.rejeitar(garantia.getId());
                mensagemLabel.setText("Garantia rejeitada.");
            }

            garantia.setEstado(novoEstado);
            garantiasTable.refresh();
            carregarGarantias();
        } catch (RuntimeException e) {
            mensagemLabel.setText("Nao foi possivel atualizar a garantia.");
        }
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
    private void goClients() {
        UiNavigator.showClients();
    }

    @FXML
    private void goProducts() {
        UiNavigator.showProducts();
    }

    @FXML
    private void logout() {
        UiNavigator.showLogin();
    }
}
