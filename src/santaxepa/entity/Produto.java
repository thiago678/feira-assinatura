package com.santaxepa.entity;

import com.santaxepa.entity.enums.TipoProduto;

import java.util.Date;

public class Produto {

    private String id;
    private String nome;
    private TipoProduto tipo;
    private String descricao;
    private String urlImagem;
    private boolean disponivel;
    private boolean sazonal;
    private int quantidadePadrao;
    private Date dataCadastro;
    private Date dataAtualizacao;

    public Produto(String id, String nome, TipoProduto tipo, String descricao,
                   String urlImagem, boolean disponivel, boolean sazonal, int quantidadePadrao) {
        this.id = id;
        this.nome = nome;
        this.tipo = tipo;
        this.descricao = descricao;
        this.urlImagem = urlImagem;
        this.disponivel = disponivel;
        this.sazonal = sazonal;
        this.quantidadePadrao = quantidadePadrao;
        this.dataCadastro = new Date();
        this.dataAtualizacao = new Date();
    }

    public boolean estaDisponivel()   { return disponivel; }

    public String getId()             { return id; }
    public String getNome()           { return nome; }
    public TipoProduto getTipo()      { return tipo; }
    public String getDescricao()      { return descricao; }
    public String getUrlImagem()      { return urlImagem; }
    public boolean isDisponivel()     { return disponivel; }
    public boolean isSazonal()        { return sazonal; }
    public int getQuantidadePadrao()  { return quantidadePadrao; }
    public Date getDataCadastro()     { return dataCadastro; }
    public Date getDataAtualizacao()  { return dataAtualizacao; }

    @Override
    public String toString() {
        return String.format("%s [%s]%s", nome, tipo, sazonal ? " (sazonal)" : "");
    }
}