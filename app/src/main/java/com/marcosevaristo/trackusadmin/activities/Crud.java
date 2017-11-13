package com.marcosevaristo.trackusadmin.activities;


import android.os.Bundle;

public interface Crud {

    void novo();
    void edita(Bundle bundle);
    void exclui();
    void salva();
}
