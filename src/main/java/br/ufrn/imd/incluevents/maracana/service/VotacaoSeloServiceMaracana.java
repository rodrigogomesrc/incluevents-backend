package br.ufrn.imd.incluevents.maracana.service;

import br.ufrn.imd.incluevents.framework.exceptions.BusinessException;
import br.ufrn.imd.incluevents.framework.model.Usuario;
import br.ufrn.imd.incluevents.framework.repository.VotacaoSeloRepository;
import br.ufrn.imd.incluevents.framework.service.EstabelecimentoService;
import br.ufrn.imd.incluevents.framework.service.EventoService;
import br.ufrn.imd.incluevents.framework.service.SeloService;
import br.ufrn.imd.incluevents.framework.service.UsuarioService;
import br.ufrn.imd.incluevents.framework.service.VotacaoSeloService;
import br.ufrn.imd.incluevents.maracana.model.UsuarioMaracana;
import br.ufrn.imd.incluevents.maracana.model.enums.TipoUsuarioEnumMaracana;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;

@Service
public class VotacaoSeloServiceMaracana extends VotacaoSeloService {

    public VotacaoSeloServiceMaracana(
            VotacaoSeloRepository votacaoSeloRepository,
            SeloService seloService,
            UsuarioService usuarioService,
            EventoService eventoService,
            EstabelecimentoService estabelecimentoService
    ) {
        super(votacaoSeloRepository, seloService, usuarioService, eventoService, estabelecimentoService);
    }

    @Override
    public boolean checkIfCanValidate(Usuario usuario) throws BusinessException {
        UsuarioMaracana usuarioMaracana = (UsuarioMaracana) usuario;

        return usuarioMaracana.getTipo() == TipoUsuarioEnumMaracana.ORGAO_VALIDACAO;
    }

    @Override
    public int calculateCredibilidate(Usuario usuario) throws BusinessException {
        UsuarioMaracana usuarioMaracana = (UsuarioMaracana) usuario;

        LocalDate date = usuarioMaracana.getCriadoEm().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate today = LocalDate.now();

        int acrescimo = (int) Math.floor(50 + ChronoUnit.DAYS.between(date, today) / 60.0);

        if (usuarioMaracana.getTipo() == TipoUsuarioEnumMaracana.ESPECIALISTA) {
            return 50 + acrescimo;
        } else  {
            return 10 + acrescimo;
        }
    }
}
