package pt.ipvc.estg.web.repository;

import pt.ipvc.estg.model.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VendaRepository extends JpaRepository<Venda, Integer> {

    @Query("SELECT DISTINCT v FROM Venda v " +
            "JOIN FETCH v.cliente " +
            "LEFT JOIN FETCH v.linhasVenda l " +
            "LEFT JOIN FETCH l.produto " +
            "ORDER BY v.dataVenda DESC")
    List<Venda> procurarTodasComDetalhes();

    @Query("SELECT DISTINCT v FROM Venda v " +
            "JOIN FETCH v.cliente " +
            "LEFT JOIN FETCH v.linhasVenda l " +
            "LEFT JOIN FETCH l.produto " +
            "WHERE v.dataVenda >= :inicio AND v.dataVenda < :fim " +
            "ORDER BY v.dataVenda DESC")
    List<Venda> procurarDoDiaComDetalhes(@Param("inicio") LocalDateTime inicio,
                                         @Param("fim") LocalDateTime fim);

    @Query("SELECT DISTINCT v FROM Venda v " +
            "JOIN FETCH v.cliente " +
            "LEFT JOIN FETCH v.linhasVenda l " +
            "LEFT JOIN FETCH l.produto " +
            "WHERE v.id = :id")
    Optional<Venda> procurarPorIdComDetalhes(@Param("id") int id);

    @Query("SELECT DISTINCT v FROM Venda v " +
            "JOIN FETCH v.cliente " +
            "LEFT JOIN FETCH v.linhasVenda l " +
            "LEFT JOIN FETCH l.produto " +
            "WHERE v.cliente.id = :clienteId " +
            "ORDER BY v.dataVenda DESC")
    List<Venda> procurarPorClienteComDetalhes(@Param("clienteId") int clienteId);
}
