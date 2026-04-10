package dao;

import model.Produto;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class ProdutoDAO {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("GestaoLojaDecoracaoPU");
    private EntityManager em = emf.createEntityManager();

    public void inserirProduto(Produto produto) {
        em.getTransaction().begin();
        em.persist(produto);
        em.getTransaction().commit();
    }

    public Produto buscarProdutoPorId(int id) {
        return em.find(Produto.class, id);
    }

    public List<Produto> buscarTodosProdutos() {
        return em.createQuery("FROM Produto", Produto.class).getResultList();
    }

    public void atualizarProduto(Produto produto) {
        em.getTransaction().begin();
        em.merge(produto);
        em.getTransaction().commit();
    }
}