package bll;

import dao.ProdutoDAO;
import model.Produto;

public class VendaService {

    private ProdutoDAO produtoDAO;

    public VendaService() {
        this.produtoDAO = new ProdutoDAO();
    }

    public void registarVenda(int idProduto, int quantidadeComprada, int idCliente, int idFuncionario) {
        System.out.println("\n--- A processar pedido de venda ---");

        Produto produto = produtoDAO.obterProdutoPorId(idProduto);

        if (produto == null) {
            System.out.println("❌ ERRO (BLL): O produto com o ID " + idProduto + " não existe na base de dados.");
            return;
        }

        System.out.println("Produto encontrado: " + produto.getNome() + " | Stock atual: " + produto.getQuantidadeStock());

        if (produto.getQuantidadeStock() < quantidadeComprada) {
            System.out.println("❌ ERRO (BLL): Stock insuficiente! Tentou vender " + quantidadeComprada + " unidades, mas só existem " + produto.getQuantidadeStock() + ".");
            return;
        }


        System.out.println("✅ SUCESSO (BLL): Stock validado. A enviar ordem para a DAL gravar a venda...");
    }
}