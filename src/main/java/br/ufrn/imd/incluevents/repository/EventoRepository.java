package br.ufrn.imd.incluevents.repository;

import br.ufrn.imd.incluevents.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventoRepository extends JpaRepository<Evento, Integer> {
}
