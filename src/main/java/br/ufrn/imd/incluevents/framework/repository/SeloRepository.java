package br.ufrn.imd.incluevents.framework.repository;

import br.ufrn.imd.incluevents.framework.model.Estabelecimento;
import br.ufrn.imd.incluevents.framework.model.Evento;
import br.ufrn.imd.incluevents.framework.model.Selo;
import br.ufrn.imd.incluevents.framework.model.enums.TipoSeloEnum;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SeloRepository extends JpaRepository<Selo, Integer> {
  Optional<Selo> findByEventoAndTipoSelo(Evento evento, TipoSeloEnum tipoSelo);

  Optional<Selo> findByEstabelecimentoAndTipoSelo(Estabelecimento estabelecimento, TipoSeloEnum tipoSelo);
}

