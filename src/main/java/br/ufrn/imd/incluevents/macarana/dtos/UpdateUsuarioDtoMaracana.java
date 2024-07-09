package br.ufrn.imd.incluevents.macarana.dtos;

import br.ufrn.imd.incluevents.framework.dto.UpdateUsuarioDto;

public record UpdateUsuarioDtoMaracana(String nome, String username, String senha) implements UpdateUsuarioDto {

}
