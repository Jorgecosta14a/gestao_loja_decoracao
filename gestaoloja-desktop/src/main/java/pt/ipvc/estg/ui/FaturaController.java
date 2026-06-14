package pt.ipvc.estg.ui;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import pt.ipvc.estg.model.LinhaVenda;
import pt.ipvc.estg.model.Venda;

public class FaturaController {

    @FXML private Label faturaIdLabel;
    @FXML private Label dataLabel;
    @FXML private Label clienteNomeLabel;
    @FXML private Label clienteContactoLabel;
    @FXML private Label clienteNifLabel;
    @FXML private Label totalLabel;
    @FXML private TableView<LinhaVenda> itensTable;
    @FXML private TableColumn<LinhaVenda, String> colProduto;
    @FXML private TableColumn<LinhaVenda, Integer> colQuantidade;
    @FXML private TableColumn<LinhaVenda, String> colPrecoUnitario;
    @FXML private TableColumn<LinhaVenda, String> colSubtotal;

    @FXML
    private void initialize() {
        colProduto.setCellValueFactory(cell -> new ReadOnlyStringWrapper(cell.getValue().getProduto().getNome()));
        colQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colPrecoUnitario.setCellValueFactory(cell -> new ReadOnlyStringWrapper(Formatador.moeda(cell.getValue().getPrecoUnitario())));
        colSubtotal.setCellValueFactory(cell -> {
            LinhaVenda linha = cell.getValue();
            return new ReadOnlyStringWrapper(Formatador.moeda(linha.getQuantidade() * linha.getPrecoUnitario()));
        });
    }

    public void setVenda(Venda venda) {
        if (venda == null) {
            return;
        }

        faturaIdLabel.setText("Fatura #" + venda.getId());
        dataLabel.setText(Formatador.dataHora(venda.getDataVenda()));
        clienteNomeLabel.setText(venda.getCliente().getNome());
        clienteContactoLabel.setText(textoOuTraco(venda.getCliente().getContacto()));
        clienteNifLabel.setText(textoOuTraco(venda.getCliente().getNif()));
        totalLabel.setText(Formatador.moeda(venda.getTotal()));
        itensTable.setItems(FXCollections.observableArrayList(venda.getLinhasVenda()));
    }

    private String textoOuTraco(String texto) {
        return texto == null || texto.isBlank() ? "-" : texto;
    }
}
