package com.marcosevaristo.trackusregister.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Linha implements Serializable {
    private Municipio municipio;
    private String numero;
    private String titulo;
    private String subtitulo;
    private boolean ehFavorito = false;

    private static final long serialVersionUID = 1L;

    public Linha() {
    }

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

    public static List<Linha> converteMapParaListaLinhas(Map<String, Object> lMapLinhas) {
        List<Linha> lLinhas = new ArrayList<>();
        Linha linhaAux = new Linha();
        boolean resultadoUnico = false;
        for(String umaKeyAux : lMapLinhas.keySet()) {
            if(lMapLinhas.get(umaKeyAux) instanceof Map && !umaKeyAux.equals("carros")) {
                Map<String, Object> umaLinha = (Map<String, Object>) lMapLinhas.get(umaKeyAux);
                linhaAux = new Linha();
                for(String umAttr : umaLinha.keySet()) {
                    montaUmAtributoDaLinha(linhaAux, umAttr, umaLinha.get(umAttr));
                }
                lLinhas.add(linhaAux);
            } else {
                resultadoUnico = true;
                montaUmAtributoDaLinha(linhaAux, umaKeyAux, lMapLinhas.get(umaKeyAux));
            }
        }
        if(resultadoUnico) lLinhas.add(linhaAux);
        return lLinhas;
    }

    private static void montaUmAtributoDaLinha(Linha linhaAux, String atributo, Object valor) {
        switch(atributo) {
            case "numero":
                linhaAux.setNumero(valor.toString());
                break;
            case "titulo":
                linhaAux.setTitulo(valor.toString());
                break;
            case "subtitulo":
                linhaAux.setSubtitulo(valor.toString());
                break;
            default:
                break;
        }
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

    public Municipio getMunicipio() {
        return municipio;
    }

    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }
}
