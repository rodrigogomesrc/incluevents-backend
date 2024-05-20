package br.ufrn.imd.incluevents.repository;

import br.ufrn.imd.incluevents.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
}
