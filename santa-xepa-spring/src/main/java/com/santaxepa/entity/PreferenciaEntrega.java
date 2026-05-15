package com.santaxepa.entity;

public class PreferenciaEntrega {

    private String diaSemanaPreferido;
    private String turnoPreferido;
    private String observacoes;
    private boolean aceitaSubstituicao;
    private String instrucaoAdicional;

    public PreferenciaEntrega(String diaSemanaPreferido, String turnoPreferido,
                              String observacoes, boolean aceitaSubstituicao,
                              String instrucaoAdicional) {
        this.diaSemanaPreferido = diaSemanaPreferido;
        this.turnoPreferido = turnoPreferido;
        this.observacoes = observacoes;
        this.aceitaSubstituicao = aceitaSubstituicao;
        this.instrucaoAdicional = instrucaoAdicional;
    }

    public void atualizarPreferencias(String diaSemana, String turno, String obs,
                                       boolean substituicao, String instrucao) {
        this.diaSemanaPreferido = diaSemana;
        this.turnoPreferido = turno;
        this.observacoes = obs;
        this.aceitaSubstituicao = substituicao;
        this.instrucaoAdicional = instrucao;
    }

    public boolean permiteSubstituicao()      { return aceitaSubstituicao; }

    public String getDiaSemanaPreferido()     { return diaSemanaPreferido; }
    public String getTurnoPreferido()         { return turnoPreferido; }
    public String getObservacoes()            { return observacoes; }
    public boolean isAceitaSubstituicao()     { return aceitaSubstituicao; }
    public String getInstrucaoAdicional()     { return instrucaoAdicional; }

    @Override
    public String toString() {
        return String.format("Preferência: %s, %s%s%s",
                diaSemanaPreferido, turnoPreferido,
                aceitaSubstituicao ? " (aceita substituição)" : "",
                (instrucaoAdicional != null && !instrucaoAdicional.isEmpty())
                        ? " — " + instrucaoAdicional : "");
    }
}