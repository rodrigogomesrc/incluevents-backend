package br.ufrn.imd.incluevents.service;
import br.ufrn.imd.incluevents.exceptions.BusinessException;
import br.ufrn.imd.incluevents.exceptions.SeloNotFoundException;
import br.ufrn.imd.incluevents.exceptions.enums.ExceptionTypesEnum;
import br.ufrn.imd.incluevents.model.Estabelecimento;
import br.ufrn.imd.incluevents.model.Evento;
import br.ufrn.imd.incluevents.model.Selo;
import br.ufrn.imd.incluevents.model.enums.TipoSeloEnum;
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

    public Selo getByEventoAndTipoSelo(Evento evento, TipoSeloEnum tipoSelo) throws BusinessException {
        return seloRepository.findByEventoAndTipoSelo(evento, tipoSelo).orElseThrow(() ->
            new BusinessException("Selo não encontrado", ExceptionTypesEnum.NOT_FOUND)
        );
    }

    public Selo getByEstabelecimentoAndTipoSelo(Estabelecimento estabelecimento, TipoSeloEnum tipoSelo) throws BusinessException {
        return seloRepository.findByEstabelecimentoAndTipoSelo(estabelecimento, tipoSelo).orElseThrow(() ->
            new BusinessException("Selo não encontrado", ExceptionTypesEnum.NOT_FOUND)
        );
    }
}
