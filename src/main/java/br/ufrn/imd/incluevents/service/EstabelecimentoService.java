package br.ufrn.imd.incluevents.service;

import br.ufrn.imd.incluevents.dto.CreateEstabelecimentoDto;
import br.ufrn.imd.incluevents.dto.EstabelecimentoDto;
import br.ufrn.imd.incluevents.dto.UpdateEstabelecimentoDto;
import br.ufrn.imd.incluevents.exceptions.BusinessException;
import br.ufrn.imd.incluevents.exceptions.enums.ExceptionTypesEnum;
import br.ufrn.imd.incluevents.model.Estabelecimento;
import br.ufrn.imd.incluevents.model.Selo;
import br.ufrn.imd.incluevents.repository.EstabelecimentoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

@Service
public class EstabelecimentoService {

    private final EstabelecimentoRepository estabelecimentoRepository;
    private final SeloService seloService;

    public EstabelecimentoService(EstabelecimentoRepository estabelecimentoRepository, SeloService seloService) {
        this.estabelecimentoRepository = estabelecimentoRepository;
        this.seloService = seloService;
    }

    public List<Estabelecimento> findAll() {
        return this.estabelecimentoRepository.findAll();
    }

    public EstabelecimentoDto createEstabelecimento(CreateEstabelecimentoDto estabelecimento) throws BusinessException {
        if (estabelecimento.nome().isEmpty() || estabelecimento.nome().equals(" ")) {
            throw new BusinessException("Nome inválido", ExceptionTypesEnum.BAD_REQUEST);
        }

        Estabelecimento newEstabelecimento = new Estabelecimento();
        newEstabelecimento.setNome(estabelecimento.nome());
        newEstabelecimento.setEndereco(estabelecimento.endereco());
        newEstabelecimento.setTelefone(estabelecimento.telefone());
        Estabelecimento created = estabelecimentoRepository.save(newEstabelecimento);
        return new EstabelecimentoDto(created.getId(), created.getNome(),
                created.getEndereco(), created.getTelefone(), created.getSelos());
    }

    public EstabelecimentoDto updateEstabelecimento(UpdateEstabelecimentoDto estabelecimento) throws BusinessException {
        if (estabelecimento.nome().isEmpty() || estabelecimento.nome().equals(" ")) {
            throw new BusinessException("Nome inválido", ExceptionTypesEnum.BAD_REQUEST);
        }
        Estabelecimento estabelecimentoToUpdate = new Estabelecimento();
        estabelecimentoToUpdate.setId(estabelecimento.id());
        estabelecimentoToUpdate.setNome(estabelecimento.nome());
        estabelecimentoToUpdate.setEndereco(estabelecimento.endereco());
        estabelecimentoToUpdate.setTelefone(estabelecimento.telefone());

        Estabelecimento updated = estabelecimentoRepository.save(estabelecimentoToUpdate);
        return new EstabelecimentoDto(updated.getId(), updated.getNome(),
                updated.getEndereco(), updated.getTelefone(), updated.getSelos());

    }

    public EstabelecimentoDto getById(int id) throws BusinessException {
        if (id < 0) {
            throw new BusinessException("Id inválido", ExceptionTypesEnum.BAD_REQUEST);
        }

        Optional<Estabelecimento> found =  estabelecimentoRepository.findById(id);
        if (found.isEmpty()) {
            throw new BusinessException("Estabelecimento não encontrado", ExceptionTypesEnum.NOT_FOUND);
        }

        Estabelecimento estabelecimento = found.get();
        return new EstabelecimentoDto(estabelecimento.getId(), estabelecimento.getNome(),
                estabelecimento.getEndereco(), estabelecimento.getTelefone(), estabelecimento.getSelos());
    }

    public EstabelecimentoDto addSeloToEstabelecimento(int estabelecimentoId, int seloId)
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
        Estabelecimento estabelecimento;
        try {
            selo = seloService.getById(seloId);
            estabelecimento = estabelecimentoOptional.get();
            estabelecimento.getSelos().add(selo);
            Estabelecimento saved = estabelecimentoRepository.save(estabelecimento);
            return new EstabelecimentoDto(saved.getId(), saved.getNome(),
                    saved.getEndereco(), saved.getTelefone(), saved.getSelos());

        } catch (BusinessException e) {
            throw new BusinessException("Selo não encontrado", ExceptionTypesEnum.NOT_FOUND);
        }
    }

    public EstabelecimentoDto removeSeloFromEstabelecimento(int estabelecimentoId, int seloId)
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
            Estabelecimento fromRemoved = estabelecimentoRepository.save(estabelecimento);
            return new EstabelecimentoDto(fromRemoved.getId(), fromRemoved.getNome(),
                    fromRemoved.getEndereco(), fromRemoved.getTelefone(), fromRemoved.getSelos());
        } catch (BusinessException e) {
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
