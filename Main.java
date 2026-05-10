package com.santaxepa;

import com.santaxepa.boundary.GuiSantaXepa;

/**
 * Ponto de entrada do projeto Santa Xepa - N2 Engenharia de Software.
 *
 * Executa o caso de uso "Assinar Serviço de Feira" implementado conforme
 * o Diagrama de Sequência UML entregue na fase de análise e design.
 */
public class Main {
    public static void main(String[] args) {
        new GuiSantaXepa().executarFluxo();
    }
}
