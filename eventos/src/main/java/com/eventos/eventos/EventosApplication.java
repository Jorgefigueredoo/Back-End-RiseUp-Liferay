package com.eventos.eventos;

import com.eventos.eventos.model.Usuario;
import com.eventos.eventos.model.Perfil;
import com.eventos.eventos.repository.UsuarioRepository;
import com.eventos.eventos.repository.PerfilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;

@SpringBootApplication
public class EventosApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventosApplication.class, args);
    }

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PerfilRepository perfilRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initDatabase() {
        return args -> {

            if (usuarioRepository.findByEmail("teste@email.com").isEmpty()) {
                System.out.println("--- Criando usuário 'teste@email.com' ---");
                Usuario usuarioTeste = new Usuario();
                usuarioTeste.setNomeUsuario("testuser");
                usuarioTeste.setEmail("teste@email.com");
                usuarioTeste.setSenha(passwordEncoder.encode("123456"));

                Usuario usuarioSalvo = usuarioRepository.save(usuarioTeste);
                System.out.println(">>> Usuário de teste criado.");

                System.out.println("--- Criando perfil para 'testuser' ---");
                Perfil perfilTeste = new Perfil();
                perfilTeste.setUsuario(usuarioSalvo);
                perfilTeste.setNomeCompleto("Usuário de Teste");
                perfilTeste.setTitulo("Testador de Software");
                perfilTeste.setSobreMim("Eu sou um usuário de teste automático.");
                perfilTeste.setHabilidades(List.of("Java", "Spring", "Testes"));

                perfilRepository.save(perfilTeste);
                System.out.println(">>> Perfil de teste criado.");

            } else {
                System.out.println("--- Usuário 'teste@email.com' já existe. ---");
            }

            if (usuarioRepository.findByEmail("jorge@email.com").isEmpty()) {
                System.out.println("--- Criando usuário 'jorge@email.com' ---");
                Usuario usuarioJorge = new Usuario();
                usuarioJorge.setNomeUsuario("jorgeuser");
                usuarioJorge.setEmail("jorge@email.com");
                usuarioJorge.setSenha(passwordEncoder.encode("12345"));

                Usuario jorgeSalvo = usuarioRepository.save(usuarioJorge);
                System.out.println(">>> Usuário 'jorge' criado.");

                System.out.println("--- Criando perfil para 'jorgeuser' ---");
                Perfil perfilJorge = new Perfil();
                perfilJorge.setUsuario(jorgeSalvo);
                perfilJorge.setNomeCompleto("Jorge da Silva");
                perfilJorge.setTitulo("Desenvolvedor Backend");
                perfilJorge.setSobreMim("Gosto de criar APIs e mexer com bancos de dados.");
                perfilJorge.setHabilidades(List.of("Spring Boot", "SQL", "Docker"));

                perfilRepository.save(perfilJorge);
                System.out.println(">>> Perfil de Jorge criado.");

            } else {
                System.out.println("--- Usuário 'jorge@email.com' já existe. ---");
            }
        };
    }
}