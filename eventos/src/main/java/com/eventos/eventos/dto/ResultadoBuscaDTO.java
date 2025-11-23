package com.eventos.eventos.dto;

public class ResultadoBuscaDTO {
    private String nome;
    private String descricao;
    private String urlPerfil; // Este campo corresponde ao LINK (ex: perfil.html?id=1)
    
    // --- NOVO CAMPO ADICIONADO ---
    private String fotoPerfilUrl; // Este campo corresponde à FOTO (ex: http://cloudinary...)

    // Construtor vazio (necessário para serialização JSON em alguns casos)
    public ResultadoBuscaDTO() {
    }

    // Construtor atualizado para receber a foto
    // A ordem aqui é importante: nome, descricao, urlPerfil (LINK), fotoPerfilUrl (FOTO)
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

    // Este método retorna o LINK do perfil ou evento
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