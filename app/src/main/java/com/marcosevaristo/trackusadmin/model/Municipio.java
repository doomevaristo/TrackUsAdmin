package com.marcosevaristo.trackusadmin.model;

import android.support.annotation.TransitionRes;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Municipio implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String nome;
    private List<Linha> linhas;

    public Municipio(){}

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public List<Linha> getLinhas() {
        return linhas;
    }
    public void setLinhas(List<Linha> linhas) {
        this.linhas = linhas;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.id).append(" - ").append(this.nome);
        return sb.toString();
    }

    public static List<Municipio> converteListMapParaListaMunicipios(List<Map<String, Object>> mapValues) {
        List<Municipio> lMunicipios = new ArrayList<>();
        Long idAux = null;
        String nomeAux = null;
        List<Linha> listLinhas = null;
        Municipio municipioAux;

        for(Map<String, Object> umMunicipio : mapValues) {
            if(umMunicipio != null) {
                for(String umAtributoMun : umMunicipio.keySet()) {
                    switch(umAtributoMun) {
                        case "id":
                            idAux = (Long) umMunicipio.get(umAtributoMun);
                            break;
                        case "nome":
                            nomeAux = umMunicipio.get(umAtributoMun).toString();
                            break;
                        case "linhas":
                            listLinhas = Linha.converteMapParaListaLinhas((Map)umMunicipio.get(umAtributoMun));
                        default:
                            break;
                    }
                }
                municipioAux = new Municipio();
                municipioAux.setId(idAux);
                municipioAux.setNome(nomeAux);
                municipioAux.setLinhas(listLinhas);
                lMunicipios.add(municipioAux);
            }
        }

        return lMunicipios;
    }
}