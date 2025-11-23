package com.eventos.eventos.dto;

public class MensagemDTO {
    private String nome;
    private String sobrenome;
    private String email;
    private String telefone;
    private String pais;
    private String areaTrabalho; // Corresponde ao campo "Área de trabalho"
    private String motivo;       // Corresponde ao campo "Motivo do contato"

    // Construtores
    public MensagemDTO() {}

    public MensagemDTO(String nome, String sobrenome, String email, String telefone, String pais, String areaTrabalho, String motivo) {
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.email = email;
        this.telefone = telefone;
        this.pais = pais;
        this.areaTrabalho = areaTrabalho;
        this.motivo = motivo;
    }

    // Getters e Setters
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getSobrenome() { return sobrenome; }
    public void setSobrenome(String sobrenome) { this.sobrenome = sobrenome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefone() { return telefone; }
    public void setTelefone(String telefone) { this.telefone = telefone; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    public String getAreaTrabalho() { return areaTrabalho; }
    public void setAreaTrabalho(String areaTrabalho) { this.areaTrabalho = areaTrabalho; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }
}