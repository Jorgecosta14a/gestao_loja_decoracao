package pt.ipvc.estg.bll;

import pt.ipvc.estg.model.Produto;
import pt.ipvc.estg.model.Venda;

import java.util.List;

public class DashboardService {

    private final ProdutoService produtoService = new ProdutoService();
    private final VendaService vendaService = new VendaService();

    public double totalVendasHoje() {
        return vendaService.totalVendasDoDia();
    }

    public long totalProdutos() {
        return produtoService.contarProdutos();
    }

    public long totalStock() {
        return produtoService.totalStock();
    }

    public List<Produto> produtosComStockBaixo() {
        return produtoService.listarStockBaixo();
    }

    public List<Venda> ultimasVendas() {
        return vendaService.listarVendas();
    }
}