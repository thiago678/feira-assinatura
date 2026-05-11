package com.santaxepa.entity;

import com.santaxepa.entity.enums.StatusPagamento;

import java.util.Date;
import java.util.UUID;

/**
 * Entidade Pagamento. Mensagens 36 (validarPagamento) e 37 (create;).
 * Simula a comunicação com a Operadora do Cartão de Crédito.
 */
public class Pagamento {

    private String id;
    private double valor;
    private StatusPagamento statusPagamento;
    private String motivoRecusa;
    private String codigoTransacao;
    private String codigoAutorizacao;
    private int tentativas;
    private Date dataCriacao;
    private Date dataProcessamento;

    private CartaoCredito cartaoCredito;

    public Pagamento(double valor, CartaoCredito cartaoCredito) {
        this.id = "PAG-" + System.currentTimeMillis();
        this.valor = valor;
        this.cartaoCredito = cartaoCredito;
        this.statusPagamento = StatusPagamento.PENDENTE;
        this.tentativas = 0;
        this.dataCriacao = new Date();
        this.codigoTransacao = "TX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    /**
     * Mensagem 36: validarPagamento(metodoPagamento). Simula a comunicação
     * com a operadora — em ambiente real seria uma chamada HTTP/SOAP. Aqui
     * aprovamos automaticamente se o cartão estiver ativo.
     */
    public boolean validarPagamento() {
        this.statusPagamento = StatusPagamento.PROCESSANDO;
        this.tentativas++;
        try {
            Thread.sleep(400); // simula latência da operadora
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (cartaoCredito != null && cartaoCredito.isAtivo()) {
            String autorizacao = "AUT-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            aprovar(autorizacao);
            return true;
        }
        recusar("Cartão inválido ou inativo");
        return false;
    }

    public void aprovar(String codigoAutorizacao) {
        this.codigoAutorizacao = codigoAutorizacao;
        this.statusPagamento = StatusPagamento.APROVADO;
        this.dataProcessamento = new Date();
    }

    public void recusar(String motivo) {
        this.motivoRecusa = motivo;
        this.statusPagamento = StatusPagamento.RECUSADO;
        this.dataProcessamento = new Date();
    }

    public void mudarStatus(StatusPagamento novoStatus) {
        this.statusPagamento = novoStatus;
    }

    public void incrementarTentativas() { this.tentativas++; }

    public boolean foiAprovado() {
        return statusPagamento == StatusPagamento.APROVADO;
    }

    public String getId() { return id; }
    public double getValor() { return valor; }
    public StatusPagamento getStatusPagamento() { return statusPagamento; }
    public String getMotivoRecusa() { return motivoRecusa; }
    public String getCodigoTransacao() { return codigoTransacao; }
    public String getCodigoAutorizacao() { return codigoAutorizacao; }
    public int getTentativas() { return tentativas; }
    public CartaoCredito getCartaoCredito() { return cartaoCredito; }

    @Override
    public String toString() {
        return String.format("Pagamento %s | R$ %.2f | %s%s",
                codigoTransacao, valor, statusPagamento,
                codigoAutorizacao != null ? " | aut: " + codigoAutorizacao : "");
    }
}
