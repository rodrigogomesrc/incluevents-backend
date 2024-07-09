package br.ufrn.imd.incluevents.ufpb.controller;

import br.ufrn.imd.incluevents.framework.controller.UsuarioController;
import br.ufrn.imd.incluevents.framework.controller.component.GetUsuarioLogadoHelper;
import br.ufrn.imd.incluevents.framework.service.UsuarioService;
import br.ufrn.imd.incluevents.natal.dtos.CreateUsuarioDtoNatal;
import br.ufrn.imd.incluevents.natal.dtos.UpdateUsuarioDtoNatal;
import br.ufrn.imd.incluevents.ufpb.dto.CreateUsuarioDtoUfpb;
import br.ufrn.imd.incluevents.ufpb.dto.UpdateUsuarioDtoUfpb;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "http://localhost:3000")
public class UsuarioControllerUfpb extends UsuarioController {
    public UsuarioControllerUfpb(UsuarioService usuarioService, GetUsuarioLogadoHelper getUsuarioLogadoHelper) {
        super(usuarioService, getUsuarioLogadoHelper);
    }

    @PostMapping()
    public ResponseEntity<?> createUsuario(@RequestBody CreateUsuarioDtoUfpb createUsuarioDtoUfpb) {

        return super.createUsuarioBase(createUsuarioDtoUfpb);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuario(@PathVariable(name = "id") Integer id, @RequestBody UpdateUsuarioDtoUfpb updateUsuarioDtoUfpb) {
        return super.updateUsuarioBase(id, updateUsuarioDtoUfpb);
    }
}
