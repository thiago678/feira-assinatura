package com.santaxepa.entity;

import com.santaxepa.entity.enums.StatusAssinatura;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Entidade HistoricoStatus. Mensagens 31.1: create;(aguardandoAprovacao)
 * e 38.1: registrarStatus(aprovado).
 */
public class HistoricoStatus {

    /** Registro individual de mudança de status. */
    public static class Registro {
        private final StatusAssinatura status;
        private final Date dataRegistro;
        private final String observacao;

        public Registro(StatusAssinatura status, String observacao) {
            this.status = status;
            this.dataRegistro = new Date();
            this.observacao = observacao;
        }

        public StatusAssinatura getStatus() { return status; }
        public Date getDataRegistro() { return dataRegistro; }
        public String getObservacao() { return observacao; }

        @Override
        public String toString() {
            return String.format("[%tF %<tT] %s — %s", dataRegistro, status, observacao);
        }
    }

    private String id;
    private List<Registro> registros = new ArrayList<>();

    public HistoricoStatus(StatusAssinatura statusInicial) {
        this.id = "HST-" + System.currentTimeMillis();
        registrarStatus(statusInicial, "Status inicial");
    }

    /** Mensagem 38.1: registrarStatus(novoStatus) */
    public void registrarStatus(StatusAssinatura novoStatus, String observacao) {
        this.registros.add(new Registro(novoStatus, observacao));
    }

    public String getId() { return id; }
    public List<Registro> getRegistros() { return new ArrayList<>(registros); }
    public Registro getUltimoRegistro() {
        return registros.isEmpty() ? null : registros.get(registros.size() - 1);
    }
}
