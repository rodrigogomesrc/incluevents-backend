package br.ufrn.imd.incluevents.natal.service;

import br.ufrn.imd.incluevents.framework.dto.ValidateDocumentacaoDto;
import br.ufrn.imd.incluevents.framework.exceptions.BusinessException;
import br.ufrn.imd.incluevents.framework.exceptions.enums.ExceptionTypesEnum;
import br.ufrn.imd.incluevents.framework.model.DocumentacaoSelo;
import br.ufrn.imd.incluevents.framework.model.Usuario;
import br.ufrn.imd.incluevents.framework.service.*;
import br.ufrn.imd.incluevents.natal.model.UsuarioNatal;
import br.ufrn.imd.incluevents.natal.model.enums.TipoUsuarioEnumNatal;
import org.springframework.stereotype.Service;

import br.ufrn.imd.incluevents.framework.repository.DocumentacaoSeloRepository;

@Service
public class DocumentacaoSeloServiceNatal extends DocumentacaoSeloService {

    public DocumentacaoSeloServiceNatal(DocumentacaoSeloRepository documentacaoSeloRepository, SeloService seloService, StorageService storageService, EventoService eventoService, EstabelecimentoService estabelecimentoService, VotacaoSeloService votacaoSeloService, UsuarioService usuarioService) {
        super(documentacaoSeloRepository, seloService, storageService, eventoService, estabelecimentoService, votacaoSeloService, usuarioService);
    }

    @Override
    public void processValidacao(DocumentacaoSelo documentacaoSelo, ValidateDocumentacaoDto validateDocumentacaoDto){
        UsuarioNatal usuarioDocumentacao = (UsuarioNatal) documentacaoSelo.getUsuario();

        int reputacao = usuarioDocumentacao.getReputacao() + (validateDocumentacaoDto.valida() ? 10 : -10);

        ((UsuarioServiceNatal) usuarioService).updateReputacao(usuarioDocumentacao, reputacao);
    }

    @Override
    public void checkIfCanCreate(Usuario criadorEntidade, Usuario usuario) throws BusinessException {
        UsuarioNatal usuarioNatal = (UsuarioNatal) usuario;

        if (criadorEntidade != null && criadorEntidade.getId() != usuarioNatal.getId()) {
            throw new BusinessException("Apenas o criador pode enviar a solicitação de documentação", ExceptionTypesEnum.FORBIDDEN);
        } else if (criadorEntidade == null && usuarioNatal.getReputacao() < 70) {
            throw new BusinessException("Reputação insuficiente para envio de documentação", ExceptionTypesEnum.FORBIDDEN);
        }

    }

    @Override
    public void checkIfCanValidate( Usuario validadorEvento) throws BusinessException {
        UsuarioNatal validador = (UsuarioNatal) validadorEvento;

        if (validador.getTipo() != TipoUsuarioEnumNatal.PREFEITURA) {
            throw new BusinessException("Apenas a prefeitura pode validar a documentação", ExceptionTypesEnum.FORBIDDEN);
        }
    }
}
