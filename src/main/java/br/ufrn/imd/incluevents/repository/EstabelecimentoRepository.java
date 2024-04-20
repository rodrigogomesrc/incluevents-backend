package br.ufrn.imd.incluevents.repository;

import br.ufrn.imd.incluevents.model.Estabelecimento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstabelecimentoRepository extends JpaRepository<Estabelecimento, Integer> {
}
