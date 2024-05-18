package br.ufrn.imd.incluevents.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import br.ufrn.imd.incluevents.dto.CreateVotacaoSeloDto;
import br.ufrn.imd.incluevents.dto.ValidateVotacaoDto;
import br.ufrn.imd.incluevents.exceptions.BusinessException;
import br.ufrn.imd.incluevents.exceptions.enums.ExceptionTypesEnum;
import br.ufrn.imd.incluevents.model.Estabelecimento;
import br.ufrn.imd.incluevents.model.EstabelecimentoGrupoVotacaoSelo;
import br.ufrn.imd.incluevents.model.Evento;
import br.ufrn.imd.incluevents.model.EventoGrupoVotacaoSelo;
import br.ufrn.imd.incluevents.model.GrupoVotacaoSelo;
import br.ufrn.imd.incluevents.model.Selo;
import br.ufrn.imd.incluevents.model.Usuario;
import br.ufrn.imd.incluevents.model.VotacaoSelo;
import br.ufrn.imd.incluevents.model.enums.TipoSeloEnum;
import br.ufrn.imd.incluevents.repository.VotacaoSeloRepository;
import jakarta.transaction.Transactional;

@Service
public class VotacaoSeloService {
    private final VotacaoSeloRepository votacaoSeloRepository;

    private final UsuarioService usuarioService;
    private final SeloService seloService;
    private final EventoService eventoService;
    private final EstabelecimentoService estabelecimentoService;

    public VotacaoSeloService(
        VotacaoSeloRepository votacaoSeloRepository,
        UsuarioService usuarioService,
        SeloService seloService,
        EventoService eventoService,
        EstabelecimentoService estabelecimentoService
    ) {
        this.votacaoSeloRepository = votacaoSeloRepository;

        this.usuarioService = usuarioService;
        this.seloService = seloService;
        this.eventoService = eventoService;
        this.estabelecimentoService = estabelecimentoService;
    }

    private void validate(ValidateVotacaoDto validateVotacaoDto) throws BusinessException {
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

    private void validate(CreateVotacaoSeloDto createVotacaoSeloDto) throws BusinessException {
        List<String> errors = new ArrayList<>();

        if (createVotacaoSeloDto.idEvento() == null && createVotacaoSeloDto.idEstabelecimento() == null) {
            errors.add("Deve ter o campo idEvento ou idEstabelecimento");
        } else if (createVotacaoSeloDto.idEvento() != null && createVotacaoSeloDto.idEstabelecimento() != null) {
            errors.add("Deve ter apenas um dos campos idEvento ou idEstabelecimento");
        } else if (createVotacaoSeloDto.idEstabelecimento() != null) {
            if (createVotacaoSeloDto.idEstabelecimento() < 0) {
                errors.add("Id do estabelecimento inválido");
            }

            if (!createVotacaoSeloDto.tipoSelo().getTipoEntidade().equals("ESTABELECIMENTO")) {
                errors.add("Tipo do selo inválido");
            }
        } else if (createVotacaoSeloDto.idEvento() != null) {
            if (createVotacaoSeloDto.idEvento() < 0) {
                errors.add("Id do evento inválido");
            }

            if (!createVotacaoSeloDto.tipoSelo().getTipoEntidade().equals("EVENTO")) {
                errors.add("Tipo do selo inválido");
            }
        }

        if (createVotacaoSeloDto.tipoSelo() == null) {
            errors.add("Deve ter campo tipoSelo");
        }

        if (createVotacaoSeloDto.possuiSelo() == null) {
            errors.add("Deve ter campo possuiSelo");
        }

        if (errors.size() > 0) {
            throw new BusinessException(String.join("\n", errors), ExceptionTypesEnum.BAD_REQUEST);
        }
    }

    @Transactional
    public VotacaoSelo create(CreateVotacaoSeloDto createVotacaoSeloDto, Usuario usuario) throws
        BusinessException
    {
        Evento evento = null;
        Estabelecimento estabelecimento = null;
        Selo selo;

        validate(createVotacaoSeloDto);

        if (createVotacaoSeloDto.idEvento() != null) {
            evento = eventoService.getById(createVotacaoSeloDto.idEvento());

            try {
                selo = seloService.getByEventoAndTipoSelo(evento, createVotacaoSeloDto.tipoSelo());
            } catch (BusinessException e) {
                selo = null;
            }
        } else {
            estabelecimento = estabelecimentoService.getEstabelecimentoById(createVotacaoSeloDto.idEstabelecimento()).orElseThrow(() ->
                new BusinessException("Estabelecimento não encontrado", ExceptionTypesEnum.NOT_FOUND)
            );

            try {
                selo = seloService.getByEstabelecimentoAndTipoSelo(estabelecimento, createVotacaoSeloDto.tipoSelo());
            } catch (BusinessException e) {
                selo = null;
            }
        }

        if (selo == null) {
            selo = new Selo();

            selo.setTipoSelo(createVotacaoSeloDto.tipoSelo());
            selo.setEvento(evento);
            selo.setEstabelecimento(estabelecimento);
            selo.setValidado(false);

            seloService.save(selo);
        } else if (selo.getValidado()) {
            throw new BusinessException("Selo já validado", ExceptionTypesEnum.CONFLICT);
        }

        if (votacaoSeloRepository.findByUsuarioAndSelo(usuario, selo).isPresent()) {
            throw new BusinessException("Validação já criada", ExceptionTypesEnum.CONFLICT);
        }

        VotacaoSelo votacaoSelo = new VotacaoSelo();

        votacaoSelo.setDescricao(createVotacaoSeloDto.descricao());
        votacaoSelo.setPossuiSelo(createVotacaoSeloDto.possuiSelo());
        votacaoSelo.setScore(usuario.getReputacao());
        votacaoSelo.setSelo(selo);
        votacaoSelo.setUsuario(usuario);

        return votacaoSeloRepository.save(votacaoSelo);
    }

    public VotacaoSelo create(CreateVotacaoSeloDto createVotacaoSeloDto, Integer idUsuario) throws BusinessException {
        Usuario usuario = usuarioService.getUsuarioById(idUsuario);

        return create(createVotacaoSeloDto, usuario);
    }

    public VotacaoSelo getById(Integer id) throws BusinessException {
        return votacaoSeloRepository.findById(id).orElseThrow(() ->
            new BusinessException("Validação não encontrada", ExceptionTypesEnum.NOT_FOUND)
        );
    }

    public List<VotacaoSelo> getByUsuario(Usuario usuario) {
        return votacaoSeloRepository.findByUsuario(usuario);
    }

    public List<VotacaoSelo> getByUsuario(Integer idUsuario) throws BusinessException {
        Usuario usuario = usuarioService.getUsuarioById(idUsuario);

        return getByUsuario(usuario);
    }

    public List<TipoSeloEnum> getDisponiveisByEstabelecimento(Integer idEstabelecimento, Usuario usuario) throws
        BusinessException
    {
        final Estabelecimento estabelecimento = estabelecimentoService.getEstabelecimentoById(idEstabelecimento).orElseThrow(() ->
            new BusinessException("Estabelecimento não encontrado", ExceptionTypesEnum.NOT_FOUND)
        );

        return Stream.of(TipoSeloEnum.values())
            .parallel()
            .filter(tipoSelo -> {
                if (!tipoSelo.getTipoEntidade().equals("ESTABELECIMENTO")) {
                    return false;
                }

                Selo selo;

                try {
                    selo = seloService.getByEstabelecimentoAndTipoSelo(estabelecimento, tipoSelo);
                } catch (BusinessException e) {
                    selo = null;
                }

                if (selo == null) {
                    return true;
                } else if (selo.getValidado()) {
                    return false;
                } else {
                    return !votacaoSeloRepository.findByUsuarioAndSelo(usuario, selo).isPresent();
                }
            })
            .collect(Collectors.toList());
    }

    public List<TipoSeloEnum> getDisponiveisByEstabelecimento(Integer idEstabelecimento, Integer idUsuario) throws BusinessException {
        Usuario usuario = usuarioService.getUsuarioById(idUsuario);

        return getDisponiveisByEstabelecimento(idEstabelecimento, usuario);
    }

    public List<TipoSeloEnum> getDisponiveisByEvento(Integer idEvento, Usuario usuario) throws
        BusinessException
    {
        final Evento evento = eventoService.getById(idEvento);

        return Stream.of(TipoSeloEnum.values())
            .parallel()
            .filter(tipoSelo -> {
                if (!tipoSelo.getTipoEntidade().equals("EVENTO")) {
                    return false;
                }

                Selo selo;

                try {
                    selo = seloService.getByEventoAndTipoSelo(evento, tipoSelo);
                } catch (BusinessException e) {
                    selo = null;
                }

                if (selo == null) {
                    return true;
                } else if (selo.getValidado()) {
                    return false;
                } else {
                    return !votacaoSeloRepository.findByUsuarioAndSelo(usuario, selo).isPresent();
                }
            })
            .collect(Collectors.toList());
    }

    public List<TipoSeloEnum> getDisponiveisByEvento(Integer idEvento, Integer idUsuario) throws BusinessException {
        Usuario usuario = usuarioService.getUsuarioById(idUsuario);

        return getDisponiveisByEvento(idEvento, usuario);
    }

    public List<EventoGrupoVotacaoSelo> getValidacoesPendentesByEvento() {
        return eventoService
            .findAll()
            .stream()
            .parallel()
            .map(evento -> {
                List<GrupoVotacaoSelo> gruposVotacaoSelo = votacaoSeloRepository.findValidacoesPendentesByEvento(evento.getId());

                return new EventoGrupoVotacaoSelo(evento, gruposVotacaoSelo);
            })
            .collect(Collectors.toList());
    }

    public List<EstabelecimentoGrupoVotacaoSelo> getValidacoesPendentesByEstabelecimento() {
        return estabelecimentoService
            .findAll()
            .stream()
            .parallel()
            .map(estabelecimento -> {
                List<GrupoVotacaoSelo> gruposVotacaoSelo = votacaoSeloRepository.findValidacoesPendentesByEstabelecimento(estabelecimento.getId());

                return new EstabelecimentoGrupoVotacaoSelo(estabelecimento, gruposVotacaoSelo);
            })
            .collect(Collectors.toList());
    }

    public void validateVotacao(final ValidateVotacaoDto validateVotacaoDto) throws BusinessException {
        validate(validateVotacaoDto);

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

                int reputacao = votacaoSelo.getUsuario().getReputacao() + (votacaoSelo.getPossuiSelo() == validateVotacaoDto.possuiSelo() ? 5 : -3);

                try {
                    usuarioService.updateReputacaoById(votacaoSelo.getUsuario().getId(), reputacao);
                } catch (BusinessException error) {
                    return;
                }
            });

        seloService.validateSeloById(validateVotacaoDto.idSelo());
    }
}