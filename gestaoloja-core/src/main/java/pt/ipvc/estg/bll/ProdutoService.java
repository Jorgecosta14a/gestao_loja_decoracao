package pt.ipvc.estg.bll;

import pt.ipvc.estg.dao.ProdutoDAO;
import pt.ipvc.estg.model.Produto;
import java.util.List;

public class ProdutoService {
    private final ProdutoDAO dao = new ProdutoDAO();

    public Produto adicionarProduto(Produto p) {
        return dao.inserirProduto(p);
    }

    public Produto buscarPorId(int id) {
        return dao.buscarProdutoPorId(id);
    }

    public List<Produto> listarTodos() {
        return dao.buscarTodosProdutos();
    }

    public List<Produto> listarStockBaixo() {
        return dao.buscarProdutosComStockBaixo(5);
    }

    public Produto atualizarProduto(Produto produto) {
        return dao.atualizarProduto(produto);
    }

    public void removerProduto(int id) {
        dao.removerProdutoSeSemVendas(id);
    }

    public int limparDuplicadosMesaVidro() {
        return dao.removerDuplicadosPorNome("Mesa de Vidro");
    }

    public long contarProdutos() {
        return dao.contarProdutos();
    }

    public long totalStock() {
        return dao.totalStock();
    }
}
