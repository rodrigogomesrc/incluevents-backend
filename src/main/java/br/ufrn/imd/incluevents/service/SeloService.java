package br.ufrn.imd.incluevents.service;
import br.ufrn.imd.incluevents.dto.CreateSeloDto;
import br.ufrn.imd.incluevents.exceptions.BusinessException;
import br.ufrn.imd.incluevents.exceptions.enums.ExceptionTypesEnum;
import br.ufrn.imd.incluevents.model.Estabelecimento;
import br.ufrn.imd.incluevents.model.Evento;
import br.ufrn.imd.incluevents.model.Selo;
import br.ufrn.imd.incluevents.model.enums.TipoSeloEnum;
import br.ufrn.imd.incluevents.repository.EstabelecimentoRepository;
import br.ufrn.imd.incluevents.repository.EventoRepository;
import br.ufrn.imd.incluevents.repository.SeloRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
public class SeloService {
    private final SeloRepository seloRepository;
    private final EventoRepository eventoRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;

    public SeloService(SeloRepository seloRepository, EventoRepository eventoRepository, EstabelecimentoRepository estabelecimentoRepository) {
        this.seloRepository = seloRepository;
        this.eventoRepository = eventoRepository;
        this.estabelecimentoRepository = estabelecimentoRepository;
    }

    public void validate(CreateSeloDto createSeloDto) throws BusinessException {
        List<String> errors = new ArrayList<>();

        if (createSeloDto.idEvento() == null && createSeloDto.idEstabelecimento() == null) {
            errors.add("Deve ter o campo idEvento ou idEstabelecimento");
        } else if (createSeloDto.idEvento() != null && createSeloDto.idEstabelecimento() != null) {
            errors.add("Deve ter apenas um dos campos idEvento ou idEstabelecimento");
        } else if (createSeloDto.idEstabelecimento() != null && createSeloDto.idEstabelecimento() < 0) {
            errors.add("Id do estabelecimento inválido");
        } else if (createSeloDto.idEvento() != null && createSeloDto.idEvento() < 0) {
            errors.add("Id do evento inválido");
        }

        if (createSeloDto.tipoSelo() == null) {
            errors.add("Deve ter campo tipoSelo");
        } else if (createSeloDto.idEvento() != null && createSeloDto.tipoSelo().getTipoEntidade() != "EVENTO") {
            errors.add("Tipo de selo inválido");
        } else if (createSeloDto.idEstabelecimento() != null && createSeloDto.tipoSelo().getTipoEntidade() != "ESTABELECIMENTO") {
            errors.add("Tipo de selo inválido");
        }

        if (errors.size() > 0) {
            throw new BusinessException(String.join("\n", errors), ExceptionTypesEnum.BAD_REQUEST);
        }
    }

    public Selo create(CreateSeloDto createSeloDto) throws BusinessException {
        validate(createSeloDto);

        Optional<Selo> seloOptional;
        Evento evento = null;
        Estabelecimento estabelecimento = null;

        if (createSeloDto.idEvento() != null) {
            evento = eventoRepository.findById(createSeloDto.idEvento()).orElseThrow(() ->
                new BusinessException("Evento não encontrado", ExceptionTypesEnum.NOT_FOUND)
            );

            seloOptional = seloRepository.findByEventoAndTipoSelo(evento, createSeloDto.tipoSelo());
        } else {
            estabelecimento = estabelecimentoRepository.findById(createSeloDto.idEstabelecimento()).orElseThrow(() ->
                new BusinessException("Estabelecimento não encontrado", ExceptionTypesEnum.NOT_FOUND)
            );

            seloOptional = seloRepository.findByEstabelecimentoAndTipoSelo(estabelecimento, createSeloDto.tipoSelo());
        }

        if (seloOptional.isPresent()) {
            throw new BusinessException("Selo já existe", ExceptionTypesEnum.CONFLICT);
        }

        Selo selo = new Selo();

        selo.setEvento(evento);
        selo.setEstabelecimento(estabelecimento);
        selo.setTipoSelo(createSeloDto.tipoSelo());
        selo.setValidado(false);

        seloRepository.save(selo);

        return selo;
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
        Evento evento = eventoRepository.findById(idEvento).orElseThrow(() ->
            new BusinessException("Evento não encontrado", ExceptionTypesEnum.NOT_FOUND)
        );

        return seloRepository.findByEventoAndTipoSelo(evento, tipoSelo).orElseThrow(() ->
            new BusinessException("Selo não encontrado", ExceptionTypesEnum.NOT_FOUND)
        );
    }

    public Selo getByIdEstabelecimentoAndTipoSelo(Integer idEstabelecimento, TipoSeloEnum tipoSelo) throws BusinessException {
        Estabelecimento estabelecimento = estabelecimentoRepository.findById(idEstabelecimento).orElseThrow(() ->
            new BusinessException("Estabelecimento não encontrado", ExceptionTypesEnum.NOT_FOUND)
        );

        return seloRepository.findByEstabelecimentoAndTipoSelo(estabelecimento, tipoSelo).orElseThrow(() ->
            new BusinessException("Selo não encontrado", ExceptionTypesEnum.NOT_FOUND)
        );
    }

    public void validateSeloById(Integer idSelo) throws BusinessException {
        if (idSelo == null || idSelo < 0) {
            throw new BusinessException("Id do selo inválido", ExceptionTypesEnum.BAD_REQUEST);
        }

        Selo selo = seloRepository.findById(idSelo).orElseThrow(() ->
            new BusinessException("Selo não encontrado", ExceptionTypesEnum.NOT_FOUND)
        );

        selo.setValidado(true);

        seloRepository.save(selo);
    }
}
