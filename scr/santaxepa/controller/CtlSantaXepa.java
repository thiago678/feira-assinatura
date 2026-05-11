package com.santaxepa.controller;

import com.santaxepa.entity.*;
import com.santaxepa.entity.enums.StatusAssinante;
import com.santaxepa.entity.enums.StatusAssinatura;
import com.santaxepa.entity.enums.StatusCesta;
import com.santaxepa.entity.enums.TipoProduto;
import com.santaxepa.persistence.ArquivoCSV;

import java.util.List;

/**
 * Controller do caso de uso "Assinar Serviço de Feira" (Santa Xepa).
 *
 * Implementa as 41 mensagens do diagrama de sequência. Cada método público
 * corresponde a uma mensagem disparada pela boundary (guiSantaXepa).
 *
 * Fluxo: validarNumero -> validarCodigoConfirmacao -> buscarPlanos
 *        -> confirmarPlano -> buscarCatalogoSemanal (loop 3x)
 *        -> adicionarItemCesta -> confirmarCestaEEndereco
 *        -> processarPagamento -> retorno do protocolo.
 */
public class CtlSantaXepa {

    // Entidades referenciadas pelo controller (correspondência com o diagrama)
    private final Planos planos = new Planos();
    private final CatalogoProdutos catalogo = new CatalogoProdutos();
    private final ArquivoCSV persistencia = new ArquivoCSV();

    // Estado da sessão (transação de assinatura em andamento)
    private VerificacaoSMS verificacaoSMS;
    private Assinante assinante;
    private Planos.Plano planoSelecionado;
    private Cesta cesta;
    private Endereco endereco;
    private PreferenciaEntrega preferencia;
    private Assinatura assinatura;
    private CartaoCredito cartao;
    private Pagamento pagamento;
    private Entrega entrega;

    // ========================================================================
    //  Mensagens 1-2: validação do número de celular
    // ========================================================================

    /** Mensagem 1: validarNumero(). Dispara mensagem 2: enviarCodigoSMS(). */
    public boolean validarNumero(String telefone) {
        if (telefone == null || telefone.replaceAll("\\D", "").length() < 10) {
            return false;
        }
        // Mensagem 2: enviarCodigoSMS()
        this.verificacaoSMS = new VerificacaoSMS(telefone);
        verificacaoSMS.enviarCodigoSMS();
        return true;
    }

    // ========================================================================
    //  Mensagens 4 - 4.2: validação do código + criação do Assinante
    // ========================================================================

    /**
     * Mensagem 4: validarCodigoConfirmacao(codigo).
     * Dispara 4.1: validarCodigo() em VerificacaoSMS
     * e 4.2: &lt;&lt;create&gt;&gt;(dadosCelular) em Assinante.
     */
    public boolean validarCodigoConfirmacao(String codigo) {
        if (verificacaoSMS == null) return false;
        // 4.1: validarCodigo()
        boolean ok = verificacaoSMS.validarCodigo(codigo);
        if (ok) {
            // 4.2: <<create>>(dadosCelular)
            this.assinante = new Assinante(verificacaoSMS.getTelefoneDestino());
            assinante.atualizarStatus(StatusAssinante.ATIVO);
            persistencia.salvarAssinante(assinante);
        }
        return ok;
    }

    public int getTentativasRestantes() {
        return verificacaoSMS == null ? 0 : verificacaoSMS.getTentativasRestantes();
    }

    // ========================================================================
    //  Mensagens 5 - 8: planos
    // ========================================================================

    /** Mensagem 5: buscarPlanos(). */
    public List<Planos.Plano> buscarPlanos() {
        return planos.buscarPlanos();
    }

    /** Mensagem 8: confirmarPlano(idPlano). Mensagem 9: &lt;&lt;create&gt;&gt; (Cesta). */
    public boolean confirmarPlano(String idPlano) {
        Planos.Plano p = planos.buscarPorId(idPlano);
        if (p == null) return false;
        this.planoSelecionado = p;
        // Mensagem 9: <<create>>(plano) — instancia a Cesta vinculada ao plano
        this.cesta = new Cesta();
        cesta.setNome("Cesta " + p.getNome());
        return true;
    }

    public Planos.Plano getPlanoSelecionado() { return planoSelecionado; }

    // ========================================================================
    //  Mensagens 10/15/20 - 14/19/24: loop de montagem da cesta
    // ========================================================================

    /**
     * Mensagens 10/15/20: buscarCatalogoSemanal(tipo).
     * Dispara internamente verificarDisponibilidade() (10.1/15.1/20.1)
     * e buscarDetalhesProduto() (10.2/15.2/20.2).
     */
    public List<ItemCatalogo> buscarCatalogoSemanal(TipoProduto tipo) {
        return catalogo.buscarCatalogoSemanal(tipo);
    }

    /**
     * Mensagens 13/18/23: confirmarFrutas/Legumes/Verduras().
     * Dispara 14/19/24: adicionarItemCesta(itens) e 14.1/19.1/24.1: &lt;&lt;create&gt;&gt; ItemCesta.
     */
    public void adicionarItensCesta(String idItemCatalogo, int quantidade) {
        ItemCatalogo ic = catalogo.buscarPorId(idItemCatalogo);
        if (ic == null) return;
        // 14.1 / 19.1 / 24.1: <<create>>(ItemCesta)
        ItemCesta item = new ItemCesta(ic, quantidade);
        // 14 / 19 / 24: adicionarItemCesta(item)
        cesta.adicionarItemCesta(item);
        ic.reservar(quantidade);
    }

    public Cesta getCesta() { return cesta; }

    // ========================================================================
    //  Mensagens 27 - 31.1: endereço, preferências, criação da Assinatura
    // ========================================================================

    /**
     * Mensagem 27: confirmarCestaEEndereco(...).
     * Dispara 28: &lt;&lt;create&gt;&gt;(endereco), 29: &lt;&lt;create&gt;&gt;(preferencia),
     * 30: &lt;&lt;create&gt;&gt;(Assinatura), 31: mudarStatus(aguardandoAprovacao)
     * e 31.1: &lt;&lt;create&gt;&gt; HistoricoStatus.
     */
    public Assinatura confirmarCestaEEndereco(Endereco enderecoDigitado, PreferenciaEntrega prefDigitada) {
        // 28: <<create>>(endereco)
        this.endereco = enderecoDigitado;
        // 29: <<create>>(preferencias)
        this.preferencia = prefDigitada;
        // confirma a cesta
        cesta.confirmarCesta();
        cesta.mudarStatus(StatusCesta.CONFIRMADA);
        // 30: <<create>>(plano, endereco, pref)
        this.assinatura = new Assinatura(planoSelecionado, endereco, preferencia, cesta);
        // 31.1: <<create>>(aguardandoAprovacao) — HistoricoStatus
        HistoricoStatus hist = new HistoricoStatus(StatusAssinatura.AGUARDANDO_APROVACAO);
        assinatura.setHistoricoStatus(hist);
        // 31: mudarStatus(aguardandoAprovacao) — explícito (status inicial já é esse, mas registramos)
        assinatura.mudarStatus(StatusAssinatura.AGUARDANDO_APROVACAO);
        return assinatura;
    }

    public double getValorTotalCesta() {
        return cesta == null ? 0 : cesta.calcularValorTotal();
    }

    // ========================================================================
    //  Mensagens 34 - 39: pagamento e geração do protocolo
    // ========================================================================

    /**
     * Mensagem 34: processarPagamento(dadosCartao).
     * Dispara 35: &lt;&lt;create&gt;&gt;(CartaoCredito), 37: &lt;&lt;create&gt;&gt;(Pagamento),
     * 36: validarPagamento(metodoPagamento) -> Operadora,
     * 38: mudarStatus(aprovado) e 38.1: registrarStatus(aprovado),
     * 39: gerarNumeroProtocolo().
     *
     * @return número de protocolo se aprovado, null se recusado.
     */
    public String processarPagamento(String numeroCartao, String nomeTitular,
                                     String validade, String bandeira) {
        // 35: <<create>>(dadosCartao)
        this.cartao = new CartaoCredito(numeroCartao, nomeTitular, validade, bandeira);
        // 37: <<create>>(metodoPagamento) — instanciamos antes para registrar a tentativa
        this.pagamento = new Pagamento(assinatura.getValorMensal(), cartao);
        // 36: validarPagamento(metodoPagamento) — chamada à Operadora
        boolean aprovado = pagamento.validarPagamento();
        if (!aprovado) {
            assinatura.mudarStatus(StatusAssinatura.SUSPENSA);
            assinatura.getHistoricoStatus().registrarStatus(
                    StatusAssinatura.SUSPENSA, "Pagamento recusado: " + pagamento.getMotivoRecusa());
            persistencia.salvarPagamento(pagamento, assinatura);
            return null;
        }
        // 38: mudarStatus(aprovado)
        assinatura.mudarStatus(StatusAssinatura.APROVADA);
        // 38.1: registrarStatus(aprovado) — já é feito automaticamente em mudarStatus()
        // 39: gerarNumeroProtocolo()
        String protocolo = assinatura.gerarNumeroProtocolo();
        // persistir tudo
        persistencia.salvarPagamento(pagamento, assinatura);
        persistencia.salvarCesta(cesta, assinatura);
        persistencia.salvarAssinatura(assinatura, assinante);
        persistencia.salvarHistorico(assinatura);
        return protocolo;
    }

    // ========================================================================
    //  Mensagem 40 - 41: criação da Entrega + retorno
    // ========================================================================

    /**
     * Mensagem 40: &lt;&lt;create&gt;&gt;(numProtocolo, endereco, pref) — Entrega.
     * Mensagem 41: exibirSucessoEProtocolo(dadosEntrega, numProtocolo).
     *
     * @return string formatada com os dados da entrega para a boundary exibir.
     */
    public Entrega criarEntrega() {
        if (assinatura == null || assinatura.getNumeroProtocolo() == null) {
            return null;
        }
        // 40: <<create>>(numProtocolo, endereco, pref)
        this.entrega = new Entrega(assinatura.getNumeroProtocolo(), endereco, preferencia);
        persistencia.salvarEntrega(entrega, assinatura);
        return entrega;
    }

    public Assinatura getAssinatura() { return assinatura; }
    public Pagamento getPagamento() { return pagamento; }
    public Entrega getEntrega() { return entrega; }
}
