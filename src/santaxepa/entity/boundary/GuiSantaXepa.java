package com.santaxepa.boundary;

import com.santaxepa.controller.CtlSantaXepa;
import com.santaxepa.entity.Assinatura;
import com.santaxepa.entity.Endereco;
import com.santaxepa.entity.Entrega;
import com.santaxepa.entity.ItemCatalogo;
import com.santaxepa.entity.PreferenciaEntrega;
import com.santaxepa.entity.Planos;
import com.santaxepa.entity.enums.TipoProduto;

import java.util.List;
import java.util.Scanner;

/**
 * Boundary do caso de uso "Assinar Serviço de Feira".
 * Corresponde ao objeto guiSantaXepa no diagrama de sequência.
 *
 * Cada bloco numerado abaixo mapeia 1-para-1 as mensagens do diagrama UML.
 */
public class GuiSantaXepa {

    private final Scanner sc = new Scanner(System.in);
    private final CtlSantaXepa controller = new CtlSantaXepa();

    public void executarFluxo() {
        cabecalho();

        // ----- mensagens 0..4: identificação do Assinante -----
        if (!fluxoVerificacaoCelular()) return;

        // ----- mensagens 5..9: escolha de plano -----
        if (!fluxoEscolhaPlano()) return;

        // ----- mensagens 10..24: montagem da cesta (loop) -----
        fluxoMontagemCesta();

        // ----- mensagens 25..31: endereço + preferências + criação da assinatura -----
        if (!fluxoEnderecoEPreferencias()) return;

        // ----- mensagens 32..39: pagamento -----
        if (!fluxoPagamento()) return;

        // ----- mensagens 40..41: criação da entrega + sucesso -----
        fluxoFinalizacao();
    }

    // ========================================================================
    private void cabecalho() {
        System.out.println("============================================================");
        System.out.println("        SANTA XEPA - Assinatura de Serviço de Feira         ");
        System.out.println("    Caso de Uso N2 - Implementação do Diagrama de Sequência  ");
        System.out.println("============================================================\n");
    }

    // ------------------- Bloco SMS (msgs 0..4) ------------------------------
    private boolean fluxoVerificacaoCelular() {
        // mensagem 0: informarCelular()
        System.out.print("Informe seu celular (ex: 11999998888): ");
        String tel = sc.nextLine().trim();

        // mensagem 1: validarNumero() -> dispara mensagem 2: enviarCodigoSMS()
        if (!controller.validarNumero(tel)) {
            System.out.println("Número inválido. Encerrando.");
            return false;
        }

        // mensagens 3..4: informarCodigoSMS / validarCodigoConfirmacao
        for (int i = 0; i < 3; i++) {
            System.out.print("Digite o código recebido por SMS (6 dígitos): ");
            String codigo = sc.nextLine().trim();
            if (controller.validarCodigoConfirmacao(codigo)) {
                System.out.println("✓ Celular verificado. Cadastro de Assinante criado.\n");
                return true;
            }
            System.out.println("Código inválido. Tentativas restantes: " + controller.getTentativasRestantes());
        }
        System.out.println("Excedeu tentativas. Encerrando fluxo.");
        return false;
    }

    // ------------------- Bloco Planos (msgs 5..9) ---------------------------
    private boolean fluxoEscolhaPlano() {
        // mensagem 5: buscarPlanos()
        List<Planos.Plano> ps = controller.buscarPlanos();

        // mensagem 6: exibirPlanos()
        System.out.println(">>> PLANOS DISPONÍVEIS:");
        for (Planos.Plano p : ps) System.out.println("  " + p);

        // mensagens 7..8: selecionar/confirmar plano
        System.out.print("\nDigite o ID do plano desejado: ");
        String id = sc.nextLine().trim();
        if (!controller.confirmarPlano(id)) {
            System.out.println("Plano inválido. Encerrando.");
            return false;
        }
        System.out.println("✓ Plano selecionado: " + controller.getPlanoSelecionado().getNome());
        System.out.println();
        return true;
    }

    // ------------------- Bloco loop Cesta (msgs 10..24) ---------------------
    /**
     * Reflete o fragmento "loop(1,3) [para cada tipo: fruta, legume, verdura]"
     * do diagrama de sequência. As mensagens 10..14, 15..19 e 20..24 são
     * idênticas em estrutura — apenas o tipo de produto muda.
     */
    private void fluxoMontagemCesta() {
        TipoProduto[] tipos = { TipoProduto.FRUTA, TipoProduto.LEGUME, TipoProduto.VERDURA };
        String[] rotulos    = { "FRUTAS",          "LEGUMES",          "VERDURAS"          };

        for (int i = 0; i < tipos.length; i++) {
            System.out.println(">>> ESCOLHA DAS " + rotulos[i] + ":");

            // msgs 10/15/20: buscarCatalogoSemanal(tipo) — internamente
            // 10.1/15.1/20.1 e 10.2/15.2/20.2
            List<ItemCatalogo> itens = controller.buscarCatalogoSemanal(tipos[i]);

            // msgs 11/16/21: exibirItensCatalogo*()
            for (ItemCatalogo ic : itens) System.out.println("  [" + ic.getId() + "] " + ic);

            // msgs 12-13 / 17-18 / 22-23: selecionar / confirmar
            System.out.println("Selecione (formato: ID:qtd, ID:qtd ...) ou ENTER p/ pular:");
            String linha = sc.nextLine().trim();
            if (linha.isEmpty()) { System.out.println(); continue; }

            // msgs 14/19/24 + 14.1/19.1/24.1: adicionarItemCesta + <<create>>(ItemCesta)
            for (String par : linha.split(",")) {
                String[] kv = par.trim().split(":");
                if (kv.length == 2) {
                    try {
                        controller.adicionarItensCesta(kv[0].trim(), Integer.parseInt(kv[1].trim()));
                    } catch (NumberFormatException e) {
                        System.out.println("  (ignorado: " + par + ")");
                    }
                }
            }
            System.out.println();
        }
    }

    // ------------------- Bloco Endereço + Preferência (msgs 25..31.1) -------
    private boolean fluxoEnderecoEPreferencias() {
        // msg 25: exibirCestaESolicitarEnderecoEPreferencias()
        System.out.println(">>> RESUMO DA SUA CESTA:");
        System.out.println(controller.getCesta());

        if (controller.getCesta().getTotalItens() == 0) {
            System.out.println("Cesta vazia. Encerrando fluxo.");
            return false;
        }

        // msg 26: informarEnderecoEPreferencias()
        System.out.println(">>> ENDEREÇO DE ENTREGA:");
        System.out.print("CEP: ");          String cep = sc.nextLine().trim();
        System.out.print("Logradouro: ");   String log = sc.nextLine().trim();
        System.out.print("Número: ");       String num = sc.nextLine().trim();
        System.out.print("Complemento: ");  String compl = sc.nextLine().trim();
        System.out.print("Bairro: ");       String bairro = sc.nextLine().trim();
        System.out.print("Cidade: ");       String cidade = sc.nextLine().trim();
        System.out.print("UF: ");           String uf = sc.nextLine().trim();
        System.out.print("Ponto de referência: "); String ref = sc.nextLine().trim();
        Endereco end = new Endereco(cep, log, num, compl, bairro, cidade, uf, ref);

        System.out.println("\n>>> PREFERÊNCIAS DE ENTREGA:");
        System.out.print("Dia da semana (1=Dom..7=Sab): ");
        int dia;
        try { dia = Integer.parseInt(sc.nextLine().trim()); } catch (Exception e) { dia = 6; }
        System.out.print("Faixa horário (08-12 / 13-17 / 18-21): ");
        String faixa = sc.nextLine().trim();
        System.out.print("Observações: ");
        String obs = sc.nextLine().trim();
        System.out.print("Entrega sem contato (s/n)? ");
        boolean semContato = sc.nextLine().trim().equalsIgnoreCase("s");
        PreferenciaEntrega pref = new PreferenciaEntrega(dia, faixa, obs, semContato);

        // msg 27: confirmarCestaEEndereco()
        // dispara 28..31.1
        Assinatura asn = controller.confirmarCestaEEndereco(end, pref);
        System.out.println("\n✓ Assinatura criada (status: " + asn.getStatus() + ")\n");
        return true;
    }

    // ------------------- Bloco Pagamento (msgs 32..39) ----------------------
    private boolean fluxoPagamento() {
        // msg 32: exibirTotalESolicitarPagamento()
        System.out.println(">>> PAGAMENTO");
        System.out.printf("Valor mensal a cobrar: R$ %.2f%n", controller.getAssinatura().getValorMensal());

        // msg 33: informarDadosCartao()
        System.out.print("Número do cartão: ");   String numero = sc.nextLine().trim();
        System.out.print("Titular: ");            String titular = sc.nextLine().trim();
        System.out.print("Validade (MM/AA): ");   String val = sc.nextLine().trim();
        System.out.print("Bandeira (Visa/Master/Elo): "); String band = sc.nextLine().trim();

        // msg 34: processarPagamento(dadosCartao)
        // dispara 35, 36 (-> Operadora), 37, 38, 38.1, 39
        String protocolo = controller.processarPagamento(numero, titular, val, band);

        if (protocolo == null) {
            System.out.println("\n✗ Pagamento RECUSADO. Tente novamente mais tarde.");
            return false;
        }

        System.out.println("\n✓ Pagamento APROVADO!");
        System.out.println("  Transação: " + controller.getPagamento().getCodigoTransacao());
        System.out.println("  Autorização: " + controller.getPagamento().getCodigoAutorizacao());
        System.out.println("  Protocolo gerado: " + protocolo + "\n");
        return true;
    }

    // ------------------- Bloco Finalização (msgs 40..41) --------------------
    private void fluxoFinalizacao() {
        // msg 40: <<create>>(numProtocolo, endereco, pref) -> Entrega
        Entrega e = controller.criarEntrega();
        if (e == null) {
            System.out.println("Falha ao criar entrega.");
            return;
        }
        // msg 41: exibirSucessoEProtocolo(dadosEntrega, numProtocolo)
        System.out.println(">>> ASSINATURA CONCLUÍDA COM SUCESSO!");
        System.out.println("------------------------------------------------------------");
        System.out.println("Protocolo: " + controller.getAssinatura().getNumeroProtocolo());
        System.out.println(e.dadosEntrega());
        System.out.println("------------------------------------------------------------");
        System.out.println("Os dados foram persistidos em /data (CSVs).");
    }
}
