package com.santaxepa.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Entidade Planos. Catálogo dos planos de assinatura disponíveis.
 * Mensagem 5: buscarPlanos()
 */
public class Planos {

    /** Representa um plano individual ofertado pela Santa Xepa. */
    public static class Plano {
        private String id;
        private String nome;
        private String descricao;
        private double valorMensal;
        private int qtdItensFruta;
        private int qtdItensLegume;
        private int qtdItensVerdura;

        public Plano(String id, String nome, String descricao, double valorMensal,
                     int qtdFruta, int qtdLegume, int qtdVerdura) {
            this.id = id;
            this.nome = nome;
            this.descricao = descricao;
            this.valorMensal = valorMensal;
            this.qtdItensFruta = qtdFruta;
            this.qtdItensLegume = qtdLegume;
            this.qtdItensVerdura = qtdVerdura;
        }

        public String getId() { return id; }
        public String getNome() { return nome; }
        public String getDescricao() { return descricao; }
        public double getValorMensal() { return valorMensal; }
        public int getQtdItensFruta() { return qtdItensFruta; }
        public int getQtdItensLegume() { return qtdItensLegume; }
        public int getQtdItensVerdura() { return qtdItensVerdura; }

        @Override
        public String toString() {
            return String.format("[%s] %s — R$ %.2f/mês (%dF + %dL + %dV)",
                    id, nome, valorMensal, qtdItensFruta, qtdItensLegume, qtdItensVerdura);
        }
    }

    private List<Plano> planosDisponiveis = new ArrayList<>();

    public Planos() {
        // Carga inicial dos planos (em um sistema real viria de um BD/CSV)
        planosDisponiveis.add(new Plano("P01", "Mini Xepa", "Cesta para 1-2 pessoas", 49.90, 3, 2, 2));
        planosDisponiveis.add(new Plano("P02", "Família Xepa", "Cesta para 3-4 pessoas", 89.90, 5, 4, 4));
    }

    /** Mensagem 5: buscarPlanos() */
    public List<Plano> buscarPlanos() {
        return new ArrayList<>(planosDisponiveis);
    }

    public Plano buscarPorId(String idPlano) {
        return planosDisponiveis.stream()
                .filter(p -> p.getId().equalsIgnoreCase(idPlano))
                .findFirst()
                .orElse(null);
    }
}
