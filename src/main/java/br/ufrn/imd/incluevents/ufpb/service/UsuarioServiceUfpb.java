package br.ufrn.imd.incluevents.ufpb.service;

import br.ufrn.imd.incluevents.framework.dto.CreateUsuarioDto;
import br.ufrn.imd.incluevents.framework.dto.UpdateUsuarioDto;
import br.ufrn.imd.incluevents.framework.exceptions.BusinessException;
import br.ufrn.imd.incluevents.framework.exceptions.enums.ExceptionTypesEnum;
import br.ufrn.imd.incluevents.framework.model.Usuario;
import br.ufrn.imd.incluevents.framework.repository.UsuarioRepository;
import br.ufrn.imd.incluevents.framework.service.UsuarioService;
import br.ufrn.imd.incluevents.ufpb.dto.CreateUsuarioDtoUfpb;
import br.ufrn.imd.incluevents.ufpb.model.UsuarioUfpb;
import br.ufrn.imd.incluevents.ufpb.model.enums.TipoUsuarioEnumUfpb;

import org.springframework.stereotype.Service;

@Service
public class UsuarioServiceUfpb extends UsuarioService {
    public UsuarioServiceUfpb(UsuarioRepository usuarioRepository) {
        super(usuarioRepository);
    }

    @Override
    public Usuario parseDtoToEntity(CreateUsuarioDto createUsuarioDto) throws BusinessException {
        CreateUsuarioDtoUfpb createUsuarioDtoUfpb = (CreateUsuarioDtoUfpb) createUsuarioDto;

        if (createUsuarioDtoUfpb.tipo() == null) {
            throw new BusinessException("Usuário deve ter tipo", ExceptionTypesEnum.BAD_REQUEST);
        }

        if (createUsuarioDtoUfpb.tipo() == TipoUsuarioEnumUfpb.SERVIDOR) {
            if (createUsuarioDtoUfpb.cargo() == null || createUsuarioDtoUfpb.tempoServico() == null) {
                throw new BusinessException("Deve ter cargo e tempo de serviço", ExceptionTypesEnum.BAD_REQUEST);
            }
        }

        if (createUsuarioDtoUfpb.tipo() == TipoUsuarioEnumUfpb.ESTUDANTE) {
            if (createUsuarioDtoUfpb.imc() == null) {
                throw new BusinessException("Deve ter imc", ExceptionTypesEnum.BAD_REQUEST);
            }
        }

        UsuarioUfpb usuarioUfpb = new UsuarioUfpb();

        super.parseDtoToEntity(createUsuarioDtoUfpb, usuarioUfpb);

        usuarioUfpb.setImc(createUsuarioDtoUfpb.imc());
        usuarioUfpb.setCargo(createUsuarioDtoUfpb.cargo());
        usuarioUfpb.setTempoServico(createUsuarioDtoUfpb.tempoServico());
        usuarioUfpb.setTipo(createUsuarioDtoUfpb.tipo());

        return usuarioUfpb;
    }

    @Override
    public Usuario parseDtoToEntity(UpdateUsuarioDto updateUsuarioDto) throws BusinessException {
        UsuarioUfpb usuarioUfpb = new UsuarioUfpb();

        super.parseDtoToEntity(updateUsuarioDto, usuarioUfpb);

        return usuarioUfpb;
    }
}
