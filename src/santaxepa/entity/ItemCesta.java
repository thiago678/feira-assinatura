package com.santaxepa.entity;

public class ItemCesta {

    private String id;
    private ItemCatalogo itemCatalogo;
    private int quantidade;
    private boolean substituido;
    private String observacao;

    public ItemCesta(ItemCatalogo itemCatalogo, int quantidade) {
        this.id = "IT-" + System.nanoTime();
        this.itemCatalogo = itemCatalogo;
        this.quantidade = quantidade;
        this.substituido = false;
        this.observacao = "";
    }

    public void adicionarItem() { this.quantidade++; }

    public double getSubtotal() { return 0; }

    public String getId()                  { return id; }
    public ItemCatalogo getItemCatalogo()  { return itemCatalogo; }
    public int getQuantidade()             { return quantidade; }
    public boolean isSubstituido()         { return substituido; }
    public void setSubstituido(boolean s)  { this.substituido = s; }
    public String getObservacao()          { return observacao; }
    public void setObservacao(String obs)  { this.observacao = obs; }

    @Override
    public String toString() {
        return String.format("%dx %s%s",
                quantidade,
                itemCatalogo.getProduto().getNome(),
                substituido ? " (substituído)" : "");
    }
}