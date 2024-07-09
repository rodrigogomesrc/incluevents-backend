package br.ufrn.imd.incluevents.natal.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.ufrn.imd.incluevents.framework.controller.UsuarioController;
import br.ufrn.imd.incluevents.framework.controller.component.GetUsuarioLogadoHelper;
import br.ufrn.imd.incluevents.framework.service.UsuarioService;
import br.ufrn.imd.incluevents.natal.dtos.CreateUsuarioDtoNatal;
import br.ufrn.imd.incluevents.natal.dtos.UpdateUsuarioDtoNatal;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "http://localhost:3000")
public class UsuarioControllerNatal extends UsuarioController {
    public UsuarioControllerNatal(UsuarioService usuarioService, GetUsuarioLogadoHelper getUsuarioLogadoHelper) {
        super(usuarioService, getUsuarioLogadoHelper);
    }

    @PostMapping()
    public ResponseEntity<?> createUsuario(@RequestBody CreateUsuarioDtoNatal createUsuarioDtoNatal) {

        return super.createUsuarioBase(createUsuarioDtoNatal);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuario(@PathVariable(name = "id") Integer id, @RequestBody UpdateUsuarioDtoNatal updateUsuarioDtoNatal) {
        return super.updateUsuarioBase(id, updateUsuarioDtoNatal);
    }
}
