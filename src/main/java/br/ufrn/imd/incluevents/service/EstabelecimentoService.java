package br.ufrn.imd.incluevents.service;

import br.ufrn.imd.incluevents.exceptions.EstabelecimentoNotFoundException;
import br.ufrn.imd.incluevents.exceptions.SeloNotFoundException;
import br.ufrn.imd.incluevents.model.Estabelecimento;
import br.ufrn.imd.incluevents.model.Selo;
import br.ufrn.imd.incluevents.repository.EstabelecimentoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EstabelecimentoService {

    private final EstabelecimentoRepository estabelecimentoRepository;
    private final SeloService seloService;

    public EstabelecimentoService(EstabelecimentoRepository estabelecimentoRepository, SeloService seloService) {
        this.estabelecimentoRepository = estabelecimentoRepository;
        this.seloService = seloService;
    }

    public Estabelecimento createEstabelecimento(Estabelecimento estabelecimento) {
        return estabelecimentoRepository.save(estabelecimento);
    }

    public Optional<Estabelecimento> getEstabelecimentoById(int id) {
        return estabelecimentoRepository.findById(id);
    }

    public Estabelecimento addSeloToEstabelecimento(int estabelecimentoId, int seloId)
            throws EstabelecimentoNotFoundException, SeloNotFoundException {
        Optional<Estabelecimento> estabelecimentoOptional = estabelecimentoRepository.findById(estabelecimentoId);
        if (estabelecimentoOptional.isEmpty()) {
            throw new EstabelecimentoNotFoundException();
        }

        Selo selo = seloService.getById(seloId);
        Estabelecimento estabelecimento = estabelecimentoOptional.get();
        estabelecimento.getSelos().add(selo);
        return estabelecimentoRepository.save(estabelecimento);
    }

}
