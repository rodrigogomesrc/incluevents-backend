package br.ufrn.imd.incluevents.macarana.service;

import br.ufrn.imd.incluevents.framework.dto.CreateUsuarioDto;
import br.ufrn.imd.incluevents.framework.dto.UpdateUsuarioDto;
import br.ufrn.imd.incluevents.framework.exceptions.BusinessException;
import br.ufrn.imd.incluevents.framework.exceptions.enums.ExceptionTypesEnum;
import br.ufrn.imd.incluevents.framework.model.Usuario;
import br.ufrn.imd.incluevents.framework.repository.UsuarioRepository;
import br.ufrn.imd.incluevents.framework.service.StorageService;
import br.ufrn.imd.incluevents.framework.service.UsuarioService;
import br.ufrn.imd.incluevents.macarana.dtos.CreateUsuarioDtoMaracana;
import br.ufrn.imd.incluevents.macarana.model.UsuarioMaracana;
import br.ufrn.imd.incluevents.macarana.model.enums.TipoUsuarioEnumMaracana;

import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UsuarioServiceMaracana extends UsuarioService {
    private final StorageService storageService;

    public UsuarioServiceMaracana(UsuarioRepository usuarioRepository, StorageService storageService) {
        super(usuarioRepository);
        this.storageService = storageService;
    }

    @Override
    public Usuario parseDtoToEntity(CreateUsuarioDto createUsuarioDto) throws BusinessException {
        CreateUsuarioDtoMaracana createUsuarioDtoMaracana = (CreateUsuarioDtoMaracana) createUsuarioDto;

        if (createUsuarioDtoMaracana.tipo() == null) {
            throw new BusinessException("Usuário deve ter tipo", ExceptionTypesEnum.BAD_REQUEST);
        }

        if (createUsuarioDtoMaracana.tipo() == TipoUsuarioEnumMaracana.ESPECIALISTA) {
            if (createUsuarioDtoMaracana.documentacao() == null) {
                throw new BusinessException("Deve ter documentação de especialista", ExceptionTypesEnum.BAD_REQUEST);
            }
        }

        UsuarioMaracana usuarioMaracana = new UsuarioMaracana();


        super.parseDtoToEntity(createUsuarioDtoMaracana, usuarioMaracana);

        usuarioMaracana.setCriadoEm(new Date());
        usuarioMaracana.setTipo(createUsuarioDtoMaracana.tipo());
        usuarioMaracana.setNomeDocumentacao(createUsuarioDtoMaracana.documentacao().getOriginalFilename());
        usuarioMaracana.setUrlDocumentacao(this.storageService.store(createUsuarioDtoMaracana.documentacao()));

        return usuarioMaracana;
    }

    @Override
    public Usuario parseDtoToEntity(UpdateUsuarioDto updateUsuarioDto) throws BusinessException {
        UsuarioMaracana usuarioMaracana = new UsuarioMaracana();

        super.parseDtoToEntity(updateUsuarioDto, usuarioMaracana);

        return usuarioMaracana;
    }
}
