package br.ufrn.imd.incluevents.controller;

import br.ufrn.imd.incluevents.dto.CreateUsuarioDto;
import br.ufrn.imd.incluevents.dto.UpdateUsuarioDto;
import br.ufrn.imd.incluevents.exceptions.BusinessException;
import br.ufrn.imd.incluevents.model.Usuario;
import br.ufrn.imd.incluevents.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "http://localhost:3000")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    public UsuarioController(UsuarioService usuarioService){
        this.usuarioService = usuarioService;
    }

    @PostMapping()
    public ResponseEntity<?> createUsuario(@RequestBody CreateUsuarioDto createUsuarioDto){
        try{
            Usuario createdUsuario = usuarioService.createUsuario(createUsuarioDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUsuario);
        }catch(BusinessException e){
            return ResponseEntity.status(GetHttpCode.getHttpCode(e.getType())).body(e.getMessage());
        }catch (Exception e){
            logger.error("Erro ao salvar Usuário", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar Usuário");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUsuario(@PathVariable("id") int id){
        try{
            Usuario usuario = usuarioService.getUsuarioById(id);
            return ResponseEntity.ok().body(usuario);
        }catch(BusinessException e){
            return ResponseEntity.status(GetHttpCode.getHttpCode(e.getType())).body(e.getMessage());
        }catch (Exception e){
            logger.error("Erro ao buscar Usuário", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao buscar Usuário");
        }
    }
    @GetMapping
    public ResponseEntity<?> getUsuarios(){
        try{
            List<Usuario> usuarios = usuarioService.getUsuarios();
            return ResponseEntity.ok().body(usuarios);
        }catch(BusinessException e){
            return ResponseEntity.status(GetHttpCode.getHttpCode(e.getType())).body(e.getMessage());
        } catch (Exception e){
            logger.error("Erro ao buscar Usuários", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao buscar Usuários");
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuario(@PathVariable("id") int id, @RequestBody UpdateUsuarioDto updateUsuarioDto){
        try{
            usuarioService.updateUserById(id, updateUsuarioDto);
            return ResponseEntity.ok().body(updateUsuarioDto);
        } catch(BusinessException e){
            return ResponseEntity.status(GetHttpCode.getHttpCode(e.getType())).body(e.getMessage());
        } catch (Exception e){
            logger.error("Erro ao alterar Usuário", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao alterar Usuário");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUsuario(@PathVariable("id") int id){
        try{
            usuarioService.deleteUsuarioById(id);
            return ResponseEntity.ok().body("Usuário excluído com sucesso!");
        } catch(BusinessException e){
            return ResponseEntity.status(GetHttpCode.getHttpCode(e.getType())).body(e.getMessage());
        } catch(Exception e){
            logger.error("Erro ao excluir Usuário", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao excluir Usuário");
        }
    }
}
