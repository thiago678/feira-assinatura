package com.santaxepa.entity;

import com.santaxepa.entity.enums.TipoProduto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Entidade CatalogoProdutos. Mensagens 10/15/20: buscarCatalogoSemanal(tipo).
 * Mantém os ItemCatalogo da semana corrente.
 */
public class CatalogoProdutos {

    private Date semanaReferencia;
    private List<ItemCatalogo> itens = new ArrayList<>();

    public CatalogoProdutos() {
        this.semanaReferencia = new Date();
        carregarCatalogoSemanal();
    }

    /** Carga inicial do catálogo (em produção viria de BD/CSV). */
    private void carregarCatalogoSemanal() {
        // Frutas
        itens.add(new ItemCatalogo("IC01", new Produto("PR01", "Banana Prata",   TipoProduto.FRUTA,   "kg", 6.50, "Banana fresca"),    50));
        itens.add(new ItemCatalogo("IC02", new Produto("PR02", "Maçã Gala",      TipoProduto.FRUTA,   "kg", 9.90, "Maçã nacional"),    30));
        itens.add(new ItemCatalogo("IC03", new Produto("PR03", "Mamão Papaia",   TipoProduto.FRUTA,   "un", 7.00, "Mamão maduro"),     20));
        itens.add(new ItemCatalogo("IC04", new Produto("PR04", "Laranja Pera",   TipoProduto.FRUTA,   "kg", 5.50, "Laranja para suco"),60));
        // Legumes
        itens.add(new ItemCatalogo("IC05", new Produto("PR05", "Cenoura",        TipoProduto.LEGUME,  "kg", 4.50, "Cenoura nacional"), 40));
        itens.add(new ItemCatalogo("IC06", new Produto("PR06", "Batata Inglesa", TipoProduto.LEGUME,  "kg", 5.00, "Batata para uso geral"), 50));
        itens.add(new ItemCatalogo("IC07", new Produto("PR07", "Tomate Italiano",TipoProduto.LEGUME,  "kg", 8.90, "Tomate molho"),     35));
        itens.add(new ItemCatalogo("IC08", new Produto("PR08", "Cebola",         TipoProduto.LEGUME,  "kg", 4.00, "Cebola amarela"),   45));
        // Verduras
        itens.add(new ItemCatalogo("IC09", new Produto("PR09", "Alface Crespa",  TipoProduto.VERDURA, "un", 3.50, "Alface fresca"),    25));
        itens.add(new ItemCatalogo("IC10", new Produto("PR10", "Rúcula",         TipoProduto.VERDURA, "maço", 4.50, "Rúcula orgânica"),20));
        itens.add(new ItemCatalogo("IC11", new Produto("PR11", "Couve Manteiga", TipoProduto.VERDURA, "maço", 4.00, "Couve hidropônica"), 18));
        itens.add(new ItemCatalogo("IC12", new Produto("PR12", "Espinafre",      TipoProduto.VERDURA, "maço", 5.00, "Espinafre fresco"), 15));
    }

    /**
     * Mensagens 10/15/20: buscarCatalogoSemanal(tipo).
     * Internamente invoca verificarDisponibilidade() (10.1/15.1/20.1) para cada item.
     */
    public List<ItemCatalogo> buscarCatalogoSemanal(TipoProduto tipo) {
        return itens.stream()
                .filter(ic -> ic.getProduto().getTipo() == tipo)
                .filter(this::verificarDisponibilidade)
                .collect(Collectors.toList());
    }

    /** Mensagens 10.1/15.1/20.1: verificarFrutas/Legumes/VerdurasDisponiveis() */
    public boolean verificarDisponibilidade(ItemCatalogo item) {
        return item.estaDisponivel();
    }

    public ItemCatalogo buscarPorId(String idItem) {
        return itens.stream()
                .filter(ic -> ic.getId().equalsIgnoreCase(idItem))
                .findFirst()
                .orElse(null);
    }

    public Date getSemanaReferencia() { return semanaReferencia; }
}
