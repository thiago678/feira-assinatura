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

public class GuiSantaXepa {

    private final Scanner sc = new Scanner(System.in);
    private final CtlSantaXepa controller = new CtlSantaXepa();

    public void executarFluxo() {
        cabecalho();
        if (!fluxoVerificacaoCelular())    return;
        if (!fluxoEscolhaPlano())          return;
        fluxoMontagemCesta();
        if (!fluxoEnderecoEPreferencias()) return;
        if (!fluxoPagamento())             return;
        fluxoFinalizacao();
    }

    private void cabecalho() {
        System.out.println("============================================================");
        System.out.println("        SANTA XEPA - Assinatura de Serviço de Feira         ");
        System.out.println("============================================================\n");
    }

    private boolean fluxoVerificacaoCelular() {
        System.out.print("Informe seu celular (ex: 11999998888): ");
        String tel = sc.nextLine().trim();

        if (!controller.validarNumero(tel)) {
            System.out.println("Número inválido. Encerrando.");
            return false;
        }

        for (int i = 0; i < 3; i++) {
            System.out.print("Digite o código recebido por SMS (6 dígitos): ");
            String codigo = sc.nextLine().trim();
            if (controller.validarCodigoConfirmacao(codigo)) {
                System.out.println("✓ Celular verificado.\n");
                return true;
            }
            System.out.println("Código inválido. Tentativas restantes: "
                    + controller.getTentativasRestantes());
        }
        System.out.println("Excedeu tentativas. Encerrando.");
        return false;
    }

    private boolean fluxoEscolhaPlano() {
        List<Planos.Plano> ps = controller.buscarPlanos();

        System.out.println(">>> PLANOS DISPONÍVEIS:");
        for (Planos.Plano p : ps) System.out.println("  " + p);

        System.out.print("\nDigite o ID do plano desejado: ");
        String id = sc.nextLine().trim();

        if (!controller.confirmarPlano(id)) {
            System.out.println("Plano inválido. Encerrando.");
            return false;
        }

        Planos.Plano p = controller.getPlanoSelecionado();
        System.out.printf("✓ Plano: %s | até %dF + %dL + %dV%n%n",
                p.getNome(), p.getQtdFrutas(), p.getQtdLegumes(), p.getQtdVerduras());
        return true;
    }

    private void fluxoMontagemCesta() {
        Planos.Plano plano = controller.getPlanoSelecionado();
        TipoProduto[] tipos   = { TipoProduto.FRUTA, TipoProduto.LEGUME, TipoProduto.VERDURA };
        String[]      rotulos = { "FRUTAS",           "LEGUMES",          "VERDURAS"          };
        int[]         limites = { plano.getQtdFrutas(), plano.getQtdLegumes(), plano.getQtdVerduras() };

        for (int i = 0; i < tipos.length; i++) {
            List<ItemCatalogo> itens = controller.buscarCatalogoSemanal(tipos[i]);

            System.out.println(">>> " + rotulos[i] + " (escolha até " + limites[i] + " ou ENTER para pular):");
            for (ItemCatalogo ic : itens)
                System.out.printf("  [%s] %s%n", ic.getId(), ic.getProduto().getNome());

            System.out.print("IDs separados por vírgula: ");
            String linha = sc.nextLine().trim();
            if (linha.isEmpty()) { System.out.println(); continue; }

            int adicionados = 0;
            for (String id : linha.split(",")) {
                if (adicionados >= limites[i]) {
                    System.out.println("  (limite atingido)");
                    break;
                }
                String idLimpo = id.trim().toUpperCase();
                ItemCatalogo ic = controller.buscarItemCatalogo(idLimpo);
                if (ic == null) { System.out.println("  (não encontrado: " + idLimpo + ")"); continue; }

                // usa quantidadePadrao — usuário não escolhe quantidade
                controller.adicionarItensCesta(idLimpo, ic.getProduto().getQuantidadePadrao());
                System.out.println("  ✓ " + ic.getProduto().getNome());
                adicionados++;
            }
            System.out.println();
        }
    }

    private boolean fluxoEnderecoEPreferencias() {
        System.out.println(">>> RESUMO DA CESTA:");
        System.out.println(controller.getCesta());

        if (controller.getCesta().getTotalItens() == 0) {
            System.out.println("Cesta vazia. Encerrando.");
            return false;
        }

        System.out.println(">>> ENDEREÇO DE ENTREGA:");
        System.out.print("CEP: ");                  String cep    = sc.nextLine().trim();
        System.out.print("Logradouro: ");            String log    = sc.nextLine().trim();
        System.out.print("Número: ");                String num    = sc.nextLine().trim();
        System.out.print("Complemento: ");           String compl  = sc.nextLine().trim();
        System.out.print("Bairro: ");                String bairro = sc.nextLine().trim();
        System.out.print("Cidade: ");                String cidade = sc.nextLine().trim();
        System.out.print("UF: ");                    String uf     = sc.nextLine().trim();
        System.out.print("Ponto de referência: ");   String ref    = sc.nextLine().trim();
        Endereco end = new Endereco(cep, log, num, compl, bairro, cidade, uf, ref);

        System.out.println("\n>>> PREFERÊNCIAS DE ENTREGA:");
        System.out.print("Dia preferido (ex: SEGUNDA, SEXTA, SABADO): "); String dia   = sc.nextLine().trim();
        System.out.print("Turno (MANHA / TARDE / NOITE): ");              String turno = sc.nextLine().trim();
        System.out.print("Observações (ou ENTER): ");                      String obs   = sc.nextLine().trim();
        System.out.print("Aceita substituição de itens? (s/n): ");
        boolean aceita = sc.nextLine().trim().equalsIgnoreCase("s");
        System.out.print("Instrução adicional (ou ENTER): ");              String inst  = sc.nextLine().trim();

        PreferenciaEntrega pref = new PreferenciaEntrega(dia, turno, obs, aceita, inst);
        Assinatura asn = controller.confirmarCestaEEndereco(end, pref);
        System.out.println("\n✓ Assinatura criada (status: " + asn.getStatus() + ")\n");
        return true;
    }

    private boolean fluxoPagamento() {
        System.out.println(">>> PAGAMENTO");
        System.out.printf("Valor mensal: R$ %.2f%n", controller.getAssinatura().getValorMensal());

        System.out.print("Número do cartão: ");           String numero  = sc.nextLine().trim();
        System.out.print("Titular: ");                    String titular = sc.nextLine().trim();
        System.out.print("Validade (MM/AA): ");           String val     = sc.nextLine().trim();
        System.out.print("Bandeira (Visa/Master/Elo): "); String band    = sc.nextLine().trim();

        String protocolo = controller.processarPagamento(numero, titular, val, band);

        if (protocolo == null) {
            System.out.println("\n✗ Pagamento RECUSADO.");
            return false;
        }

        System.out.println("\n✓ Pagamento APROVADO!");
        System.out.println("  Transação:   " + controller.getPagamento().getCodigoTransacao());
        System.out.println("  Autorização: " + controller.getPagamento().getCodigoAutorizacao());
        System.out.println("  Protocolo:   " + protocolo + "\n");
        return true;
    }

    private void fluxoFinalizacao() {
        Entrega e = controller.criarEntrega();
        if (e == null) { System.out.println("Falha ao criar entrega."); return; }

        System.out.println(">>> ASSINATURA CONCLUÍDA COM SUCESSO!");
        System.out.println("------------------------------------------------------------");
        System.out.println("Protocolo: " + controller.getAssinatura().getNumeroProtocolo());
        System.out.println(e.dadosEntrega());
        System.out.println("------------------------------------------------------------");
        System.out.println("Dados salvos em /data (CSVs).");
    }
}