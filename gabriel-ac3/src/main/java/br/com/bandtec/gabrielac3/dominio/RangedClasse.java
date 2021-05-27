package br.com.bandtec.gabrielac3.dominio;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Entity
public class RangedClasse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Column(length = 10)
    private String nome;

    @PositiveOrZero()
    @Max(99)
    private Integer conhecimento;

    @PositiveOrZero
    @Max(99)
    private Integer inteligencia;

    @PositiveOrZero
    @Max(99)
    private Integer fe;

    @NotBlank
    @Column(length = 17)
    private String canalizador;

    @ManyToOne
    private TipoMagia tipo;

    @PositiveOrZero
    private Double soulLevel;

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

    public Integer getConhecimento() {
        return conhecimento;
    }

    public void setConhecimento(Integer conhecimento) {
        this.conhecimento = conhecimento;
    }

    public Integer getInteligencia() {
        return inteligencia;
    }

    public void setInteligencia(Integer inteligencia) {
        this.inteligencia = inteligencia;
    }

    public Integer getFe() {
        return fe;
    }

    public void setFe(Integer fe) {
        this.fe = fe;
    }

    public String getCanalizador() {
        return canalizador;
    }

    public void setCanalizador(String canalizador) {
        this.canalizador = canalizador;
    }

    public TipoMagia getTipo() {
        return tipo;
    }

    public void setTipoMagia(TipoMagia tipo) {
        this.tipo = tipo;
    }

    public Double getSoulLevel() {
        return soulLevel;
    }

    public void setSoulLevel(Double soulLevel) {
        this.soulLevel = soulLevel;
    }
}
