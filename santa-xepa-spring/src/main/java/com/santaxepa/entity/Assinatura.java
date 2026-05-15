package com.santaxepa.entity;

import com.santaxepa.entity.enums.StatusAssinatura;

import java.util.Date;
import java.util.UUID;

/**
 * Entidade Assinatura. Mensagem 30: &lt;&lt;create&gt;&gt;(plano, endereco, pref).
 * Mensagens 31, 38: mudarStatus().  Mensagem 39: gerarNumeroProtocolo().
 */
public class Assinatura {

    private String id;
    private double valorMensal;
    private StatusAssinatura status;
    private String numeroProtocolo;
    private Date dataInicio;
    private Date dataFim;
    private Date dataCancelamento;
    private String motivoCancelamento;
    private boolean renovacaoAutomatica;
    private Date dataProximaRenovacao;
    private Date dataCriacao;
    private Date dataAtualizacao;

    // Associações
    private Planos.Plano plano;
    private Endereco endereco;
    private PreferenciaEntrega preferenciaEntrega;
    private Cesta cesta;
    private HistoricoStatus historicoStatus;

    public Assinatura(Planos.Plano plano, Endereco endereco, PreferenciaEntrega pref, Cesta cesta) {
        this.id = "ASN-" + System.currentTimeMillis();
        this.plano = plano;
        this.endereco = endereco;
        this.preferenciaEntrega = pref;
        this.cesta = cesta;
        this.valorMensal = plano.getValorMensal();
        this.dataInicio = new Date();
        this.dataCriacao = this.dataInicio;
        this.dataAtualizacao = this.dataInicio;
        this.renovacaoAutomatica = true;
        // próxima renovação em 30 dias
        this.dataProximaRenovacao = new Date(dataInicio.getTime() + 30L * 24 * 60 * 60 * 1000);
        this.status = StatusAssinatura.AGUARDANDO_APROVACAO;
    }

    /** Mensagem 39: gerarNumeroProtocolo() */
    public String gerarNumeroProtocolo() {
        if (this.numeroProtocolo == null) {
            // Protocolo curto e legível: SX-AAAAMMDD-XXXX
            String hash = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
            this.numeroProtocolo = String.format("SX-%tY%<tm%<td-%s", new Date(), hash);
        }
        return numeroProtocolo;
    }

    /** Mensagens 31, 38: mudarStatus(novoStatus) */
    public void mudarStatus(StatusAssinatura novoStatus) {
        this.status = novoStatus;
        this.dataAtualizacao = new Date();
        if (this.historicoStatus != null) {
            historicoStatus.registrarStatus(novoStatus, "Atualização de status");
        }
    }

    public void setHistoricoStatus(HistoricoStatus historicoStatus) {
        this.historicoStatus = historicoStatus;
    }

    public void cancelar(String motivo) {
        this.status = StatusAssinatura.CANCELADA;
        this.motivoCancelamento = motivo;
        this.dataCancelamento = new Date();
        this.dataAtualizacao = this.dataCancelamento;
    }

    public boolean estaAtiva() {
        return status == StatusAssinatura.ATIVA || status == StatusAssinatura.APROVADA;
    }

    public String getId() { return id; }
    public double getValorMensal() { return valorMensal; }
    public StatusAssinatura getStatus() { return status; }
    public String getNumeroProtocolo() { return numeroProtocolo; }
    public Date getDataInicio() { return dataInicio; }
    public Planos.Plano getPlano() { return plano; }
    public Endereco getEndereco() { return endereco; }
    public PreferenciaEntrega getPreferenciaEntrega() { return preferenciaEntrega; }
    public Cesta getCesta() { return cesta; }
    public HistoricoStatus getHistoricoStatus() { return historicoStatus; }
    public boolean isRenovacaoAutomatica() { return renovacaoAutomatica; }
    public Date getDataProximaRenovacao() { return dataProximaRenovacao; }
}
