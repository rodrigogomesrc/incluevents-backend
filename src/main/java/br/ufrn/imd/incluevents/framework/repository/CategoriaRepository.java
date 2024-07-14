package br.ufrn.imd.incluevents.framework.repository;

import br.ufrn.imd.incluevents.framework.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
}
