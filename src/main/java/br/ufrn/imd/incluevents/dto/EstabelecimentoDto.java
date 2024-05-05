package br.ufrn.imd.incluevents.dto;

import br.ufrn.imd.incluevents.model.Selo;

import java.util.Set;

public record EstabelecimentoDto(int id, String nome, String endereco, String telefone, Set<Selo> selos) {
}
