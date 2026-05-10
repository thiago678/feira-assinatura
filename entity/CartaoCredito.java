package com.santaxepa.entity;

import com.santaxepa.entity.enums.MetodoDePagamento;

import java.util.Date;

/**
 * Entidade CartaoCredito. Mensagem 35: &lt;&lt;create&gt;&gt;(dadosCartao).
 * O número do cartão é armazenado mascarado (apenas os 4 últimos dígitos).
 */
public class CartaoCredito {

    private String id;
    private MetodoDePagamento tipo;
    private String numCartaoMascarado;
    private String bandeira;
    private String nomeTitular;
    private String validadeCartao; // MM/AA
    private boolean ativo;
    private Date dataCadastro;
    private Date dataAtualizacao;

    public CartaoCredito(String numeroCompleto, String nomeTitular, String validadeCartao, String bandeira) {
        this.id = "CC-" + System.currentTimeMillis();
        this.tipo = MetodoDePagamento.CARTAO_CREDITO;
        this.numCartaoMascarado = mascarar(numeroCompleto);
        this.nomeTitular = nomeTitular;
        this.validadeCartao = validadeCartao;
        this.bandeira = bandeira;
        this.ativo = true;
        this.dataCadastro = new Date();
        this.dataAtualizacao = this.dataCadastro;
    }

    private String mascarar(String numero) {
        if (numero == null || numero.length() < 4) return "****";
        String ultimos = numero.substring(numero.length() - 4);
        return "**** **** **** " + ultimos;
    }

    public String getId() { return id; }
    public MetodoDePagamento getTipo() { return tipo; }
    public String getNumCartaoMascarado() { return numCartaoMascarado; }
    public String getBandeira() { return bandeira; }
    public String getNomeTitular() { return nomeTitular; }
    public String getValidadeCartao() { return validadeCartao; }
    public boolean isAtivo() { return ativo; }
    public Date getDataCadastro() { return dataCadastro; }

    @Override
    public String toString() {
        return String.format("%s %s | titular: %s | val: %s",
                bandeira, numCartaoMascarado, nomeTitular, validadeCartao);
    }
}
