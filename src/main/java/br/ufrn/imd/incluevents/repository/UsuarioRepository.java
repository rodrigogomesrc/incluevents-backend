package br.ufrn.imd.incluevents.repository;

import br.ufrn.imd.incluevents.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
}
