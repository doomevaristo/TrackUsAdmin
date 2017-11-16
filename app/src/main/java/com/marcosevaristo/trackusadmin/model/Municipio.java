package com.marcosevaristo.trackusadmin.model;

import android.support.annotation.TransitionRes;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Municipio implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String nome;
    private List<Linha> linhas;

    public Municipio(){}

    public String getId() {
        return id;
    }
    public void setId(String id) {
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

    public static List<Municipio> converteListMapParaListaMunicipios(Map<String, Object> mapValues) {
        List<Municipio> lMunicipios = new ArrayList<>();
        String idAux = null;
        String nomeAux = null;
        List<Linha> listLinhas = null;
        Municipio municipioAux;

        for(String umMunicipioID : mapValues.keySet()) {
            Map<String, Object> umMunicipioMap = (Map<String, Object>) mapValues.get(umMunicipioID);
            for(String umAtributoMun : umMunicipioMap.keySet()) {
                switch(umAtributoMun) {
                    case "id":
                        idAux = umMunicipioMap.get(umAtributoMun).toString();
                        break;
                    case "nome":
                        nomeAux = umMunicipioMap.get(umAtributoMun).toString();
                        break;
                    case "linhas":
                        listLinhas = Linha.converteMapParaListaLinhas((Map)umMunicipioMap.get(umAtributoMun));
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

        return lMunicipios;
    }
}
