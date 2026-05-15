package com.santaxepa.entity;

import com.santaxepa.entity.enums.TipoProduto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class CatalogoProdutos {

    private Date semanaReferencia;
    private List<ItemCatalogo> itens = new ArrayList<>();

    public CatalogoProdutos() {
        this.semanaReferencia = new Date();
        carregarCatalogoSemanal();
    }

    private void carregarCatalogoSemanal() {
        // Produto(id, nome, tipo, descricao, urlImagem, disponivel, sazonal, quantidadePadrao)
        itens.add(new ItemCatalogo("IC01", new Produto("PR01", "Banana Prata",   TipoProduto.FRUTA,  "Banana fresca",      "", true, false, 1), 50));
        itens.add(new ItemCatalogo("IC02", new Produto("PR02", "Maçã Gala",      TipoProduto.FRUTA,  "Maçã nacional",      "", true, false, 1), 30));
        itens.add(new ItemCatalogo("IC03", new Produto("PR03", "Mamão Papaia",   TipoProduto.FRUTA,  "Mamão maduro",       "", true, true,  1), 20));
        itens.add(new ItemCatalogo("IC04", new Produto("PR04", "Laranja Pera",   TipoProduto.FRUTA,  "Laranja para suco",  "", true, false, 1), 60));
        itens.add(new ItemCatalogo("IC05", new Produto("PR05", "Cenoura",        TipoProduto.LEGUME, "Cenoura nacional",   "", true, false, 1), 40));
        itens.add(new ItemCatalogo("IC06", new Produto("PR06", "Batata Inglesa", TipoProduto.LEGUME, "Batata uso geral",   "", true, false, 1), 50));
        itens.add(new ItemCatalogo("IC07", new Produto("PR07", "Tomate Italiano",TipoProduto.LEGUME, "Tomate para molho",  "", true, false, 1), 35));
        itens.add(new ItemCatalogo("IC08", new Produto("PR08", "Cebola",         TipoProduto.LEGUME, "Cebola amarela",     "", true, false, 1), 45));
        itens.add(new ItemCatalogo("IC09", new Produto("PR09", "Alface Crespa",  TipoProduto.VERDURA,"Alface fresca",      "", true, false, 1), 25));
        itens.add(new ItemCatalogo("IC10", new Produto("PR10", "Rúcula",         TipoProduto.VERDURA,"Rúcula orgânica",    "", true, false, 1), 20));
        itens.add(new ItemCatalogo("IC11", new Produto("PR11", "Couve Manteiga", TipoProduto.VERDURA,"Couve hidropônica",  "", true, false, 1), 18));
        itens.add(new ItemCatalogo("IC12", new Produto("PR12", "Espinafre",      TipoProduto.VERDURA,"Espinafre fresco",   "", true, false, 1), 15));
    }

    public List<ItemCatalogo> buscarCatalogoSemanal(TipoProduto tipo) {
        return itens.stream()
                .filter(ic -> ic.getProduto().getTipo() == tipo)
                .filter(this::verificarDisponibilidade)
                .collect(Collectors.toList());
    }

    public void atualizarCatalogo() {
        this.semanaReferencia = new Date();
        this.itens.clear();
        carregarCatalogoSemanal();
    }

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