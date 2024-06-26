package br.ufrn.imd.incluevents.framework.controller.component;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import br.ufrn.imd.incluevents.framework.exceptions.BusinessException;
import br.ufrn.imd.incluevents.framework.exceptions.enums.ExceptionTypesEnum;
import br.ufrn.imd.incluevents.framework.model.Usuario;
import br.ufrn.imd.incluevents.framework.service.UsuarioService;

@Component
public class GetUsuarioLogadoHelper {
    private final UsuarioService usuarioService;

    public GetUsuarioLogadoHelper(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    public Usuario getUsuarioLogado() throws BusinessException {

        try {
            Usuario usuario = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            return usuarioService.getUsuarioById(usuario.getId());
        } catch (ClassCastException e) {
            throw new BusinessException("Usuário logado não encontrado", ExceptionTypesEnum.NOT_FOUND);
        } catch (BusinessException e) {
            throw new BusinessException("Usuário logado não encontrado", ExceptionTypesEnum.NOT_FOUND);
        }
    }
}
