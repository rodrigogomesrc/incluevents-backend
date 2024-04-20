package br.ufrn.imd.incluevents.model;

import jakarta.persistence.*;

import java.util.Set;

@Entity
@Table(name = "estabelecimento")
public class Estabelecimento {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String nome;
    private String endereco;
    private String telefone;

    @OneToMany(mappedBy = "estabelecimento")
    private Set<Evento> eventos;

    @OneToMany
    private Set<Selo> selos;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Set<Evento> getEventos() {
        return eventos;
    }

    public void setEventos(Set<Evento> eventos) {
        this.eventos = eventos;
    }

    public Set<Selo> getSelos() {
        return selos;
    }

    public void setSelos(Set<Selo> selos) {
        this.selos = selos;
    }
}
