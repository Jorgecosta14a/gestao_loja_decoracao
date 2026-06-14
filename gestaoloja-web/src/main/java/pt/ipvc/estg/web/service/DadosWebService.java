package pt.ipvc.estg.web.service;

import jakarta.persistence.EntityManager;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pt.ipvc.estg.model.Cliente;
import pt.ipvc.estg.model.Garantia;
import pt.ipvc.estg.model.Produto;
import pt.ipvc.estg.model.Venda;
import pt.ipvc.estg.web.repository.ClienteRepository;
import pt.ipvc.estg.web.repository.GarantiaRepository;
import pt.ipvc.estg.web.repository.ProdutoRepository;
import pt.ipvc.estg.web.repository.VendaRepository;

import java.util.List;

@Component
public class DadosWebService implements ApplicationRunner {

    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final VendaRepository vendaRepository;
    private final GarantiaRepository garantiaRepository;
    private final EntityManager entityManager;

    public DadosWebService(ClienteRepository clienteRepository,
                           ProdutoRepository produtoRepository,
                           VendaRepository vendaRepository,
                           GarantiaRepository garantiaRepository,
                           EntityManager entityManager) {
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
        this.vendaRepository = vendaRepository;
        this.garantiaRepository = garantiaRepository;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        garantirClientes();
        garantirProdutos();
        limparProdutosDuplicados();
        garantirVenda();
        garantirGarantia();
    }

    private void garantirClientes() {
        garantirClienteConsumidorFinal();

        if (clienteRepository.count() <= 1) {
            clienteRepository.save(new Cliente("Joao Silva", "910000000", "222333444"));
            clienteRepository.save(new Cliente("Ana Ferreira", "911222333", "250250250"));
            clienteRepository.save(new Cliente("Marta Santos", "912345678", "233444555"));
        }
    }

    private void garantirClienteConsumidorFinal() {
        if (!clienteRepository.existsByNif(LojaWebService.NIF_CONSUMIDOR_FINAL)) {
            clienteRepository.save(new Cliente("Consumidor Final", "", LojaWebService.NIF_CONSUMIDOR_FINAL));
        }
    }

    private void garantirProdutos() {
        garantirProduto("Sofa Minimalista", "Veludo verde / pernas de carvalho", 1250.00, 14);
        garantirProduto("Cadeira Eames", "Pele preta / estrutura nogueira", 850.00, 28);
        garantirProduto("Mesa de Jantar", "Carvalho macico / 8 lugares", 2100.00, 2);
        garantirProduto("Vaso Escultural", "Ceramica mate / colecao zen", 145.00, 52);
        garantirProduto("Candeeiro Loft", "Metal preto / luz quente", 220.00, 3);
        atualizarImagensProdutos();
    }

    private void garantirProduto(String nome, String descricao, double preco, int stock) {
        if (produtoRepository.existsByNomeIgnoreCase(nome)) {
            return;
        }
        produtoRepository.save(new Produto(nome, descricao, preco, stock, Produto.imagemPadrao(nome)));
    }

    private void atualizarImagensProdutos() {
        List<Produto> produtos = produtoRepository.findAll();
        for (Produto produto : produtos) {
            produto.setImagemUrl(Produto.imagemPadrao(produto.getNome()));
        }
        produtoRepository.saveAll(produtos);
    }

    private void limparProdutosDuplicados() {
        List<Produto> produtos = entityManager.createQuery(
                        "SELECT p FROM Produto p WHERE LOWER(p.nome) = LOWER(:nome) ORDER BY p.id",
                        Produto.class)
                .setParameter("nome", "Mesa de Vidro")
                .getResultList();

        if (produtos.size() <= 1) {
            return;
        }

        Produto principal = produtos.get(0);
        for (int i = 1; i < produtos.size(); i++) {
            Produto duplicado = produtos.get(i);
            entityManager.createQuery("UPDATE LinhaVenda l SET l.produto = :principal WHERE l.produto = :duplicado")
                    .setParameter("principal", principal)
                    .setParameter("duplicado", duplicado)
                    .executeUpdate();
            produtoRepository.delete(duplicado);
        }
    }

    private void garantirVenda() {
        if (vendaRepository.count() > 0) {
            return;
        }

        List<Cliente> clientes = clienteRepository.findAll();
        List<Produto> produtos = produtoRepository.findAll();
        if (clientes.isEmpty() || produtos.isEmpty()) {
            return;
        }

        Produto produto = produtos.get(0);
        Venda venda = new Venda(clientes.get(0));
        venda.adicionarProduto(produto, 1);
        produto.setQuantidadeStock(Math.max(0, produto.getQuantidadeStock() - 1));
        vendaRepository.save(venda);
    }

    private void garantirGarantia() {
        if (garantiaRepository.count() > 0) {
            return;
        }

        List<Venda> vendas = vendaRepository.procurarTodasComDetalhes();
        if (!vendas.isEmpty()) {
            Venda venda = vendas.get(0);
            garantiaRepository.save(new Garantia(venda, venda.getCliente(), "Defeito no acabamento do produto entregue."));
        }
    }
}
