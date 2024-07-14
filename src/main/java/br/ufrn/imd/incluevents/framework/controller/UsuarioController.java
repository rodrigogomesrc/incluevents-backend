package br.ufrn.imd.incluevents.framework.controller;

import br.ufrn.imd.incluevents.framework.controller.component.GetUsuarioLogadoHelper;
import br.ufrn.imd.incluevents.framework.dto.CreateUsuarioDto;
import br.ufrn.imd.incluevents.framework.dto.UpdateUsuarioDto;
import br.ufrn.imd.incluevents.framework.exceptions.BusinessException;
import br.ufrn.imd.incluevents.framework.model.Usuario;
import br.ufrn.imd.incluevents.framework.service.UsuarioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "http://localhost:3000")
public abstract class UsuarioController {

    protected final UsuarioService usuarioService;
    protected final GetUsuarioLogadoHelper getUsuarioLogadoHelper;
    protected static final Logger logger = LoggerFactory.getLogger(UsuarioController.class);

    public UsuarioController(UsuarioService usuarioService, GetUsuarioLogadoHelper getUsuarioLogadoHelper){
        this.usuarioService = usuarioService;
        this.getUsuarioLogadoHelper = getUsuarioLogadoHelper;
    }

    public ResponseEntity<?> createUsuarioBase(CreateUsuarioDto createUsuarioDto){
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

    @GetMapping("/me")
    public ResponseEntity<?> getUsuarioLogado(){
        try{
            Usuario usuario = getUsuarioLogadoHelper.getUsuarioLogado();

            return ResponseEntity.status(HttpStatus.OK).body(usuario);
        }catch(BusinessException e){
            return ResponseEntity.status(GetHttpCode.getHttpCode(e.getType())).body(e.getMessage());
        }catch (Exception e){
            logger.error("Erro ao buscar Usuário", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao buscar Usuário");
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

    public ResponseEntity<?> updateUsuarioBase(int id, UpdateUsuarioDto updateUsuarioDto){
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
