package br.ufrn.imd.incluevents.service;

import br.ufrn.imd.incluevents.exceptions.BusinessException;
import br.ufrn.imd.incluevents.exceptions.enums.ExceptionTypesEnum;
import br.ufrn.imd.incluevents.model.Estabelecimento;
import br.ufrn.imd.incluevents.model.Evento;
import br.ufrn.imd.incluevents.model.Selo;
import br.ufrn.imd.incluevents.model.enums.TipoSeloEnum;
import br.ufrn.imd.incluevents.repository.SeloRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Service
public class SeloService {
    private final SeloRepository seloRepository;

    private final EventoService eventoService;
    private final EstabelecimentoService estabelecimentoService;

    public SeloService(SeloRepository seloRepository, EventoService eventoService, EstabelecimentoService estabelecimentoService) {
        this.seloRepository = seloRepository;
        this.eventoService = eventoService;
        this.estabelecimentoService = estabelecimentoService;
    }

    public Selo createToEventoIfNotExists(Evento evento, TipoSeloEnum tipoSelo) throws BusinessException {
        if (evento == null) {
            throw new BusinessException("Evento não pode ser nulo", ExceptionTypesEnum.BAD_REQUEST);
        }

        if (!tipoSelo.getTipoEntidade().equals("EVENTO")) {
            throw new BusinessException("TIpo do selo inválido", ExceptionTypesEnum.BAD_REQUEST);
        }

        Optional<Selo> seloOptional;

        seloOptional = seloRepository.findByEventoAndTipoSelo(evento, tipoSelo);

        if (seloOptional.isPresent()) {
            return seloOptional.get();
        }

        Selo selo = new Selo();

        selo.setEvento(evento);
        selo.setTipoSelo(tipoSelo);
        selo.setValidado(false);

        seloRepository.save(selo);

        return selo;
    }

    public Selo createToEventoIfNotExists(Integer idEvento, TipoSeloEnum tipoSelo) throws BusinessException {
        return createToEventoIfNotExists(eventoService.getById(idEvento), tipoSelo);
    }

    public Selo createToEstabelecimentoIfNotExists(Estabelecimento estabelecimento, TipoSeloEnum tipoSelo) throws BusinessException {
        if (estabelecimento == null) {
            throw new BusinessException("Estabelecimento não pode ser nulo", ExceptionTypesEnum.BAD_REQUEST);
        }

        if (!tipoSelo.getTipoEntidade().equals("ESTABELECIMENTO")) {
            throw new BusinessException("TIpo do selo inválido", ExceptionTypesEnum.BAD_REQUEST);
        }

        Optional<Selo> seloOptional;

        seloOptional = seloRepository.findByEstabelecimentoAndTipoSelo(estabelecimento, tipoSelo);

        if (seloOptional.isPresent()) {
            return seloOptional.get();
        }

        Selo selo = new Selo();

        selo.setEstabelecimento(estabelecimento);
        selo.setTipoSelo(tipoSelo);
        selo.setValidado(false);

        seloRepository.save(selo);

        return selo;
    }

    public Selo createToEstabelecimentoIfNotExists(Integer idEstabelecimento, TipoSeloEnum tipoSelo) throws BusinessException {
        return createToEstabelecimentoIfNotExists(estabelecimentoService.getEstabelecimentoById(idEstabelecimento), tipoSelo);
    }

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

    public Selo getByIdEventoAndTipoSelo(Integer idEvento, TipoSeloEnum tipoSelo) throws BusinessException {
        Evento evento = eventoService.getById(idEvento);

        return seloRepository.findByEventoAndTipoSelo(evento, tipoSelo).orElseThrow(() ->
            new BusinessException("Selo não encontrado", ExceptionTypesEnum.NOT_FOUND)
        );
    }

    public Selo getByIdEstabelecimentoAndTipoSelo(Integer idEstabelecimento, TipoSeloEnum tipoSelo) throws BusinessException {
        Estabelecimento estabelecimento = estabelecimentoService.getEstabelecimentoById(idEstabelecimento);

        return seloRepository.findByEstabelecimentoAndTipoSelo(estabelecimento, tipoSelo).orElseThrow(() ->
            new BusinessException("Selo não encontrado", ExceptionTypesEnum.NOT_FOUND)
        );
    }

    public void validateSelo(Selo selo) throws BusinessException {
        selo.setValidado(true);

        seloRepository.save(selo);
    }
}
