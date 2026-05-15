package com.santaxepa.persistence;

import com.santaxepa.entity.*;
import com.santaxepa.entity.enums.StatusAssinatura;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Persistência dos dados em arquivos CSV na pasta /data.
 * Criada automaticamente se não existir.
 *
 * Arquivos gerados:
 *   data/assinantes.csv
 *   data/assinaturas.csv
 *   data/cestas.csv
 *   data/itens_cesta.csv
 *   data/pagamentos.csv
 *   data/entregas.csv
 *   data/historico.csv
 */
public class ArquivoCSV {

    private static final String DIR = "data";
    private static final String SEP = ";";
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public ArquivoCSV() {
        new File(DIR).mkdirs();
    }

    // ── Assinante ────────────────────────────────────────────────────────────

    public void salvarAssinante(Assinante a) {
        String cab = "id;telefone;status;telefoneCelularVerificado;dataCadastro";
        String lin = join(a.getId(), a.getTelefone(), a.getStatus().name(),
                String.valueOf(a.isTelefoneCelularVerificado()), fmt(a.getDataCadastro()));
        append("assinantes.csv", cab, lin);
    }

    // ── Assinatura ───────────────────────────────────────────────────────────

    public void salvarAssinatura(Assinatura a, Assinante assinante) {
        String cab = "id;idAssinante;telefone;nomePlano;valorMensal;status;numeroProtocolo;" +
                     "renovacaoAutomatica;dataInicio;dataProximaRenovacao;dataCriacao";
        String lin = join(
                a.getId(),
                assinante.getId(),
                assinante.getTelefone(),
                a.getPlano().getNome(),
                fmt(a.getValorMensal()),
                a.getStatus().name(),
                nvl(a.getNumeroProtocolo()),
                String.valueOf(a.isRenovacaoAutomatica()),
                fmt(a.getDataInicio()),
                fmt(a.getDataProximaRenovacao()),
                fmt(a.getDataInicio())
        );
        append("assinaturas.csv", cab, lin);
    }

    // ── Cesta ────────────────────────────────────────────────────────────────

    public void salvarCesta(Cesta c, Assinatura a) {
        String cab = "idCesta;idAssinatura;nome;status;totalItens;semanaReferencia;dataConfirmacao";
        String lin = join(
                c.getId(), a.getId(), nvl(c.getNome()), c.getStatus().name(),
                String.valueOf(c.getTotalItens()), fmt(c.getSemanaReferencia()),
                fmt(c.getDataConfirmacao())
        );
        append("cestas.csv", cab, lin);

        String cabI = "idCesta;idItem;nomeProduto;tipoProduto;quantidade";
        for (ItemCesta item : c.getItens()) {
            String linI = join(
                    c.getId(), item.getId(),
                    item.getItemCatalogo().getProduto().getNome(),
                    item.getItemCatalogo().getProduto().getTipo().name(),
                    String.valueOf(item.getQuantidade())
            );
            append("itens_cesta.csv", cabI, linI);
        }
    }

    // ── Pagamento ────────────────────────────────────────────────────────────

    public void salvarPagamento(Pagamento p, Assinatura a) {
        String cab = "id;idAssinatura;valor;statusPagamento;codigoAutorizacao;" +
                     "codigoTransacao;motivoRecusa;tentativas;bandeira;" +
                     "numCartaoMascarado;dataProcessamento";
        CartaoCredito cc = p.getCartaoCredito();
        String lin = join(
                p.getId(), a.getId(),
                fmt(p.getValor()),
                p.getStatusPagamento().name(),
                nvl(p.getCodigoAutorizacao()),
                nvl(p.getCodigoTransacao()),
                nvl(p.getMotivoRecusa()),
                String.valueOf(p.getTentativas()),
                cc != null ? nvl(cc.getBandeira()) : "",
                cc != null ? nvl(cc.getNumCartaoMascarado()) : "",
                fmt(new Date())
        );
        append("pagamentos.csv", cab, lin);
    }

    // ── Entrega ──────────────────────────────────────────────────────────────

    public void salvarEntrega(Entrega e, Assinatura a) {
        String cab = "id;idAssinatura;numeroProtocolo;codigoRastreio;status;" +
                     "dataEntregaPrevista;endereco;preferencia;dataCriacao";
        String lin = join(
                e.getId(), a.getId(),
                e.getNumeroProtocolo(),
                e.getCodigoRastreio(),
                e.getStatus().name(),
                fmt(e.getDataEntregaPrevista()),
                e.getEnderecoEntrega().formatarEnderecoCompleto(),
                e.getPreferencia() != null ? e.getPreferencia().toString() : "",
                fmt(e.getDataCriacao())
        );
        append("entregas.csv", cab, lin);
    }

    // ── Histórico ────────────────────────────────────────────────────────────

    public void salvarHistorico(Assinatura a) {
        if (a.getHistoricoStatus() == null) return;
        String cab = "idHistorico;idAssinatura;entidade;statusAnterior;statusNovo;" +
                     "motivo;responsavel;dataAlteracao";
        for (HistoricoStatus.Registro r : a.getHistoricoStatus().getRegistros()) {
            String lin = join(
                    a.getHistoricoStatus().getId(), a.getId(),
                    r.getEntidade(), r.getStatusAnterior(), r.getStatusNovo(),
                    nvl(r.getMotivo()), nvl(r.getResponsavel()),
                    fmt(r.getDataAlteracao())
            );
            append("historico.csv", cab, lin);
        }
    }

    // ── Utilitários ──────────────────────────────────────────────────────────

    private void append(String arquivo, String cabecalho, String linha) {
        File f = new File(DIR, arquivo);
        boolean novo = !f.exists();
        try (PrintWriter pw = new PrintWriter(new FileWriter(f, true))) {
            if (novo) pw.println(cabecalho);
            pw.println(linha);
        } catch (IOException e) {
            System.err.println("[CSV] Erro ao salvar " + arquivo + ": " + e.getMessage());
        }
    }

    private String join(String... campos) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < campos.length; i++) {
            if (i > 0) sb.append(SEP);
            String v = campos[i] == null ? "" : campos[i].replace("\"", "\"\"");
            if (v.contains(SEP) || v.contains("\n") || v.contains("\""))
                sb.append('"').append(v).append('"');
            else
                sb.append(v);
        }
        return sb.toString();
    }

    private String nvl(String s)  { return s == null ? "" : s; }
    private String fmt(Date d)    { return d == null ? "" : SDF.format(d); }
    private String fmt(double v)  { return String.format("%.2f", v); }
}