package br.ufrn.imd.incluevents.natal.service;

import br.ufrn.imd.incluevents.framework.dto.ValidateVotacaoDto;
import br.ufrn.imd.incluevents.framework.exceptions.BusinessException;
import br.ufrn.imd.incluevents.framework.model.VotacaoSelo;
import br.ufrn.imd.incluevents.framework.model.Usuario;
import br.ufrn.imd.incluevents.framework.repository.VotacaoSeloRepository;
import br.ufrn.imd.incluevents.framework.service.EstabelecimentoService;
import br.ufrn.imd.incluevents.framework.service.EventoService;
import br.ufrn.imd.incluevents.framework.service.SeloService;
import br.ufrn.imd.incluevents.framework.service.UsuarioService;
import br.ufrn.imd.incluevents.framework.service.VotacaoSeloService;
import br.ufrn.imd.incluevents.natal.model.UsuarioNatal;
import br.ufrn.imd.incluevents.natal.model.enums.TipoUsuarioEnumNatal;
import org.springframework.stereotype.Service;

@Service
public class VotacaoSeloServiceNatal extends VotacaoSeloService {

    public VotacaoSeloServiceNatal(
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
        UsuarioNatal usuarioNatal = (UsuarioNatal) usuario;

        return usuarioNatal.getTipo() == TipoUsuarioEnumNatal.PREFEITURA;
    }

    @Override
    public int calculateCredibilidate(Usuario usuario) throws BusinessException {
        UsuarioNatal usuarioNatal = (UsuarioNatal) usuario;

        return usuarioNatal.getReputacao();
    }

    @Override
    public void processValidacao(VotacaoSelo votacaoSelo, ValidateVotacaoDto validateVotacaoDto) {
        UsuarioNatal usuarioVotacao = (UsuarioNatal) votacaoSelo.getUsuario();

        int reputacao = usuarioVotacao.getReputacao() + (votacaoSelo.getPossuiSelo() == validateVotacaoDto.possuiSelo() ? 5 : -3);
        UsuarioServiceNatal usuarioServiceNatal = (UsuarioServiceNatal) this.usuarioService;
        usuarioServiceNatal.updateReputacao(usuarioVotacao, reputacao);


        /* Deve ir pra classe de sistema específico
        Usuario usuarioDocumentacao = documentacaoSelo.getUsuario();

        int reputacao = usuarioDocumentacao.getReputacao() + (validateDocumentacaoDto.valida() ? 10 : -10);

        usuarioService.updateReputacao(usuarioDocumentacao, reputacao);
        */
    }
}
