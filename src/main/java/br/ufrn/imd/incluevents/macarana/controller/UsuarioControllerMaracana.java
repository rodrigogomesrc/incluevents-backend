package br.ufrn.imd.incluevents.macarana.controller;

import br.ufrn.imd.incluevents.framework.controller.UsuarioController;
import br.ufrn.imd.incluevents.framework.controller.component.GetUsuarioLogadoHelper;
import br.ufrn.imd.incluevents.framework.service.UsuarioService;
import br.ufrn.imd.incluevents.macarana.dtos.CreateUsuarioDtoMaracana;
import br.ufrn.imd.incluevents.macarana.dtos.UpdateUsuarioDtoMaracana;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "http://localhost:3000")
public class UsuarioControllerMaracana extends UsuarioController {
    public UsuarioControllerMaracana(UsuarioService usuarioService, GetUsuarioLogadoHelper getUsuarioLogadoHelper) {
        super(usuarioService, getUsuarioLogadoHelper);
    }

    @PostMapping()
    public ResponseEntity<?> createUsuario(@RequestBody CreateUsuarioDtoMaracana createUsuarioDtoMaracana) {

        return super.createUsuarioBase(createUsuarioDtoMaracana);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuario(@PathVariable(name = "id") Integer id, @RequestBody UpdateUsuarioDtoMaracana createUsuarioDtoMaracana) {
        return super.updateUsuarioBase(id, createUsuarioDtoMaracana);
    }
}
