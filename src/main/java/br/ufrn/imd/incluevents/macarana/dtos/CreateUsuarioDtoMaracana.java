package br.ufrn.imd.incluevents.macarana.dtos;

import br.ufrn.imd.incluevents.framework.dto.CreateUsuarioDto;
import br.ufrn.imd.incluevents.macarana.model.enums.TipoUsuarioEnumMaracana;

public record CreateUsuarioDtoMaracana(String nome, String email, String senha, String username, TipoUsuarioEnumMaracana tipo) implements CreateUsuarioDto {

}
