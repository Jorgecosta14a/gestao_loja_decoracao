package pt.ipvc.estg.web.repository;

import pt.ipvc.estg.model.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Integer> {
    List<Produto> findByQuantidadeStockLessThanEqualOrderByQuantidadeStockAsc(int limite);
    List<Produto> findByNomeContainingIgnoreCaseOrderByNomeAsc(String nome);
    boolean existsByNomeIgnoreCase(String nome);
}
