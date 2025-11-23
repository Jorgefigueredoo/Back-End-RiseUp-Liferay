package com.eventos.eventos.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "contatos")
public class Contato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) // O nome é obrigatório no formulário
    private String nome;

    private String sobrenome;

    @Column(name = "email_corporativo", nullable = false)
    private String emailCorporativo;

    private String telefone;

    private String pais;

    @Column(name = "area_trabalho")
    private String areaTrabalho;

    @Column(columnDefinition = "TEXT") // Permite textos longos
    private String motivoContato;

    private LocalDateTime dataEnvio;

    // --- CONSTRUTORES ---

    // Construtor vazio (obrigatório para o JPA)
    public Contato() {
        this.dataEnvio = LocalDateTime.now();
    }

    // Construtor cheio (opcional, mas útil)
    public Contato(String nome, String sobrenome, String emailCorporativo, String telefone, String pais, String areaTrabalho, String motivoContato) {
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.emailCorporativo = emailCorporativo;
        this.telefone = telefone;
        this.pais = pais;
        this.areaTrabalho = areaTrabalho;
        this.motivoContato = motivoContato;
        this.dataEnvio = LocalDateTime.now();
    }

    // --- GETTERS E SETTERS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public String getEmailCorporativo() {
        return emailCorporativo;
    }

    public void setEmailCorporativo(String emailCorporativo) {
        this.emailCorporativo = emailCorporativo;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getPais() {
        return pais;
    }

    public void setPais(String pais) {
        this.pais = pais;
    }

    public String getAreaTrabalho() {
        return areaTrabalho;
    }

    public void setAreaTrabalho(String areaTrabalho) {
        this.areaTrabalho = areaTrabalho;
    }

    public String getMotivoContato() {
        return motivoContato;
    }

    public void setMotivoContato(String motivoContato) {
        this.motivoContato = motivoContato;
    }

    public LocalDateTime getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(LocalDateTime dataEnvio) {
        this.dataEnvio = dataEnvio;
    }
}