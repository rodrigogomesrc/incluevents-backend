package br.ufrn.imd.incluevents.framework.model;

import br.ufrn.imd.incluevents.framework.model.enums.OrigemEventoEnum;
import jakarta.persistence.*;

import java.util.Date;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "evento")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String nome;
    private String local;
    @Column(length = 3000)
    private String descricao;
    @Column(length = 1000)
    private String urlOriginal;
    @Column(length = 1000)
    private String imagemUrl;
    private Date inicio;
    private Date fim;
    private OrigemEventoEnum origem;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "evento_categoria",
            joinColumns = @JoinColumn(name = "evento_id"),
            inverseJoinColumns = @JoinColumn(name = "categoria_id"))
    private Set<Categoria> categorias;

    @JsonIgnore
    @OneToMany(mappedBy = "evento")
    private Set<Selo> selos;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_estabelecimento")
    private Estabelecimento estabelecimento;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_criador")
    private Usuario criador;

    @OneToMany(mappedBy = "evento", fetch = FetchType.EAGER)
    private Set<Feedback> feedbacks;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFim() {
        return fim;
    }

    public void setFim(Date fim) {
        this.fim = fim;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getUrlOriginal() {
        return urlOriginal;
    }

    public void setUrlOriginal(String urlOriginal) {
        this.urlOriginal = urlOriginal;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }

    public OrigemEventoEnum getOrigem() {
        return origem;
    }

    public void setOrigem(OrigemEventoEnum origem) {
        this.origem = origem;
    }

    public Set<Categoria> getCategorias() {
        return categorias;
    }

    public void setCategorias(Set<Categoria> categorias) {
        this.categorias = categorias;
    }

    public void addSelo(Selo selo) {
        this.selos.add(selo);
    }

    public void removeSelo(Selo selo) {
        this.selos.remove(selo);
    }

    public Set<Selo> getSelos() {
        return selos;
    }

    public void setSelos(Set<Selo> selos) {
        this.selos = selos;
    }

    public Estabelecimento getEstabelecimento() {
        return estabelecimento;
    }

    public void setEstabelecimento(Estabelecimento estabelecimento) {
        this.estabelecimento = estabelecimento;
    }

    public Usuario getCriador() {
        return criador;
    }

    public void setCriador(Usuario criador) {
        this.criador = criador;
    }

    public Set<Feedback> getFeedbacks() {
        return feedbacks;
    }

    public void setFeedbacks(Set<Feedback> feedbacks) {
        this.feedbacks = feedbacks;
    }

    @Override
    public String toString() {
        return "Evento{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                ", urlOriginal='" + urlOriginal + '\'' +
                ", imagemUrl='" + imagemUrl + '\'' +
                ", inicio=" + inicio +
                ", fim=" + fim +
                ", origem=" + origem +
                '}';
    }
}
