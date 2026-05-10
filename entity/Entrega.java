package com.santaxepa.entity;

import com.santaxepa.entity.enums.StatusEntrega;

import java.util.Date;
import java.util.UUID;

/**
 * Entidade Entrega. Mensagem 40: &lt;&lt;create&gt;&gt;(numProtocolo, endereco, pref).
 */
public class Entrega {

    private String id;
    private String codigoRastreio;
    private StatusEntrega status;
    private Date dataCriacao;
    private Date dataEntregaPrevista;
    private Date dataEntregaRealizada;
    private int tentativasEntrega;
    private String nomeRecebedor;
    private String observacaoEntregador;

    private String numeroProtocolo;
    private Endereco enderecoEntrega;
    private PreferenciaEntrega preferencia;

    public Entrega(String numeroProtocolo, Endereco endereco, PreferenciaEntrega pref) {
        this.id = "ENT-" + System.currentTimeMillis();
        this.numeroProtocolo = numeroProtocolo;
        this.enderecoEntrega = endereco;
        this.preferencia = pref;
        this.status = StatusEntrega.AGUARDANDO_MONTAGEM;
        this.dataCriacao = new Date();
        this.tentativasEntrega = 0;
        this.codigoRastreio = gerarCodigoRastreio();
        // entrega prevista para 7 dias após a criação
        this.dataEntregaPrevista = new Date(dataCriacao.getTime() + 7L * 24 * 60 * 60 * 1000);
    }

    public String gerarCodigoRastreio() {
        return "SX" + UUID.randomUUID().toString().replace("-", "")
                .substring(0, 9).toUpperCase() + "BR";
    }

    public void mudarStatus(StatusEntrega novoStatus) { this.status = novoStatus; }
    public StatusEntrega getStatus() { return status; }

    public void marcarComoEntregue(String nomeRecebedor) {
        this.status = StatusEntrega.ENTREGUE;
        this.nomeRecebedor = nomeRecebedor;
        this.dataEntregaRealizada = new Date();
    }

    public void incrementarTentativa() { this.tentativasEntrega++; }
    public boolean foiEntregue() { return status == StatusEntrega.ENTREGUE; }

    public String getId() { return id; }
    public String getCodigoRastreio() { return codigoRastreio; }
    public Date getDataCriacao() { return dataCriacao; }
    public Date getDataEntregaPrevista() { return dataEntregaPrevista; }
    public String getNumeroProtocolo() { return numeroProtocolo; }
    public Endereco getEnderecoEntrega() { return enderecoEntrega; }
    public PreferenciaEntrega getPreferencia() { return preferencia; }

    /** Formata para retorno ao Controller (msg "return: dadosEntrega"). */
    public String dadosEntrega() {
        return String.format(
                "Entrega %s%n  Rastreio: %s%n  Endereço: %s%n  Previsão: %tF%n  %s",
                id, codigoRastreio, enderecoEntrega.formatarEnderecoCompleto(),
                dataEntregaPrevista, preferencia);
    }
}
