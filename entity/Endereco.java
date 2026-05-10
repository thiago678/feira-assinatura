package com.santaxepa.entity;

import java.util.Date;

/**
 * Entidade Endereco. Mensagem 28: &lt;&lt;create&gt;&gt;(dadosEndereco).
 */
public class Endereco {

    private String id;
    private String cep;
    private String logradouro;
    private String numero;
    private String complemento;
    private String bairro;
    private String cidade;
    private String estado;
    private String pontoReferencia;
    private boolean ativo;
    private Date dataCadastro;

    public Endereco(String cep, String logradouro, String numero, String complemento,
                    String bairro, String cidade, String estado, String pontoReferencia) {
        this.id = "END-" + System.currentTimeMillis();
        this.cep = cep;
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.pontoReferencia = pontoReferencia;
        this.ativo = true;
        this.dataCadastro = new Date();
    }

    public void ativar() { this.ativo = true; }
    public void desativar() { this.ativo = false; }
    public boolean estaAtivo() { return ativo; }

    public String formatarEnderecoCompleto() {
        return String.format("%s, %s%s - %s, %s/%s - CEP %s%s",
                logradouro, numero,
                (complemento != null && !complemento.isEmpty()) ? " (" + complemento + ")" : "",
                bairro, cidade, estado, cep,
                (pontoReferencia != null && !pontoReferencia.isEmpty()) ? " | Ref: " + pontoReferencia : "");
    }

    public String getId() { return id; }
    public String getCep() { return cep; }
    public String getLogradouro() { return logradouro; }
    public String getNumero() { return numero; }
    public String getComplemento() { return complemento; }
    public String getBairro() { return bairro; }
    public String getCidade() { return cidade; }
    public String getEstado() { return estado; }
    public String getPontoReferencia() { return pontoReferencia; }
    public Date getDataCadastro() { return dataCadastro; }

    @Override
    public String toString() {
        return formatarEnderecoCompleto();
    }
}
