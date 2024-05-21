package br.ufrn.imd.incluevents.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import br.ufrn.imd.incluevents.dto.CreateDocumentacaoSeloDto;
import br.ufrn.imd.incluevents.dto.CreateSeloDto;
import br.ufrn.imd.incluevents.dto.EstabelecimentoDocumentacaoSeloDto;
import br.ufrn.imd.incluevents.dto.EventoDocumentacoesSeloDto;
import br.ufrn.imd.incluevents.dto.ValidateDcoumentacaoDto;
import br.ufrn.imd.incluevents.exceptions.BusinessException;
import br.ufrn.imd.incluevents.exceptions.enums.ExceptionTypesEnum;
import br.ufrn.imd.incluevents.model.DocumentacaoSelo;
import br.ufrn.imd.incluevents.model.Estabelecimento;
import br.ufrn.imd.incluevents.model.Evento;
import br.ufrn.imd.incluevents.model.Selo;
import br.ufrn.imd.incluevents.model.Usuario;
import br.ufrn.imd.incluevents.model.enums.TipoSeloEnum;
import br.ufrn.imd.incluevents.repository.DocumentacaoSeloRepository;
import br.ufrn.imd.incluevents.repository.EstabelecimentoRepository;
import br.ufrn.imd.incluevents.repository.EventoRepository;
import br.ufrn.imd.incluevents.repository.UsuarioRepository;
import jakarta.transaction.Transactional;

@Service
public class DocumentacaoSeloService {
    private final DocumentacaoSeloRepository documentacaoSeloRepository;

    private final SeloService seloService;
    private final StorageService storageService;

    private final EventoRepository eventoRepository;
    private final EstabelecimentoRepository estabelecimentoRepository;
    private final UsuarioRepository usuarioRepository;

    public DocumentacaoSeloService(
        DocumentacaoSeloRepository documentacaoSeloRepository,
        SeloService seloService,
        StorageService storageService,
        EventoRepository eventoRepository,
        EstabelecimentoRepository estabelecimentoRepository,
        UsuarioRepository usuarioRepository
    ) {
        this.documentacaoSeloRepository = documentacaoSeloRepository;

        this.seloService = seloService;
        this.storageService = storageService;
        this.eventoRepository = eventoRepository;
        this.estabelecimentoRepository = estabelecimentoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    private void validate(CreateDocumentacaoSeloDto createDocumentacaoSeloDto) throws BusinessException {
        List<String> errors = new ArrayList<>();

        try {
            CreateSeloDto createSeloDto = new CreateSeloDto(createDocumentacaoSeloDto.tipoSelo(), createDocumentacaoSeloDto.idEvento(), createDocumentacaoSeloDto.idEstabelecimento());

            seloService.validate(createSeloDto);
        } catch (BusinessException e) {
            errors.add(e.getMessage());
        }

        if (createDocumentacaoSeloDto.arquivo() == null) {
            errors.add("Deve ter o campo arquivo");
        }

        if (errors.size() > 0) {
            throw new BusinessException(String.join("\n", errors), ExceptionTypesEnum.BAD_REQUEST);
        }
    }

    private void validate(ValidateDcoumentacaoDto validateDcoumentacaoDto) throws BusinessException {
        List<String> errors = new ArrayList<>();

        if (validateDcoumentacaoDto.idDocumentacao() == null) {
            errors.add("Deve ter o campo idDocumentacao");
        }

        if (validateDcoumentacaoDto.valida() == null) {
            errors.add("Deve ter o campo valida");
        }

        if (errors.size() > 0) {
            throw new BusinessException(String.join("\n", errors), ExceptionTypesEnum.BAD_REQUEST);
        }
    }

    @Transactional
    public DocumentacaoSelo create(CreateDocumentacaoSeloDto createDocumentacaoSeloDto, Usuario usuario, String baseUrl) throws BusinessException {
        Selo selo;

        validate(createDocumentacaoSeloDto);

        if (createDocumentacaoSeloDto.idEvento() != null) {
            Evento evento = eventoRepository.findById(createDocumentacaoSeloDto.idEvento()).orElseThrow(() ->
                new BusinessException("Evento não encontrado", ExceptionTypesEnum.NOT_FOUND)
            );

            if (evento.getCriador() != null && evento.getCriador().getId() != usuario.getId()) {
                throw new BusinessException("Apenas o criador do evento pode enviar a solicitação de documentação", null);
            } else if (evento.getCriador() == null && usuario.getReputacao() < 70) {
                throw new BusinessException("Reputação insuficiente para envio de documentação", null);
            }
        } else {
            Estabelecimento estabelecimento = estabelecimentoRepository.findById(createDocumentacaoSeloDto.idEstabelecimento()).orElseThrow(() ->
                new BusinessException("Estabelecimento não encontrado", ExceptionTypesEnum.NOT_FOUND)
            );

            if (estabelecimento.getCriador() != null && estabelecimento.getCriador().getId() != usuario.getId()) {
                throw new BusinessException("Apenas o criador do evento pode enviar a solicitação de documentação", null);
            } else if (estabelecimento.getCriador() == null && usuario.getReputacao() < 70) {
                throw new BusinessException("Reputação insuficiente para envio de documentação", null);
            }
        }

        try {
            CreateSeloDto createSeloDto = new CreateSeloDto(createDocumentacaoSeloDto.tipoSelo(), createDocumentacaoSeloDto.idEvento(), createDocumentacaoSeloDto.idEstabelecimento());

            selo = seloService.create(createSeloDto);
        } catch (BusinessException e) {
            if (e.getType() != ExceptionTypesEnum.CONFLICT) {
                throw e;
            }

            if (createDocumentacaoSeloDto.idEvento() != null) {
                selo = seloService.getByIdEventoAndTipoSelo(createDocumentacaoSeloDto.idEvento(), createDocumentacaoSeloDto.tipoSelo());
            } else {
                selo = seloService.getByIdEstabelecimentoAndTipoSelo(createDocumentacaoSeloDto.idEstabelecimento(), createDocumentacaoSeloDto.tipoSelo());
            }
        }

        if (selo.getValidado()) {
            throw new BusinessException("Selo já validado", ExceptionTypesEnum.CONFLICT);
        }

        if (documentacaoSeloRepository.findByUsuarioAndSelo(usuario, selo).isPresent()) {
            throw new BusinessException("Documentação já criada", ExceptionTypesEnum.CONFLICT);
        }

        String nomeArquivo = storageService.store(createDocumentacaoSeloDto.arquivo());

        DocumentacaoSelo documentacaoSelo = new DocumentacaoSelo();

        documentacaoSelo.setSelo(selo);
        documentacaoSelo.setUsuario(usuario);
        documentacaoSelo.setUrlArquivo(baseUrl + "/" + nomeArquivo);
        documentacaoSelo.setNomeArquivo(createDocumentacaoSeloDto.arquivo().getOriginalFilename());

        return documentacaoSeloRepository.save(documentacaoSelo);
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
                    return !documentacaoSeloRepository.findByUsuarioAndSelo(usuario, selo).isPresent();
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
                    return !documentacaoSeloRepository.findByUsuarioAndSelo(usuario, selo).isPresent();
                }
            })
            .collect(Collectors.toList());
    }

    public List<EventoDocumentacoesSeloDto> getPendentesByEvento() {
        return eventoRepository
            .findAll()
            .stream()
            .parallel()
            .map(evento -> {
                List<DocumentacaoSelo> documentacoesSelo = documentacaoSeloRepository.findValidacoesPendentesByEvento(evento.getId());

                return new EventoDocumentacoesSeloDto(evento, documentacoesSelo);
            })
            .filter(eventoDocumentacoesSelo -> {
                return eventoDocumentacoesSelo.documentacoesSelo().size() > 0;
            })
            .collect(Collectors.toList());
    }

    public List<EstabelecimentoDocumentacaoSeloDto> getPendentesByEstabelecimento() {
        return estabelecimentoRepository
            .findAll()
            .stream()
            .parallel()
            .map(estabelecimento -> {
                List<DocumentacaoSelo> documentacoesSelo = documentacaoSeloRepository.findValidacoesPendentesByEstabelecimento(estabelecimento.getId());

                return new EstabelecimentoDocumentacaoSeloDto(estabelecimento, documentacoesSelo);
            })
            .filter(estabelecimentoDocumentacoesSelo -> {
                return estabelecimentoDocumentacoesSelo.documentacoesSelo().size() > 0;
            })
            .collect(Collectors.toList());
    }

    @Transactional
    public void validateDocumentacao(ValidateDcoumentacaoDto validateDcoumentacaoDto) throws BusinessException {
        validate(validateDcoumentacaoDto);

        DocumentacaoSelo documentacaoSelo = documentacaoSeloRepository.findById(validateDcoumentacaoDto.idDocumentacao()).orElseThrow(() ->
            new BusinessException("Documentação não encontrada", ExceptionTypesEnum.NOT_FOUND)
        );

        if (documentacaoSelo.getValida() != null) {
            throw new BusinessException("Documentação já validada", ExceptionTypesEnum.CONFLICT);
        }

        documentacaoSelo.setValida(validateDcoumentacaoDto.valida());

        documentacaoSeloRepository.save(documentacaoSelo);

        Usuario usuario = documentacaoSelo.getUsuario();

        usuario.setReputacao(validateDcoumentacaoDto.valida() ? usuario.getReputacao() + 10 : usuario.getReputacao() - 10);

        usuarioRepository.save(usuario);
    }
}
