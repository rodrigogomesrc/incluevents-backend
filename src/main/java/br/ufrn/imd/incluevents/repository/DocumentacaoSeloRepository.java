package br.ufrn.imd.incluevents.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.ufrn.imd.incluevents.model.DocumentacaoSelo;
import br.ufrn.imd.incluevents.model.Selo;
import br.ufrn.imd.incluevents.model.Usuario;

public interface DocumentacaoSeloRepository extends JpaRepository<DocumentacaoSelo, Integer> {
  public Optional<DocumentacaoSelo> findByUsuarioAndSelo(Usuario usuario, Selo selo);
}
