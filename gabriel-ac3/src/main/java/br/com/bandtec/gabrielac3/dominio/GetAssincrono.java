package br.com.bandtec.gabrielac3.dominio;

public class GetAssincrono {
    private Integer protocolo;
    private Integer idClasse;

    public GetAssincrono(Integer protocolo, Integer idClasse) {
        this.protocolo = protocolo;
        this.idClasse = idClasse;
    }

    public Integer getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(Integer protocolo) {
        this.protocolo = protocolo;
    }

    public Integer getIdClasse() {
        return idClasse;
    }

    public void setIdClasse(Integer idClasse) {
        this.idClasse = idClasse;
    }
}
