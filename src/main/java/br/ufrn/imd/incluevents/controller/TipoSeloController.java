package br.ufrn.imd.incluevents.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.ufrn.imd.incluevents.model.enums.TipoSeloEnum;

@RestController
@RequestMapping("tipos-selo")
@CrossOrigin(origins = "http://localhost:8080")
public class TipoSeloController {
    @GetMapping
    public ResponseEntity<?> getTipoSelos(@RequestParam(required = false) final String tipoEntidade) {
        if (tipoEntidade != null) {
            if (!tipoEntidade.equals("ESTABELECIMENTO") && !tipoEntidade.equals("EVENTO")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("tipoEntidade deve ser ESTABELECIMENTO ou EVENTO");
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body(
            Stream.of(TipoSeloEnum.values())
                .filter(tipoSelo -> {
                    return tipoEntidade == null || tipoEntidade.equals(tipoSelo.getTipoEntidade());
                })
                .map(tipoSelo -> {
                    Map<String, String> object = new HashMap<>();

                    object.put("tipoSelo", tipoSelo.getTipoSelo());
                    object.put("tipoEntidade", tipoSelo.getTipoEntidade());
                    object.put("nome", tipoSelo.getNome());
                    object.put("icone", tipoSelo.getIcone());
                    object.put("descricao", tipoSelo.getDescricao());

                    return object;
                })
                .collect(Collectors.toList())
        );
    }
}
