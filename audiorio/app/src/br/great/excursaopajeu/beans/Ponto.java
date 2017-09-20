package br.great.excursaopajeu.beans;

import com.google.android.gms.maps.model.LatLng;

public class Ponto {

    private String nome;
    private String descricao;
    private LatLng localizacao;

    public Ponto(String nome, LatLng localizacao) {
        this.nome = nome;
        this.descricao = descricao;
        this.localizacao = localizacao;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LatLng getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(LatLng localizacao) {
        this.localizacao = localizacao;
    }
}
