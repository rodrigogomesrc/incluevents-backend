package br.ufrn.imd.incluevents.natal.service;

import org.springframework.stereotype.Service;

import br.ufrn.imd.incluevents.framework.dto.CreateUsuarioDto;
import br.ufrn.imd.incluevents.framework.dto.UpdateUsuarioDto;
import br.ufrn.imd.incluevents.framework.exceptions.BusinessException;
import br.ufrn.imd.incluevents.framework.exceptions.enums.ExceptionTypesEnum;
import br.ufrn.imd.incluevents.framework.model.Usuario;
import br.ufrn.imd.incluevents.framework.repository.UsuarioRepository;
import br.ufrn.imd.incluevents.framework.service.UsuarioService;
import br.ufrn.imd.incluevents.natal.dtos.CreateUsuarioDtoNatal;
import br.ufrn.imd.incluevents.natal.model.UsuarioNatal;

@Service
public class UsuarioServiceNatal extends UsuarioService {
    public UsuarioServiceNatal(UsuarioRepository usuarioRepository) {
        super(usuarioRepository);
    }

    @Override
    public Usuario parseDtoToEntity(CreateUsuarioDto createUsuarioDto) throws BusinessException {
        CreateUsuarioDtoNatal createUsuarioDtoNatal = (CreateUsuarioDtoNatal) createUsuarioDto;

        if (createUsuarioDtoNatal.tipo() == null) {
            throw new BusinessException("Usuário deve ter tipo", ExceptionTypesEnum.BAD_REQUEST);
        }

        UsuarioNatal usuarioNatal = new UsuarioNatal();

        super.parseDtoToEntity(createUsuarioDtoNatal, usuarioNatal);

        usuarioNatal.setReputacao(50);
        usuarioNatal.setTipo(createUsuarioDtoNatal.tipo());

        return usuarioNatal;
    }

    @Override
    public Usuario parseDtoToEntity(UpdateUsuarioDto updateUsuarioDto) throws BusinessException {
        UsuarioNatal usuarioNatal = new UsuarioNatal();

        super.parseDtoToEntity(updateUsuarioDto, usuarioNatal);

        return usuarioNatal;
    }

    public void updateReputacao(Usuario usuario, int reputacao) {
        UsuarioNatal usuarioNatal = (UsuarioNatal) usuario;

        usuarioNatal.setReputacao(Math.min(500, Math.max(0, reputacao)));

        usuarioRepository.save(usuario);
    }
}
