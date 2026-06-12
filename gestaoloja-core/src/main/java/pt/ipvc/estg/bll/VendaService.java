package pt.ipvc.estg.bll;

import pt.ipvc.estg.dao.JpaUtil;
import pt.ipvc.estg.dao.VendaDAO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import pt.ipvc.estg.model.Cliente;
import pt.ipvc.estg.model.Produto;
import pt.ipvc.estg.model.Venda;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VendaService {

    private final VendaDAO vendaDAO = new VendaDAO();
    private final ClienteService clienteService = new ClienteService();

    public String realizarVenda(Cliente cliente, Produto produto, int quantidade) {
        Map<Produto, Integer> itens = new LinkedHashMap<>();
        itens.put(produto, quantidade);
        return realizarVenda(cliente, itens);
    }

    public String realizarVenda(Cliente cliente, Map<Produto, Integer> itens) {
        if (cliente == null || cliente.getId() <= 0) {
            cliente = clienteService.garantirConsumidorFinal();
        }

        if (itens == null || itens.isEmpty()) {
            return "Erro: adicione pelo menos um produto a venda.";
        }

        for (Map.Entry<Produto, Integer> item : itens.entrySet()) {
            if (item.getKey() == null || item.getValue() == null || item.getValue() <= 0) {
                return "Erro: existem produtos ou quantidades invalidas.";
            }
        }

        EntityManager em = JpaUtil.criarEntityManager();
        try {
            em.getTransaction().begin();

            Cliente clienteGerido = em.find(Cliente.class, cliente.getId());
            if (clienteGerido == null) {
                return rollbackComMensagem(em, "Erro: cliente nao encontrado.");
            }

            Venda venda = new Venda(clienteGerido);
            for (Map.Entry<Produto, Integer> item : itens.entrySet()) {
                Produto produtoGerido = em.find(Produto.class, item.getKey().getId(), LockModeType.PESSIMISTIC_WRITE);
                int quantidade = item.getValue();

                if (produtoGerido == null) {
                    return rollbackComMensagem(em, "Erro: produto nao encontrado.");
                }

                if (produtoGerido.getQuantidadeStock() < quantidade) {
                    return rollbackComMensagem(em, "Erro: stock insuficiente para " + produtoGerido.getNome() + ".");
                }

                produtoGerido.setQuantidadeStock(produtoGerido.getQuantidadeStock() - quantidade);
                venda.adicionarProduto(produtoGerido, quantidade);
            }

            em.persist(venda);
            em.getTransaction().commit();
            return "Venda n. " + venda.getId() + " realizada com sucesso.";
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            return "Erro ao registar venda: " + e.getMessage();
        } finally {
            em.close();
        }
    }

    public List<Venda> listarVendas() {
        return vendaDAO.buscarTodas();
    }

    public List<Venda> listarVendasDoDia() {
        return vendaDAO.buscarVendasDoDia();
    }

    public double totalVendasDoDia() {
        return listarVendasDoDia().stream()
                .mapToDouble(Venda::getTotal)
                .sum();
    }

    private String rollbackComMensagem(EntityManager em, String mensagem) {
        if (em.getTransaction().isActive()) {
            em.getTransaction().rollback();
        }
        return mensagem;
    }
}
