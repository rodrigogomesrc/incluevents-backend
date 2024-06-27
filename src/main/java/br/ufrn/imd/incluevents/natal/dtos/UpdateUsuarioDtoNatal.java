package br.ufrn.imd.incluevents.natal.dtos;

import br.ufrn.imd.incluevents.framework.dto.UpdateUsuarioDto;

public record UpdateUsuarioDtoNatal(String nome, String username, String senha) implements UpdateUsuarioDto {

}
