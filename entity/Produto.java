package com.santaxepa.entity;

import com.santaxepa.entity.enums.TipoProduto;

/**
 * Entidade Produto. Representa o produto físico (nome, tipo, valor unitário etc.).
 */
public class Produto {

    private String id;
    private String nome;
    private TipoProduto tipo;
    private String unidadeMedida; // kg, un, maço
    private double precoUnitario;
    private String descricao;

    public Produto(String id, String nome, TipoProduto tipo, String unidadeMedida,
                   double precoUnitario, String descricao) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.unidadeMedida = unidadeMedida;
        this.precoUnitario = precoUnitario;
        this.descricao = descricao;
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public TipoProduto getTipo() { return tipo; }
    public String getUnidadeMedida() { return unidadeMedida; }
    public double getPrecoUnitario() { return precoUnitario; }
    public String getDescricao() { return descricao; }

    @Override
    public String toString() {
        return String.format("%s (%s) - R$ %.2f / %s",
                nome, tipo, precoUnitario, unidadeMedida);
    }
}
