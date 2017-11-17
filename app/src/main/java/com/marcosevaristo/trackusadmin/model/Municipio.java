package com.marcosevaristo.trackusadmin.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Municipio implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String nome;
    private Map<String, Linha> linhas;

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
    public Map<String, Linha> getLinhas() {
        return linhas;
    }
    public void setLinhas(Map<String, Linha> linhas) {
        this.linhas = linhas;
    }

    @Override
    public String toString() {
        return this.nome;
    }
}
