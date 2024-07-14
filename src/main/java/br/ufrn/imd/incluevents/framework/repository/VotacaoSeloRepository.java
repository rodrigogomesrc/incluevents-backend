package br.ufrn.imd.incluevents.framework.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.ufrn.imd.incluevents.framework.dto.GrupoVotacaoSeloDto;
import br.ufrn.imd.incluevents.framework.model.Selo;
import br.ufrn.imd.incluevents.framework.model.Usuario;
import br.ufrn.imd.incluevents.framework.model.VotacaoSelo;

public interface VotacaoSeloRepository extends JpaRepository<VotacaoSelo, Integer> {
  public Optional<VotacaoSelo> findByUsuarioAndSelo(Usuario usuario, Selo selo);

  public List<VotacaoSelo> findByUsuario(Usuario usuario);

  public List<VotacaoSelo> findBySelo(Selo selo);

  @Query(
    value =
      "SELECT new br.ufrn.imd.incluevents.framework.dto.GrupoVotacaoSeloDto("
      + "  s,"
      + "  SUM(vs.score),"
      + "  COUNT(vs),"
      + "  SUM(CASE WHEN possuiSelo = true THEN vs.score ELSE 0 END),"
      + "  SUM(CASE WHEN possuiSelo = false THEN vs.score ELSE 0 END),"
      + "  SUM(CASE WHEN possuiSelo = true THEN 1 ELSE 0 END),"
      + "  SUM(CASE WHEN possuiSelo = false THEN 1 ELSE 0 END)"
      + ") "
      + "FROM Selo s "
      + "INNER JOIN s.votacoesSelo vs "
      + "WHERE s.evento.id = :idEvento AND s.validado = false AND vs.verificado = false "
      + "GROUP BY s "
//      + "HAVING SUM(vs.score) >= 500 "
//      + "AND SUM(CASE WHEN possuiSelo = true THEN vs.score ELSE 0 END) * 100 / SUM(vs.score) >= 70"
  )
  public List<GrupoVotacaoSeloDto> findValidacoesPendentesByEvento(@Param("idEvento") int idEvento);

  @Query(
    value =
      "SELECT new br.ufrn.imd.incluevents.framework.dto.GrupoVotacaoSeloDto("
      + "  s,"
      + "  SUM(vs.score),"
      + "  COUNT(vs),"
      + "  SUM(CASE WHEN possuiSelo = true THEN vs.score ELSE 0 END),"
      + "  SUM(CASE WHEN possuiSelo = false THEN vs.score ELSE 0 END),"
      + "  SUM(CASE WHEN possuiSelo = true THEN 1 ELSE 0 END),"
      + "  SUM(CASE WHEN possuiSelo = false THEN 1 ELSE 0 END)"
      + ") "
      + "FROM Selo s "
      + "INNER JOIN s.votacoesSelo vs "
      + "WHERE s.estabelecimento.id = :idEstabelecimento AND s.validado = false AND vs.verificado = false "
      + "GROUP BY s "
      + "HAVING SUM(vs.score) >= 500 "
      + "AND SUM(CASE WHEN possuiSelo = true THEN vs.score ELSE 0 END) * 100 / SUM(vs.score) >= 70"
  )
  public List<GrupoVotacaoSeloDto> findValidacoesPendentesByEstabelecimento(@Param("idEstabelecimento") int idEstabelecimento);
}
