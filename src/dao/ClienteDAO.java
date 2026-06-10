package dao;

import jakarta.persistence.EntityManager;
import model.Cliente;

import java.util.List;

public class ClienteDAO {

    public Cliente inserirCliente(Cliente cliente) {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(cliente);
            em.getTransaction().commit();
            return cliente;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Cliente buscarClientePorId(int id) {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            return em.find(Cliente.class, id);
        } finally {
            em.close();
        }
    }

    public List<Cliente> buscarTodosClientes() {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            return em.createQuery("SELECT c FROM Cliente c ORDER BY c.nome", Cliente.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Cliente buscarPorNif(String nif) {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            List<Cliente> clientes = em.createQuery("SELECT c FROM Cliente c WHERE c.nif = :nif", Cliente.class)
                    .setParameter("nif", nif)
                    .getResultList();
            return clientes.isEmpty() ? null : clientes.get(0);
        } finally {
            em.close();
        }
    }

    public Cliente atualizarCliente(Cliente cliente) {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            em.getTransaction().begin();
            Cliente atualizado = em.merge(cliente);
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

    public void removerCliente(int id) {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            Cliente cliente = em.find(Cliente.class, id);
            if (cliente != null) {
                em.getTransaction().begin();
                em.remove(cliente);
                em.getTransaction().commit();
            }
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
