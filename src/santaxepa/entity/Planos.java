package com.santaxepa.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Planos {

    public static class Plano {
        private String id;
        private String nome;
        private String tipoPlano;
        private String descricao;
        private double preco;
        private int qtdFrutas;
        private int qtdLegumes;
        private int qtdVerduras;
        private int qtdTotalItens;
        private String periodicidade;
        private boolean ativo;
        private Date dataCriacao;

        public Plano(String id, String nome, String tipoPlano, String descricao,
                     double preco, int qtdFrutas, int qtdLegumes, int qtdVerduras,
                     String periodicidade) {
            this.id = id;
            this.nome = nome;
            this.tipoPlano = tipoPlano;
            this.descricao = descricao;
            this.preco = preco;
            this.qtdFrutas = qtdFrutas;
            this.qtdLegumes = qtdLegumes;
            this.qtdVerduras = qtdVerduras;
            this.qtdTotalItens = qtdFrutas + qtdLegumes + qtdVerduras;
            this.periodicidade = periodicidade;
            this.ativo = true;
            this.dataCriacao = new Date();
        }

        public void ativar()            { this.ativo = true; }
        public void desativar()         { this.ativo = false; }
        public boolean estaAtivo()      { return ativo; }
        public int calcularTotalItens() { return qtdFrutas + qtdLegumes + qtdVerduras; }

        public String getId()            { return id; }
        public String getNome()          { return nome; }
        public String getTipoPlano()     { return tipoPlano; }
        public String getDescricao()     { return descricao; }
        public double getPreco()         { return preco; }
        public double getValorMensal()   { return preco; }
        public int getQtdFrutas()        { return qtdFrutas; }
        public int getQtdLegumes()       { return qtdLegumes; }
        public int getQtdVerduras()      { return qtdVerduras; }
        public int getQtdTotalItens()    { return qtdTotalItens; }
        public String getPeriodicidade() { return periodicidade; }
        public Date getDataCriacao()     { return dataCriacao; }
        public int getQtdItensFruta()    { return qtdFrutas; }
        public int getQtdItensLegume()   { return qtdLegumes; }
        public int getQtdItensVerdura()  { return qtdVerduras; }

        @Override
        public String toString() {
            return String.format("[%s] %s — R$ %.2f/mês (%dF + %dL + %dV)",
                    id, nome, preco, qtdFrutas, qtdLegumes, qtdVerduras);
        }
    }

    private List<Plano> planosDisponiveis = new ArrayList<>();

    public Planos() {
        planosDisponiveis.add(new Plano("P01", "Mini Xepa",
                "BASICO", "Cesta para 1-2 pessoas",
                49.90, 3, 2, 2, "SEMANAL"));
        planosDisponiveis.add(new Plano("P02", "Família Xepa",
                "INTERMEDIARIO", "Cesta para 3-4 pessoas",
                89.90, 5, 4, 4, "SEMANAL"));
        planosDisponiveis.add(new Plano("P03", "Xepa Premium",
                "PREMIUM", "Cesta para 5+ pessoas",
                139.90, 8, 6, 6, "SEMANAL"));
    }

    public List<Plano> buscarPlanos() { return new ArrayList<>(planosDisponiveis); }

    public Plano buscarPorId(String idPlano) {
        return planosDisponiveis.stream()
                .filter(p -> p.getId().equalsIgnoreCase(idPlano))
                .findFirst()
                .orElse(null);
    }
}
