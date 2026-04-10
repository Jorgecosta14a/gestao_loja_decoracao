package main;

import bll.ClienteService;
import bll.ProdutoService;
import bll.VendaService;
import model.Cliente;
import model.Produto;
import dao.ClienteDAO;

public class Main {
    public static void main(String[] args) {

        ClienteService clienteService = new ClienteService();
        ProdutoService produtoService = new ProdutoService();
        VendaService vendaService = new VendaService();

        // 1. Criar dados
        Cliente c1 = new Cliente("Jorge Engenheiro", "911222333", "250250250");
        new ClienteDAO().inserirCliente(c1);

        Produto p1 = new Produto("Mesa de Vidro", "Mesa sala de jantar", 150.00, 5);
        produtoService.adicionarProduto(p1);

        // 2. Tentar vendas
        System.out.println(vendaService.realizarVenda(c1, p1, 2)); // Sucesso
        System.out.println(vendaService.realizarVenda(c1, p1, 10)); // Erro Stock

        // 3. MOSTRAR TUDO (O toque final para a Entrega 2)
        vendaService.exibirRelatorioVendas();
    }
}