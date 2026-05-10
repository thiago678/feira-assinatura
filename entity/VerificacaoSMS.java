package com.santaxepa.entity;

import com.santaxepa.entity.enums.StatusVerificacao;

import java.util.Date;
import java.util.Random;

/**
 * Entidade que representa o processo de verificação por SMS do telefone do
 * Assinante. Gera um código aleatório de 6 dígitos com expiração de 5 minutos
 * e até 3 tentativas de validação.
 */
public class VerificacaoSMS {

    private static final int VALIDADE_MINUTOS = 5;
    private static final int MAX_TENTATIVAS = 3;

    private String codigoEnviado;
    private String telefoneDestino;
    private Date dataEnvio;
    private Date dataExpiracao;
    private Date dataValidacao;
    private StatusVerificacao status;
    private int tentativasRestantes;

    public VerificacaoSMS(String telefoneDestino) {
        this.telefoneDestino = telefoneDestino;
        this.tentativasRestantes = MAX_TENTATIVAS;
        this.status = StatusVerificacao.AGUARDANDO;
    }

    /** Mensagem 2: enviarCodigoSMS() */
    public void enviarCodigoSMS() {
        this.codigoEnviado = String.format("%06d", new Random().nextInt(1_000_000));
        this.dataEnvio = new Date();
        this.dataExpiracao = new Date(dataEnvio.getTime() + VALIDADE_MINUTOS * 60_000L);
        // Em produção, aqui seria invocado o gateway SMS (Twilio, AWS SNS etc.).
        // Para fins acadêmicos, o código é exibido no console.
        System.out.println("[SMS] Código enviado para " + telefoneDestino + ": " + codigoEnviado);
    }

    /** Mensagem 4.1: validarCodigo(codigo) */
    public boolean validarCodigo(String codigo) {
        if (estaExpirado()) {
            this.status = StatusVerificacao.EXPIRADO;
            return false;
        }
        if (tentativasRestantes <= 0) {
            this.status = StatusVerificacao.EXCEDEU_TENTATIVAS;
            return false;
        }
        if (this.codigoEnviado.equals(codigo)) {
            this.status = StatusVerificacao.VALIDADO;
            this.dataValidacao = new Date();
            return true;
        }
        decrementarTentativa();
        return false;
    }

    public boolean estaExpirado() {
        return new Date().after(dataExpiracao);
    }

    public void decrementarTentativa() {
        if (tentativasRestantes > 0) tentativasRestantes--;
    }

    // ---------- Getters ----------
    public String getCodigoEnviado() { return codigoEnviado; }
    public String getTelefoneDestino() { return telefoneDestino; }
    public Date getDataEnvio() { return dataEnvio; }
    public Date getDataExpiracao() { return dataExpiracao; }
    public Date getDataValidacao() { return dataValidacao; }
    public StatusVerificacao getStatus() { return status; }
    public int getTentativasRestantes() { return tentativasRestantes; }
}
