package br.ufrn.imd.incluevents.maracana.dtos;

import org.springframework.web.multipart.MultipartFile;

import br.ufrn.imd.incluevents.framework.dto.CreateUsuarioDto;
import br.ufrn.imd.incluevents.maracana.model.enums.TipoUsuarioEnumMaracana;

public record CreateUsuarioDtoMaracana(String nome, String email, String senha, String username, TipoUsuarioEnumMaracana tipo, MultipartFile documentacao) implements CreateUsuarioDto {

}
