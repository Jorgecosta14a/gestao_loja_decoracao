package model;

public enum EstadoGarantia {
    EM_ANALISE("Em analise"),
    APROVADA("Aprovada"),
    REJEITADA("Rejeitada");

    private final String descricao;

    EstadoGarantia(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
