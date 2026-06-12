package pt.ipvc.estg.dao;

import jakarta.persistence.EntityManager;
import pt.ipvc.estg.model.EstadoGarantia;
import pt.ipvc.estg.model.Garantia;

import java.util.List;

public class GarantiaDAO {

    public Garantia inserir(Garantia garantia) {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            em.getTransaction().begin();
            Garantia guardada = em.merge(garantia);
            em.getTransaction().commit();
            return guardada;
        } catch (RuntimeException e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Garantia> buscarTodas() {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            return em.createQuery(
                            "SELECT g FROM Garantia g " +
                                    "JOIN FETCH g.cliente " +
                                    "JOIN FETCH g.venda " +
                                    "ORDER BY g.dataPedido DESC",
                            Garantia.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public long contarPorEstado(EstadoGarantia estado) {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            return em.createQuery("SELECT COUNT(g) FROM Garantia g WHERE g.estado = :estado", Long.class)
                    .setParameter("estado", estado)
                    .getSingleResult();
        } finally {
            em.close();
        }
    }

    public Garantia atualizarEstado(int id, EstadoGarantia estado) {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            em.getTransaction().begin();
            Garantia garantia = em.find(Garantia.class, id);
            if (garantia != null) {
                garantia.setEstado(estado);
            }
            em.getTransaction().commit();
            return garantia;
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
