package br.ufrn.imd.incluevents.service;
import br.ufrn.imd.incluevents.model.Selo;
import br.ufrn.imd.incluevents.repository.SeloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SeloService {

    @Autowired
    private SeloRepository seloRepository;

    public ResponseEntity<Selo> save(Selo selo) {
        Selo novoSelo = seloRepository.save(selo);
        return ResponseEntity.ok().body(novoSelo);
    }

    public ResponseEntity<Selo> update(Selo selo) {
        if (seloRepository.existsById(selo.getId())) {
            Selo seloAtualizado = seloRepository.save(selo);
            return ResponseEntity.ok().body(seloAtualizado);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public void deleteById(Integer id) {
        seloRepository.deleteById(id);
    }

    public ResponseEntity<Selo> getById(Integer id) {
        Optional<Selo> selo = seloRepository.findById(id);
        return selo.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    public ResponseEntity<List<Selo>> findAll() {
        List<Selo> selos = seloRepository.findAll();
        return ResponseEntity.ok().body(selos);
    }
}