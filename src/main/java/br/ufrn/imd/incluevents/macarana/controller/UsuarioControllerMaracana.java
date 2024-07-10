package br.ufrn.imd.incluevents.macarana.controller;

import br.ufrn.imd.incluevents.framework.controller.GetHttpCode;
import br.ufrn.imd.incluevents.framework.controller.UsuarioController;
import br.ufrn.imd.incluevents.framework.controller.component.GetUsuarioLogadoHelper;
import br.ufrn.imd.incluevents.framework.exceptions.BusinessException;
import br.ufrn.imd.incluevents.framework.service.UsuarioService;
import br.ufrn.imd.incluevents.macarana.dtos.CreateUsuarioDtoMaracana;
import br.ufrn.imd.incluevents.macarana.dtos.UpdateUsuarioDtoMaracana;
import br.ufrn.imd.incluevents.macarana.dtos.ValidateDocumentacaoUsuarioDtoMaracana;
import br.ufrn.imd.incluevents.macarana.model.UsuarioMaracana;
import br.ufrn.imd.incluevents.macarana.service.UsuarioServiceMaracana;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "http://localhost:3000")
public class UsuarioControllerMaracana extends UsuarioController {

    public UsuarioControllerMaracana(UsuarioService usuarioService, GetUsuarioLogadoHelper getUsuarioLogadoHelper) {
        super(usuarioService, getUsuarioLogadoHelper);
    }

    @PostMapping(consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> createUsuario(@RequestBody CreateUsuarioDtoMaracana createUsuarioDtoMaracana) {

        return super.createUsuarioBase(createUsuarioDtoMaracana);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuario(@PathVariable(name = "id") Integer id, @RequestBody UpdateUsuarioDtoMaracana createUsuarioDtoMaracana) {
        return super.updateUsuarioBase(id, createUsuarioDtoMaracana);
    }

    @PostMapping("/validate-documentacao")
    public ResponseEntity<?> create(ValidateDocumentacaoUsuarioDtoMaracana validateDocumentacaoUsuarioDtoMaracana) {
        try {
            UsuarioMaracana usuario = (UsuarioMaracana) getUsuarioLogadoHelper.getUsuarioLogado();

            ((UsuarioServiceMaracana) usuarioService).validateDocumentacao(validateDocumentacaoUsuarioDtoMaracana, usuario);

            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (BusinessException e) {
            return ResponseEntity.status(GetHttpCode.getHttpCode(e.getType())).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Erro ao validar documentação de usuário");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

}
