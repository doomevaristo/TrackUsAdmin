package com.marcosevaristo.trackusregister.enums;

public enum TipoCampoBusca {
    NUMERO("numero"),
    TITULO("titulo");

    private String campo;

    TipoCampoBusca(String campo) {
        this.campo = campo;
    }

    public String getCampo() {
        return this.campo;
    }
}
