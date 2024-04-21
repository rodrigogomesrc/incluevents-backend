package br.ufrn.imd.incluevents.controller;

import br.ufrn.imd.incluevents.dto.CreateUsuarioDto;
import br.ufrn.imd.incluevents.dto.UpdateUsuarioDto;
import br.ufrn.imd.incluevents.exceptions.UsuarioEmailJaExisteException;
import br.ufrn.imd.incluevents.exceptions.UsuarioNotFoundException;
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
            if(createUsuarioDto.email() == null || createUsuarioDto.nome() == null || createUsuarioDto.username() == null || createUsuarioDto.senha() == null){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Deve ter nome, email, username e senha para o cadastro");
            }

            Usuario createdUsuario = usuarioService.createUsuario(createUsuarioDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUsuario);
        }catch (UsuarioEmailJaExisteException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Esse email já existe");
        } catch (Exception e){
            logger.error("Erro ao salvar Usuário", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar Usuário");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUsuario(@PathVariable("id") int id){
        try{
            Usuario usuario = usuarioService.getUsuarioById(id);
            return ResponseEntity.ok().body(usuario);
        } catch (UsuarioNotFoundException e) {
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado com o id: " + id);
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
        } catch (UsuarioNotFoundException e) {
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nenhum Usuário foi encontrado.");
        }catch (Exception e){
            logger.error("Erro ao buscar Usuário", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao buscar Usuários");
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuario(@PathVariable("id") int id, @RequestBody UpdateUsuarioDto updateUsuarioDto){
        try{
            if(updateUsuarioDto.reputacao() != null && updateUsuarioDto.reputacao() < 0){
                return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("O valor para o parâmetro reputação é inválido");
            }
            usuarioService.updateUserById(id, updateUsuarioDto);
            return ResponseEntity.ok().body(updateUsuarioDto);
        } catch (UsuarioNotFoundException e) {
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado com o id: " + id);
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
        } catch (UsuarioNotFoundException e) {
            return  ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuário não encontrado com o id: " + id);
        }catch(Exception e){
            logger.error("Erro ao excluir Usuário", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao excluir Usuário");
        }
    }
}
