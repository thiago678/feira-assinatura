package com.santaxepa.entity;

import com.santaxepa.entity.enums.StatusAssinatura;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HistoricoStatus {

    public static class Registro {
        private final String entidade;
        private final String statusAnterior;
        private final String statusNovo;
        private final String motivo;
        private final String responsavel;
        private final Date dataAlteracao;

        public Registro(String entidade, String statusAnterior, String statusNovo,
                        String motivo, String responsavel) {
            this.entidade = entidade;
            this.statusAnterior = statusAnterior;
            this.statusNovo = statusNovo;
            this.motivo = motivo;
            this.responsavel = responsavel;
            this.dataAlteracao = new Date();
        }

        public String getEntidade()       { return entidade; }
        public String getStatusAnterior() { return statusAnterior; }
        public String getStatusNovo()     { return statusNovo; }
        public String getMotivo()         { return motivo; }
        public String getResponsavel()    { return responsavel; }
        public Date getDataAlteracao()    { return dataAlteracao; }

        // compatibilidade com código existente
        public StatusAssinatura getStatus() {
            try { return StatusAssinatura.valueOf(statusNovo); }
            catch (Exception e) { return null; }
        }
        public String getObservacao() { return motivo; }
        public Date getDataRegistro() { return dataAlteracao; }

        @Override
        public String toString() {
            return String.format("[%tF %<tT] %s: %s → %s (%s)",
                    dataAlteracao, entidade, statusAnterior, statusNovo, motivo);
        }
    }

    private String id;
    private List<Registro> registros = new ArrayList<>();

    public HistoricoStatus(StatusAssinatura statusInicial) {
        this.id = "HST-" + System.currentTimeMillis();
        registrarMudanca("Assinatura", "-", statusInicial.name(), "Status inicial", "sistema");
    }

    /** Mensagem 29.1 / 38.1: registrarStatus(novoStatus) — chamado por Assinatura */
    public void registrarStatus(StatusAssinatura novoStatus, String observacao) {
        String anterior = registros.isEmpty() ? "-"
                : registros.get(registros.size() - 1).getStatusNovo();
        registrarMudanca("Assinatura", anterior, novoStatus.name(), observacao, "sistema");
    }

    /** Conforme diagrama de classes: registrarMudanca(entidade, statusAnt, statusNovo, motivo, responsavel) */
    public void registrarMudanca(String entidade, String statusAnterior,
                                  String statusNovo, String motivo, String responsavel) {
        registros.add(new Registro(entidade, statusAnterior, statusNovo, motivo, responsavel));
    }

    /** Conforme diagrama de classes: consultarHistorico(entidade) */
    public List<Registro> consultarHistorico(String entidade) {
        List<Registro> filtrado = new ArrayList<>();
        for (Registro r : registros) {
            if (entidade == null || r.getEntidade().equalsIgnoreCase(entidade))
                filtrado.add(r);
        }
        return filtrado;
    }

    public String getId()                 { return id; }
    public List<Registro> getRegistros()  { return new ArrayList<>(registros); }
    public Registro getUltimoRegistro() {
        return registros.isEmpty() ? null : registros.get(registros.size() - 1);
    }
}