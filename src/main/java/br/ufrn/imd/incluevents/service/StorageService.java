package br.ufrn.imd.incluevents.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import br.ufrn.imd.incluevents.exceptions.BusinessException;
import br.ufrn.imd.incluevents.exceptions.enums.ExceptionTypesEnum;

@Service
public class StorageService {
    private final Path root;

    public StorageService() {
        root = Paths.get("upload");
    }

    public String store(MultipartFile file) throws BusinessException {
        if (file.isEmpty()) {
            throw new BusinessException("Arquivo não pode estar vazio", ExceptionTypesEnum.BAD_REQUEST);
        }

        try {
            String savedFileName = UUID.randomUUID().toString() + "." + FilenameUtils.getExtension(file.getOriginalFilename());

            Path destination = root.resolve(savedFileName).normalize().toAbsolutePath();

            InputStream inputStream = file.getInputStream();

            Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);

            return savedFileName;
        } catch (IOException e) {
            throw new BusinessException("Erro ao salvar arquivo", ExceptionTypesEnum.UNEXPECTED);
        }
    }


    public FileSystemResource get(String nomeArquivo) throws BusinessException {
        if (nomeArquivo == null) {
            throw new BusinessException("nomeArquivo não pode ser nulo", ExceptionTypesEnum.BAD_REQUEST);
        }

        Path destination = root.resolve(Paths.get(nomeArquivo)).normalize().toAbsolutePath();

        FileSystemResource arquivo = new FileSystemResource(destination.toString());

        if (!arquivo.exists()) {
            throw new BusinessException("Arquivo não encontrado", ExceptionTypesEnum.NOT_FOUND);
        }

        return arquivo;
    }

    public void delete(String nomeArquivo) throws BusinessException {
        if (nomeArquivo == null) {
            throw new BusinessException("nomeArquivo não pode ser nulo", ExceptionTypesEnum.BAD_REQUEST);
        }

        try {
            Path destino = root.resolve(Paths.get(nomeArquivo)).normalize().toAbsolutePath();

            Files.delete(destino);
        } catch (IOException e) {
            throw new BusinessException("Erro ao deletar arquivo", ExceptionTypesEnum.UNEXPECTED);
        }
    }
}
