package br.com.bandtec.gabrielac3.dominio;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
public class TipoMagia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Column(length = 20)
    private String nome;

    public Integer getId() { return id; }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String toString() {
        return "nome: " + nome;
    }
}
