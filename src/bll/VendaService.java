package bll;

import dao.ProdutoDAO;
import dao.VendaDAO;
import model.Cliente;
import model.LinhaVenda;
import model.Produto;
import model.Venda;
import java.util.List;

public class VendaService {

    private ProdutoDAO produtoDAO = new ProdutoDAO();
    private VendaDAO vendaDAO = new VendaDAO();

    public String realizarVenda(Cliente cliente, Produto produto, int quantidade) {
        if (produto.getQuantidadeStock() < quantidade) {
            return "❌ Erro: Stock insuficiente (" + produto.getQuantidadeStock() + " disponíveis).";
        }

        Venda venda = new Venda(cliente);
        venda.adicionarProduto(produto, quantidade);

        produto.setQuantidadeStock(produto.getQuantidadeStock() - quantidade);
        produtoDAO.atualizarProduto(produto);

        vendaDAO.salvarVenda(venda);
        return "✅ Venda nº " + venda.getId() + " realizada com sucesso!";
    }

    public void exibirRelatorioVendas() {
        List<Venda> vendas = vendaDAO.buscarTodas();
        System.out.println("\n======= RELATÓRIO DE VENDAS =======");
        for (Venda v : vendas) {
            System.out.println("Venda ID: " + v.getId() + " | Cliente: " + v.getCliente().getNome());
            for (LinhaVenda linha : v.getLinhasVenda()) {
                System.out.println("   -> Produto: " + linha.getProduto().getNome() +
                        " | Qtd: " + linha.getQuantidade() +
                        " | Preço Un: " + linha.getPrecoUnitario() + "€");
            }
            System.out.println("-----------------------------------");
        }
    }
}