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
import br.ufrn.imd.incluevents.framework.model.enums.TipoUsuarioEnum;
import br.ufrn.imd.incluevents.framework.repository.VotacaoSeloRepository;
import jakarta.transaction.Transactional;

@Service
public class VotacaoSeloService {
    private final VotacaoSeloRepository votacaoSeloRepository;

    private final SeloService seloService;
    private final UsuarioService usuarioService;
    private final EventoService eventoService;
    private final EstabelecimentoService estabelecimentoService;

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

    private void validateDto(CreateVotacaoSeloDto createVotacaoSeloDto) throws BusinessException {
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

    private void validateDto(ValidateVotacaoDto validateVotacaoDto) throws BusinessException {
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

    @Transactional
    public VotacaoSelo create(CreateVotacaoSeloDto createVotacaoSeloDto, Usuario usuario) throws
        BusinessException
    {
        validateDto(createVotacaoSeloDto);

        Selo selo;

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
        votacaoSelo.setScore(usuario.getReputacao());
        votacaoSelo.setSelo(selo);
        votacaoSelo.setUsuario(usuario);

        return votacaoSeloRepository.save(votacaoSelo);
    }

    public List<TipoSeloEnum> getDisponiveisByEstabelecimento(Integer idEstabelecimento, Usuario usuario) throws
        BusinessException
    {
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
        BusinessException
    {
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

    public List<EventoGrupoVotacaoSeloDto> getValidacoesPendentesByEvento(Usuario usuario) throws BusinessException {
        if (usuario.getTipo() != TipoUsuarioEnum.PREFEITURA) {
            throw new BusinessException("Você não tem acesso a esse recurso", ExceptionTypesEnum.FORBIDDEN);
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
        if (usuario.getTipo() != TipoUsuarioEnum.PREFEITURA) {
            throw new BusinessException("Você não tem acesso a esse recurso", ExceptionTypesEnum.FORBIDDEN);
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

    @Transactional
    public void validateVotacao(final ValidateVotacaoDto validateVotacaoDto, Usuario usuario) throws BusinessException {
        if (usuario.getTipo() != TipoUsuarioEnum.PREFEITURA) {
            throw new BusinessException("Você não tem acesso a esse recurso", ExceptionTypesEnum.FORBIDDEN);
        }

        validateDto(validateVotacaoDto);

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


                /* Deve ir pra classe de sistema específico
                Usuario usuarioVotacao = votacaoSelo.getUsuario();

                int reputacao = usuarioVotacao.getReputacao() + (votacaoSelo.getPossuiSelo() == validateVotacaoDto.possuiSelo() ? 5 : -3);

                usuarioService.updateReputacao(usuarioVotacao, reputacao);
                */
            });

        if (validateVotacaoDto.possuiSelo()) {
            seloService.validateSelo(selo);
        }
    }
}
