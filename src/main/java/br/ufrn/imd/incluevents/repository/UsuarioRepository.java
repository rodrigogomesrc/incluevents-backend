package br.ufrn.imd.incluevents.repository;

import br.ufrn.imd.incluevents.model.Selo;
import br.ufrn.imd.incluevents.model.Usuario;
import br.ufrn.imd.incluevents.model.Validacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    public Optional<Usuario> findByEmail(String email);
}
