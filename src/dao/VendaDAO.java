package dao;

import model.Venda;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class VendaDAO {
    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("GestaoLojaDecoracaoPU");
    private EntityManager em = emf.createEntityManager();

    public void salvarVenda(Venda venda) {
        em.getTransaction().begin();
        em.persist(venda);
        em.getTransaction().commit();
    }

    public List<Venda> buscarTodas() {
        return em.createQuery("FROM Venda", Venda.class).getResultList();
    }
}