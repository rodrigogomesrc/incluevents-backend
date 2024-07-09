package br.ufrn.imd.incluevents.ufpb.dto;

import br.ufrn.imd.incluevents.framework.dto.UpdateUsuarioDto;

public record UpdateUsuarioDtoUfpb(String nome, String username, String senha) implements UpdateUsuarioDto {

}
