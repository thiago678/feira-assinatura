package com.santaxepa.entity;

/**
 * Entidade PreferenciaEntrega. Mensagem 29: &lt;&lt;create&gt;&gt;(preferencias).
 */
public class PreferenciaEntrega {

    /** Dia da semana preferido para entrega (1=Domingo ... 7=Sábado). */
    private int diaSemanaPreferido;
    /** Faixa de horário preferida (ex.: "08-12", "13-17", "18-21"). */
    private String faixaHorario;
    private String observacoes;
    private boolean entregaSemContato;

    public PreferenciaEntrega(int diaSemanaPreferido, String faixaHorario,
                              String observacoes, boolean entregaSemContato) {
        this.diaSemanaPreferido = diaSemanaPreferido;
        this.faixaHorario = faixaHorario;
        this.observacoes = observacoes;
        this.entregaSemContato = entregaSemContato;
    }

    public int getDiaSemanaPreferido() { return diaSemanaPreferido; }
    public String getFaixaHorario() { return faixaHorario; }
    public String getObservacoes() { return observacoes; }
    public boolean isEntregaSemContato() { return entregaSemContato; }

    public String diaSemanaTexto() {
        String[] dias = {"Domingo", "Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado"};
        return dias[(diaSemanaPreferido - 1) % 7];
    }

    @Override
    public String toString() {
        return String.format("Preferência: %s, %sh%s%s",
                diaSemanaTexto(), faixaHorario,
                entregaSemContato ? " (sem contato)" : "",
                (observacoes != null && !observacoes.isEmpty()) ? " — " + observacoes : "");
    }
}
