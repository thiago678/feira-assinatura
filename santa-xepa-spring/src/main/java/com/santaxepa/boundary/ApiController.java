package com.santaxepa.boundary;

import com.santaxepa.controller.CtlSantaXepa;
import com.santaxepa.entity.*;
import com.santaxepa.entity.enums.TipoProduto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ApiController {

    private final Map<String, CtlSantaXepa> sessoes = new HashMap<>();

    private CtlSantaXepa getSessao(String sessionId) {
        return sessoes.computeIfAbsent(sessionId, k -> new CtlSantaXepa());
    }

    @PostMapping("/sms/enviar")
    public ResponseEntity<Map<String, Object>> enviarSMS(@RequestBody Map<String, String> body) {
        String sessionId = body.getOrDefault("sessionId", UUID.randomUUID().toString());
        String telefone  = body.get("telefone");
        CtlSantaXepa ctl = getSessao(sessionId);
        boolean ok = ctl.validarNumero(telefone);
        Map<String, Object> resp = new HashMap<>();
        resp.put("sessionId", sessionId);
        resp.put("sucesso", ok);
        resp.put("mensagem", ok ? "Código enviado! (veja o console do servidor)" : "Número inválido.");
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/sms/validar")
    public ResponseEntity<Map<String, Object>> validarCodigo(@RequestBody Map<String, String> body) {
        CtlSantaXepa ctl = getSessao(body.get("sessionId"));
        boolean ok = ctl.validarCodigoConfirmacao(body.get("codigo"));
        Map<String, Object> resp = new HashMap<>();
        resp.put("sucesso", ok);
        resp.put("tentativasRestantes", ctl.getTentativasRestantes());
        resp.put("mensagem", ok ? "Celular verificado!" : "Código inválido.");
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/planos")
    public ResponseEntity<List<Map<String, Object>>> listarPlanos(@RequestParam String sessionId) {
        CtlSantaXepa ctl = getSessao(sessionId);
        List<Map<String, Object>> lista = ctl.buscarPlanos().stream().map(p -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id",       p.getId());
            m.put("nome",     p.getNome());
            m.put("descricao",p.getDescricao());
            m.put("preco",    p.getPreco());
            m.put("frutas",   p.getQtdFrutas());
            m.put("legumes",  p.getQtdLegumes());
            m.put("verduras", p.getQtdVerduras());
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @PostMapping("/planos/confirmar")
    public ResponseEntity<Map<String, Object>> confirmarPlano(@RequestBody Map<String, String> body) {
        CtlSantaXepa ctl = getSessao(body.get("sessionId"));
        boolean ok = ctl.confirmarPlano(body.get("idPlano"));
        Map<String, Object> resp = new HashMap<>();
        resp.put("sucesso", ok);
        if (ok) {
            Planos.Plano p = ctl.getPlanoSelecionado();
            resp.put("plano", p.getNome());
            resp.put("preco", p.getPreco());
        }
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/catalogo/{tipo}")
    public ResponseEntity<List<Map<String, Object>>> catalogo(
            @PathVariable String tipo, @RequestParam String sessionId) {
        CtlSantaXepa ctl = getSessao(sessionId);
        TipoProduto tp;
        switch (tipo.toUpperCase()) {
            case "FRUTA":   tp = TipoProduto.FRUTA;   break;
            case "LEGUME":  tp = TipoProduto.LEGUME;  break;
            case "VERDURA": tp = TipoProduto.VERDURA; break;
            default: return ResponseEntity.badRequest().build();
        }
        List<Map<String, Object>> itens = ctl.buscarCatalogoSemanal(tp).stream().map(ic -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id",               ic.getId());
            m.put("nome",             ic.getProduto().getNome());
            m.put("tipo",             ic.getProduto().getTipo().toString());
            // envia quantidadePadrao para o front saber que não pede qtd ao usuário
            m.put("quantidadePadrao", ic.getProduto().getQuantidadePadrao());
            return m;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(itens);
    }

    @PostMapping("/cesta/adicionar")
    public ResponseEntity<Map<String, Object>> adicionarItem(@RequestBody Map<String, String> body) {
        CtlSantaXepa ctl = getSessao(body.get("sessionId"));
        String idItem = body.get("idItem");
        // usa quantidadePadrao do produto — usuário não escolhe quantidade
        ItemCatalogo ic = ctl.buscarItemCatalogo(idItem);
        if (ic == null) {
            Map<String, Object> err = new HashMap<>();
            err.put("sucesso", false);
            err.put("mensagem", "Item não encontrado: " + idItem);
            return ResponseEntity.ok(err);
        }
        ctl.adicionarItensCesta(idItem, ic.getProduto().getQuantidadePadrao());
        Map<String, Object> resp = new HashMap<>();
        resp.put("sucesso", true);
        resp.put("totalItens", ctl.getCesta().getTotalItens());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/cesta/confirmar")
    public ResponseEntity<Map<String, Object>> confirmarCesta(@RequestBody Map<String, String> body) {
        CtlSantaXepa ctl = getSessao(body.get("sessionId"));
        Endereco end = new Endereco(
            body.getOrDefault("cep", ""),
            body.getOrDefault("logradouro", ""),
            body.getOrDefault("numero", ""),
            body.getOrDefault("complemento", ""),
            body.getOrDefault("bairro", ""),
            body.getOrDefault("cidade", ""),
            body.getOrDefault("uf", ""),
            body.getOrDefault("referencia", "")
        );
        PreferenciaEntrega pref = new PreferenciaEntrega(
            body.getOrDefault("dia", "SEXTA"),
            body.getOrDefault("turno", "MANHA"),
            body.getOrDefault("obs", ""),
            Boolean.parseBoolean(body.getOrDefault("substituicao", "true")),
            body.getOrDefault("instrucao", "")
        );
        Assinatura asn = ctl.confirmarCestaEEndereco(end, pref);
        Map<String, Object> resp = new HashMap<>();
        resp.put("sucesso", true);
        resp.put("assinaturaId", asn.getId());
        resp.put("status", asn.getStatus().toString());
        resp.put("valorMensal", asn.getValorMensal());
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/pagamento")
    public ResponseEntity<Map<String, Object>> pagamento(@RequestBody Map<String, String> body) {
        CtlSantaXepa ctl = getSessao(body.get("sessionId"));

        // CPF pertence ao Assinante (diagrama de classes: Assinante.cpf)
        // Associamos ao assinante antes de processar o pagamento
        String cpf = body.getOrDefault("cpf", "");
        if (!cpf.isEmpty()) {
            ctl.atualizarCpfAssinante(cpf);
        }

        String protocolo = ctl.processarPagamento(
            body.get("numeroCartao"),
            body.get("titular"),
            body.get("validade"),
            body.getOrDefault("bandeira", "Visa")
        );
        Map<String, Object> resp = new HashMap<>();
        if (protocolo != null) {
            ctl.criarEntrega();
            resp.put("sucesso", true);
            resp.put("protocolo", protocolo);
            resp.put("transacao", ctl.getPagamento().getCodigoTransacao());
        } else {
            resp.put("sucesso", false);
            resp.put("mensagem", "Pagamento recusado.");
        }
        return ResponseEntity.ok(resp);
    }
}