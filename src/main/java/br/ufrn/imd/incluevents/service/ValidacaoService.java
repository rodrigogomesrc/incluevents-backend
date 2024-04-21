package br.ufrn.imd.incluevents.service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.ufrn.imd.incluevents.dto.CreateValidacaoDto;
import br.ufrn.imd.incluevents.exceptions.EstabelecimentoNotFoundException;
import br.ufrn.imd.incluevents.exceptions.EventoNotFoundException;
import br.ufrn.imd.incluevents.exceptions.SeloJaValidadoException;
import br.ufrn.imd.incluevents.exceptions.TipoSeloInvalidoException;
import br.ufrn.imd.incluevents.exceptions.UsuarioNotFoundException;
import br.ufrn.imd.incluevents.exceptions.ValidacaoJaCriadaException;
import br.ufrn.imd.incluevents.exceptions.ValidacaoNotFoundException;
import br.ufrn.imd.incluevents.model.Estabelecimento;
import br.ufrn.imd.incluevents.model.Evento;
import br.ufrn.imd.incluevents.model.Selo;
import br.ufrn.imd.incluevents.model.Usuario;
import br.ufrn.imd.incluevents.model.Validacao;
import br.ufrn.imd.incluevents.model.enums.TipoSeloEnum;
import br.ufrn.imd.incluevents.repository.EstabelecimentoRepository;
import br.ufrn.imd.incluevents.repository.EventoRepository;
import br.ufrn.imd.incluevents.repository.SeloRepository;
import br.ufrn.imd.incluevents.repository.UsuarioRepository;
import br.ufrn.imd.incluevents.repository.ValidacaoRepository;
import jakarta.transaction.Transactional;

@Service
public class ValidacaoService {
    @Autowired
    private ValidacaoRepository validacaoRepository;

    @Autowired
    private EventoRepository eventoRepository;

    @Autowired
    private EstabelecimentoRepository estabelecimentoRepository;

    @Autowired
    private SeloRepository seloRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public Validacao create(CreateValidacaoDto createValidacaoDto) throws
        EventoNotFoundException,
        EstabelecimentoNotFoundException,
        SeloJaValidadoException,
        UsuarioNotFoundException,
        ValidacaoJaCriadaException,
        TipoSeloInvalidoException
    {
        Evento evento = null;
        Estabelecimento estabelecimento = null;
        Selo selo;

        if (createValidacaoDto.idEvento() != null) {
            if (!createValidacaoDto.tipoSelo().getTipoEntidade().equals("EVENTO")) {
                throw new TipoSeloInvalidoException();
            }

            evento = eventoRepository.findById(createValidacaoDto.idEvento()).orElseThrow(EventoNotFoundException::new);

            selo = seloRepository.findByEventoAndTipoSelo(evento, createValidacaoDto.tipoSelo()).orElse(null);
        } else {
            if (!createValidacaoDto.tipoSelo().getTipoEntidade().equals("ESTABELECIMENTO")) {
                throw new TipoSeloInvalidoException();
            }

            estabelecimento = estabelecimentoRepository.findById(createValidacaoDto.idEstabelecimento()).orElseThrow(EstabelecimentoNotFoundException::new);

            selo = seloRepository.findByEstabelecimentoAndTipoSelo(estabelecimento, createValidacaoDto.tipoSelo()).orElse(null);
        }

        if (selo == null) {
            selo = new Selo();

            selo.setTipoSelo(createValidacaoDto.tipoSelo());
            selo.setEvento(evento);
            selo.setEstabelecimento(estabelecimento);
            selo.setValidado(false);
        } else if (selo.getValidado()) {
            throw new SeloJaValidadoException();
        }

        seloRepository.save(selo);

        Usuario usuario = usuarioRepository.findById(createValidacaoDto.idUsuario()).orElseThrow(UsuarioNotFoundException::new);

        if (validacaoRepository.findByUsuarioAndSelo(usuario, selo).isPresent()) {
            throw new ValidacaoJaCriadaException();
        }

        Validacao validacao = new Validacao();

        validacao.setDescricao(createValidacaoDto.descricao());
        validacao.setVoto(createValidacaoDto.possuiSelo() ? usuario.getReputacao() : -usuario.getReputacao());
        validacao.setSelo(selo);
        validacao.setUsuario(usuario);

        return validacaoRepository.save(validacao);
    }

    public Validacao getById(Integer id) throws ValidacaoNotFoundException {
        return validacaoRepository.findById(id).orElseThrow(ValidacaoNotFoundException::new);
    }

    public List<Validacao> getByUsuario(Integer idUsuario) throws UsuarioNotFoundException {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElseThrow(UsuarioNotFoundException::new);

        return validacaoRepository.findByUsuario(usuario);
    }

    public List<TipoSeloEnum> getDisponiveisByEstabelecimento(int idUsuario, int idEstabelecimento) throws
        EstabelecimentoNotFoundException,
        UsuarioNotFoundException
    {
        final Estabelecimento estabelecimento = estabelecimentoRepository.findById(idEstabelecimento).orElseThrow(EstabelecimentoNotFoundException::new);

        final Usuario usuario = usuarioRepository.findById(idUsuario).orElseThrow(UsuarioNotFoundException::new);

        return Stream.of(TipoSeloEnum.values())
            .parallel()
            .filter(tipoSelo -> {
                if (!tipoSelo.getTipoEntidade().equals("ESTABELECIMENTO")) {
                    return false;
                }

                Selo selo = seloRepository.findByEstabelecimentoAndTipoSelo(estabelecimento, tipoSelo).orElse(null);

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
        EventoNotFoundException,
        UsuarioNotFoundException
    {
        final Evento evento = eventoRepository.findById(idEvento).orElseThrow(EventoNotFoundException::new);

        final Usuario usuario = usuarioRepository.findById(idUsuario).orElseThrow(UsuarioNotFoundException::new);

        return Stream.of(TipoSeloEnum.values())
            .parallel()
            .filter(tipoSelo -> {
                if (!tipoSelo.getTipoEntidade().equals("EVENTO")) {
                    return false;
                }

                Selo selo = seloRepository.findByEventoAndTipoSelo(evento, tipoSelo).orElse(null);

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
