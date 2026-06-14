package pt.ipvc.estg.web.repository;

import pt.ipvc.estg.model.EstadoGarantia;
import pt.ipvc.estg.model.Garantia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GarantiaRepository extends JpaRepository<Garantia, Integer> {

    @Query("SELECT g FROM Garantia g " +
            "JOIN FETCH g.cliente " +
            "JOIN FETCH g.venda " +
            "ORDER BY g.dataPedido DESC")
    List<Garantia> procurarTodasComDetalhes();

    long countByEstado(EstadoGarantia estado);
}
