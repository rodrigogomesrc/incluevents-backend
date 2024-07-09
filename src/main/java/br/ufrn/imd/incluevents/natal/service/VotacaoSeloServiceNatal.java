package br.ufrn.imd.incluevents.natal.service;

import br.ufrn.imd.incluevents.framework.dto.CreateVotacaoSeloDto;
import br.ufrn.imd.incluevents.framework.dto.EstabelecimentoGrupoVotacaoSeloDto;
import br.ufrn.imd.incluevents.framework.dto.EventoGrupoVotacaoSeloDto;
import br.ufrn.imd.incluevents.framework.dto.GrupoVotacaoSeloDto;
import br.ufrn.imd.incluevents.framework.dto.ValidateVotacaoDto;
import br.ufrn.imd.incluevents.framework.exceptions.BusinessException;
import br.ufrn.imd.incluevents.framework.exceptions.enums.ExceptionTypesEnum;
import br.ufrn.imd.incluevents.framework.model.Selo;
import br.ufrn.imd.incluevents.framework.model.VotacaoSelo;
import br.ufrn.imd.incluevents.framework.model.Usuario;
import br.ufrn.imd.incluevents.framework.model.enums.TipoUsuarioEnum;
import br.ufrn.imd.incluevents.framework.repository.VotacaoSeloRepository;
import br.ufrn.imd.incluevents.framework.service.EstabelecimentoService;
import br.ufrn.imd.incluevents.framework.service.EventoService;
import br.ufrn.imd.incluevents.framework.service.SeloService;
import br.ufrn.imd.incluevents.framework.service.UsuarioService;
import br.ufrn.imd.incluevents.framework.service.VotacaoSeloService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
    public VotacaoSelo create(CreateVotacaoSeloDto createVotacaoSeloDto, Usuario usuario) throws BusinessException {
        System.out.println("Criando Votação Selo Natal");

        return super.create(createVotacaoSeloDto, usuario);
    }

    @Override
    @Transactional
    public void validateVotacao(final ValidateVotacaoDto validateVotacaoDto, Usuario usuario) throws BusinessException {
        if (usuario.getTipo() != TipoUsuarioEnum.PREFEITURA) {
            throw new BusinessException("Você não tem acesso a esse recurso", ExceptionTypesEnum.FORBIDDEN);
        }

        validateVotacaoDto(validateVotacaoDto);

        Selo selo = seloService.getById(validateVotacaoDto.idSelo());

        if (selo.getValidado()) {
            throw new BusinessException("Selo já validado", ExceptionTypesEnum.CONFLICT);
        }

        List<VotacaoSelo> votacoesSelo = votacaoSeloRepository.findBySelo(selo);

        votacoesSelo
                .stream()
                .parallel()
                .forEach(votacaoSelo -> {
                    if (votacaoSelo.getVerificado()) {
                        return;
                    }

                    votacaoSelo.setVerificado(true);
                    processVotacao(votacaoSelo, validateVotacaoDto);

                });

        if (validateVotacaoDto.possuiSelo()) {
            seloService.validateSelo(selo);
        }
    }

    private void processVotacao(VotacaoSelo votacaoSelo, ValidateVotacaoDto validateVotacaoDto) {
        Usuario usuarioVotacao = votacaoSelo.getUsuario();

        int reputacao = usuarioVotacao.getReputacao() + (votacaoSelo.getPossuiSelo() == validateVotacaoDto.possuiSelo() ? 5 : -3);
        UsuarioServiceNatal usuarioServiceNatal = (UsuarioServiceNatal) this.usuarioService;
        usuarioServiceNatal.updateReputacao(usuarioVotacao, reputacao);
    }

    @Override
    public List<EventoGrupoVotacaoSeloDto> getValidacoesPendentesByEvento(Usuario usuario) throws BusinessException {
        if (usuario.getTipo() != TipoUsuarioEnum.PREFEITURA) {
            throw new BusinessException("Você não tem acesso a esse recurso", ExceptionTypesEnum.FORBIDDEN);
        }

        return eventoService
                .findAll()
                .stream()
                .parallel()
                .map(evento -> {
                    List<GrupoVotacaoSeloDto> gruposVotacaoSelo = votacaoSeloRepository.findValidacoesPendentesByEvento(evento.getId());

                    return new EventoGrupoVotacaoSeloDto(evento, gruposVotacaoSelo);
                })
                .filter(item -> {
                    return item.getGruposVotacaoSelo().size() > 0;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<EstabelecimentoGrupoVotacaoSeloDto> getValidacoesPendentesByEstabelecimento(Usuario usuario) throws BusinessException {
        if (usuario.getTipo() != TipoUsuarioEnum.PREFEITURA) {
            throw new BusinessException("Você não tem acesso a esse recurso", ExceptionTypesEnum.FORBIDDEN);
        }
        return estabelecimentoService
                .findAll()
                .stream()
                .parallel()
                .map(estabelecimento -> {
                    List<GrupoVotacaoSeloDto> gruposVotacaoSelo = votacaoSeloRepository.findValidacoesPendentesByEstabelecimento(estabelecimento.getId());

                    return new EstabelecimentoGrupoVotacaoSeloDto(estabelecimento, gruposVotacaoSelo);
                })
                .filter(item -> {
                    return item.getGruposVotacaoSelo().size() > 0;
                })
                .collect(Collectors.toList());
    }
}
