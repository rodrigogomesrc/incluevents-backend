package br.ufrn.imd.incluevents.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.ufrn.imd.incluevents.model.Selo;
import br.ufrn.imd.incluevents.model.Usuario;
import br.ufrn.imd.incluevents.model.Validacao;

public interface ValidacaoRepository extends JpaRepository<Validacao, Integer> {
  public Optional<Validacao> findByUsuarioAndSelo(Usuario usuario, Selo selo);

  public List<Validacao> findByUsuario(Usuario usuario);
}
