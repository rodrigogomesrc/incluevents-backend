package br.ufrn.imd.incluevents.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import br.ufrn.imd.incluevents.dto.CreateDocumentacaoSeloDto;
import br.ufrn.imd.incluevents.dto.CreateSeloDto;
import br.ufrn.imd.incluevents.exceptions.BusinessException;
import br.ufrn.imd.incluevents.exceptions.enums.ExceptionTypesEnum;
import br.ufrn.imd.incluevents.model.DocumentacaoSelo;
import br.ufrn.imd.incluevents.model.Selo;
import br.ufrn.imd.incluevents.model.Usuario;
import br.ufrn.imd.incluevents.repository.DocumentacaoSeloRepository;

@Service
public class DocumentacaoSeloService {
    private final DocumentacaoSeloRepository documentacaoSeloRepository;

    private final UsuarioService usuarioService;
    private final SeloService seloService;
    private final StorageService storageService;

    public DocumentacaoSeloService(
        DocumentacaoSeloRepository documentacaoSeloRepository,
        UsuarioService usuarioService,
        SeloService seloService,
        StorageService storageService
    ) {
        this.documentacaoSeloRepository = documentacaoSeloRepository;

        this.usuarioService = usuarioService;
        this.seloService = seloService;
        this.storageService = storageService;
    }

    public void validate(CreateDocumentacaoSeloDto createDocumentacaoSeloDto) throws BusinessException {
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

    public DocumentacaoSelo create(CreateDocumentacaoSeloDto createDocumentacaoSeloDto, Usuario usuario, String baseUrl) throws BusinessException {
        Selo selo;

        validate(createDocumentacaoSeloDto);

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

    public DocumentacaoSelo create(CreateDocumentacaoSeloDto createDocumentacaoSeloDto, Integer idUsuario, String baseUrl) throws BusinessException {
        Usuario usuario = usuarioService.getUsuarioById(idUsuario);

        return create(createDocumentacaoSeloDto, usuario, baseUrl);
    }
}
