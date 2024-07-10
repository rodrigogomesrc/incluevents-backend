package br.ufrn.imd.incluevents.ufpb.service;

import org.springframework.stereotype.Service;

import br.ufrn.imd.incluevents.framework.exceptions.BusinessException;
import br.ufrn.imd.incluevents.framework.exceptions.enums.ExceptionTypesEnum;
import br.ufrn.imd.incluevents.framework.model.DocumentacaoSelo;
import br.ufrn.imd.incluevents.framework.repository.DocumentacaoSeloRepository;
import br.ufrn.imd.incluevents.framework.service.DocumentacaoSeloService;
import br.ufrn.imd.incluevents.framework.service.EstabelecimentoService;
import br.ufrn.imd.incluevents.framework.service.EventoService;
import br.ufrn.imd.incluevents.framework.service.SeloService;
import br.ufrn.imd.incluevents.framework.service.StorageService;
import br.ufrn.imd.incluevents.framework.service.VotacaoSeloService;
import br.ufrn.imd.incluevents.ufpb.dto.PreValidateDocumentacaoDtoUfpb;
import br.ufrn.imd.incluevents.ufpb.model.DocumentacaoSeloUfpb;

@Service
public class DocumentacaoSeloServiceUfpb extends DocumentacaoSeloService {
    public DocumentacaoSeloServiceUfpb(DocumentacaoSeloRepository documentacaoSeloRepository, SeloService seloService,
            StorageService storageService, EventoService eventoService, EstabelecimentoService estabelecimentoService,
            VotacaoSeloService votacaoSeloService) {
        super(documentacaoSeloRepository, seloService, storageService, eventoService, estabelecimentoService,
            votacaoSeloService);
    }

    @Override
    protected DocumentacaoSelo instantiate() {
        DocumentacaoSeloUfpb documentacaoSeloUfpb = new DocumentacaoSeloUfpb();

        documentacaoSeloUfpb.setPreValidada(false);

        return documentacaoSeloUfpb;
    }

    public void preValidate(PreValidateDocumentacaoDtoUfpb preValidateDocumentacaoDtoUfpb) throws BusinessException {
        if (preValidateDocumentacaoDtoUfpb.idDocumentacao() == null) {
            throw new BusinessException("Precisa de idDocumentacao", ExceptionTypesEnum.BAD_REQUEST);
        }

        DocumentacaoSeloUfpb documentacaoSeloUfpb = (DocumentacaoSeloUfpb) this.getById(preValidateDocumentacaoDtoUfpb.idDocumentacao());

        documentacaoSeloUfpb.setPreValidada(true);

        this.documentacaoSeloRepository.save(documentacaoSeloUfpb);
    }
}
