package com.santaxepa.persistence;

import com.santaxepa.entity.*;

import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Camada de persistência simples em arquivos CSV. Cada entidade gera/atualiza
 * um arquivo separado em /data. Para fins acadêmicos, optamos por append
 * (escrever uma linha por execução do caso de uso).
 *
 * Estrutura dos arquivos:
 *   data/assinantes.csv
 *   data/assinaturas.csv
 *   data/cestas.csv
 *   data/itens_cesta.csv
 *   data/pagamentos.csv
 *   data/entregas.csv
 *   data/historico_status.csv
 */
public class ArquivoCSV {

    private static final String DATA_DIR = "data";
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String SEP = ";";

    public ArquivoCSV() {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
            criarCabecalhosSeNaoExistirem();
        } catch (IOException e) {
            System.err.println("[Persistencia] Erro ao inicializar diretório data/: " + e.getMessage());
        }
    }

    private void criarCabecalhosSeNaoExistirem() throws IOException {
        criarCabecalho("assinantes.csv",
                "id;telefone;status;dataCadastro;telefoneVerificado");
        criarCabecalho("assinaturas.csv",
                "id;protocolo;assinanteId;planoId;valorMensal;status;dataInicio");
        criarCabecalho("cestas.csv",
                "id;assinaturaId;totalItens;valorTotal;status;dataConfirmacao");
        criarCabecalho("itens_cesta.csv",
                "idItemCesta;cestaId;produto;tipo;quantidade;subtotal");
        criarCabecalho("pagamentos.csv",
                "id;assinaturaId;valor;status;codigoTransacao;codigoAutorizacao;cartaoMascarado");
        criarCabecalho("entregas.csv",
                "id;assinaturaId;protocolo;codigoRastreio;status;dataPrevista;endereco");
        criarCabecalho("historico_status.csv",
                "assinaturaId;status;dataRegistro;observacao");
    }

    private void criarCabecalho(String nomeArquivo, String cabecalho) throws IOException {
        Path p = Paths.get(DATA_DIR, nomeArquivo);
        if (!Files.exists(p)) {
            Files.write(p, (cabecalho + System.lineSeparator()).getBytes());
        }
    }

    private void append(String arquivo, String linha) {
        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(Paths.get(DATA_DIR, arquivo).toFile(), true))) {
            bw.write(linha);
            bw.newLine();
        } catch (IOException e) {
            System.err.println("[Persistencia] Erro ao escrever " + arquivo + ": " + e.getMessage());
        }
    }

    private String fmt(Date d) {
        return d == null ? "" : SDF.format(d);
    }

    // =================== Métodos de persistência ===================

    public void salvarAssinante(Assinante a) {
        append("assinantes.csv", String.join(SEP,
                a.getId(),
                nz(a.getTelefone()),
                a.getStatus().name(),
                fmt(a.getDataCadastro()),
                String.valueOf(a.isTelefoneCelularVerificado())));
    }

    public void salvarAssinatura(Assinatura asn, Assinante assinante) {
        append("assinaturas.csv", String.join(SEP,
                asn.getId(),
                nz(asn.getNumeroProtocolo()),
                assinante.getId(),
                asn.getPlano().getId(),
                String.format("%.2f", asn.getValorMensal()),
                asn.getStatus().name(),
                fmt(asn.getDataInicio())));
    }

    public void salvarCesta(Cesta c, Assinatura asn) {
        append("cestas.csv", String.join(SEP,
                c.getId(),
                asn.getId(),
                String.valueOf(c.getTotalItens()),
                String.format("%.2f", c.getValorTotal()),
                c.getStatus().name(),
                fmt(c.getDataConfirmacao())));
        for (ItemCesta it : c.getItens()) {
            append("itens_cesta.csv", String.join(SEP,
                    it.getId(),
                    c.getId(),
                    it.getItemCatalogo().getProduto().getNome(),
                    it.getItemCatalogo().getProduto().getTipo().name(),
                    String.valueOf(it.getQuantidade()),
                    String.format("%.2f", it.getSubtotal())));
        }
    }

    public void salvarPagamento(Pagamento p, Assinatura asn) {
        append("pagamentos.csv", String.join(SEP,
                p.getId(),
                asn.getId(),
                String.format("%.2f", p.getValor()),
                p.getStatusPagamento().name(),
                nz(p.getCodigoTransacao()),
                nz(p.getCodigoAutorizacao()),
                p.getCartaoCredito() != null ? p.getCartaoCredito().getNumCartaoMascarado() : ""));
    }

    public void salvarEntrega(Entrega e, Assinatura asn) {
        append("entregas.csv", String.join(SEP,
                e.getId(),
                asn.getId(),
                nz(e.getNumeroProtocolo()),
                e.getCodigoRastreio(),
                e.getStatus().name(),
                fmt(e.getDataEntregaPrevista()),
                "\"" + e.getEnderecoEntrega().formatarEnderecoCompleto() + "\""));
    }

    public void salvarHistorico(Assinatura asn) {
        if (asn.getHistoricoStatus() == null) return;
        for (HistoricoStatus.Registro r : asn.getHistoricoStatus().getRegistros()) {
            append("historico_status.csv", String.join(SEP,
                    asn.getId(),
                    r.getStatus().name(),
                    fmt(r.getDataRegistro()),
                    nz(r.getObservacao())));
        }
    }

    private String nz(String s) { return s == null ? "" : s; }
}
