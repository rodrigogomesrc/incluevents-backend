package br.ufrn.imd.incluevents.service;

import br.ufrn.imd.incluevents.exceptions.BusinessException;
import br.ufrn.imd.incluevents.exceptions.SeloNotFoundException;
import br.ufrn.imd.incluevents.exceptions.enums.ExceptionTypesEnum;
import br.ufrn.imd.incluevents.model.Estabelecimento;
import br.ufrn.imd.incluevents.model.Selo;
import br.ufrn.imd.incluevents.repository.EstabelecimentoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EstabelecimentoService {

    private final EstabelecimentoRepository estabelecimentoRepository;
    private final SeloService seloService;

    public EstabelecimentoService(EstabelecimentoRepository estabelecimentoRepository, SeloService seloService) {
        this.estabelecimentoRepository = estabelecimentoRepository;
        this.seloService = seloService;
    }

    public Estabelecimento createEstabelecimento(Estabelecimento estabelecimento) throws BusinessException {
        if (estabelecimento.getNome().isEmpty() || estabelecimento.getNome().equals(" ")) {
            throw new BusinessException("Nome inválido", ExceptionTypesEnum.BAD_REQUEST);
        }

        return estabelecimentoRepository.save(estabelecimento);
    }

    public Estabelecimento updateEstabelecimento(Estabelecimento estabelecimento) throws BusinessException {
        if (estabelecimento.getNome().isEmpty() || estabelecimento.getNome().equals(" ")) {
            throw new BusinessException("Nome inválido", ExceptionTypesEnum.BAD_REQUEST);
        }
        return estabelecimentoRepository.save(estabelecimento);
    }

    public Estabelecimento getEstabelecimentoById(int id) throws BusinessException {
        if (id < 0) {
            throw new BusinessException("Id inválido", ExceptionTypesEnum.BAD_REQUEST);
        }

        return estabelecimentoRepository.findById(id).orElseThrow(() ->
            new BusinessException("Estabelecimento não encontrado", ExceptionTypesEnum.NOT_FOUND)
        );
    }

    public Estabelecimento addSeloToEstabelecimento(int estabelecimentoId, int seloId)
            throws BusinessException {

        if (estabelecimentoId < 0) {
            throw new BusinessException("Id do estabelecimento inválido", ExceptionTypesEnum.BAD_REQUEST);
        }

        if (seloId < 0) {
            throw new BusinessException("Id do selo inválido", ExceptionTypesEnum.BAD_REQUEST);
        }

        Optional<Estabelecimento> estabelecimentoOptional = estabelecimentoRepository.findById(estabelecimentoId);
        if (estabelecimentoOptional.isEmpty()) {
            throw new BusinessException("Estabelecimento não encontrado", ExceptionTypesEnum.NOT_FOUND);
        }

        Selo selo;
        try {
            selo = seloService.getById(seloId);
            Estabelecimento estabelecimento = estabelecimentoOptional.get();
            estabelecimento.getSelos().add(selo);
            return estabelecimentoRepository.save(estabelecimento);
        } catch (SeloNotFoundException e) {
            throw new BusinessException("Selo não encontrado", ExceptionTypesEnum.NOT_FOUND);
        }
    }

    public Estabelecimento removeSeloFromEstabelecimento(int estabelecimentoId, int seloId)
            throws BusinessException {
        Optional<Estabelecimento> estabelecimentoOptional = estabelecimentoRepository.findById(estabelecimentoId);

        if (estabelecimentoId < 0) {
            throw new BusinessException("Id do estabelecimento inválido", ExceptionTypesEnum.BAD_REQUEST);
        }

        if (seloId < 0) {
            throw new BusinessException("Id do selo inválido", ExceptionTypesEnum.BAD_REQUEST);
        }

        if (estabelecimentoOptional.isEmpty()) {
            throw new BusinessException("Estabelecimento não encontrado", ExceptionTypesEnum.NOT_FOUND);
        }

        Selo selo;
        try {
            selo = seloService.getById(seloId);
            Estabelecimento estabelecimento = estabelecimentoOptional.get();
            estabelecimento.getSelos().remove(selo);
            return estabelecimentoRepository.save(estabelecimento);
        } catch (SeloNotFoundException e) {
            throw new BusinessException("Selo não encontrado", ExceptionTypesEnum.NOT_FOUND);
        }
    }

    public void deleteEstabelecimento(int id) throws BusinessException {

        if (id < 0) {
            throw new BusinessException("Id inválido", ExceptionTypesEnum.BAD_REQUEST);
        }
        estabelecimentoRepository.deleteById(id);
    }

}
