package com.santaxepa.entity;

/**
 * Entidade ItemCatalogo. Representa um produto disponível no catálogo semanal,
 * com sua quantidade em estoque e flag de disponibilidade.
 */
public class ItemCatalogo {

    private String id;
    private Produto produto;
    private int quantidadeDisponivel;
    private boolean disponivel;

    public ItemCatalogo(String id, Produto produto, int quantidadeDisponivel) {
        this.id = id;
        this.produto = produto;
        this.quantidadeDisponivel = quantidadeDisponivel;
        this.disponivel = quantidadeDisponivel > 0;
    }

    /** Mensagem 10.2 / 15.2 / 20.2: buscarDetalhesProduto() */
    public Produto buscarDetalhesProduto() {
        return this.produto;
    }

    public boolean estaDisponivel() {
        return disponivel && quantidadeDisponivel > 0;
    }

    public void reservar(int qtd) {
        if (qtd <= quantidadeDisponivel) {
            this.quantidadeDisponivel -= qtd;
            if (this.quantidadeDisponivel == 0) this.disponivel = false;
        }
    }

    public String getId() { return id; }
    public Produto getProduto() { return produto; }
    public int getQuantidadeDisponivel() { return quantidadeDisponivel; }

    @Override
    public String toString() {
        return String.format("%s | estoque: %d", produto, quantidadeDisponivel);
    }
}
