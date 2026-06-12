package pt.ipvc.estg.bll;

import pt.ipvc.estg.model.Cliente;
import pt.ipvc.estg.model.Produto;
import pt.ipvc.estg.model.Venda;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DadosIniciaisService {

    private final ClienteService clienteService = new ClienteService();
    private final ProdutoService produtoService = new ProdutoService();
    private final VendaService vendaService = new VendaService();
    private final GarantiaService garantiaService = new GarantiaService();

    public void garantirDados() {
        garantirClientes();
        garantirProdutos();
        limparProdutosDuplicados();
        garantirVendas();
        garantirGarantias();
    }

    private void garantirClientes() {
        if (!clienteService.listarTodos().isEmpty()) {
            return;
        }

        clienteService.registarCliente(new Cliente("Joao Silva", "910000000", "222333444"));
        clienteService.registarCliente(new Cliente("Ana Ferreira", "911222333", "250250250"));
        clienteService.registarCliente(new Cliente("Marta Santos", "912345678", "233444555"));
    }

    private void garantirProdutos() {
        List<Produto> produtos = produtoService.listarTodos();
        garantirProduto(produtos, "Sofa Minimalista", "Veludo verde / pernas de carvalho", 1250.00, 14);
        garantirProduto(produtos, "Cadeira Eames", "Pele preta / estrutura nogueira", 850.00, 28);
        garantirProduto(produtos, "Mesa de Jantar", "Carvalho macico / 8 lugares", 2100.00, 2);
        garantirProduto(produtos, "Vaso Escultural", "Ceramica mate / colecao zen", 145.00, 52);
        garantirProduto(produtos, "Candeeiro Loft", "Metal preto / luz quente", 220.00, 3);
    }

    private void garantirProduto(List<Produto> existentes, String nome, String descricao, double preco, int stock) {
        boolean jaExiste = existentes.stream().anyMatch(produto -> produto.getNome().equalsIgnoreCase(nome));
        if (jaExiste) {
            return;
        }
        Produto produto = new Produto(nome, descricao, preco, stock, Produto.imagemPadrao(nome));
        produtoService.adicionarProduto(produto);
        existentes.add(produto);
    }

    private void limparProdutosDuplicados() {
        produtoService.limparDuplicadosMesaVidro();
    }

    private void garantirVendas() {
        if (!vendaService.listarVendas().isEmpty()) {
            return;
        }

        List<Cliente> clientes = clienteService.listarTodos();
        List<Produto> produtos = produtoService.listarTodos();
        if (clientes.isEmpty() || produtos.size() < 2) {
            return;
        }

        Map<Produto, Integer> primeiraVenda = new LinkedHashMap<>();
        primeiraVenda.put(produtos.get(0), 1);
        primeiraVenda.put(produtos.get(1), 2);
        vendaService.realizarVenda(clientes.get(0), primeiraVenda);
    }

    private void garantirGarantias() {
        if (!garantiaService.listarTodas().isEmpty()) {
            return;
        }

        List<Venda> vendas = vendaService.listarVendas();
        if (!vendas.isEmpty()) {
            garantiaService.registarGarantia(vendas.get(0), "Defeito no acabamento do produto entregue.");
        }
    }
}
