package br.ufrn.imd.incluevents.model.enums;

public enum TipoSeloEnum {
    RAMPA("ESTABELECIMENTO", "RAMPA", "icon-rampa", "Rampa de Acesso", "Este estabelecimento possui rampas de acesso para pessoas com deficiência de locomoção."),
    INTERPRETE("EVENTO", "INTERPRETE", "icon-libras", "Intérprete de Libras", "Este evento oferece intérprete de Libras para pessoas com deficiência auditiva."),
    ELEVADOR("ESTABELECIMENTO", "Elevador", "icon-elevador", "Elevador", "Este estabelecimento possui elevador com acesso à diferentes pisos para pessoas com deficiência de locomoção e idosos."),
    BANHEIRO("ESTABELECIMENTO", "BANHEIRO", "icon-banheiro", "Banheiro Acessível", "Este estabelecimento dispõe de banheiros adaptados para pessoas com deficiência."),
    ESTACIONAMENTO("ESTABELECIMENTO", "ESTACIONAMENTO", "icon-estacionamento", "Estacionamento Adaptado", "Este estabelecimento oferece vagas de estacionamento reservadas para pessoas com deficiência de locomoção e idosos."),
    BRAILLE("ESTABELECIMENTO", "BRAILLE", "icon-braille", "Informações em Braille", "Este estabelecimento possue informações em braille para pessoas com deficiência visual."),
    GUIA("EVENTO", "GUIA", "icon-guia", "Guia para Deficientes Visuais", "Este evento oferece guias para auxiliar pessoas com deficiência visual.");

    private String tipoEntidade;
    private String tipoSelo;
    private String icone;
    private String nome;
    private String descricao;

    TipoSeloEnum(String tipoEntidade, String tipoSelo, String icone, String nome, String descricao) {
        this.tipoEntidade = tipoEntidade;
        this.tipoSelo = tipoSelo;
        this.icone = icone;
        this.nome = nome;
        this.descricao = descricao;
    }

    public String getTipoEntidade() {
        return tipoEntidade;
    }

    public String getTipoSelo() {
        return tipoSelo;
    }

    public String getIcone() {
        return icone;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }
}
