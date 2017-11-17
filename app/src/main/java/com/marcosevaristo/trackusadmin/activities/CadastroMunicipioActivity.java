package com.marcosevaristo.trackusadmin.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.marcosevaristo.trackusadmin.App;
import com.marcosevaristo.trackusadmin.R;
import com.marcosevaristo.trackusadmin.database.firebase.FirebaseUtils;
import com.marcosevaristo.trackusadmin.model.Municipio;

public class CadastroMunicipioActivity extends AppCompatActivity implements Crud, View.OnClickListener{

    private Boolean isFabOpen = false;
    private FloatingActionButton fabMenu,fabAdd,fabSave,fabDel,fabClone,fabLinhas;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private TextInputEditText etNome;

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
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
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
        FirebaseUtils.getMunicipiosReference(municipio.getId()).getRef().removeValue();
        Toast.makeText(App.getAppContext(), App.getAppContext().getString(R.string.municipio_excluido_sucesso, municipio.getId().toString()), Toast.LENGTH_SHORT).show();
        novo();
    }

    @Override
    public void salva() {
        setupTelaNoMunicipio();
        DatabaseReference referenciaFirebase = FirebaseUtils.getMunicipiosReference(municipio.getId());
        if(municipio.getId() == null) {
            referenciaFirebase = referenciaFirebase.push();
            municipio.setId(referenciaFirebase.getKey());
            referenciaFirebase.setValue(municipio);
        } else {
            referenciaFirebase.setValue(municipio);
        }

        App.toast(R.string.municipio_salvo_sucesso, municipio.toString());
    }

    private void setupTelaNoMunicipio() {
        municipio.setNome(etNome.getText().toString());
    }

    private void setaMunicipioEmTela(Municipio municipio) {
        etNome = (TextInputEditText) findViewById(R.id.etNomeMunicipio);
        etNome.setText(municipio != null ? municipio.getNome() : null);
    }

    private void setupFloatingActionButtons() {
        fabMenu = (FloatingActionButton)findViewById(R.id.fab_menu);
        fabAdd = (FloatingActionButton)findViewById(R.id.fab_add);
        fabDel = (FloatingActionButton)findViewById(R.id.fab_del);
        fabSave = (FloatingActionButton)findViewById(R.id.fab_save);
        fabClone = (FloatingActionButton)findViewById(R.id.fab_clone);
        fabLinhas = (FloatingActionButton)findViewById(R.id.fab_linhas_municipio);

        fab_open = AnimationUtils.loadAnimation(App.getAppContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(App.getAppContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(App.getAppContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(App.getAppContext(),R.anim.rotate_backward);

        fabMenu.setOnClickListener(this);
        fabAdd.setOnClickListener(this);
        fabSave.setOnClickListener(this);
        fabDel.setOnClickListener(this);
        fabClone.setOnClickListener(this);
        fabLinhas.setOnClickListener(this);
    }

    public void animateFAB(){
        if(isFabOpen){
            fabMenu.startAnimation(rotate_backward);
            fabAdd.startAnimation(fab_close);
            fabSave.startAnimation(fab_close);
            fabDel.startAnimation(fab_close);
            fabClone.startAnimation(fab_close);
            fabLinhas.startAnimation(fab_close);

            fabAdd.setClickable(false);
            fabSave.setClickable(false);
            fabDel.setClickable(false);
            fabClone.setClickable(false);
            fabLinhas.setClickable(false);
            isFabOpen = false;
        } else {
            fabMenu.startAnimation(rotate_forward);
            fabAdd.startAnimation(fab_open);
            fabSave.startAnimation(fab_open);
            fabClone.startAnimation(fab_open);
            if(municipio != null && municipio.getId() != null) {
                fabDel.startAnimation(fab_open);
                fabLinhas.startAnimation(fab_open);
            }

            fabAdd.setClickable(true);
            fabSave.setClickable(true);
            fabClone.setClickable(true);
            if(municipio != null && municipio.getId() != null) {
                fabDel.setClickable(true);
                fabLinhas.setClickable(true);
            }

            isFabOpen = true;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.fab_menu:
                break;
            case R.id.fab_add:
                novo();
                break;
            case R.id.fab_save:
                salva();
                break;
            case R.id.fab_del:
                exclui();
                break;
            case R.id.fab_linhas_municipio:
                Intent intent = new Intent(App.getAppContext(), ConsultaLinhasActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("municipio", municipio);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            case R.id.fab_clone:
                //TODO:clonar
                break;
        }
        animateFAB();
    }
}
