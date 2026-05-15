package com.santaxepa.entity;

import com.santaxepa.entity.enums.StatusAssinante;

import java.util.Date;

/**
 * Entidade Assinante. Mensagem 4.2 do diagrama de sequência:
 */
public class Assinante {

    private String id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private StatusAssinante status;
    private Date dataCadastro;
    private Date dataAtualizacao;
    private boolean telefoneCelularVerificado;

    public Assinante(String telefone) {
        this.id = "ASS-" + System.currentTimeMillis();
        this.telefone = telefone;
        this.telefoneCelularVerificado = true; // criado após verificação SMS bem-sucedida
        this.status = StatusAssinante.PENDENTE_VERIFICACAO;
        this.dataCadastro = new Date();
        this.dataAtualizacao = this.dataCadastro;
    }

    public boolean verificarCelular() {
        return telefoneCelularVerificado;
    }

    public void ativar() {
        this.status = StatusAssinante.ATIVO;
        this.dataAtualizacao = new Date();
    }

    public void desativar() {
        this.status = StatusAssinante.INATIVO;
        this.dataAtualizacao = new Date();
    }

    public void atualizarStatus(StatusAssinante novoStatus) {
        this.status = novoStatus;
        this.dataAtualizacao = new Date();
    }

    // ---------- Getters / Setters ----------
    public String getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; this.dataAtualizacao = new Date(); }
    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; this.dataAtualizacao = new Date(); }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; this.dataAtualizacao = new Date(); }
    public String getTelefone() { return telefone; }
    public StatusAssinante getStatus() { return status; }
    public Date getDataCadastro() { return dataCadastro; }
    public Date getDataAtualizacao() { return dataAtualizacao; }
    public boolean isTelefoneCelularVerificado() { return telefoneCelularVerificado; }

    @Override
    public String toString() {
        return String.format("Assinante[%s, telefone=%s, status=%s]", id, telefone, status);
    }
}
