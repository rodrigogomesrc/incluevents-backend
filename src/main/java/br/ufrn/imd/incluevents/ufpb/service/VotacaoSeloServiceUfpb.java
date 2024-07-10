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

        return usuarioUfpb.getTipo() == TipoUsuarioEnumUfpb.ORGAO_VALIDACAO;
    }

    @Override
    public int calculateCredibilidate(Usuario usuario) throws BusinessException {
        UsuarioUfpb usuarioUfpb = (UsuarioUfpb) usuario;

        if (usuarioUfpb.getTipo() == TipoUsuarioEnumUfpb.ESTUDANTE) {
            return 10;
        } else if (usuarioUfpb.getTipo() == TipoUsuarioEnumUfpb.SERVIDOR) {
            double ponderacao = 0;

            if (usuarioUfpb.getCargo() == CargoEnumUfpb.PROFESSOR) {
                ponderacao = 0.4;
            } else if (usuarioUfpb.getCargo() == CargoEnumUfpb.REITORIA) {
                ponderacao = 0.7;
            } else if (usuarioUfpb.getCargo() == CargoEnumUfpb.COORDENACAO) {
                ponderacao = 1;
            } else {
                ponderacao = 0.2;
            }

            return (int) Math.floor(15 + usuarioUfpb.getTempoServico() * ponderacao);
        } else {
            return 5;
        }
    }
}
