package br.ufrn.imd.incluevents.maracana.service;

import br.ufrn.imd.incluevents.framework.exceptions.BusinessException;
import br.ufrn.imd.incluevents.framework.exceptions.enums.ExceptionTypesEnum;
import br.ufrn.imd.incluevents.framework.model.Usuario;
import br.ufrn.imd.incluevents.framework.repository.DocumentacaoSeloRepository;
import br.ufrn.imd.incluevents.framework.service.*;
import br.ufrn.imd.incluevents.maracana.model.UsuarioMaracana;
import br.ufrn.imd.incluevents.maracana.model.enums.TipoUsuarioEnumMaracana;
import br.ufrn.imd.incluevents.natal.model.UsuarioNatal;
import br.ufrn.imd.incluevents.natal.model.enums.TipoUsuarioEnumNatal;
import br.ufrn.imd.incluevents.ufpb.model.UsuarioUfpb;
import br.ufrn.imd.incluevents.ufpb.model.enums.CargoEnumUfpb;
import br.ufrn.imd.incluevents.ufpb.model.enums.TipoUsuarioEnumUfpb;
import org.springframework.stereotype.Service;

@Service
public class DocumentacaoSeloServiceMaracana extends DocumentacaoSeloService {


    public DocumentacaoSeloServiceMaracana(DocumentacaoSeloRepository documentacaoSeloRepository, SeloService seloService, StorageService storageService, EventoService eventoService, EstabelecimentoService estabelecimentoService, VotacaoSeloService votacaoSeloService, UsuarioService usuarioService) {
        super(documentacaoSeloRepository, seloService, storageService, eventoService, estabelecimentoService, votacaoSeloService, usuarioService);
    }

    @Override
    public void checkIfCanCreate(Usuario criadorValidacao, Usuario usuario) throws BusinessException {
        UsuarioMaracana criador = (UsuarioMaracana) criadorValidacao;

        if (criador == null) {
            throw new BusinessException("É necessário ter um criador", ExceptionTypesEnum.BAD_REQUEST.FORBIDDEN);
        }else if(criador.getTipo() != TipoUsuarioEnumMaracana.ESPECIALISTA || !criador.getDocumentacaoValida()){
            throw new BusinessException("É necessário ser um especialista com documentação válida", ExceptionTypesEnum.BAD_REQUEST.FORBIDDEN);
        }
    }

    @Override
    public void checkIfCanValidate( Usuario validadorEvento) throws BusinessException {

        UsuarioMaracana validador = (UsuarioMaracana) validadorEvento;

        if (validadorEvento == null) {
            throw new BusinessException("É necessário ter um validador", ExceptionTypesEnum.BAD_REQUEST.FORBIDDEN);
        }else if(validador.getTipo() != TipoUsuarioEnumMaracana.ORGAO_VALIDACAO){
            throw new BusinessException("É necessário ser um órgão validador", ExceptionTypesEnum.BAD_REQUEST.FORBIDDEN);
        }
    }
}
