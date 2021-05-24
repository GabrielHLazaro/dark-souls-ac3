package br.com.bandtec.gabrielac3.dominio;

public class ResultadoRequisicao {

    private Integer protocolo;

    private Object resultadoRequisicao;

    public ResultadoRequisicao(Integer protocolo, Object resultadoRequisicao) {
        this.protocolo = protocolo;
        this.resultadoRequisicao = resultadoRequisicao;
    }

    public Integer getProtocolo() {
        return protocolo;
    }

    public void setProtocolo(Integer protocolo) {
        this.protocolo = protocolo;
    }

    public Object getResultadoRequisicao() {
        return resultadoRequisicao;
    }

    public void setResultadoRequisicao(Object resultadoRequisicao) {
        this.resultadoRequisicao = resultadoRequisicao;
    }
}
