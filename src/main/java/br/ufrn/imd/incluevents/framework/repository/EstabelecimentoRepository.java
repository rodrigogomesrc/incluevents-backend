package br.ufrn.imd.incluevents.framework.repository;

import br.ufrn.imd.incluevents.framework.model.Estabelecimento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstabelecimentoRepository extends JpaRepository<Estabelecimento, Integer> {
}
