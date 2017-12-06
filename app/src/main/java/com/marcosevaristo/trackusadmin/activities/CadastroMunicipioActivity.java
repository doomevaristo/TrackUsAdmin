package com.marcosevaristo.trackusadmin.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.marcosevaristo.trackusadmin.app.App;
import com.marcosevaristo.trackusadmin.R;
import com.marcosevaristo.trackusadmin.utils.FirebaseUtils;
import com.marcosevaristo.trackusadmin.model.Municipio;
import com.marcosevaristo.trackusadmin.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class CadastroMunicipioActivity extends AppCompatActivity implements ICrud, View.OnClickListener{

    private Boolean isFabOpen = false;
    private FloatingActionButton fabMenu,fabAdd,fabSave,fabDel,fabLinhas;
    private TextView labelFabAdd, labelFabSave, labelFabDel, labelFabListLinhas;
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
        FirebaseUtils.getMunicipiosReference(municipio.getId()).removeValue();
        App.toast(R.string.municipio_excluido_sucesso, municipio.getNome());
        novo();
    }

    @Override
    public void salva() {
        if(setupTelaNoMunicipio()) {
            DatabaseReference referenciaFirebase = FirebaseUtils.getMunicipiosReference(municipio.getId());
            if(municipio.getId() == null) {
                referenciaFirebase = referenciaFirebase.push();
                municipio.setId(referenciaFirebase.getKey());
                referenciaFirebase.setValue(municipio);
            } else {
                Map<String, Object> mapValues = new HashMap<>();
                mapValues.put("nome", municipio.getNome());
                referenciaFirebase.updateChildren(mapValues);
            }
            App.toast(R.string.municipio_salvo_sucesso, municipio.toString());
        }
    }

    private boolean setupTelaNoMunicipio() {
        if(StringUtils.isNotBlank(etNome.getText().toString())) {
            municipio.setNome(etNome.getText().toString());
        } else {
            App.toast(R.string.campo_x_obrigatorio, "Nome");
            return false;
        }
        return true;
    }

    private void setaMunicipioEmTela(Municipio municipio) {
        etNome = (TextInputEditText) findViewById(R.id.etNomeMunicipio);
        etNome.setText(municipio != null ? municipio.getNome() : null);
        etNome.requestFocus();
    }

    private void setupFloatingActionButtons() {
        fabMenu = (FloatingActionButton)findViewById(R.id.fab_menu);
        fabAdd = (FloatingActionButton)findViewById(R.id.fab_add);
        fabDel = (FloatingActionButton)findViewById(R.id.fab_del);
        fabSave = (FloatingActionButton)findViewById(R.id.fab_save);
        fabLinhas = (FloatingActionButton)findViewById(R.id.fab_linhas_municipio);

        labelFabAdd = (TextView) findViewById(R.id.labelFabAdd);
        labelFabSave = (TextView) findViewById(R.id.labelFabSave);
        labelFabDel = (TextView) findViewById(R.id.labelFabDel);
        labelFabListLinhas = (TextView) findViewById(R.id.labelFabListLinhas);

        fab_open = AnimationUtils.loadAnimation(App.getAppContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(App.getAppContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(App.getAppContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(App.getAppContext(),R.anim.rotate_backward);

        fabMenu.setOnClickListener(this);
        fabAdd.setOnClickListener(this);
        fabSave.setOnClickListener(this);
        fabDel.setOnClickListener(this);
        fabLinhas.setOnClickListener(this);
        labelFabAdd.setOnClickListener(this);
        labelFabSave.setOnClickListener(this);
        labelFabDel.setOnClickListener(this);
        labelFabListLinhas.setOnClickListener(this);
    }

    public void animateFAB(){
        if(isFabOpen){
            fabMenu.startAnimation(rotate_backward);

            fabAdd.startAnimation(fab_close);
            labelFabAdd.startAnimation(fab_close);

            fabSave.startAnimation(fab_close);
            labelFabSave.startAnimation(fab_close);
            if(municipio != null && StringUtils.isNotBlank(municipio.getId())) {
                fabDel.startAnimation(fab_close);
                labelFabDel.startAnimation(fab_close);

                fabLinhas.startAnimation(fab_close);
                labelFabListLinhas.startAnimation(fab_close);
            }

            fabAdd.setClickable(false);
            labelFabAdd.setClickable(false);

            fabSave.setClickable(false);
            labelFabSave.setClickable(false);
            if(municipio != null && StringUtils.isNotBlank(municipio.getId())) {
                fabDel.setClickable(false);
                labelFabDel.setClickable(false);

                fabLinhas.setClickable(false);
                labelFabListLinhas.setClickable(false);
            }
            isFabOpen = false;
        } else {
            fabMenu.startAnimation(rotate_forward);

            fabAdd.startAnimation(fab_open);
            labelFabAdd.startAnimation(fab_open);

            fabSave.startAnimation(fab_open);
            labelFabSave.startAnimation(fab_open);
            if(municipio != null && StringUtils.isNotBlank(municipio.getId())) {
                fabDel.startAnimation(fab_open);
                labelFabDel.startAnimation(fab_open);

                fabLinhas.startAnimation(fab_open);
                labelFabListLinhas.startAnimation(fab_open);
            }

            fabAdd.setClickable(true);
            labelFabAdd.setClickable(true);

            fabSave.setClickable(true);
            labelFabSave.setClickable(true);
            if(municipio != null && StringUtils.isNotBlank(municipio.getId())) {
                fabDel.setClickable(true);
                labelFabDel.setClickable(true);

                fabLinhas.setClickable(true);
                labelFabListLinhas.setClickable(true);
            }

            isFabOpen = true;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        animateFAB();
        switch (id){
            case R.id.fab_menu:
                break;
            case R.id.fab_add:
            case R.id.labelFabAdd:
                novo();
                break;
            case R.id.fab_save:
            case R.id.labelFabSave:
                salva();
                break;
            case R.id.fab_del:
            case R.id.labelFabDel:
                new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialog))
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(R.string.confirm_delete_dialog_title)
                        .setMessage(R.string.confirm_delete_dialog_content)
                        .setPositiveButton(R.string.sim, new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                exclui();
                            }

                        })
                        .setNegativeButton(R.string.nao, null)
                        .show();
                break;
            case R.id.fab_linhas_municipio:
            case R.id.labelFabListLinhas:
                Intent intent = new Intent(App.getAppContext(), ConsultaLinhasActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("municipio", municipio);
                intent.putExtras(bundle);
                startActivity(intent);
                break;
        }
    }
}
