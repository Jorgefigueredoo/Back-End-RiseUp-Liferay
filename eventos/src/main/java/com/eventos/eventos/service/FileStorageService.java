package com.eventos.eventos.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path rootLocation = Paths.get("uploads/fotos");

    public FileStorageService() {
        try {
            
            Files.createDirectories(rootLocation);
        } catch (Exception e) {
            
            throw new RuntimeException("Não foi possível criar o diretório de upload. Verifique as permissões de escrita.", e);
        }
    }

    public String salvarArquivo(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("Falha ao salvar arquivo vazio.");
        }

        try {
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String uniqueFilename = UUID.randomUUID().toString() + extension;

            Path destinationFile = this.rootLocation.resolve(uniqueFilename)
                                                  .toAbsolutePath();
            Files.copy(file.getInputStream(), destinationFile);

            return "/fotos/" + uniqueFilename; 

        } catch (Exception e) {
            throw new RuntimeException("Falha ao salvar o arquivo: " + e.getMessage(), e);
        }
    }
}