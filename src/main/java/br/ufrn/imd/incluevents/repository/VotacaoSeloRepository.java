package br.ufrn.imd.incluevents.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.ufrn.imd.incluevents.model.GrupoVotacaoSelo;
import br.ufrn.imd.incluevents.model.Selo;
import br.ufrn.imd.incluevents.model.Usuario;
import br.ufrn.imd.incluevents.model.VotacaoSelo;

public interface VotacaoSeloRepository extends JpaRepository<VotacaoSelo, Integer> {
  public Optional<VotacaoSelo> findByUsuarioAndSelo(Usuario usuario, Selo selo);

  public List<VotacaoSelo> findByUsuario(Usuario usuario);

  public List<VotacaoSelo> findBySelo(Selo selo);

  @Query(
    value =
      "SELECT selo.id as idSelo,"
      + " selo.tiposelo as tipoSelo,"
      + " COUNT(validacao_selo.id) as totalEnvios,"
      + " SUM(CASE WHEN validacao_selo.possuiSelo = TRUE THEN validacao_selo.score ELSE 0 END) as scorePositivo,"
      + " SUM(CASE WHEN validacao_selo.possuiSelo = FALSE THEN validacao_selo.score ELSE 0 END) as scoreNegativo"
      + " FROM selo"
      + " INNER JOIN validacao_selo ON selo.id = validacao_selo.id_selo"
      + " GROUP BY selo.id"
      + " HAVING SUM(validacao_selo.score) >= 0",
    nativeQuery = true
  )
  public List<GrupoVotacaoSelo> findValidacoesPendentes();
}
