package pt.ipvc.estg.bll;

import pt.ipvc.estg.dao.GarantiaDAO;
import pt.ipvc.estg.model.EstadoGarantia;
import pt.ipvc.estg.model.Garantia;
import pt.ipvc.estg.model.Venda;

import java.util.List;

public class GarantiaService {

    private final GarantiaDAO garantiaDAO = new GarantiaDAO();

    public Garantia registarGarantia(Venda venda, String motivo) {
        if (venda == null) {
            throw new IllegalArgumentException("Selecione uma venda.");
        }

        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Indique o motivo do pedido.");
        }

        Garantia garantia = new Garantia(venda, venda.getCliente(), motivo.trim());
        return garantiaDAO.inserir(garantia);
    }

    public List<Garantia> listarTodas() {
        return garantiaDAO.buscarTodas();
    }

    public long contarPorEstado(EstadoGarantia estado) {
        return garantiaDAO.contarPorEstado(estado);
    }

    public Garantia aprovar(int id) {
        return garantiaDAO.atualizarEstado(id, EstadoGarantia.APROVADA);
    }

    public Garantia rejeitar(int id) {
        return garantiaDAO.atualizarEstado(id, EstadoGarantia.REJEITADA);
    }
}