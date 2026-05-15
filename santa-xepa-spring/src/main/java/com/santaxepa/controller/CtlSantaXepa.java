package com.santaxepa.controller;

import com.santaxepa.entity.*;
import com.santaxepa.entity.enums.StatusAssinante;
import com.santaxepa.entity.enums.StatusAssinatura;
import com.santaxepa.entity.enums.StatusCesta;
import com.santaxepa.entity.enums.TipoProduto;
import com.santaxepa.persistence.ArquivoCSV;

import java.util.List;

public class CtlSantaXepa {

    private final Planos           planos      = new Planos();
    private final CatalogoProdutos catalogo    = new CatalogoProdutos();
    private final ArquivoCSV       persistencia = new ArquivoCSV();

    private VerificacaoSMS     verificacaoSMS;
    private Assinante          assinante;
    private Planos.Plano       planoSelecionado;
    private Cesta              cesta;
    private Endereco           endereco;
    private PreferenciaEntrega preferencia;
    private Assinatura         assinatura;
    private CartaoCredito      cartao;
    private Pagamento          pagamento;
    private Entrega            entrega;

    public boolean validarNumero(String telefone) {
        if (telefone == null || telefone.replaceAll("\\D", "").length() < 10) return false;
        this.verificacaoSMS = new VerificacaoSMS(telefone);
        verificacaoSMS.enviarCodigoSMS();
        return true;
    }

    public boolean validarCodigoConfirmacao(String codigo) {
        if (verificacaoSMS == null) return false;
        boolean ok = verificacaoSMS.validarCodigo(codigo);
        if (ok) {
            this.assinante = new Assinante(verificacaoSMS.getTelefoneDestino());
            assinante.atualizarStatus(StatusAssinante.ATIVO);
            persistencia.salvarAssinante(assinante);
        }
        return ok;
    }

    public int getTentativasRestantes() {
        return verificacaoSMS == null ? 0 : verificacaoSMS.getTentativasRestantes();
    }

    public List<Planos.Plano> buscarPlanos() { return planos.buscarPlanos(); }

    public boolean confirmarPlano(String idPlano) {
        Planos.Plano p = planos.buscarPorId(idPlano);
        if (p == null) return false;
        this.planoSelecionado = p;
        this.cesta = new Cesta(planoSelecionado);
        return true;
    }

    public Planos.Plano getPlanoSelecionado() { return planoSelecionado; }

    public List<ItemCatalogo> buscarCatalogoSemanal(TipoProduto tipo) {
        return catalogo.buscarCatalogoSemanal(tipo);
    }

    public ItemCatalogo buscarItemCatalogo(String id) {
        return catalogo.buscarPorId(id);
    }

    public void adicionarItensCesta(String idItemCatalogo, int quantidade) {
        ItemCatalogo ic = catalogo.buscarPorId(idItemCatalogo);
        if (ic == null) return;
        ItemCesta item = new ItemCesta(ic, quantidade);
        cesta.adicionarItemCesta(item);
        ic.reservar(quantidade);
    }

    public Cesta getCesta() { return cesta; }

    public Assinatura confirmarCestaEEndereco(Endereco enderecoDigitado,
                                               PreferenciaEntrega prefDigitada) {
        this.endereco    = enderecoDigitado;
        this.preferencia = prefDigitada;
        cesta.confirmarCesta();
        cesta.mudarStatus(StatusCesta.CONFIRMADA);
        this.assinatura  = new Assinatura(planoSelecionado, endereco, preferencia, cesta);
        HistoricoStatus hist = new HistoricoStatus(StatusAssinatura.AGUARDANDO_APROVACAO);
        assinatura.setHistoricoStatus(hist);
        assinatura.mudarStatus(StatusAssinatura.AGUARDANDO_APROVACAO);
        return assinatura;
    }

    public double getValorTotalCesta() { return cesta == null ? 0 : cesta.calcularValorTotal(); }

    public String processarPagamento(String numeroCartao, String nomeTitular,
                                     String validade, String bandeira) {
        this.cartao    = new CartaoCredito(numeroCartao, nomeTitular, validade, bandeira);
        this.pagamento = new Pagamento(assinatura.getValorMensal(), cartao);
        boolean aprovado = pagamento.validarPagamento();
        if (!aprovado) {
            assinatura.mudarStatus(StatusAssinatura.SUSPENSA);
            assinatura.getHistoricoStatus().registrarStatus(
                    StatusAssinatura.SUSPENSA,
                    "Pagamento recusado: " + pagamento.getMotivoRecusa());
            persistencia.salvarPagamento(pagamento, assinatura);
            return null;
        }
        assinatura.mudarStatus(StatusAssinatura.APROVADA);
        String protocolo = assinatura.gerarNumeroProtocolo();
        assinatura.getHistoricoStatus().registrarStatus(
                StatusAssinatura.APROVADA, "Pagamento aprovado");
        persistencia.salvarAssinatura(assinatura, assinante);
        persistencia.salvarCesta(cesta, assinatura);
        persistencia.salvarPagamento(pagamento, assinatura);
        persistencia.salvarHistorico(assinatura);
        return protocolo;
    }

    public Entrega criarEntrega() {
        if (assinatura == null || assinatura.getNumeroProtocolo() == null) return null;
        this.entrega = new Entrega(assinatura.getNumeroProtocolo(), endereco, preferencia);
        persistencia.salvarEntrega(entrega, assinatura);
        return entrega;
    }

    public void atualizarCpfAssinante(String cpf) {
        if (assinante != null) assinante.setCpf(cpf);
    }

    public Assinatura getAssinatura() { return assinatura; }
    public Pagamento  getPagamento()  { return pagamento; }
    public Entrega    getEntrega()    { return entrega; }
}
