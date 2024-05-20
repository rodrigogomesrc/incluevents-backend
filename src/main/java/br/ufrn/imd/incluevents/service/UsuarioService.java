package br.ufrn.imd.incluevents.service;

import br.ufrn.imd.incluevents.dto.CreateUsuarioDto;
import br.ufrn.imd.incluevents.dto.UpdateUsuarioDto;
import br.ufrn.imd.incluevents.exceptions.*;
import br.ufrn.imd.incluevents.exceptions.enums.ExceptionTypesEnum;
import br.ufrn.imd.incluevents.model.Usuario;
import br.ufrn.imd.incluevents.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario createUsuario(CreateUsuarioDto createUsuarioDto) throws BusinessException {


        if(createUsuarioDto.email() == null || createUsuarioDto.nome() == null || createUsuarioDto.username() == null || createUsuarioDto.senha() == null){
            throw new BusinessException("Deve ter nome, email, username e senha para o cadastro", ExceptionTypesEnum.BAD_REQUEST);
        }
        if(usuarioRepository.findByEmail(createUsuarioDto.email()).isPresent()){
            throw new BusinessException("Esse email já existe", ExceptionTypesEnum.BAD_REQUEST);
        }
        if(usuarioRepository.findByUsername(createUsuarioDto.username()) != null){
            throw new BusinessException("Esse username já existe", ExceptionTypesEnum.BAD_REQUEST);
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(createUsuarioDto.senha());

        Usuario usuario = new Usuario();
        usuario.setNome(createUsuarioDto.nome());
        usuario.setUsername(createUsuarioDto.username());
        usuario.setEmail(createUsuarioDto.email());
        usuario.setSenha(encryptedPassword);
        usuario.setReputacao(50);
        usuario.setTipo(createUsuarioDto.tipo());

        return usuarioRepository.save(usuario);
    }

    public Usuario getUsuarioById(int id) throws BusinessException {

        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
        if(!usuarioOptional.isPresent()){
            throw new BusinessException("Usuário não encontrado com o id: " + id, ExceptionTypesEnum.NOT_FOUND);
        }

        return usuarioOptional.get();
    }

    public Usuario getUsuarioByUsername(String username) throws BusinessException {

        Usuario usuario = (Usuario) usuarioRepository.findByUsername(username);
        if(usuario == null){
            throw new BusinessException("Usuário não encontrado com o username: " + username, ExceptionTypesEnum.NOT_FOUND);
        }
        return usuario;
    }

    public List<Usuario> getUsuarios() throws BusinessException {

        List<Usuario> usuarios = usuarioRepository.findAll();

        if(usuarios.isEmpty()){
            throw new BusinessException("Nenhum Usuário foi encontrado.", ExceptionTypesEnum.NOT_FOUND);
        }
        return usuarios;
    }
    public void updateUserById(int id, UpdateUsuarioDto updateUsuarioDto) throws BusinessException {

        if(updateUsuarioDto.reputacao() != null && updateUsuarioDto.reputacao() < 0){
            throw new BusinessException("O valor para o parâmetro reputação é inválido.", ExceptionTypesEnum.BAD_REQUEST);
        }

        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);

        if(!usuarioOptional.isPresent()){
            throw new BusinessException("Usuário não encontrado com o id: " + id, ExceptionTypesEnum.NOT_FOUND);
        }

        Usuario usuario = usuarioOptional.get();

        if (updateUsuarioDto.nome() != null) {
            usuario.setNome(updateUsuarioDto.nome());
        }

        if (updateUsuarioDto.username() != null) {
            usuario.setUsername(updateUsuarioDto.username());
        }

        if (updateUsuarioDto.senha() != null) {
            String encryptedPassword = new BCryptPasswordEncoder().encode(updateUsuarioDto.senha());
            usuario.setSenha(encryptedPassword);
        }

        if (updateUsuarioDto.reputacao() != null) {
            usuario.setReputacao( updateUsuarioDto.reputacao() );
        }

        usuarioRepository.save(usuario);
    }
      
    public void deleteUsuarioById(int id) throws BusinessException {
        var usuarioExists = usuarioRepository.existsById(id);

        if (!usuarioExists) {
            throw new BusinessException("Usuário não encontrado com o id: " + id, ExceptionTypesEnum.NOT_FOUND);
        }
        usuarioRepository.deleteById(id);
    }

    public void updateReputacaoById(Integer id, Integer reputacao) throws BusinessException {
        if (id == null || id < 0) {
            throw new BusinessException("Id do usuário inválido", ExceptionTypesEnum.BAD_REQUEST);
        }

        if (reputacao == null) {
            reputacao = 50;
        }

        Usuario usuario = usuarioRepository.findById(id).orElseThrow(() ->
            new BusinessException("Usuário não encontrado", ExceptionTypesEnum.NOT_FOUND)
        );

        usuario.setReputacao(Math.min(500, Math.max(0, reputacao)));

        usuarioRepository.save(usuario);
    }
}
