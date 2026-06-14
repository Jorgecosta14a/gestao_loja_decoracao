package pt.ipvc.estg.dao;

import jakarta.persistence.EntityManager;
import pt.ipvc.estg.model.Venda;

import java.time.LocalDateTime;
import java.util.List;

public class VendaDAO {

    public List<Venda> buscarTodas() {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            return em.createQuery(
                            "SELECT DISTINCT v FROM Venda v " +
                                    "JOIN FETCH v.cliente " +
                                    "LEFT JOIN FETCH v.linhasVenda l " +
                                    "LEFT JOIN FETCH l.produto " +
                                    "ORDER BY v.dataVenda DESC",
                            Venda.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Venda> buscarVendasDoDia() {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            LocalDateTime inicio = LocalDateTime.now().toLocalDate().atStartOfDay();
            LocalDateTime fim = inicio.plusDays(1);
            return em.createQuery(
                            "SELECT DISTINCT v FROM Venda v " +
                                    "JOIN FETCH v.cliente " +
                                    "LEFT JOIN FETCH v.linhasVenda l " +
                                    "LEFT JOIN FETCH l.produto " +
                                    "WHERE v.dataVenda >= :inicio AND v.dataVenda < :fim " +
                                    "ORDER BY v.dataVenda DESC",
                            Venda.class)
                    .setParameter("inicio", inicio)
                    .setParameter("fim", fim)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Venda> buscarPorCliente(int clienteId) {
        EntityManager em = JpaUtil.criarEntityManager();
        try {
            return em.createQuery(
                            "SELECT DISTINCT v FROM Venda v " +
                                    "JOIN FETCH v.cliente " +
                                    "LEFT JOIN FETCH v.linhasVenda l " +
                                    "LEFT JOIN FETCH l.produto " +
                                    "WHERE v.cliente.id = :clienteId " +
                                    "ORDER BY v.dataVenda DESC",
                            Venda.class)
                    .setParameter("clienteId", clienteId)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
