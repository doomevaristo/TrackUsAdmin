package com.marcosevaristo.trackusregister.activities;


import android.os.Bundle;

public interface Crud {

    void novo();
    void edita(Bundle bundle);
    void exclui();
    void limpa();
    void salva();
}
