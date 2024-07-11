package br.ufrn.imd.incluevents.maracana.dtos;

import br.ufrn.imd.incluevents.framework.dto.UpdateUsuarioDto;

public record UpdateUsuarioDtoMaracana(String nome, String username, String senha) implements UpdateUsuarioDto {

}
