package br.ufrn.imd.incluevents.service;

import br.ufrn.imd.incluevents.dto.CreateEstabelecimentoDto;
import br.ufrn.imd.incluevents.dto.EstabelecimentoDto;
import br.ufrn.imd.incluevents.dto.UpdateEstabelecimentoDto;
import br.ufrn.imd.incluevents.exceptions.BusinessException;
import br.ufrn.imd.incluevents.exceptions.enums.ExceptionTypesEnum;
import br.ufrn.imd.incluevents.model.Estabelecimento;
import br.ufrn.imd.incluevents.model.Usuario;
import br.ufrn.imd.incluevents.repository.EstabelecimentoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;

@Service
public class EstabelecimentoService {

    private final EstabelecimentoRepository estabelecimentoRepository;

    public EstabelecimentoService(EstabelecimentoRepository estabelecimentoRepository) {
        this.estabelecimentoRepository = estabelecimentoRepository;
    }

    public List<Estabelecimento> findAll() {
        return this.estabelecimentoRepository.findAll();
    }

    public EstabelecimentoDto createEstabelecimento(CreateEstabelecimentoDto estabelecimento, Usuario usuario) throws BusinessException {
        if (estabelecimento.nome().isEmpty() || estabelecimento.nome().equals(" ")) {
            throw new BusinessException("Nome inválido", ExceptionTypesEnum.BAD_REQUEST);
        }

        Estabelecimento newEstabelecimento = new Estabelecimento();
        newEstabelecimento.setNome(estabelecimento.nome());
        newEstabelecimento.setEndereco(estabelecimento.endereco());
        newEstabelecimento.setTelefone(estabelecimento.telefone());
        newEstabelecimento.setCriador(usuario);
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

    public Estabelecimento getEstabelecimentoById(int id) throws BusinessException {
        if (id < 0) {
            throw new BusinessException("Id inválido", ExceptionTypesEnum.BAD_REQUEST);
        }

        Optional<Estabelecimento> found =  estabelecimentoRepository.findById(id);
        if (found.isEmpty()) {
            throw new BusinessException("Estabelecimento não encontrado", ExceptionTypesEnum.NOT_FOUND);
        }

        Estabelecimento estabelecimento = found.get();

        return estabelecimento;
    }

    public void deleteEstabelecimento(int id) throws BusinessException {

        if (id < 0) {
            throw new BusinessException("Id inválido", ExceptionTypesEnum.BAD_REQUEST);
        }
        estabelecimentoRepository.deleteById(id);
    }

}
