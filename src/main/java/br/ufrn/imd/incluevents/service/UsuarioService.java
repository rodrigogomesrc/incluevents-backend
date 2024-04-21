package br.ufrn.imd.incluevents.service;

import br.ufrn.imd.incluevents.dto.CreateUsuarioDto;
import br.ufrn.imd.incluevents.dto.UpdateUsuarioDto;
import br.ufrn.imd.incluevents.exceptions.UsuarioNotFoundException;
import br.ufrn.imd.incluevents.model.Usuario;
import br.ufrn.imd.incluevents.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario createUsuario(CreateUsuarioDto createUsuarioDto) {
        Usuario usuario = new Usuario();
        usuario.setNome(createUsuarioDto.nome());
        usuario.setUsername(createUsuarioDto.username());
        usuario.setEmail(createUsuarioDto.email());
        usuario.setSenha(createUsuarioDto.senha());
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

    public void updateUserById(int id, UpdateUsuarioDto updateUsuarioDto) throws UsuarioNotFoundException {

        var usuarioEntity = usuarioRepository.findById(id);

        if (!usuarioEntity.isPresent()) {
            throw new UsuarioNotFoundException();
        }else{
            Usuario usuario = usuarioEntity.get();

            if (updateUsuarioDto.nome() != null) {
                usuario.setNome(updateUsuarioDto.nome());
            }

            if (updateUsuarioDto.username() != null) {
                usuario.setUsername(updateUsuarioDto.username());
            }

            if (updateUsuarioDto.senha() != null) {
                usuario.setSenha(updateUsuarioDto.senha());
            }

            if (updateUsuarioDto.reputacao() != null) {
                usuario.setReputacao( Integer.parseInt(updateUsuarioDto.reputacao()) );
            }

            usuarioRepository.save(usuario);
        }

    }
    public void deleteUsuarioById(int id)  throws UsuarioNotFoundException {
        var usuarioExists = usuarioRepository.existsById(id);

        if (!usuarioExists) {
            throw new UsuarioNotFoundException();
        }
        usuarioRepository.deleteById(id);
    }

}
