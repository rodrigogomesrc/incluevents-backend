package br.ufrn.imd.incluevents.macarana.service;

import br.ufrn.imd.incluevents.framework.dto.CreateUsuarioDto;
import br.ufrn.imd.incluevents.framework.dto.UpdateUsuarioDto;
import br.ufrn.imd.incluevents.framework.exceptions.BusinessException;
import br.ufrn.imd.incluevents.framework.exceptions.enums.ExceptionTypesEnum;
import br.ufrn.imd.incluevents.framework.model.Usuario;
import br.ufrn.imd.incluevents.framework.repository.UsuarioRepository;
import br.ufrn.imd.incluevents.framework.service.UsuarioService;
import br.ufrn.imd.incluevents.macarana.dtos.CreateUsuarioDtoMaracana;
import br.ufrn.imd.incluevents.macarana.model.UsuarioMaracana;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UsuarioServiceMaracana extends UsuarioService {
    public UsuarioServiceMaracana(UsuarioRepository usuarioRepository) {
        super(usuarioRepository);
    }

    @Override
    public Usuario parseDtoToEntity(CreateUsuarioDto createUsuarioDto) throws BusinessException {
        CreateUsuarioDtoMaracana createUsuarioDtoMaracana = (CreateUsuarioDtoMaracana) createUsuarioDto;

        if (createUsuarioDtoMaracana.tipo() == null) {
            throw new BusinessException("Usuário deve ter tipo", ExceptionTypesEnum.BAD_REQUEST);
        }

        UsuarioMaracana usuarioMaracana = new UsuarioMaracana();

        super.parseDtoToEntity(createUsuarioDtoMaracana, usuarioMaracana);

        usuarioMaracana.setCriadoEm(new Date());
        usuarioMaracana.setTipo(createUsuarioDtoMaracana.tipo());

        return usuarioMaracana;
    }

    @Override
    public Usuario parseDtoToEntity(UpdateUsuarioDto updateUsuarioDto) throws BusinessException {
        UsuarioMaracana usuarioMaracana = new UsuarioMaracana();

        super.parseDtoToEntity(updateUsuarioDto, usuarioMaracana);

        return usuarioMaracana;
    }
}
