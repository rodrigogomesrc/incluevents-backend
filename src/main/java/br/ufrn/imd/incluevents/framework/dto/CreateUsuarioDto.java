package br.ufrn.imd.incluevents.framework.dto;

import br.ufrn.imd.incluevents.framework.model.enums.TipoUsuarioEnum;

public record CreateUsuarioDto(String nome, String email, String username, String senha, TipoUsuarioEnum tipo) {
}
