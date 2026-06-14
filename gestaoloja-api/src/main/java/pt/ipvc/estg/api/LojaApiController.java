package pt.ipvc.estg.api;

import pt.ipvc.estg.model.Cliente;
import pt.ipvc.estg.model.EstadoGarantia;
import pt.ipvc.estg.model.Garantia;
import pt.ipvc.estg.model.LinhaVenda;
import pt.ipvc.estg.model.Produto;
import pt.ipvc.estg.model.Venda;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ipvc.estg.web.service.LojaWebService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class LojaApiController {

    private final LojaWebService lojaService;

    public LojaApiController(LojaWebService lojaService) {
        this.lojaService = lojaService;
    }

    @GetMapping("/dashboard")
    public DashboardResumoResponse dashboard() {
        return new DashboardResumoResponse(
                lojaService.totalVendasHoje(),
                lojaService.totalProdutos(),
                lojaService.totalStock(),
                lojaService.listarStockBaixo().size(),
                lojaService.contarGarantias(EstadoGarantia.EM_ANALISE),
                lojaService.contarGarantias(EstadoGarantia.APROVADA),
                lojaService.contarGarantias(EstadoGarantia.REJEITADA)
        );
    }

    @GetMapping("/clientes")
    public List<ClienteResponse> listarClientes() {
        return lojaService.listarClientes().stream()
                .map(this::toClienteResponse)
                .toList();
    }

    @PostMapping("/clientes")
    public ResponseEntity<?> criarCliente(@RequestBody ClienteRequest request) {
        try {
            Cliente cliente = lojaService.guardarCliente(request.nome(), request.contacto(), request.nif());
            return ResponseEntity.status(HttpStatus.CREATED).body(toClienteResponse(cliente));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResponse(e.getMessage()));
        }
    }

    @GetMapping("/produtos")
    public List<ProdutoResponse> listarProdutos() {
        return lojaService.listarProdutos(null).stream()
                .map(this::toProdutoResponse)
                .toList();
    }

    @PostMapping("/produtos")
    public ResponseEntity<?> criarProduto(@RequestBody ProdutoRequest request) {
        try {
            Produto produto = lojaService.guardarProduto(
                    request.nome(),
                    request.descricao(),
                    request.preco(),
                    request.stock()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(toProdutoResponse(produto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResponse(e.getMessage()));
        }
    }

    @GetMapping("/vendas")
    public List<VendaResponse> listarVendas() {
        return lojaService.listarVendas().stream()
                .map(this::toVendaResponse)
                .toList();
    }

    @PostMapping("/vendas")
    public ResponseEntity<?> registarVenda(@RequestBody VendaRequest request) {
        try {
            Venda venda = lojaService.registarVenda(
                    request.clienteId() == null ? 0 : request.clienteId(),
                    request.produtoId(),
                    request.quantidade()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(toVendaResponse(venda));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResponse(e.getMessage()));
        }
    }

    @GetMapping("/garantias")
    public List<GarantiaResponse> listarGarantias() {
        return lojaService.listarGarantias().stream()
                .map(this::toGarantiaResponse)
                .toList();
    }

    @PostMapping("/garantias")
    public ResponseEntity<?> registarGarantia(@RequestBody GarantiaRequest request) {
        try {
            Garantia garantia = lojaService.registarGarantia(request.vendaId(), request.motivo());
            return ResponseEntity.status(HttpStatus.CREATED).body(toGarantiaResponse(garantia));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResponse(e.getMessage()));
        }
    }

    @PatchMapping("/garantias/{id}/estado")
    public ResponseEntity<?> atualizarGarantia(@PathVariable int id, @RequestBody EstadoGarantiaRequest request) {
        try {
            EstadoGarantia estado = EstadoGarantia.valueOf(request.estado());
            lojaService.atualizarEstadoGarantia(id, estado);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ErroResponse("Estado de garantia invalido."));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErroResponse(e.getMessage()));
        }
    }

    private ClienteResponse toClienteResponse(Cliente cliente) {
        return new ClienteResponse(cliente.getId(), cliente.getNome(), cliente.getContacto(), cliente.getNif());
    }

    private ProdutoResponse toProdutoResponse(Produto produto) {
        return new ProdutoResponse(
                produto.getId(),
                produto.getNome(),
                produto.getDescricao(),
                produto.getPreco(),
                produto.getQuantidadeStock(),
                produto.getImagemUrl()
        );
    }

    private VendaResponse toVendaResponse(Venda venda) {
        List<LinhaVendaResponse> linhas = venda.getLinhasVenda().stream()
                .map(this::toLinhaVendaResponse)
                .toList();
        return new VendaResponse(
                venda.getId(),
                venda.getCliente().getId(),
                venda.getCliente().getNome(),
                venda.getDataVenda(),
                venda.getTotal(),
                linhas
        );
    }

    private LinhaVendaResponse toLinhaVendaResponse(LinhaVenda linha) {
        return new LinhaVendaResponse(
                linha.getProduto().getId(),
                linha.getProduto().getNome(),
                linha.getQuantidade(),
                linha.getPrecoUnitario()
        );
    }

    private GarantiaResponse toGarantiaResponse(Garantia garantia) {
        return new GarantiaResponse(
                garantia.getId(),
                garantia.getVenda().getId(),
                garantia.getCliente().getId(),
                garantia.getCliente().getNome(),
                garantia.getMotivo(),
                garantia.getEstado().name(),
                garantia.getEstado().getDescricao()
        );
    }

    public record DashboardResumoResponse(double totalVendasHoje,
                                          long totalProdutos,
                                          long totalStock,
                                          int produtosStockBaixo,
                                          long garantiasEmAnalise,
                                          long garantiasAprovadas,
                                          long garantiasRejeitadas) {
    }

    public record ClienteRequest(String nome, String contacto, String nif) {
    }

    public record ClienteResponse(int id, String nome, String contacto, String nif) {
    }

    public record ProdutoRequest(String nome, String descricao, double preco, int stock) {
    }

    public record ProdutoResponse(int id,
                                  String nome,
                                  String descricao,
                                  double preco,
                                  int quantidadeStock,
                                  String imagemUrl) {
    }

    public record VendaRequest(Integer clienteId, int produtoId, int quantidade) {
    }

    public record VendaResponse(int id,
                                int clienteId,
                                String clienteNome,
                                LocalDateTime dataVenda,
                                double total,
                                List<LinhaVendaResponse> linhas) {
    }

    public record LinhaVendaResponse(int produtoId, String produtoNome, int quantidade, double precoUnitario) {
    }

    public record GarantiaRequest(int vendaId, String motivo) {
    }

    public record GarantiaResponse(int id,
                                   int vendaId,
                                   int clienteId,
                                   String clienteNome,
                                   String motivo,
                                   String estado,
                                   String estadoDescricao) {
    }

    public record EstadoGarantiaRequest(String estado) {
    }

    public record ErroResponse(String erro) {
    }
}
