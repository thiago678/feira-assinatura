package com.santaxepa.entity;

/**
 * Entidade ItemCesta. Mensagens 14.1, 19.1, 24.1: &lt;&lt;create&gt;&gt;.
 * Representa um item escolhido pelo Assinante e adicionado à Cesta.
 */
public class ItemCesta {

    private String id;
    private ItemCatalogo itemCatalogo;
    private int quantidade;
    private double subtotal;

    public ItemCesta(ItemCatalogo itemCatalogo, int quantidade) {
        this.id = "IT-" + System.nanoTime();
        this.itemCatalogo = itemCatalogo;
        this.quantidade = quantidade;
        this.subtotal = quantidade * itemCatalogo.getProduto().getPrecoUnitario();
    }

    public void adicionarItem(int qtdAdicional) {
        this.quantidade += qtdAdicional;
        this.subtotal = quantidade * itemCatalogo.getProduto().getPrecoUnitario();
    }

    public String getId() { return id; }
    public ItemCatalogo getItemCatalogo() { return itemCatalogo; }
    public int getQuantidade() { return quantidade; }
    public double getSubtotal() { return subtotal; }

    @Override
    public String toString() {
        return String.format("%dx %s = R$ %.2f",
                quantidade,
                itemCatalogo.getProduto().getNome(),
                subtotal);
    }
}
