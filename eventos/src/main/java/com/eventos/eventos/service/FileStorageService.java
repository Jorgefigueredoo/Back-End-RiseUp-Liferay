package com.eventos.eventos.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class FileStorageService {

    private final Cloudinary cloudinary;

    public FileStorageService() {
        // ☁️ CONFIGURAÇÃO DO CLOUDINARY COM SUAS CHAVES
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "dsuzr2xir");
        config.put("api_key", "264323727786834");
        config.put("api_secret", "ea4jOyz2IDsHK7wrTkqtiFI5DLs");
        
        this.cloudinary = new Cloudinary(config);
    }

    public String salvarArquivo(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Falha ao salvar arquivo vazio.");
            }

            // 1. Converte o arquivo da memória (MultipartFile) para um arquivo físico temporário
            File uploadedFile = convertMultiPartToFile(file);
            
            // 2. Envia para a nuvem (Cloudinary)
            Map uploadResult = cloudinary.uploader().upload(uploadedFile, ObjectUtils.emptyMap());
            
            // 3. Remove o arquivo temporário do disco local (limpeza)
            boolean deleted = uploadedFile.delete();
            if (!deleted) {
                System.out.println("Aviso: Não foi possível deletar o arquivo temporário " + uploadedFile.getName());
            }
            
            // 4. Retorna a URL pública e segura (HTTPS) da imagem hospedada
            return uploadResult.get("secure_url").toString();

        } catch (Exception e) {
            throw new RuntimeException("Falha ao salvar o arquivo na nuvem: " + e.getMessage(), e);
        }
    }

    // Método auxiliar necessário porque o Cloudinary precisa de um objeto File, não MultipartFile
    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        // Cria um arquivo temporário com o nome original
        File convFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }
}