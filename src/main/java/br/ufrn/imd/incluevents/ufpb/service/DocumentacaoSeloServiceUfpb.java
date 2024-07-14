package br.ufrn.imd.incluevents.ufpb.service;

import br.ufrn.imd.incluevents.framework.exceptions.BusinessException;
import br.ufrn.imd.incluevents.framework.exceptions.enums.ExceptionTypesEnum;
import br.ufrn.imd.incluevents.framework.model.Usuario;
import br.ufrn.imd.incluevents.framework.service.*;
import br.ufrn.imd.incluevents.ufpb.model.UsuarioUfpb;
import br.ufrn.imd.incluevents.ufpb.model.enums.CargoEnumUfpb;
import br.ufrn.imd.incluevents.ufpb.model.enums.TipoUsuarioEnumUfpb;
import org.springframework.stereotype.Service;

import br.ufrn.imd.incluevents.framework.repository.DocumentacaoSeloRepository;

@Service
public class DocumentacaoSeloServiceUfpb extends DocumentacaoSeloService {

    public DocumentacaoSeloServiceUfpb(DocumentacaoSeloRepository documentacaoSeloRepository, SeloService seloService, StorageService storageService, EventoService eventoService, EstabelecimentoService estabelecimentoService, VotacaoSeloService votacaoSeloService, UsuarioService usuarioService) {
        super(documentacaoSeloRepository, seloService, storageService, eventoService, estabelecimentoService, votacaoSeloService, usuarioService);
    }

    @Override
    public void checkIfCanCreate(Usuario criadorEntidade, Usuario usuario) throws BusinessException {
        UsuarioUfpb criadorEventoUfpb = (UsuarioUfpb) usuario;

        if (usuario == null) {
            throw new BusinessException("É necessário ter um criador", ExceptionTypesEnum.FORBIDDEN);
        }else if(criadorEventoUfpb.getTipo() != TipoUsuarioEnumUfpb.SERVIDOR && criadorEventoUfpb.getTipo() != TipoUsuarioEnumUfpb.ESTUDANTE){
            throw new BusinessException("É necessário ser um servidor ou estudante", ExceptionTypesEnum.FORBIDDEN);
        }else if(criadorEventoUfpb.getTipo() == TipoUsuarioEnumUfpb.ESTUDANTE && criadorEventoUfpb.getImc() < 7.0){
            throw new BusinessException("Se for um estudante, é necessário ter o imc maior ou igual a 7", ExceptionTypesEnum.FORBIDDEN);
        }

    }


    @Override
    public void checkIfCanValidate( Usuario validadorEvento)  throws BusinessException {
        UsuarioUfpb validatorEventoUfpb = (UsuarioUfpb) validadorEvento;

        if (validadorEvento == null) {
            throw new BusinessException("É necessário ter um validador", ExceptionTypesEnum.FORBIDDEN);
        }else if(validatorEventoUfpb.getTipo() != TipoUsuarioEnumUfpb.SERVIDOR || validatorEventoUfpb.getCargo() != CargoEnumUfpb.COORDENACAO){
            throw new BusinessException("É necessário ser da coordenação", ExceptionTypesEnum.FORBIDDEN);
        }
    }
}
