package pt.ipvc.estg.web.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pt.ipvc.estg.model.Cliente;
import pt.ipvc.estg.model.EstadoGarantia;
import pt.ipvc.estg.model.Venda;
import pt.ipvc.estg.web.service.CarrinhoVendaItem;
import pt.ipvc.estg.web.service.LojaWebService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Controller
public class LojaWebController {

    private static final String CARRINHO_VENDA = "carrinhoVenda";

    private final LojaWebService lojaService;

    public LojaWebController(LojaWebService lojaService) {
        this.lojaService = lojaService;
    }

    @GetMapping("/")
    public String inicio() {
        return "redirect:/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("active", "dashboard");
        model.addAttribute("totalVendasHoje", lojaService.totalVendasHoje());
        model.addAttribute("totalProdutos", lojaService.totalProdutos());
        model.addAttribute("stockBaixo", lojaService.listarStockBaixo());
        model.addAttribute("ultimasVendas", lojaService.listarVendas());
        return "dashboard";
    }

    @GetMapping("/produtos")
    public String produtos(@RequestParam(name = "pesquisa", required = false) String pesquisa, Model model) {
        model.addAttribute("active", "produtos");
        model.addAttribute("pesquisa", pesquisa == null ? "" : pesquisa);
        model.addAttribute("produtos", lojaService.listarProdutos(pesquisa));
        model.addAttribute("totalProdutos", lojaService.totalProdutos());
        model.addAttribute("totalStock", lojaService.totalStock());
        model.addAttribute("stockBaixo", lojaService.listarStockBaixo().size());
        return "produtos";
    }

    @PostMapping("/produtos")
    public String guardarProduto(@RequestParam(value = "id_produto", defaultValue = "0") int idProduto,
                                 @RequestParam("nome") String nome,
                                 @RequestParam("descricao") String descricao,
                                 @RequestParam("preco") double preco,
                                 @RequestParam("stock") int stock,
                                 @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                 RedirectAttributes redirect) {
        try {
            String imagemUrl = guardarImagemProduto(imageFile);
            if (idProduto > 0) {
                lojaService.atualizarProduto(idProduto, nome, descricao, preco, stock, imagemUrl);
                redirect.addFlashAttribute("mensagem", "Produto atualizado com sucesso.");
            } else {
                lojaService.guardarProduto(nome, descricao, preco, stock, imagemUrl);
                redirect.addFlashAttribute("mensagem", "Produto guardado com sucesso.");
            }
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/produtos";
    }

    @PostMapping("/produtos/eliminar")
    public String eliminarProduto(@RequestParam("id_produto_eliminar") int id, RedirectAttributes redirect) {
        try {
            lojaService.eliminarProduto(id);
            redirect.addFlashAttribute("mensagem", "Produto eliminado com sucesso.");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/produtos";
    }

    private String guardarImagemProduto(MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) {
            return null;
        }

        String contentType = imageFile.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new IllegalArgumentException("O ficheiro selecionado deve ser uma imagem.");
        }

        String originalFilename = imageFile.getOriginalFilename();
        String cleanFilename = originalFilename == null
                ? "produto"
                : Paths.get(originalFilename).getFileName().toString().replaceAll("[^A-Za-z0-9._-]", "_");
        String filename = UUID.randomUUID() + "-" + cleanFilename;

        try {
            Path sourceUploadDir = resolverDiretorioStaticImages();
            Files.createDirectories(sourceUploadDir);
            Path uploadedFile = sourceUploadDir.resolve(filename);
            imageFile.transferTo(uploadedFile);

            Path targetStaticDir = Paths.get("gestaoloja-web", "target", "classes", "static");
            if (!Files.exists(targetStaticDir)) {
                targetStaticDir = Paths.get("target", "classes", "static");
            }
            if (Files.exists(targetStaticDir)) {
                Path targetUploadDir = targetStaticDir.resolve(Paths.get("images", "products"));
                Files.createDirectories(targetUploadDir);
                Files.copy(uploadedFile, targetUploadDir.resolve(filename));
            }

            return "/images/products/" + filename;
        } catch (IOException e) {
            throw new IllegalStateException("Nao foi possivel guardar a imagem do produto.", e);
        }
    }

    private Path resolverDiretorioStaticImages() {
        Path moduleStaticDir = Paths.get("gestaoloja-web", "src", "main", "resources", "static");
        if (Files.exists(moduleStaticDir)) {
            return moduleStaticDir.resolve(Paths.get("images", "products"));
        }
        return Paths.get("src", "main", "resources", "static", "images", "products");
    }

    @GetMapping("/clientes")
    public String clientes(@RequestParam(name = "clienteId", required = false) Integer clienteId, Model model) {
        model.addAttribute("active", "clientes");
        model.addAttribute("clientes", lojaService.listarClientes());
        if (clienteId != null && clienteId > 0) {
            try {
                Cliente clienteSelecionado = lojaService.obterCliente(clienteId);
                model.addAttribute("clienteSelecionado", clienteSelecionado);
                model.addAttribute("vendasCliente", lojaService.listarVendasCliente(clienteId));
            } catch (RuntimeException e) {
                model.addAttribute("erro", e.getMessage());
            }
        }
        return "clientes";
    }

    @PostMapping("/clientes")
    public String guardarCliente(@RequestParam(value = "id_cliente", defaultValue = "0") int idCliente,
                                 @RequestParam("nome") String nome,
                                 @RequestParam("contacto") String contacto,
                                 @RequestParam("nif") String nif,
                                 RedirectAttributes redirect) {
        try {
            Cliente clienteGuardado;
            if (idCliente > 0) {
                clienteGuardado = lojaService.atualizarCliente(idCliente, nome, contacto, nif);
                redirect.addFlashAttribute("mensagem", "Cliente atualizado com sucesso.");
            } else {
                clienteGuardado = lojaService.guardarCliente(nome, contacto, nif);
                redirect.addFlashAttribute("mensagem", "Cliente guardado com sucesso.");
            }
            return "redirect:/clientes?clienteId=" + clienteGuardado.getId();
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
            if (idCliente > 0) {
                return "redirect:/clientes?clienteId=" + idCliente;
            }
        }
        return "redirect:/clientes";
    }

    @PostMapping("/clientes/eliminar")
    public String eliminarCliente(@RequestParam("id_cliente_eliminar") int id, RedirectAttributes redirect) {
        try {
            lojaService.eliminarCliente(id);
            redirect.addFlashAttribute("mensagem", "Cliente eliminado com sucesso.");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/clientes";
    }

    @GetMapping("/vendas")
    public String vendas(Model model, HttpSession session) {
        List<CarrinhoVendaItem> carrinho = obterCarrinho(session);
        model.addAttribute("active", "vendas");
        model.addAttribute("clientes", lojaService.listarClientesIdentificados());
        model.addAttribute("produtos", lojaService.listarProdutos(null));
        model.addAttribute("vendas", lojaService.listarVendas());
        model.addAttribute("carrinho", carrinho);
        model.addAttribute("totalCarrinho", calcularTotalCarrinho(carrinho));
        return "vendas";
    }

    @PostMapping("/vendas/adicionar")
    public String adicionarProdutoVenda(@RequestParam("produtoId") int produtoId,
                                        @RequestParam("quantidade") int quantidade,
                                        HttpSession session,
                                        RedirectAttributes redirect) {
        try {
            lojaService.adicionarAoCarrinho(obterCarrinho(session), produtoId, quantidade);
            redirect.addFlashAttribute("mensagem", "Produto adicionado a fatura.");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/vendas";
    }

    @PostMapping("/vendas/remover")
    public String removerProdutoVenda(@RequestParam("produtoId") int produtoId,
                                      HttpSession session,
                                      RedirectAttributes redirect) {
        lojaService.removerDoCarrinho(obterCarrinho(session), produtoId);
        redirect.addFlashAttribute("mensagem", "Produto removido da fatura.");
        return "redirect:/vendas";
    }

    @PostMapping("/vendas/remover/{index}")
    public String removerItemVenda(@PathVariable("index") int index,
                                   HttpSession session,
                                   RedirectAttributes redirect) {
        List<CarrinhoVendaItem> carrinho = obterCarrinho(session);
        if (index >= 0 && index < carrinho.size()) {
            carrinho.remove(index);
            redirect.addFlashAttribute("mensagem", "Produto removido da fatura.");
        } else {
            redirect.addFlashAttribute("erro", "Item da fatura nao encontrado.");
        }
        return "redirect:/vendas";
    }

    @PostMapping("/vendas/limpar")
    public String limparVenda(HttpSession session, RedirectAttributes redirect) {
        session.removeAttribute(CARRINHO_VENDA);
        redirect.addFlashAttribute("mensagem", "Fatura limpa.");
        return "redirect:/vendas";
    }

    @PostMapping("/vendas/finalizar")
    public String finalizarVenda(@RequestParam(name = "clienteId", defaultValue = "0") int clienteId,
                                 HttpSession session,
                                 RedirectAttributes redirect) {
        try {
            lojaService.finalizarVenda(clienteId, obterCarrinho(session));
            session.removeAttribute(CARRINHO_VENDA);
            redirect.addFlashAttribute("mensagem", "Venda registada com sucesso.");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/vendas";
    }

    @GetMapping("/fatura/{id}")
    public String fatura(@PathVariable("id") int id, Model model) {
        Venda venda = lojaService.obterVendaComDetalhes(id);
        model.addAttribute("venda", venda);
        model.addAttribute("total", venda.getTotal());
        return "fatura";
    }

    @GetMapping("/garantias")
    public String garantias(Model model) {
        model.addAttribute("active", "garantias");
        model.addAttribute("garantias", lojaService.listarGarantias());
        model.addAttribute("vendas", lojaService.listarVendas());
        model.addAttribute("emAnalise", lojaService.contarGarantias(EstadoGarantia.EM_ANALISE));
        model.addAttribute("aprovadas", lojaService.contarGarantias(EstadoGarantia.APROVADA));
        model.addAttribute("rejeitadas", lojaService.contarGarantias(EstadoGarantia.REJEITADA));
        return "garantias";
    }

    @PostMapping("/garantias")
    public String registarGarantia(@RequestParam("vendaId") int vendaId,
                                   @RequestParam("motivo") String motivo,
                                   RedirectAttributes redirect) {
        try {
            lojaService.registarGarantia(vendaId, motivo);
            redirect.addFlashAttribute("mensagem", "Pedido de garantia registado.");
        } catch (RuntimeException e) {
            redirect.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/garantias";
    }

    @PostMapping("/garantias/{id}/aprovar")
    public String aprovarGarantia(@PathVariable("id") int id, RedirectAttributes redirect) {
        lojaService.atualizarEstadoGarantia(id, EstadoGarantia.APROVADA);
        redirect.addFlashAttribute("mensagem", "Garantia aprovada.");
        return "redirect:/garantias";
    }

    @PostMapping("/garantias/{id}/rejeitar")
    public String rejeitarGarantia(@PathVariable("id") int id, RedirectAttributes redirect) {
        lojaService.atualizarEstadoGarantia(id, EstadoGarantia.REJEITADA);
        redirect.addFlashAttribute("mensagem", "Garantia rejeitada.");
        return "redirect:/garantias";
    }

    @SuppressWarnings("unchecked")
    private List<CarrinhoVendaItem> obterCarrinho(HttpSession session) {
        Object carrinho = session.getAttribute(CARRINHO_VENDA);
        if (carrinho instanceof List<?>) {
            return (List<CarrinhoVendaItem>) carrinho;
        }

        List<CarrinhoVendaItem> novoCarrinho = new ArrayList<>();
        session.setAttribute(CARRINHO_VENDA, novoCarrinho);
        return novoCarrinho;
    }

    private double calcularTotalCarrinho(List<CarrinhoVendaItem> carrinho) {
        double total = 0.0;
        for (CarrinhoVendaItem item : carrinho) {
            total += item.getPrecoUnitario() * item.getQuantidade();
        }
        return total;
    }
}
