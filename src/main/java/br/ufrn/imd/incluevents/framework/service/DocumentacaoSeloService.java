package br.ufrn.imd.incluevents.framework.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import br.ufrn.imd.incluevents.framework.dto.CreateDocumentacaoSeloDto;
import br.ufrn.imd.incluevents.framework.dto.EstabelecimentoDocumentacaoSeloDto;
import br.ufrn.imd.incluevents.framework.dto.EventoDocumentacoesSeloDto;
import br.ufrn.imd.incluevents.framework.dto.ValidateDocumentacaoDto;
import br.ufrn.imd.incluevents.framework.dto.ValidateVotacaoDto;
import br.ufrn.imd.incluevents.framework.exceptions.BusinessException;
import br.ufrn.imd.incluevents.framework.exceptions.enums.ExceptionTypesEnum;
import br.ufrn.imd.incluevents.framework.model.DocumentacaoSelo;
import br.ufrn.imd.incluevents.framework.model.Estabelecimento;
import br.ufrn.imd.incluevents.framework.model.Evento;
import br.ufrn.imd.incluevents.framework.model.Selo;
import br.ufrn.imd.incluevents.framework.model.Usuario;
import br.ufrn.imd.incluevents.framework.model.enums.TipoSeloEnum;
import br.ufrn.imd.incluevents.framework.repository.DocumentacaoSeloRepository;
import jakarta.transaction.Transactional;

@Service
public abstract class DocumentacaoSeloService {
    protected final DocumentacaoSeloRepository documentacaoSeloRepository;

    protected final SeloService seloService;
    protected final StorageService storageService;
    protected final EventoService eventoService;
    protected final EstabelecimentoService estabelecimentoService;
    protected final VotacaoSeloService votacaoSeloService;

    protected final UsuarioService usuarioService;

    public DocumentacaoSeloService(
            DocumentacaoSeloRepository documentacaoSeloRepository,
            SeloService seloService,
            StorageService storageService,
            EventoService eventoService,
            EstabelecimentoService estabelecimentoService,
            VotacaoSeloService votacaoSeloService, UsuarioService usuarioService
    ) {
        this.documentacaoSeloRepository = documentacaoSeloRepository;

        this.seloService = seloService;
        this.storageService = storageService;
        this.eventoService = eventoService;
        this.estabelecimentoService = estabelecimentoService;
        this.votacaoSeloService = votacaoSeloService;
        this.usuarioService = usuarioService;
    }

    protected DocumentacaoSelo instantiate() {
        return new DocumentacaoSelo();
    }

    private void validateDto(CreateDocumentacaoSeloDto createDocumentacaoSeloDto) throws BusinessException {
        List<String> errors = new ArrayList<>();

        if (createDocumentacaoSeloDto.idEvento() == null && createDocumentacaoSeloDto.idEstabelecimento() == null) {
            errors.add("Deve ter o campo idEvento ou idEstabelecimento");
        } else if (createDocumentacaoSeloDto.idEvento() != null && createDocumentacaoSeloDto.idEstabelecimento() != null) {
            errors.add("Deve ter apenas um dos campos idEvento ou idEstabelecimento");
        } else if (createDocumentacaoSeloDto.idEstabelecimento() != null && createDocumentacaoSeloDto.idEstabelecimento() < 0) {
            errors.add("Id do estabelecimento inválido");
        } else if (createDocumentacaoSeloDto.idEvento() != null && createDocumentacaoSeloDto.idEvento() < 0) {
            errors.add("Id do evento inválido");
        }

        if (createDocumentacaoSeloDto.tipoSelo() == null) {
            errors.add("Deve ter campo tipoSelo");
        } else if (createDocumentacaoSeloDto.idEvento() != null && !createDocumentacaoSeloDto.tipoSelo().getTipoEntidade().equals("EVENTO")) {
            errors.add("Tipo de selo inválido");
        } else if (createDocumentacaoSeloDto.idEstabelecimento() != null && !createDocumentacaoSeloDto.tipoSelo().getTipoEntidade().equals("ESTABELECIMENTO")) {
            errors.add("Tipo de selo inválido");
        }

        if (createDocumentacaoSeloDto.arquivo() == null) {
            errors.add("Deve ter o campo arquivo");
        }

        if (errors.size() > 0) {
            throw new BusinessException(String.join("\n", errors), ExceptionTypesEnum.BAD_REQUEST);
        }
    }

    private void validateDto(ValidateDocumentacaoDto validateDocumentacaoDto) throws BusinessException {
        List<String> errors = new ArrayList<>();

        if (validateDocumentacaoDto.idDocumentacao() == null) {
            errors.add("Deve ter o campo idDocumentacao");
        }

        if (validateDocumentacaoDto.valida() == null) {
            errors.add("Deve ter o campo valida");
        }

        if (errors.size() > 0) {
            throw new BusinessException(String.join("\n", errors), ExceptionTypesEnum.BAD_REQUEST);
        }
    }

    @Transactional
    public DocumentacaoSelo create(CreateDocumentacaoSeloDto createDocumentacaoSeloDto, Usuario usuario, String baseUrl) throws BusinessException {
        Selo selo;
        Evento evento = null;
        Estabelecimento estabelecimento = null;

        validateDto(createDocumentacaoSeloDto);

        if (createDocumentacaoSeloDto.idEvento() != null) {
            evento = eventoService.getById(createDocumentacaoSeloDto.idEvento());

            checkIfCanCreate(evento.getCriador(), usuario);

            selo = seloService.createToEventoIfNotExists(evento, createDocumentacaoSeloDto.tipoSelo());
        } else {
            estabelecimento = estabelecimentoService.getEstabelecimentoById(createDocumentacaoSeloDto.idEstabelecimento());

            checkIfCanCreate(estabelecimento.getCriador(), usuario);

            selo = seloService.createToEstabelecimentoIfNotExists(estabelecimento, createDocumentacaoSeloDto.tipoSelo());
        }

        if (selo.getValidado()) {
            throw new BusinessException("Selo já validado", ExceptionTypesEnum.CONFLICT);
        }

        if (documentacaoSeloRepository.findByUsuarioAndSelo(usuario, selo).isPresent()) {
            throw new BusinessException("Documentação já criada", ExceptionTypesEnum.CONFLICT);
        }

        String nomeArquivo = storageService.store(createDocumentacaoSeloDto.arquivo());

        DocumentacaoSelo documentacaoSelo = this.instantiate();

        documentacaoSelo.setSelo(selo);
        documentacaoSelo.setUsuario(usuario);
        documentacaoSelo.setUrlArquivo(baseUrl + "/upload/" + nomeArquivo);
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

    public List<EventoDocumentacoesSeloDto> getValidacoesPendentesByEvento(Usuario usuario) throws BusinessException {
        checkIfCanValidate(usuario);

        return eventoService
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

    public List<EstabelecimentoDocumentacaoSeloDto> getValidacoesPendentesByEstabelecimento(Usuario usuario) throws BusinessException {
        checkIfCanValidate(usuario);

        return estabelecimentoService
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

    public DocumentacaoSelo getById(int idDocumentacao) throws BusinessException {
        DocumentacaoSelo documentacaoSelo = documentacaoSeloRepository.findById(idDocumentacao).orElseThrow(() ->
            new BusinessException("Documentação não encontrada", ExceptionTypesEnum.NOT_FOUND)
        );

        return documentacaoSelo;
    }

    @Transactional
    public void validateDocumentacao(ValidateDocumentacaoDto validateDocumentacaoDto, Usuario usuario) throws BusinessException {
        checkIfCanValidate(usuario);

        validateDto(validateDocumentacaoDto);

        DocumentacaoSelo documentacaoSelo = this.getById(validateDocumentacaoDto.idDocumentacao());

        if (documentacaoSelo.getValida() != null) {
            throw new BusinessException("Documentação já validada", ExceptionTypesEnum.CONFLICT);
        }

        documentacaoSelo.setValida(validateDocumentacaoDto.valida());

        documentacaoSeloRepository.save(documentacaoSelo);

        Selo selo = documentacaoSelo.getSelo();

        if (validateDocumentacaoDto.valida()) {
            ValidateVotacaoDto validateVotacaoDto = new ValidateVotacaoDto(selo.getId(), true);

            votacaoSeloService.validateVotacao(validateVotacaoDto, usuario);
        }

        Evento evento = selo.getEvento();
        Estabelecimento estabelecimento = selo.getEstabelecimento();

        Usuario criador;

        if (evento != null) {
            criador = evento.getCriador();
        } else {
            criador = estabelecimento.getCriador();
        }

        if (criador != null) {
            return;
        }

        processValidacao(documentacaoSelo, validateDocumentacaoDto);
    }


    public abstract void checkIfCanCreate(Usuario criadorEntidade, Usuario usuario)  throws BusinessException;
    public abstract void checkIfCanValidate( Usuario validadorEvento)  throws BusinessException;

    public void processValidacao(DocumentacaoSelo documentacaoSelo, ValidateDocumentacaoDto validateDocumentacaoDto){
        return;
    }
}
