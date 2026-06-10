package dao;

import jakarta.persistence.EntityManager;
import model.Produto;

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
