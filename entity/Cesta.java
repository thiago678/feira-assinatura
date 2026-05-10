package com.santaxepa.entity;

import com.santaxepa.entity.enums.StatusCesta;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Entidade Cesta. Mensagens 14, 19, 24: adicionarItemCesta(itens).
 */
public class Cesta {

    private String id;
    private String nome;
    private Date semanaReferencia;
    private Date dataConfirmacao;
    private Date dataMontagem;
    private StatusCesta status;
    private double valorTotal;
    private int totalItens;
    private List<ItemCesta> itens = new ArrayList<>();

    public Cesta() {
        this.id = "CST-" + System.currentTimeMillis();
        this.semanaReferencia = new Date();
        this.status = StatusCesta.EM_MONTAGEM;
        this.valorTotal = 0.0;
        this.totalItens = 0;
    }

    /** Mensagens 14, 19, 24: adicionarItemCesta(item) */
    public void adicionarItemCesta(ItemCesta item) {
        this.itens.add(item);
        recalcular();
    }

    public void removerItemCesta(ItemCesta item) {
        this.itens.remove(item);
        recalcular();
    }

    public double calcularValorTotal() {
        this.valorTotal = itens.stream().mapToDouble(ItemCesta::getSubtotal).sum();
        return valorTotal;
    }

    public int calcularTotalItens() {
        this.totalItens = itens.stream().mapToInt(ItemCesta::getQuantidade).sum();
        return totalItens;
    }

    public void confirmarCesta() {
        this.status = StatusCesta.CONFIRMADA;
        this.dataConfirmacao = new Date();
    }

    public void mudarStatus(StatusCesta novoStatus) {
        this.status = novoStatus;
    }

    private void recalcular() {
        calcularValorTotal();
        calcularTotalItens();
    }

    public String getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Date getSemanaReferencia() { return semanaReferencia; }
    public Date getDataConfirmacao() { return dataConfirmacao; }
    public Date getDataMontagem() { return dataMontagem; }
    public StatusCesta getStatus() { return status; }
    public double getValorTotal() { return valorTotal; }
    public int getTotalItens() { return totalItens; }
    public List<ItemCesta> getItens() { return new ArrayList<>(itens); }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Cesta %s | %d itens | R$ %.2f%n", id, totalItens, valorTotal));
        for (ItemCesta i : itens) sb.append("  - ").append(i).append('\n');
        return sb.toString();
    }
}
