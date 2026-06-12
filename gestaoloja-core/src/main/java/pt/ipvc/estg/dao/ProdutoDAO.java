package pt.ipvc.estg.dao;

import jakarta.persistence.EntityManager;
import pt.ipvc.estg.model.Produto;

import java.util.List;

public class ProdutoDAO {

    public Produto inserirProduto(Produto produto) {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(produto);
            em.getTransaction().commit();
            return produto;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Produto buscarProdutoPorId(int id) {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            return em.find(Produto.class, id);
        } finally {
            em.close();
        }
    }

    public List<Produto> buscarTodosProdutos() {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            return em.createQuery("SELECT p FROM Produto p ORDER BY p.nome", Produto.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Produto> buscarProdutosComStockBaixo(int limite) {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            return em.createQuery(
                            "SELECT p FROM Produto p WHERE p.quantidadeStock <= :limite ORDER BY p.quantidadeStock",
                            Produto.class)
                    .setParameter("limite", limite)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Produto atualizarProduto(Produto produto) {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            em.getTransaction().begin();
            Produto atualizado = em.merge(produto);
            em.getTransaction().commit();
            return atualizado;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void removerProdutoSeSemVendas(int id) {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            em.getTransaction().begin();
            Produto produto = em.find(Produto.class, id);
            if (produto == null) {
                throw new IllegalArgumentException("Produto nao encontrado.");
            }

            Long totalLinhas = em.createQuery(
                            "SELECT COUNT(l) FROM LinhaVenda l WHERE l.produto.id = :id",
                            Long.class)
                    .setParameter("id", id)
                    .getSingleResult();

            if (totalLinhas != null && totalLinhas > 0) {
                throw new IllegalStateException("Nao e possivel eliminar este produto porque ja esta associado a uma venda.");
            }

            em.remove(produto);
            em.getTransaction().commit();
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public int removerDuplicadosPorNome(String nome) {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            em.getTransaction().begin();
            List<Produto> produtos = em.createQuery(
                            "SELECT p FROM Produto p WHERE LOWER(p.nome) = LOWER(:nome) ORDER BY p.id",
                            Produto.class)
                    .setParameter("nome", nome)
                    .getResultList();

            if (produtos.size() <= 1) {
                em.getTransaction().commit();
                return 0;
            }

            Produto principal = produtos.get(0);
            int removidos = 0;
            for (int i = 1; i < produtos.size(); i++) {
                Produto duplicado = produtos.get(i);
                em.createQuery("UPDATE LinhaVenda l SET l.produto = :principal WHERE l.produto = :duplicado")
                        .setParameter("principal", principal)
                        .setParameter("duplicado", duplicado)
                        .executeUpdate();
                em.remove(duplicado);
                removidos++;
            }

            em.getTransaction().commit();
            return removidos;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public long contarProdutos() {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            return em.createQuery("SELECT COUNT(p) FROM Produto p", Long.class).getSingleResult();
        } finally {
            em.close();
        }
    }

    public long totalStock() {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            Long total = em.createQuery("SELECT COALESCE(SUM(p.quantidadeStock), 0) FROM Produto p", Long.class)
                    .getSingleResult();
            return total == null ? 0 : total;
        } finally {
            em.close();
        }
    }
}
