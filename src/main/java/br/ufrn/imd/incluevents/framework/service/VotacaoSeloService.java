package br.ufrn.imd.incluevents.framework.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import br.ufrn.imd.incluevents.framework.dto.CreateVotacaoSeloDto;
import br.ufrn.imd.incluevents.framework.dto.EstabelecimentoGrupoVotacaoSeloDto;
import br.ufrn.imd.incluevents.framework.dto.EventoGrupoVotacaoSeloDto;
import br.ufrn.imd.incluevents.framework.dto.ValidateVotacaoDto;
import br.ufrn.imd.incluevents.framework.exceptions.BusinessException;
import br.ufrn.imd.incluevents.framework.exceptions.enums.ExceptionTypesEnum;
import br.ufrn.imd.incluevents.framework.model.Selo;
import br.ufrn.imd.incluevents.framework.model.Usuario;
import br.ufrn.imd.incluevents.framework.model.VotacaoSelo;
import br.ufrn.imd.incluevents.framework.model.enums.TipoSeloEnum;
import br.ufrn.imd.incluevents.framework.repository.VotacaoSeloRepository;
import jakarta.transaction.Transactional;

@Service
public abstract class VotacaoSeloService {
    protected final VotacaoSeloRepository votacaoSeloRepository;

    protected final SeloService seloService;
    protected final UsuarioService usuarioService;
    protected final EventoService eventoService;
    protected final EstabelecimentoService estabelecimentoService;

    public VotacaoSeloService(
            VotacaoSeloRepository votacaoSeloRepository,
            SeloService seloService,
            UsuarioService usuarioService,
            EventoService eventoService,
            EstabelecimentoService estabelecimentoService
    ) {
        this.votacaoSeloRepository = votacaoSeloRepository;
        this.seloService = seloService;
        this.usuarioService = usuarioService;
        this.eventoService = eventoService;
        this.estabelecimentoService = estabelecimentoService;
    }

    @Transactional
    public VotacaoSelo createVotacaoSelo(VotacaoSelo votacaoSelo) throws
            BusinessException {

        return votacaoSeloRepository.save(votacaoSelo);
    }

    protected void validateCreateDto(CreateVotacaoSeloDto createVotacaoSeloDto) throws BusinessException {
        List<String> errors = new ArrayList<>();

        if (createVotacaoSeloDto.idEvento() == null && createVotacaoSeloDto.idEstabelecimento() == null) {
            errors.add("Deve ter o campo idEvento ou idEstabelecimento");
        } else if (createVotacaoSeloDto.idEvento() != null && createVotacaoSeloDto.idEstabelecimento() != null) {
            errors.add("Deve ter apenas um dos campos idEvento ou idEstabelecimento");
        } else if (createVotacaoSeloDto.idEstabelecimento() != null && createVotacaoSeloDto.idEstabelecimento() < 0) {
            errors.add("Id do estabelecimento inválido");
        } else if (createVotacaoSeloDto.idEvento() != null && createVotacaoSeloDto.idEvento() < 0) {
            errors.add("Id do evento inválido");
        }

        if (createVotacaoSeloDto.tipoSelo() == null) {
            errors.add("Deve ter campo tipoSelo");
        } else if (createVotacaoSeloDto.idEvento() != null && !createVotacaoSeloDto.tipoSelo().getTipoEntidade().equals("EVENTO")) {
            errors.add("Tipo de selo inválido");
        } else if (createVotacaoSeloDto.idEstabelecimento() != null && !createVotacaoSeloDto.tipoSelo().getTipoEntidade().equals("ESTABELECIMENTO")) {
            errors.add("Tipo de selo inválido");
        }

        if (errors.size() > 0) {
            throw new BusinessException(String.join("\n", errors), ExceptionTypesEnum.BAD_REQUEST);
        }

        if (createVotacaoSeloDto.possuiSelo() == null) {
            errors.add("Deve ter campo possuiSelo");
        }

        if (errors.size() > 0) {
            throw new BusinessException(String.join("\n", errors), ExceptionTypesEnum.BAD_REQUEST);
        }
    }

    protected void validateVotacaoDto(ValidateVotacaoDto validateVotacaoDto) throws BusinessException {
        List<String> errors = new ArrayList<>();

        if (validateVotacaoDto.idSelo() == null) {
            errors.add("Deve ter o campo idSelo");
        }

        if (validateVotacaoDto.possuiSelo() == null) {
            errors.add("Deve ter o campo possuiSelo");
        }

        if (errors.size() > 0) {
            throw new BusinessException(String.join("\n", errors), ExceptionTypesEnum.BAD_REQUEST);
        }
    }

    public List<TipoSeloEnum> getDisponiveisByEstabelecimento(Integer idEstabelecimento, Usuario usuario) throws
            BusinessException {
        return Stream.of(TipoSeloEnum.values())
                .parallel()
                .filter(tipoSelo -> {
                    if (!tipoSelo.getTipoEntidade().equals("ESTABELECIMENTO")) {
                        return false;
                    }

                    Selo selo;

                    try {
                        selo = seloService.getByIdEstabelecimentoAndTipoSelo(idEstabelecimento, tipoSelo);
                    } catch (BusinessException e) {
                        selo = null;
                    }

                    if (selo == null) {
                        return true;
                    } else if (selo.getValidado()) {
                        return false;
                    } else {
                        return votacaoSeloRepository.findByUsuarioAndSelo(usuario, selo).isEmpty();
                    }
                })
                .collect(Collectors.toList());
    }

    public List<TipoSeloEnum> getDisponiveisByEvento(Integer idEvento, Usuario usuario) throws
            BusinessException {
        return Stream.of(TipoSeloEnum.values())
                .parallel()
                .filter(tipoSelo -> {
                    if (!tipoSelo.getTipoEntidade().equals("EVENTO")) {
                        return false;
                    }

                    Selo selo;

                    try {
                        selo = seloService.getByIdEventoAndTipoSelo(idEvento, tipoSelo);
                    } catch (BusinessException e) {
                        selo = null;
                    }

                    if (selo == null) {
                        return true;
                    } else if (selo.getValidado()) {
                        return false;
                    } else {
                        return votacaoSeloRepository.findByUsuarioAndSelo(usuario, selo).isEmpty();
                    }
                })
                .collect(Collectors.toList());
    }

    public abstract VotacaoSelo create(CreateVotacaoSeloDto createVotacaoSeloDto, Usuario usuario) throws BusinessException;

    @Transactional
    public abstract void validateVotacao(final ValidateVotacaoDto validateVotacaoDto, Usuario usuario) throws BusinessException;

    public abstract List<EventoGrupoVotacaoSeloDto> getValidacoesPendentesByEvento(Usuario usuario) throws BusinessException;

    public abstract List<EstabelecimentoGrupoVotacaoSeloDto> getValidacoesPendentesByEstabelecimento(Usuario usuario) throws BusinessException;
}
