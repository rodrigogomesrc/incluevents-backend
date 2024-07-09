package br.ufrn.imd.incluevents.ufpb.dto;

import br.ufrn.imd.incluevents.framework.dto.CreateUsuarioDto;
import br.ufrn.imd.incluevents.ufpb.model.enums.TipoUsuarioEnumUfpb;

public record CreateUsuarioDtoUfpb(String nome, String email, String senha, String username, TipoUsuarioEnumUfpb tipo, String cargo, int tempoServico) implements CreateUsuarioDto {

}
