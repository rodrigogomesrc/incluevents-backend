package br.ufrn.imd.incluevents.service;

import br.ufrn.imd.incluevents.model.Categoria;
import br.ufrn.imd.incluevents.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<Categoria> getCategorias(){
        return categoriaRepository.findAll();
    }

}
