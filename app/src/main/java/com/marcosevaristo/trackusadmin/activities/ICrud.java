package com.marcosevaristo.trackusadmin.activities;


import android.os.Bundle;

public interface ICrud {

    void novo();
    void edita(Bundle bundle);
    void exclui();
    void salva();
}
