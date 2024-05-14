package br.ufrn.imd.incluevents.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import br.ufrn.imd.incluevents.dto.CreateValidacaoDto;
import br.ufrn.imd.incluevents.exceptions.BusinessException;
import br.ufrn.imd.incluevents.exceptions.EventoNotFoundException;
import br.ufrn.imd.incluevents.exceptions.UsuarioNotFoundException;
import br.ufrn.imd.incluevents.exceptions.enums.ExceptionTypesEnum;
import br.ufrn.imd.incluevents.model.Estabelecimento;
import br.ufrn.imd.incluevents.model.Evento;
import br.ufrn.imd.incluevents.model.Selo;
import br.ufrn.imd.incluevents.model.Usuario;
import br.ufrn.imd.incluevents.model.Validacao;
import br.ufrn.imd.incluevents.model.enums.TipoSeloEnum;
import br.ufrn.imd.incluevents.repository.ValidacaoRepository;
import jakarta.transaction.Transactional;

@Service
public class ValidacaoService {
    private final ValidacaoRepository validacaoRepository;

    private final UsuarioService usuarioService;
    private final SeloService seloService;
    private final EventoService eventoService;
    private final EstabelecimentoService estabelecimentoService;

    public ValidacaoService(
        ValidacaoRepository validacaoRepository,
        UsuarioService usuarioService,
        SeloService seloService,
        EventoService eventoService,
        EstabelecimentoService estabelecimentoService
    ) {
        this.validacaoRepository = validacaoRepository;

        this.usuarioService = usuarioService;
        this.seloService = seloService;
        this.eventoService = eventoService;
        this.estabelecimentoService = estabelecimentoService;
    }

    private void validate(CreateValidacaoDto createValidacaoDto) throws BusinessException {
        List<String> errors = new ArrayList<>();

        if (createValidacaoDto.idEvento() == null && createValidacaoDto.idEstabelecimento() == null) {
            errors.add("Deve ter o campo idEvento ou idEstabelecimento");
        } else if (createValidacaoDto.idEvento() != null && createValidacaoDto.idEstabelecimento() != null) {
            errors.add("Deve ter apenas um dos campos idEvento ou idEstabelecimento");
        } else if (createValidacaoDto.idEstabelecimento() != null) {
            if (createValidacaoDto.idEstabelecimento() < 0) {
                errors.add("Id do estabelecimento inválido");
            }

            if (!createValidacaoDto.tipoSelo().getTipoEntidade().equals("ESTABELECIMENTO")) {
                errors.add("Tipo do selo inválido");
            }
        } else if (createValidacaoDto.idEvento() != null) {
            if (createValidacaoDto.idEvento() < 0) {
                errors.add("Id do evento inválido");
            }

            if (!createValidacaoDto.tipoSelo().getTipoEntidade().equals("EVENTO")) {
                errors.add("Tipo do selo inválido");
            }
        }

        if (createValidacaoDto.idUsuario() == null) {
            errors.add("Deve ter idUsuario");
        }

        if (createValidacaoDto.tipoSelo() == null) {
            errors.add("Deve ter campo tipoSelo");
        }

        if (createValidacaoDto.possuiSelo() == null) {
            errors.add("Deve ter campo possuiSelo");
        }

        if (errors.size() > 0) {
            throw new BusinessException(String.join("\n", errors), ExceptionTypesEnum.BAD_REQUEST);
        }
    }

    @Transactional
    public Validacao create(CreateValidacaoDto createValidacaoDto) throws
        BusinessException,
        UsuarioNotFoundException,
        EventoNotFoundException
    {
        Evento evento = null;
        Estabelecimento estabelecimento = null;
        Selo selo;

        validate(createValidacaoDto);

        if (createValidacaoDto.idEvento() != null) {
            evento = eventoService.getById(createValidacaoDto.idEvento());

            try {
                selo = seloService.getByEventoAndTipoSelo(evento, createValidacaoDto.tipoSelo());
            } catch (BusinessException e) {
                selo = null;
            }
        } else {
            estabelecimento = estabelecimentoService.getEstabelecimentoById(createValidacaoDto.idEstabelecimento());

            try {
                selo = seloService.getByEstabelecimentoAndTipoSelo(estabelecimento, createValidacaoDto.tipoSelo());
            } catch (BusinessException e) {
                selo = null;
            }
        }

        if (selo == null) {
            selo = new Selo();

            selo.setTipoSelo(createValidacaoDto.tipoSelo());
            selo.setEvento(evento);
            selo.setEstabelecimento(estabelecimento);
            selo.setValidado(false);

            seloService.save(selo);
        } else if (selo.getValidado()) {
            throw new BusinessException("Selo já validado", ExceptionTypesEnum.CONFLICT);
        }

        Usuario usuario = usuarioService.getUsuarioById(createValidacaoDto.idUsuario());

        if (validacaoRepository.findByUsuarioAndSelo(usuario, selo).isPresent()) {
            throw new BusinessException("Validação já criada", ExceptionTypesEnum.CONFLICT);
        }

        Validacao validacao = new Validacao();

        validacao.setDescricao(createValidacaoDto.descricao());
        validacao.setVoto(createValidacaoDto.possuiSelo() ? usuario.getReputacao() : -usuario.getReputacao());
        validacao.setSelo(selo);
        validacao.setUsuario(usuario);

        return validacaoRepository.save(validacao);
    }

    public Validacao getById(Integer id) throws BusinessException {
        return validacaoRepository.findById(id).orElseThrow(() ->
            new BusinessException("Validação não encontrada", ExceptionTypesEnum.NOT_FOUND)
        );
    }

    public List<Validacao> getByUsuario(Integer idUsuario) throws UsuarioNotFoundException {
        Usuario usuario = usuarioService.getUsuarioById(idUsuario);

        return validacaoRepository.findByUsuario(usuario);
    }

    public List<TipoSeloEnum> getDisponiveisByEstabelecimento(int idUsuario, int idEstabelecimento) throws
        BusinessException,
        UsuarioNotFoundException
    {
        final Estabelecimento estabelecimento = estabelecimentoService.getEstabelecimentoById(idEstabelecimento);

        final Usuario usuario = usuarioService.getUsuarioById(idUsuario);

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
                    return !validacaoRepository.findByUsuarioAndSelo(usuario, selo).isPresent();
                }
            })
            .collect(Collectors.toList());
    }

    public List<TipoSeloEnum> getDisponiveisByEvento(int idUsuario, int idEvento) throws
        BusinessException,
        EventoNotFoundException,
        UsuarioNotFoundException
    {
        final Evento evento = eventoService.getById(idEvento);

        final Usuario usuario = usuarioService.getUsuarioById(idUsuario);

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
                    return !validacaoRepository.findByUsuarioAndSelo(usuario, selo).isPresent();
                }
            })
            .collect(Collectors.toList());
    }
}
