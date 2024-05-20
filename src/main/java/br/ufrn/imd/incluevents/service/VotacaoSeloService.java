package br.ufrn.imd.incluevents.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import br.ufrn.imd.incluevents.dto.CreateSeloDto;
import br.ufrn.imd.incluevents.dto.CreateVotacaoSeloDto;
import br.ufrn.imd.incluevents.dto.ValidateVotacaoDto;
import br.ufrn.imd.incluevents.exceptions.BusinessException;
import br.ufrn.imd.incluevents.exceptions.enums.ExceptionTypesEnum;
import br.ufrn.imd.incluevents.model.EstabelecimentoGrupoVotacaoSelo;
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

    private void validate(CreateVotacaoSeloDto createVotacaoSeloDto) throws BusinessException {
        List<String> errors = new ArrayList<>();

        try {
            CreateSeloDto createSeloDto = new CreateSeloDto(createVotacaoSeloDto.tipoSelo(), createVotacaoSeloDto.idEvento(), createVotacaoSeloDto.idEstabelecimento());

            seloService.validate(createSeloDto);
        } catch (BusinessException e) {
            errors.add(e.getMessage());
        }

        if (createVotacaoSeloDto.possuiSelo() == null) {
            errors.add("Deve ter campo possuiSelo");
        }

        if (errors.size() > 0) {
            throw new BusinessException(String.join("\n", errors), ExceptionTypesEnum.BAD_REQUEST);
        }
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

    @Transactional
    public VotacaoSelo create(CreateVotacaoSeloDto createVotacaoSeloDto, Usuario usuario) throws
        BusinessException
    {
        Selo selo;

        validate(createVotacaoSeloDto);

        try {
            CreateSeloDto createSeloDto = new CreateSeloDto(createVotacaoSeloDto.tipoSelo(), createVotacaoSeloDto.idEvento(), createVotacaoSeloDto.idEstabelecimento());

            selo = seloService.create(createSeloDto);
        } catch (BusinessException e) {
            if (e.getType() != ExceptionTypesEnum.CONFLICT) {
                throw e;
            }

            if (createVotacaoSeloDto.idEvento() != null) {
                selo = seloService.getByIdEventoAndTipoSelo(createVotacaoSeloDto.idEvento(), createVotacaoSeloDto.tipoSelo());
            } else {
                selo = seloService.getByIdEstabelecimentoAndTipoSelo(createVotacaoSeloDto.idEstabelecimento(), createVotacaoSeloDto.tipoSelo());
            }
        }

        if (selo.getValidado()) {
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
            .filter(item -> {
                return item.getGruposVotacaoSelo().size() > 0;
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
            .filter(item -> {
                return item.getGruposVotacaoSelo().size() > 0;
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

        if (validateVotacaoDto.possuiSelo()) {
            seloService.validateSeloById(validateVotacaoDto.idSelo());
        }
    }
}
