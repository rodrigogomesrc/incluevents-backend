package br.ufrn.imd.incluevents.natal.dtos;

import br.ufrn.imd.incluevents.framework.dto.CreateUsuarioDto;
import br.ufrn.imd.incluevents.natal.model.enums.TipoUsuarioEnumNatal;

public record CreateUsuarioDtoNatal(String nome, String email, String senha, String username, TipoUsuarioEnumNatal tipo) implements CreateUsuarioDto {

}
