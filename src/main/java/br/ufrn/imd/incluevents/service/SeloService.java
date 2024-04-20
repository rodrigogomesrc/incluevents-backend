package br.ufrn.imd.incluevents.service;
import br.ufrn.imd.incluevents.exceptions.SeloNotFoundException;
import br.ufrn.imd.incluevents.model.Selo;
import br.ufrn.imd.incluevents.repository.SeloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeloService {

    @Autowired
    private SeloRepository seloRepository;

    public Selo save(Selo selo) {
        return seloRepository.save(selo);
    }

    public Selo update(Selo selo) throws SeloNotFoundException {
        if (seloRepository.existsById(selo.getId())) {
            return seloRepository.save(selo);
        }
        throw new SeloNotFoundException();
    }

    public void deleteById(Integer id) {
        seloRepository.deleteById(id);
    }

    public Selo getById(Integer id) throws SeloNotFoundException {
        return seloRepository.findById(id).orElseThrow(SeloNotFoundException::new);
    }

    public List<Selo> findAll() {
        return seloRepository.findAll();
    }
}