package br.ufrn.imd.incluevents.framework.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.ufrn.imd.incluevents.framework.model.DocumentacaoSelo;
import br.ufrn.imd.incluevents.framework.model.Selo;
import br.ufrn.imd.incluevents.framework.model.Usuario;

public interface DocumentacaoSeloRepository extends JpaRepository<DocumentacaoSelo, Integer> {
  public Optional<DocumentacaoSelo> findByUsuarioAndSelo(Usuario usuario, Selo selo);

  @Query(
    value =
      "SELECT ds "
      + "FROM DocumentacaoSelo ds "
      + "INNER JOIN ds.selo s "
      + "INNER JOIN s.evento e "
      + "WHERE e.id = :idEvento AND ds.valida IS NULL"
  )
  public List<DocumentacaoSelo> findValidacoesPendentesByEvento(@Param("idEvento") int idEvento);

  @Query(
    value =
      "SELECT ds "
      + "FROM DocumentacaoSelo ds "
      + "INNER JOIN ds.selo s "
      + "INNER JOIN s.estabelecimento e "
      + "WHERE e.id = :idEstabelecimento AND ds.valida IS NULL"
  )
  public List<DocumentacaoSelo> findValidacoesPendentesByEstabelecimento(@Param("idEstabelecimento") int idEstabelecimento);
}
