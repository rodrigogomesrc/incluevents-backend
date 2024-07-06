package br.ufrn.imd.incluevents.natal.service;

import br.ufrn.imd.incluevents.framework.model.Evento;
import br.ufrn.imd.incluevents.framework.service.EventScraper;
import br.ufrn.imd.incluevents.framework.service.SeleniumPageRetriever;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.ufrn.imd.incluevents.framework.dto.CreateUsuarioDto;
import br.ufrn.imd.incluevents.framework.exceptions.BusinessException;
import br.ufrn.imd.incluevents.framework.model.Usuario;
import br.ufrn.imd.incluevents.framework.repository.UsuarioRepository;
import br.ufrn.imd.incluevents.framework.service.UsuarioService;

import java.util.ArrayList;
import java.util.List;

@Service
public class UsuarioServiceNatal extends UsuarioService {
    public UsuarioServiceNatal(UsuarioRepository usuarioRepository) {
        super(usuarioRepository);
    }

    @Override
    public Usuario createUsuario(CreateUsuarioDto createUsuarioDto) throws BusinessException {
        System.out.println("Criando usuário Natal");

        return super.createUsuario(createUsuarioDto);
    }

    @Override
    public void validate(Usuario usuario) {
    }

    public void updateReputacao(Usuario usuario, int reputacao) {
        usuario.setReputacao(Math.min(500, Math.max(0, reputacao)));

        usuarioRepository.save(usuario);
    }
}
