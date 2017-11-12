package com.marcosevaristo.trackusregister.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.marcosevaristo.trackusregister.App;
import com.marcosevaristo.trackusregister.R;
import com.marcosevaristo.trackusregister.database.firebase.FirebaseUtils;
import com.marcosevaristo.trackusregister.model.Municipio;

public class CadastroMunicipioActivity extends AppCompatActivity implements Crud, View.OnClickListener{

    private Boolean isFabOpen = false;
    private FloatingActionButton fabMenu,fabAdd,fabDel;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;

    private Toolbar toolbar;
    private Municipio municipio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_municipio);
        setupToolbar();
        setupFloatingActionButtons();
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            edita(bundle);
        } else {
            novo();
        }
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public void novo() {
        municipio = new Municipio();
        setaMunicipioEmTela(municipio);
    }

    @Override
    public void edita(Bundle bundle) {
        municipio = (Municipio) bundle.get("municipio");
        setaMunicipioEmTela(municipio);

    }

    @Override
    public void exclui() {
        FirebaseUtils.getMunicipiosReference().child(municipio.getId().toString()).getRef().removeValue();
        Toast.makeText(App.getAppContext(), App.getAppContext().getString(R.string.municipio_excluido_sucesso, municipio.getId().toString()), Toast.LENGTH_SHORT).show();
        novo();
    }

    @Override
    public void limpa() {

    }

    @Override
    public void salva() {

    }

    private void setaMunicipioEmTela(Municipio municipio) {
        EditText textViewID = (EditText) findViewById(R.id.textViewID);
        TextInputEditText etNome = (TextInputEditText) findViewById(R.id.etNomeMunicipio);

        textViewID.setText(municipio != null && municipio.getId() != null ? municipio.getId().toString() : null);
        etNome.setText(municipio != null ? municipio.getNome() : null);
    }

    private void setupFloatingActionButtons() {
        fabMenu = (FloatingActionButton)findViewById(R.id.fab_menu_municipio);
        fabAdd = (FloatingActionButton)findViewById(R.id.fab_add_municipio);
        fabDel = (FloatingActionButton)findViewById(R.id.fab_del_municipio);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);

        fabMenu.setOnClickListener(this);
        fabAdd.setOnClickListener(this);
        fabDel.setOnClickListener(this);
    }

    public void animateFAB(){

        if(isFabOpen){

            fabMenu.startAnimation(rotate_backward);
            fabAdd.startAnimation(fab_close);
            fabDel.startAnimation(fab_close);
            fabAdd.setClickable(false);
            fabDel.setClickable(false);
            isFabOpen = false;

        } else {

            fabMenu.startAnimation(rotate_forward);
            fabAdd.startAnimation(fab_open);
            fabDel.startAnimation(fab_open);
            fabAdd.setClickable(true);
            fabDel.setClickable(true);
            isFabOpen = true;

        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.fab_menu_municipio:

                animateFAB();
                break;
            case R.id.fab_add_municipio:

                Log.d("Raj", "Fab 1");
                break;
            case R.id.fab_del_municipio:

                Log.d("Raj", "Fab 2");
                break;
        }
    }
}
