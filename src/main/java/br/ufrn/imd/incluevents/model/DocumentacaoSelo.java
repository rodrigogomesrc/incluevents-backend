package br.ufrn.imd.incluevents.model;

import jakarta.persistence.*;

@Entity
@Table(name = "documentacao_selo")
public class DocumentacaoSelo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;
    private String nomeArquivo;
	private String urlArquivo;
    private Boolean valida;


    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_selo")
    private Selo selo;

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNomeArquivo() {
		return this.nomeArquivo;
	}

	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}

	public String getUrlArquivo() {
		return urlArquivo;
	}

	public void setUrlArquivo(String urlArquivo) {
		this.urlArquivo = urlArquivo;
	}

	public Usuario getUsuario() {
		return this.usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public Selo getSelo() {
		return this.selo;
	}

	public void setSelo(Selo selo) {
		this.selo = selo;
	}

    public Boolean getValida() {
        return valida;
    }

    public void setValida(Boolean valida) {
        this.valida = valida;
    }
}
