package com.eventos.eventos.dto;

public class ResultadoBuscaDTO {
    private String nome;
    private String descricao;
    private String urlPerfil;
    
    // --- NOVO CAMPO ADICIONADO ---
    private String fotoPerfilUrl; 

    // Construtor atualizado para receber a foto
    public ResultadoBuscaDTO(String nome, String descricao, String urlPerfil, String fotoPerfilUrl) {
        this.nome = nome;
        this.descricao = descricao;
        this.urlPerfil = urlPerfil;
        this.fotoPerfilUrl = fotoPerfilUrl;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getUrlPerfil() {
        return urlPerfil;
    }

    public void setUrlPerfil(String urlPerfil) {
        this.urlPerfil = urlPerfil;
    }

    // --- GETTERS E SETTERS DA FOTO ---
    public String getFotoPerfilUrl() {
        return fotoPerfilUrl;
    }

    public void setFotoPerfilUrl(String fotoPerfilUrl) {
        this.fotoPerfilUrl = fotoPerfilUrl;
    }
}