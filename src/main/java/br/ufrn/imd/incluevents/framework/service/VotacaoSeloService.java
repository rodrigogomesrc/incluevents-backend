package br.ufrn.imd.incluevents.framework.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import br.ufrn.imd.incluevents.framework.dto.CreateVotacaoSeloDto;
import br.ufrn.imd.incluevents.framework.dto.EstabelecimentoGrupoVotacaoSeloDto;
import br.ufrn.imd.incluevents.framework.dto.EventoGrupoVotacaoSeloDto;
import br.ufrn.imd.incluevents.framework.dto.GrupoVotacaoSeloDto;
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

    public VotacaoSelo create(CreateVotacaoSeloDto createVotacaoSeloDto, Usuario usuario) throws BusinessException {
        Selo selo;

        this.validateCreateDto(createVotacaoSeloDto);

        if (createVotacaoSeloDto.idEvento() != null) {
            selo = seloService.createToEventoIfNotExists(createVotacaoSeloDto.idEvento(), createVotacaoSeloDto.tipoSelo());
        } else {
            selo = seloService.createToEstabelecimentoIfNotExists(createVotacaoSeloDto.idEstabelecimento(), createVotacaoSeloDto.tipoSelo());
        }

        if (selo.getValidado()) {
            throw new BusinessException("Selo já validado", ExceptionTypesEnum.CONFLICT);
        }

        if (votacaoSeloRepository.findByUsuarioAndSelo(usuario, selo).isPresent()) {
            throw new BusinessException("Votação do selo já criada", ExceptionTypesEnum.CONFLICT);
        }

        VotacaoSelo votacaoSelo = new VotacaoSelo();

        votacaoSelo.setDescricao(createVotacaoSeloDto.descricao());
        votacaoSelo.setPossuiSelo(createVotacaoSeloDto.possuiSelo());
        votacaoSelo.setScore(this.calculateCredibilidate(usuario));
        votacaoSelo.setSelo(selo);
        votacaoSelo.setUsuario(usuario);

        return this.votacaoSeloRepository.save(votacaoSelo);
    }

    @Transactional
    public void validateVotacao(final ValidateVotacaoDto validateVotacaoDto, Usuario usuario) throws BusinessException {
        if (!this.checkIfCanValidate(usuario)) {
            throw new BusinessException("Você não tem autorização", ExceptionTypesEnum.FORBIDDEN);
        }

        validateVotacaoDto(validateVotacaoDto);

        Selo selo = seloService.getById(validateVotacaoDto.idSelo());

        if (selo.getValidado()) {
            throw new BusinessException("Selo já validado", ExceptionTypesEnum.CONFLICT);
        }

        List<VotacaoSelo> votacoesSelo = votacaoSeloRepository.findBySelo(selo);

        votacoesSelo
                .stream()
                .parallel()
                .forEach(votacaoSelo -> {
                    if (votacaoSelo.getVerificado()) {
                        return;
                    }

                    votacaoSelo.setVerificado(true);
                    this.processValidacao(votacaoSelo, validateVotacaoDto);

                });

        if (validateVotacaoDto.possuiSelo()) {
            seloService.validateSelo(selo);
        }
    }

    public List<EventoGrupoVotacaoSeloDto> getValidacoesPendentesByEvento(Usuario usuario) throws BusinessException {
        if (!this.checkIfCanValidate(usuario)) {
            throw new BusinessException("Você não tem autorização", ExceptionTypesEnum.FORBIDDEN);
        }

        return eventoService
                .findAll()
                .stream()
                .parallel()
                .map(evento -> {
                    List<GrupoVotacaoSeloDto> gruposVotacaoSelo = votacaoSeloRepository.findValidacoesPendentesByEvento(evento.getId());

                    return new EventoGrupoVotacaoSeloDto(evento, gruposVotacaoSelo);
                })
                .filter(item -> {
                    return item.getGruposVotacaoSelo().size() > 0;
                })
                .collect(Collectors.toList());
    }

    public List<EstabelecimentoGrupoVotacaoSeloDto> getValidacoesPendentesByEstabelecimento(Usuario usuario) throws BusinessException {
        if (!this.checkIfCanValidate(usuario)) {
            throw new BusinessException("Você não tem autorização", ExceptionTypesEnum.FORBIDDEN);
        }

        return estabelecimentoService
                .findAll()
                .stream()
                .parallel()
                .map(estabelecimento -> {
                    List<GrupoVotacaoSeloDto> gruposVotacaoSelo = votacaoSeloRepository.findValidacoesPendentesByEstabelecimento(estabelecimento.getId());

                    return new EstabelecimentoGrupoVotacaoSeloDto(estabelecimento, gruposVotacaoSelo);
                })
                .filter(item -> {
                    return item.getGruposVotacaoSelo().size() > 0;
                })
                .collect(Collectors.toList());
    }

    public abstract boolean checkIfCanValidate(Usuario usuario) throws BusinessException;

    public abstract int calculateCredibilidate(Usuario usuario) throws BusinessException;

    public void processValidacao(VotacaoSelo votacaoSelo, ValidateVotacaoDto validateVotacaoDto) {}
}
