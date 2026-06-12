package pt.ipvc.estg.model;

import jakarta.persistence.*;

import java.text.Normalizer;
import java.util.Locale;

@Entity
@Table(name = "produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_produto")
    private int id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false) // Removi o precision e scale para não dar erro com o double
    private double preco;

    @Column(name = "quantidade_stock", nullable = false)
    private int quantidadeStock;

    @Column(name = "imagem_url", length = 180)
    private String imagemUrl;

    public Produto() {
    }

    public Produto(String nome, String descricao, double preco, int quantidadeStock) {
        this(nome, descricao, preco, quantidadeStock, imagemPadrao(nome));
    }

    public Produto(String nome, String descricao, double preco, int quantidadeStock, String imagemUrl) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.quantidadeStock = quantidadeStock;
        this.imagemUrl = imagemUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public int getQuantidadeStock() {
        return quantidadeStock;
    }

    public void setQuantidadeStock(int quantidadeStock) {
        this.quantidadeStock = quantidadeStock;
    }

    public String getImagemUrl() {
        if (imagemUrl == null || imagemUrl.isBlank()) {
            return imagemPadrao(nome);
        }
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }

    public static String imagemPadrao(String nome) {
        String normalizado = normalizarNome(nome);

        if (normalizado.contains("sofa")) {
            return "/img/products/sofa-minimalista.png";
        }
        if (normalizado.contains("cadeira") || normalizado.contains("eames")) {
            return "/img/products/cadeira-eames.png";
        }
        if (normalizado.contains("mesa")) {
            return "/img/products/mesa-jantar.png";
        }
        if (normalizado.contains("candeeiro") || normalizado.contains("lamp")) {
            return "/img/products/candeeiro-loft.png";
        }
        if (normalizado.contains("vaso") || normalizado.contains("ceramica")) {
            return "/img/products/vaso-escultural.png";
        }

        return "/img/products/vaso-escultural.png";
    }

    private static String normalizarNome(String nome) {
        if (nome == null) {
            return "";
        }
        return Normalizer.normalize(nome, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
    }

    @Override
    public String toString() {
        return nome + " - " + quantidadeStock + " un.";
    }
}
