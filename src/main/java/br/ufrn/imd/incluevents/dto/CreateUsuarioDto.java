package br.ufrn.imd.incluevents.dto;

import br.ufrn.imd.incluevents.model.enums.TipoUsuarioEnum;

public record CreateUsuarioDto(String nome, String email, String username, String senha, TipoUsuarioEnum tipo) {
}
