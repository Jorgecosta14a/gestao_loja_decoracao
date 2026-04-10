package bll;

import dao.ProdutoDAO;
import model.Produto;
import java.util.List;

public class ProdutoService {
    private ProdutoDAO dao = new ProdutoDAO();

    public void adicionarProduto(Produto p) {
        dao.inserirProduto(p);
    }

    public Produto buscarPorId(int id) {
        return dao.buscarProdutoPorId(id);
    }

    public List<Produto> listarTodos() {
        return dao.buscarTodosProdutos();
    }
}