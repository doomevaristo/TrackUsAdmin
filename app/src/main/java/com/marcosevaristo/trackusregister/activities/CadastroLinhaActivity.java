package com.marcosevaristo.trackusregister.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.marcosevaristo.trackusregister.App;
import com.marcosevaristo.trackusregister.R;
import com.marcosevaristo.trackusregister.database.firebase.FirebaseUtils;
import com.marcosevaristo.trackusregister.model.Linha;
import com.marcosevaristo.trackusregister.model.Municipio;

public class CadastroLinhaActivity extends AppCompatActivity implements Crud, View.OnClickListener, AdapterView.OnItemSelectedListener, OnMapReadyCallback {
    private Boolean isFabOpen = false;
    private FloatingActionButton fabMenu,fabAdd,fabDel;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private GoogleMap gMap;

    private Toolbar toolbar;
    private Linha linha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_linha);

        setupToolbar();
        setupFloatingActionButtons();
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            edita(bundle);
        } else {
            novo();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void setupSpinnerMunicipios() {
        Spinner spinnerMunicipios = (Spinner) findViewById(R.id.spinnerMunicipioLinha);
        spinnerMunicipios.setOnItemSelectedListener(this);

    }

    private void setupLinhaNaTela(){
        TextInputEditText etNumero = (TextInputEditText) findViewById(R.id.etNumeroLinha);
        TextInputEditText etTituloLinha = (TextInputEditText) findViewById(R.id.etTituloLinha);
        TextInputEditText etSubtituloLinha = (TextInputEditText) findViewById(R.id.etSubtituloLinha);

        etNumero.setText(linha != null ? linha.getNumero() : null);
        etTituloLinha.setText(linha != null ? linha.getTitulo() : null);
        etSubtituloLinha.setText(linha != null ? linha.getSubtitulo() : null);
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupFloatingActionButtons() {
        fabMenu = (FloatingActionButton)findViewById(R.id.fab_menu_linha);
        fabAdd = (FloatingActionButton)findViewById(R.id.fab_add_linha);
        fabDel = (FloatingActionButton)findViewById(R.id.fab_del_linha);

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
            case R.id.fab_menu_linha:
                animateFAB();
                break;
            case R.id.fab_add_linha:
                novo();

                break;
            case R.id.fab_del_linha:
                //TODO: pedir confirmação
                exclui();
                break;
        }
    }

    @Override
    public void novo() {
        linha = new Linha();
    }

    @Override
    public void edita(Bundle bundle) {
        linha = (Linha) bundle.get("linha");
        setupLinhaNaTela();
    }

    @Override
    public void exclui() {
        FirebaseUtils.getLinhasReference().child(linha.getNumero()).getRef().removeValue();
        Toast.makeText(App.getAppContext(), App.getAppContext().getString(R.string.linha_excluida_sucesso,
                linha.getNumero()+" - "+linha.getTitulo()), Toast.LENGTH_SHORT).show();
        novo();
    }

    @Override
    public void limpa() {

    }

    @Override
    public void salva() {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        linha.setMunicipio((Municipio) parent.getItemAtPosition(position));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        linha.setMunicipio(null);
    }
}
