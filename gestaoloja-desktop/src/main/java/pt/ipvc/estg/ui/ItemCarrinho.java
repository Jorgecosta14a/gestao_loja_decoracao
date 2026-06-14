package pt.ipvc.estg.ui;

import pt.ipvc.estg.model.Produto;

public class ItemCarrinho {

    private final Produto produto;
    private int quantidade;

    public ItemCarrinho(Produto produto, int quantidade) {
        this.produto = produto;
        this.quantidade = quantidade;
    }

    public Produto getProduto() {
        return produto;
    }

    public String getProdutoNome() {
        return produto.getNome();
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void somarQuantidade(int quantidade) {
        this.quantidade += quantidade;
    }

    public double getPrecoUnitario() {
        return produto.getPreco();
    }

    public double getSubtotal() {
        return produto.getPreco() * quantidade;
    }
}
