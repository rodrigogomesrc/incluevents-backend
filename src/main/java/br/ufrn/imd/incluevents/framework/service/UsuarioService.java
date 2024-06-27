package br.ufrn.imd.incluevents.framework.service;

import br.ufrn.imd.incluevents.framework.dto.CreateUsuarioDto;
import br.ufrn.imd.incluevents.framework.dto.UpdateUsuarioDto;
import br.ufrn.imd.incluevents.framework.exceptions.*;
import br.ufrn.imd.incluevents.framework.exceptions.enums.ExceptionTypesEnum;
import br.ufrn.imd.incluevents.framework.model.Usuario;
import br.ufrn.imd.incluevents.framework.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public abstract class UsuarioService {
    protected final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    protected void parseDtoToEntity(CreateUsuarioDto createUsuarioDto, Usuario usuario) throws BusinessException {
        if(createUsuarioDto.email() == null || createUsuarioDto.nome() == null || createUsuarioDto.username() == null || createUsuarioDto.senha() == null){
            throw new BusinessException("Deve ter nome, email, username e senha para o cadastro", ExceptionTypesEnum.BAD_REQUEST);
        }

        usuario.setNome(createUsuarioDto.nome());
        usuario.setUsername(createUsuarioDto.username());
        usuario.setEmail(createUsuarioDto.email());
    }

    protected void parseDtoToEntity(UpdateUsuarioDto createUsuarioDto, Usuario usuario) throws BusinessException {
        usuario.setNome(createUsuarioDto.nome());
        usuario.setUsername(createUsuarioDto.username());
    }

    protected abstract Usuario parseDtoToEntity(CreateUsuarioDto createUsuarioDto) throws BusinessException;

    protected abstract Usuario parseDtoToEntity(UpdateUsuarioDto updateUsuarioDto) throws BusinessException;

    public Usuario createUsuario(CreateUsuarioDto createUsuarioDto) throws BusinessException {
        Usuario usuario = this.parseDtoToEntity(createUsuarioDto);

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

        // Adicionar em método da subclasse
        // usuario.setReputacao(50);
        // usuario.setTipo(createUsuarioDto.tipo());

        usuario.setSenha(encryptedPassword);

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

        usuarioRepository.save(usuario);
    }

    public void deleteUsuarioById(int id) throws BusinessException {
        var usuarioExists = usuarioRepository.existsById(id);

        if (!usuarioExists) {
            throw new BusinessException("Usuário não encontrado com o id: " + id, ExceptionTypesEnum.NOT_FOUND);
        }
        usuarioRepository.deleteById(id);
    }
}
