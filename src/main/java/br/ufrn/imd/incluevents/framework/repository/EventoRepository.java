package br.ufrn.imd.incluevents.framework.repository;

import br.ufrn.imd.incluevents.framework.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Integer> {

    @Query("SELECT e FROM Evento e WHERE e.urlOriginal IN :urls")
    List<Evento> findByUrlOriginals(List<String> urls);

    List<Evento> findByNomeContaining(String nome);
}
