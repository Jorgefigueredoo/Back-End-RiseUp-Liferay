package com.eventos.eventos.controller;

import com.eventos.eventos.dto.MensagemDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/contato")
@CrossOrigin(origins = "*") // Permite que o Front-end acesse
public class ContatoController {

    @PostMapping("/enviar")
    public ResponseEntity<?> receberMensagem(@RequestBody MensagemDTO mensagem) {
        // Aqui você processaria a mensagem (salvar no banco, enviar email, etc.)
        // Por enquanto, vamos apenas logar no console do servidor.
        
        System.out.println("--- NOVA MENSAGEM DE CONTATO ---");
        System.out.println("Nome: " + mensagem.getNome() + " " + mensagem.getSobrenome());
        System.out.println("Email: " + mensagem.getEmail());
        System.out.println("Telefone: " + mensagem.getTelefone());
        System.out.println("País: " + mensagem.getPais());
        System.out.println("Área: " + mensagem.getAreaTrabalho());
        System.out.println("Motivo: " + mensagem.getMotivo());
        System.out.println("--------------------------------");

        return ResponseEntity.ok(Map.of("mensagem", "Recebemos seu contato com sucesso!"));
    }
}