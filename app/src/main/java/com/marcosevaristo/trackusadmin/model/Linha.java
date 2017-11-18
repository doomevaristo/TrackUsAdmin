package com.marcosevaristo.trackusadmin.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Linha implements Serializable {

    @Exclude
    private Municipio municipio;
    private String id;
    private String numero;
    private String titulo;
    private String subtitulo;
    private List<String> rota;

    private static final long serialVersionUID = 1L;

    public Linha() {}

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSubtitulo() {
        return subtitulo;
    }

    public void setSubtitulo(String subtitulo) {
        this.subtitulo = subtitulo;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.numero);
        sb.append(" - ");
        sb.append(this.titulo);
        if(this.subtitulo != null) {
            sb.append(" - ");
            sb.append(this.subtitulo);
        }
        return sb.toString();
    }

    @Exclude
    public Municipio getMunicipio() {
        return municipio;
    }

    @Exclude
    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getRota() {
        return rota;
    }

    public void setRota(List<String> rota) {
        this.rota = rota;
    }
}
