package br.ufrn.imd.incluevents.service;
import br.ufrn.imd.incluevents.exceptions.BusinessException;
import br.ufrn.imd.incluevents.exceptions.SeloNotFoundException;
import br.ufrn.imd.incluevents.exceptions.enums.ExceptionTypesEnum;
import br.ufrn.imd.incluevents.model.Selo;
import br.ufrn.imd.incluevents.repository.SeloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SeloService {

    @Autowired
    private SeloRepository seloRepository;

    public Selo save(Selo selo) throws BusinessException {
        if (selo.getTipoSelo() == null) {
            throw new BusinessException("Tipo do selo inválido", ExceptionTypesEnum.BAD_REQUEST);
        }
        return seloRepository.save(selo);
    }

    public Selo update(Selo selo) throws BusinessException {
        if (selo.getTipoSelo() == null) {
            throw new BusinessException("Selo sem tipo definido", ExceptionTypesEnum.BAD_REQUEST);
        }
        if (!seloRepository.existsById(selo.getId())) {
            throw new BusinessException("Selo não existe", ExceptionTypesEnum.NOT_FOUND);
        }
        return seloRepository.save(selo);
    }


    public void deleteById(Integer id) throws BusinessException {
        if (id < 0) {
            throw new BusinessException("Id do selo inválido", ExceptionTypesEnum.BAD_REQUEST);
        }
        if (!seloRepository.existsById(id)) {
            throw new BusinessException("Selo não encontrado", ExceptionTypesEnum.NOT_FOUND);
        }
        seloRepository.deleteById(id);
    }

    public Selo getById(Integer id) throws BusinessException {
        if (id < 0) {
            throw new BusinessException("Id do selo inválido", ExceptionTypesEnum.BAD_REQUEST);
        }
        if(seloRepository.findById(id).isEmpty()){
            throw new BusinessException("Selo não encontrado", ExceptionTypesEnum.NOT_FOUND);
        }
        return seloRepository.findById(id).get();
    }

    public List<Selo> findAll() throws BusinessException {
        return seloRepository.findAll();
    }
}