package com.santaxepa.entity;

import com.santaxepa.entity.enums.TipoProduto;

public class ItemCatalogo {

    private String id;
    private Produto produto;
    private int quantidadeDisponivel;
    private TipoProduto tipo;

    public ItemCatalogo(String id, Produto produto, int quantidadeDisponivel) {
        this.id = id;
        this.produto = produto;
        this.quantidadeDisponivel = quantidadeDisponivel;
        this.tipo = produto.getTipo();
    }

    public void ativar()       { produto = produto; }
    public void desativar()    { this.quantidadeDisponivel = 0; }
    public boolean estaAtivo() { return estaDisponivel(); }

    public int calcularTotalItens() { return quantidadeDisponivel; }

    public boolean estaDisponivel() {
        return produto.isDisponivel() && quantidadeDisponivel > 0;
    }

    /** Mensagem 10.1: verificarItensDisponiveis() para frutas */
    public boolean verificarFrutasDisponiveis() {
        return tipo == TipoProduto.FRUTA && estaDisponivel();
    }

    /** Mensagem 15.1: verificarItensDisponiveis() para legumes */
    public boolean verificarLegumesDisponiveis() {
        return tipo == TipoProduto.LEGUME && estaDisponivel();
    }

    /** Mensagem 20.1: verificarItensDisponiveis() para verduras */
    public boolean verificarVerdurasDisponiveis() {
        return tipo == TipoProduto.VERDURA && estaDisponivel();
    }

    /** Mensagem 10.2 / 15.2 / 20.2: buscarDetalhesProduto() */
    public Produto buscarDetalhesProduto() { return produto; }

    public void reservar(int qtd) {
        if (qtd <= quantidadeDisponivel) {
            this.quantidadeDisponivel -= qtd;
        }
    }

    public String getId()                { return id; }
    public Produto getProduto()          { return produto; }
    public int getQuantidadeDisponivel() { return quantidadeDisponivel; }
    public TipoProduto getTipo()         { return tipo; }

    @Override
    public String toString() {
        return String.format("%s | estoque: %d", produto.getNome(), quantidadeDisponivel);
    }
}