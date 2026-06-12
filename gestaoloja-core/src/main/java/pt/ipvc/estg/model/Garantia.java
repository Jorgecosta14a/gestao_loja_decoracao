package pt.ipvc.estg.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "garantia")
public class Garantia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_garantia")
    private int id;

    @ManyToOne
    @JoinColumn(name = "id_venda", nullable = false)
    private Venda venda;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Column(nullable = false, length = 250)
    private String motivo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoGarantia estado;

    @Column(name = "data_pedido", nullable = false)
    private LocalDateTime dataPedido;

    public Garantia() {
        this.estado = EstadoGarantia.EM_ANALISE;
        this.dataPedido = LocalDateTime.now();
    }

    public Garantia(Venda venda, Cliente cliente, String motivo) {
        this();
        this.venda = venda;
        this.cliente = cliente;
        this.motivo = motivo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Venda getVenda() {
        return venda;
    }

    public void setVenda(Venda venda) {
        this.venda = venda;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public EstadoGarantia getEstado() {
        return estado;
    }

    public void setEstado(EstadoGarantia estado) {
        this.estado = estado;
    }

    public LocalDateTime getDataPedido() {
        return dataPedido;
    }

    public void setDataPedido(LocalDateTime dataPedido) {
        this.dataPedido = dataPedido;
    }
}
