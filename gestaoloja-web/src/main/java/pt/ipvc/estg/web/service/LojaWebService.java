package pt.ipvc.estg.web.service;

import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.ipvc.estg.model.Cliente;
import pt.ipvc.estg.model.EstadoGarantia;
import pt.ipvc.estg.model.Garantia;
import pt.ipvc.estg.model.Produto;
import pt.ipvc.estg.model.Venda;
import pt.ipvc.estg.web.repository.ClienteRepository;
import pt.ipvc.estg.web.repository.GarantiaRepository;
import pt.ipvc.estg.web.repository.ProdutoRepository;
import pt.ipvc.estg.web.repository.VendaRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class LojaWebService {

    private static final int LIMITE_STOCK_BAIXO = 5;
    public static final String NIF_CONSUMIDOR_FINAL = "999999990";

    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;
    private final VendaRepository vendaRepository;
    private final GarantiaRepository garantiaRepository;
    private final EntityManager entityManager;

    public LojaWebService(ClienteRepository clienteRepository,
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

    public List<Cliente> listarClientes() {
        return clienteRepository.findAll().stream()
                .sorted(Comparator.comparing(Cliente::getNome))
                .toList();
    }

    public List<Cliente> listarClientesIdentificados() {
        return listarClientes().stream()
                .filter(cliente -> !NIF_CONSUMIDOR_FINAL.equals(cliente.getNif()))
                .toList();
    }

    public Cliente obterCliente(int id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente nao encontrado."));
    }

    public List<Produto> listarProdutos(String pesquisa) {
        if (pesquisa != null && !pesquisa.trim().isEmpty()) {
            return produtoRepository.findByNomeContainingIgnoreCaseOrderByNomeAsc(pesquisa.trim());
        }
        return produtoRepository.findAll().stream()
                .sorted(Comparator.comparing(Produto::getNome))
                .toList();
    }

    public List<Produto> listarStockBaixo() {
        return produtoRepository.findByQuantidadeStockLessThanEqualOrderByQuantidadeStockAsc(LIMITE_STOCK_BAIXO);
    }

    public List<Venda> listarVendas() {
        return vendaRepository.procurarTodasComDetalhes();
    }

    public Venda obterVendaComDetalhes(int id) {
        return vendaRepository.procurarPorIdComDetalhes(id)
                .orElseThrow(() -> new IllegalArgumentException("Venda nao encontrada."));
    }

    public List<Venda> listarVendasCliente(int clienteId) {
        return vendaRepository.procurarPorClienteComDetalhes(clienteId);
    }

    public List<Venda> listarVendasHoje() {
        LocalDateTime inicio = LocalDateTime.now().toLocalDate().atStartOfDay();
        return vendaRepository.procurarDoDiaComDetalhes(inicio, inicio.plusDays(1));
    }

    public List<Garantia> listarGarantias() {
        return garantiaRepository.procurarTodasComDetalhes();
    }

    public long totalProdutos() {
        return produtoRepository.count();
    }

    public long totalStock() {
        return produtoRepository.findAll().stream()
                .mapToLong(Produto::getQuantidadeStock)
                .sum();
    }

    public double totalVendasHoje() {
        return listarVendasHoje().stream()
                .mapToDouble(Venda::getTotal)
                .sum();
    }

    public long contarGarantias(EstadoGarantia estado) {
        return garantiaRepository.countByEstado(estado);
    }

    @Transactional
    public Cliente guardarCliente(String nome, String contacto, String nif) {
        validarCliente(nome);
        String nifTratado = normalizarNif(nif);
        if (!nifTratado.isEmpty() && clienteRepository.existsByNif(nifTratado)) {
            throw new IllegalArgumentException("Ja existe um cliente com esse NIF.");
        }

        Cliente cliente = new Cliente(
                nome.trim(),
                contacto == null ? "" : contacto.trim(),
                nifTratado
        );
        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente atualizarCliente(int id, String nome, String contacto, String nif) {
        validarCliente(nome);
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente nao encontrado."));

        String nifTratado = normalizarNif(nif);
        if (!nifTratado.isEmpty()) {
            Long clientesComNif = entityManager.createQuery(
                            "SELECT COUNT(c) FROM Cliente c WHERE c.nif = :nif AND c.id <> :id",
                            Long.class)
                    .setParameter("nif", nifTratado)
                    .setParameter("id", id)
                    .getSingleResult();

            if (clientesComNif != null && clientesComNif > 0) {
                throw new IllegalArgumentException("Ja existe outro cliente com esse NIF.");
            }
        }

        cliente.setNome(nome.trim());
        cliente.setContacto(contacto == null ? "" : contacto.trim());
        cliente.setNif(nifTratado);
        return clienteRepository.save(cliente);
    }

    @Transactional
    public void eliminarCliente(int id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente nao encontrado."));

        if (NIF_CONSUMIDOR_FINAL.equals(cliente.getNif())) {
            throw new IllegalStateException("Nao e possivel eliminar o cliente Consumidor Final.");
        }

        Long totalVendas = entityManager.createQuery(
                        "SELECT COUNT(v) FROM Venda v WHERE v.cliente.id = :id",
                        Long.class)
                .setParameter("id", id)
                .getSingleResult();
        Long totalGarantias = entityManager.createQuery(
                        "SELECT COUNT(g) FROM Garantia g WHERE g.cliente.id = :id",
                        Long.class)
                .setParameter("id", id)
                .getSingleResult();

        if ((totalVendas != null && totalVendas > 0) || (totalGarantias != null && totalGarantias > 0)) {
            throw new IllegalStateException("Nao e possivel eliminar este cliente porque ja esta associado a vendas ou garantias.");
        }

        clienteRepository.delete(cliente);
    }

    @Transactional
    public Produto guardarProduto(String nome, String descricao, double preco, int stock) {
        return guardarProduto(nome, descricao, preco, stock, null);
    }

    @Transactional
    public Produto guardarProduto(String nome, String descricao, double preco, int stock, String imagemUrl) {
        validarProduto(nome, preco, stock);
        Produto produto = new Produto(
                nome.trim(),
                descricao == null ? "" : descricao.trim(),
                preco,
                stock,
                imagemUrl == null || imagemUrl.isBlank() ? Produto.imagemPadrao(nome) : imagemUrl
        );
        return produtoRepository.save(produto);
    }

    @Transactional
    public Produto atualizarProduto(int id, String nome, String descricao, double preco, int stock) {
        return atualizarProduto(id, nome, descricao, preco, stock, null);
    }

    @Transactional
    public Produto atualizarProduto(int id, String nome, String descricao, double preco, int stock, String imagemUrl) {
        validarProduto(nome, preco, stock);
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto nao encontrado."));

        produto.setNome(nome.trim());
        produto.setDescricao(descricao == null ? "" : descricao.trim());
        produto.setPreco(preco);
        produto.setQuantidadeStock(stock);
        if (imagemUrl != null && !imagemUrl.isBlank()) {
            produto.setImagemUrl(imagemUrl);
        }
        return produtoRepository.save(produto);
    }

    @Transactional
    public void eliminarProduto(int id) {
        Produto produto = produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto nao encontrado."));

        Long totalLinhas = entityManager.createQuery(
                        "SELECT COUNT(l) FROM LinhaVenda l WHERE l.produto.id = :id",
                        Long.class)
                .setParameter("id", id)
                .getSingleResult();

        if (totalLinhas != null && totalLinhas > 0) {
            throw new IllegalStateException("Nao e possivel eliminar este produto porque ja esta associado a uma venda.");
        }

        produtoRepository.delete(produto);
    }

    @Transactional
    public Venda registarVenda(int clienteId, int produtoId, int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser superior a zero.");
        }

        Cliente cliente = clienteId <= 0
                ? garantirConsumidorFinal()
                : clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente nao encontrado."));
        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto nao encontrado."));

        if (produto.getQuantidadeStock() < quantidade) {
            throw new IllegalArgumentException("Stock insuficiente para " + produto.getNome() + ".");
        }

        Venda venda = new Venda(cliente);
        venda.adicionarProduto(produto, quantidade);
        produto.setQuantidadeStock(produto.getQuantidadeStock() - quantidade);
        return vendaRepository.save(venda);
    }

    public void adicionarAoCarrinho(List<CarrinhoVendaItem> carrinho, int produtoId, int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser superior a zero.");
        }

        Produto produto = produtoRepository.findById(produtoId)
                .orElseThrow(() -> new IllegalArgumentException("Produto nao encontrado."));

        CarrinhoVendaItem itemExistente = procurarItemCarrinho(carrinho, produtoId);
        int quantidadeAtual = itemExistente == null ? 0 : itemExistente.getQuantidade();
        int quantidadeFinal = quantidadeAtual + quantidade;

        if (produto.getQuantidadeStock() < quantidadeFinal) {
            throw new IllegalArgumentException("Stock insuficiente para " + produto.getNome() + ".");
        }

        if (itemExistente == null) {
            carrinho.add(new CarrinhoVendaItem(produto.getId(), produto.getNome(), quantidade, produto.getPreco()));
        } else {
            itemExistente.setQuantidade(quantidadeFinal);
            itemExistente.setNomeProduto(produto.getNome());
            itemExistente.setPrecoUnitario(produto.getPreco());
        }
    }

    public void removerDoCarrinho(List<CarrinhoVendaItem> carrinho, int produtoId) {
        carrinho.removeIf(item -> item.getProdutoId() == produtoId);
    }

    public double totalCarrinho(List<CarrinhoVendaItem> carrinho) {
        return carrinho.stream()
                .mapToDouble(CarrinhoVendaItem::getSubtotal)
                .sum();
    }

    @Transactional
    public Venda finalizarVenda(int clienteId, List<CarrinhoVendaItem> carrinho) {
        if (carrinho == null || carrinho.isEmpty()) {
            throw new IllegalArgumentException("Adicione pelo menos um produto a fatura.");
        }

        Cliente cliente = clienteId <= 0
                ? garantirConsumidorFinal()
                : clienteRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente nao encontrado."));

        Venda venda = new Venda(cliente);

        for (CarrinhoVendaItem item : carrinho) {
            Produto produto = produtoRepository.findById(item.getProdutoId())
                    .orElseThrow(() -> new IllegalArgumentException("Produto nao encontrado."));

            if (item.getQuantidade() <= 0) {
                throw new IllegalArgumentException("Quantidade invalida para " + produto.getNome() + ".");
            }

            if (produto.getQuantidadeStock() < item.getQuantidade()) {
                throw new IllegalArgumentException("Stock insuficiente para " + produto.getNome() + ".");
            }

            venda.adicionarProduto(produto, item.getQuantidade());
            produto.setQuantidadeStock(produto.getQuantidadeStock() - item.getQuantidade());
        }

        return vendaRepository.save(venda);
    }

    @Transactional
    public Garantia registarGarantia(int vendaId, String motivo) {
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("Indique o motivo da garantia.");
        }

        Venda venda = vendaRepository.findById(vendaId)
                .orElseThrow(() -> new IllegalArgumentException("Venda nao encontrada."));
        Garantia garantia = new Garantia(venda, venda.getCliente(), motivo.trim());
        return garantiaRepository.save(garantia);
    }

    @Transactional
    public void atualizarEstadoGarantia(int id, EstadoGarantia estado) {
        Garantia garantia = garantiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Garantia nao encontrada."));
        garantia.setEstado(estado);
    }

    private Cliente garantirConsumidorFinal() {
        return clienteRepository.findByNif(NIF_CONSUMIDOR_FINAL)
                .orElseGet(() -> clienteRepository.save(new Cliente("Consumidor Final", "", NIF_CONSUMIDOR_FINAL)));
    }

    private CarrinhoVendaItem procurarItemCarrinho(List<CarrinhoVendaItem> carrinho, int produtoId) {
        return carrinho.stream()
                .filter(item -> item.getProdutoId() == produtoId)
                .findFirst()
                .orElse(null);
    }

    private void validarProduto(String nome, double preco, int stock) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Indique o nome do produto.");
        }
        if (preco <= 0 || stock < 0) {
            throw new IllegalArgumentException("Preco ou stock invalido.");
        }
    }

    private void validarCliente(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Indique o nome do cliente.");
        }
    }

    private String normalizarNif(String nif) {
        return nif == null ? "" : nif.trim();
    }
}
