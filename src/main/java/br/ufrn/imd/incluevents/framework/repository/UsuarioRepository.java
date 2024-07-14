package br.ufrn.imd.incluevents.framework.repository;

import br.ufrn.imd.incluevents.framework.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    public Optional<Usuario> findByEmail(String email);
    public UserDetails findByUsername(String username);
}
