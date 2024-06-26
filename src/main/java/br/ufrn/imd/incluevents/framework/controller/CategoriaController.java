package br.ufrn.imd.incluevents.framework.controller;

import br.ufrn.imd.incluevents.framework.exceptions.BusinessException;
import br.ufrn.imd.incluevents.framework.model.Categoria;
import br.ufrn.imd.incluevents.framework.service.CategoriaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
@CrossOrigin(origins = "http://localhost:3000")
public class CategoriaController {

    private final CategoriaService categoriaService;
    private static final Logger logger = LoggerFactory.getLogger(SeloController.class);

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping
    public ResponseEntity<?> getCategorias(){
        try{
            List<Categoria> categorias = categoriaService.getCategorias();
            return ResponseEntity.ok().body(categorias);
        } catch (Exception e){
            logger.error("Erro ao buscar Categorias", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao buscar Categorias");
        }
    }

}
