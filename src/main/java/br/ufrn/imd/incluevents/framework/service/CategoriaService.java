package br.ufrn.imd.incluevents.framework.service;

import br.ufrn.imd.incluevents.framework.model.Categoria;
import br.ufrn.imd.incluevents.framework.repository.CategoriaRepository;
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
