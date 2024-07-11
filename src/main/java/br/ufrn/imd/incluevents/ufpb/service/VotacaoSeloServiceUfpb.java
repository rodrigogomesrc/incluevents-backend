package br.ufrn.imd.incluevents.ufpb.service;

import br.ufrn.imd.incluevents.framework.exceptions.BusinessException;
import br.ufrn.imd.incluevents.framework.model.Usuario;
import br.ufrn.imd.incluevents.framework.repository.VotacaoSeloRepository;
import br.ufrn.imd.incluevents.framework.service.EstabelecimentoService;
import br.ufrn.imd.incluevents.framework.service.EventoService;
import br.ufrn.imd.incluevents.framework.service.SeloService;
import br.ufrn.imd.incluevents.framework.service.UsuarioService;
import br.ufrn.imd.incluevents.framework.service.VotacaoSeloService;
import br.ufrn.imd.incluevents.ufpb.model.UsuarioUfpb;
import br.ufrn.imd.incluevents.ufpb.model.enums.CargoEnumUfpb;
import br.ufrn.imd.incluevents.ufpb.model.enums.TipoUsuarioEnumUfpb;
import org.springframework.stereotype.Service;

@Service
public class VotacaoSeloServiceUfpb extends VotacaoSeloService {

    public VotacaoSeloServiceUfpb(
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
        UsuarioUfpb usuarioUfpb = (UsuarioUfpb) usuario;

        return usuarioUfpb.getTipo() == TipoUsuarioEnumUfpb.SERVIDOR && usuarioUfpb.getCargo() == CargoEnumUfpb.COORDENACAO;
    }

    @Override
    public int calculateCredibilidate(Usuario usuario) throws BusinessException {
        UsuarioUfpb usuarioUfpb = (UsuarioUfpb) usuario;

        if (usuarioUfpb.getTipo() == TipoUsuarioEnumUfpb.ESTUDANTE) {
            int acrescimo = 0;

            if (usuarioUfpb.getImc() >= 5 && usuarioUfpb.getImc() < 6) {
                acrescimo = 2;
            } else if (usuarioUfpb.getImc() >= 6 && usuarioUfpb.getImc() < 7) {
                acrescimo = 3;
            } else if (usuarioUfpb.getImc() >= 7 && usuarioUfpb.getImc() < 8.5) {
                acrescimo = 4;
            } else if (usuarioUfpb.getImc() >= 8.5) {
                acrescimo = 5;
            }

            return 10 + acrescimo;
        } else {
            double ponderacao = 0;

            if (usuarioUfpb.getCargo() == CargoEnumUfpb.PROFESSOR) {
                ponderacao = 0.5;
            } else if (usuarioUfpb.getCargo() == CargoEnumUfpb.REITORIA) {
                ponderacao = 0.75;
            } else if (usuarioUfpb.getCargo() == CargoEnumUfpb.COORDENACAO) {
                ponderacao = 1;
            } else {
                ponderacao = 0.25;
            }

            return (int) Math.floor(20 + usuarioUfpb.getTempoServico() * ponderacao);
        }
    }
}
