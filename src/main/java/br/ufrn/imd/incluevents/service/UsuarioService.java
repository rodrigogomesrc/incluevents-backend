package br.ufrn.imd.incluevents.service;

import br.ufrn.imd.incluevents.dto.CreateUsuarioDto;
import br.ufrn.imd.incluevents.dto.UpdateUsuarioDto;
import br.ufrn.imd.incluevents.exceptions.UsuarioEmailJaExisteException;
import br.ufrn.imd.incluevents.exceptions.UsuarioNotFoundException;
import br.ufrn.imd.incluevents.exceptions.UsuarioUsernameJaExiste;
import br.ufrn.imd.incluevents.model.Usuario;
import br.ufrn.imd.incluevents.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
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

    public Usuario createUsuario(CreateUsuarioDto createUsuarioDto) throws UsuarioEmailJaExisteException, UsuarioUsernameJaExiste {

        if(usuarioRepository.findByEmail(createUsuarioDto.email()).isPresent()){
            throw new UsuarioEmailJaExisteException();
        }

        if(usuarioRepository.findByUsername(createUsuarioDto.username()) != null){
            throw new UsuarioUsernameJaExiste();
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(createUsuarioDto.senha());

        Usuario usuario = new Usuario();
        usuario.setNome(createUsuarioDto.nome());
        usuario.setUsername(createUsuarioDto.username());
        usuario.setEmail(createUsuarioDto.email());
        usuario.setSenha(encryptedPassword);
        usuario.setReputacao(1);

        return usuarioRepository.save(usuario);
    }

    public Usuario getUsuarioById(int id) throws UsuarioNotFoundException {

        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
        if(!usuarioOptional.isPresent()){
            throw new UsuarioNotFoundException();
        }
        return usuarioOptional.get();
    }

    public Usuario getUsuarioByUsername(String username) throws UsuarioNotFoundException {

        Usuario usuario = (Usuario) usuarioRepository.findByUsername(username);
        if(usuario == null){
            throw new UsuarioNotFoundException();
        }
        return usuario;
    }

    public List<Usuario> getUsuarios() throws UsuarioNotFoundException {

        List<Usuario> usuarios = usuarioRepository.findAll();

        if(usuarios.isEmpty()){
            throw new UsuarioNotFoundException();
        }
        return usuarios;
    }
    public void updateUserById(int id, UpdateUsuarioDto updateUsuarioDto) throws UsuarioNotFoundException {

        Usuario usuario = usuarioRepository.findById(id).orElseThrow(UsuarioNotFoundException::new);

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
    public void deleteUsuarioById(int id)  throws UsuarioNotFoundException {
        var usuarioExists = usuarioRepository.existsById(id);

        if (!usuarioExists) {
            throw new UsuarioNotFoundException();
        }
        usuarioRepository.deleteById(id);
    }

}
