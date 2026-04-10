package dao;

import model.Cliente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.util.List;

public class ClienteDAO {

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("GestaoLojaDecoracaoPU");
    private EntityManager em = emf.createEntityManager();

    public void inserirCliente(Cliente cliente) {
        em.getTransaction().begin();
        em.persist(cliente);
        em.getTransaction().commit();
    }

    public Cliente buscarClientePorId(int id) {
        return em.find(Cliente.class, id);
    }

    public List<Cliente> buscarTodosClientes() {
        return em.createQuery("FROM Cliente", Cliente.class).getResultList();
    }

    public Cliente buscarPorNif(String nif) {
        try {
            return em.createQuery("SELECT c FROM Cliente c WHERE c.nif = :nif", Cliente.class)
                    .setParameter("nif", nif)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    public void atualizarCliente(Cliente cliente) {
        em.getTransaction().begin();
        em.merge(cliente);
        em.getTransaction().commit();
    }

    public void removerCliente(int id) {
        Cliente cliente = em.find(Cliente.class, id);
        if (cliente != null) {
            em.getTransaction().begin();
            em.remove(cliente);
            em.getTransaction().commit();
        }
    }
}