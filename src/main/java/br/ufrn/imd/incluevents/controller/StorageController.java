package br.ufrn.imd.incluevents.controller;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ufrn.imd.incluevents.exceptions.BusinessException;
import br.ufrn.imd.incluevents.service.StorageService;

@RestController
@RequestMapping("/upload")
public class StorageController {
    private final StorageService storageService;

    public StorageController(StorageService storageService) {
      this.storageService = storageService;
    }

    @GetMapping("/{nomeArquivo}")
    public ResponseEntity<?> getFile(@PathVariable String nomeArquivo) {
        try {
          FileSystemResource arquivo = storageService.get(nomeArquivo);

          return ResponseEntity.status(HttpStatus.OK).body(arquivo);
        } catch (BusinessException e) {
          return ResponseEntity.status(GetHttpCode.getHttpCode(e.getType())).body(e.getMessage());
        } catch (Exception e) {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao recuperar arquivo");
        }
    }
}
