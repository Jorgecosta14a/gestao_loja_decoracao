package model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "venda")
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_venda")
    private int id;

    @Column(name = "data_venda", nullable = false)
    private LocalDateTime dataVenda;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LinhaVenda> linhasVenda = new ArrayList<>();

    public Venda() {
        this.dataVenda = LocalDateTime.now();
    }

    public Venda(Cliente cliente) {
        this();
        this.cliente = cliente;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDateTime getDataVenda() { return dataVenda; }
    public void setDataVenda(LocalDateTime dataVenda) { this.dataVenda = dataVenda; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public List<LinhaVenda> getLinhasVenda() { return linhasVenda; }
    public void setLinhasVenda(List<LinhaVenda> linhasVenda) { this.linhasVenda = linhasVenda; }

    public void adicionarProduto(Produto produto, int quantidade) {
        LinhaVenda linha = new LinhaVenda(this, produto, quantidade, produto.getPreco());
        this.linhasVenda.add(linha);
    }
}