package com.marcosevaristo.trackusadmin.activities;

import android.app.ProgressDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.marcosevaristo.trackusadmin.App;
import com.marcosevaristo.trackusadmin.R;
import com.marcosevaristo.trackusadmin.database.firebase.FirebaseUtils;
import com.marcosevaristo.trackusadmin.model.Linha;
import com.marcosevaristo.trackusadmin.model.Municipio;
import com.marcosevaristo.trackusadmin.utils.CollectionUtils;
import com.marcosevaristo.trackusadmin.utils.GoogleMapsUtils;
import com.marcosevaristo.trackusadmin.utils.MapDirectionsParser;
import com.marcosevaristo.trackusadmin.utils.StringUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CadastroLinhaActivity extends AppCompatActivity implements Crud, View.OnClickListener, OnMapReadyCallback {
    private Boolean isFabOpen = false;
    private TextInputEditText etNumero, etTituloLinha, etSubtituloLinha;
    private FloatingActionButton fabMenu,fabAdd,fabSave,fabDel,fabClr,fabClone;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private GoogleMap gMap;

    private List<Marker> markers;
    private ArrayList<LatLng> rota;
    private ProgressDialog progressDialog;

    private Municipio municipio;
    private Linha linha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_linha);
        setupToolbar();
        setupFloatingActionButtons();
        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            municipio = (Municipio) bundle.get("municipio");
        }

        if(bundle != null && bundle.get("linha") != null) {
            edita(bundle);
        } else {
            novo();
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void setupLinhaNaTela(){
        if(etNumero == null) etNumero = (TextInputEditText) findViewById(R.id.etNumeroLinha);
        if(etTituloLinha == null) etTituloLinha = (TextInputEditText) findViewById(R.id.etTituloLinha);
        if(etSubtituloLinha == null) etSubtituloLinha = (TextInputEditText) findViewById(R.id.etSubtituloLinha);

        etNumero.setText(linha != null ? linha.getNumero() : null);
        etTituloLinha.setText(linha != null ? linha.getTitulo() : null);
        etSubtituloLinha.setText(linha != null ? linha.getSubtitulo() : null);
        if(CollectionUtils.isNotEmpty(linha.getRota())) {
            rota = (ArrayList<LatLng>) GoogleMapsUtils.getListLatLngFromListString(linha.getRota());
        } else {
            clearMap();
        }

        etNumero.requestFocus();
    }

    private void setupToolbar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
    }

    private void setupFloatingActionButtons() {
        fabMenu = (FloatingActionButton)findViewById(R.id.fab_menu);
        fabAdd = (FloatingActionButton)findViewById(R.id.fab_add);
        fabDel = (FloatingActionButton)findViewById(R.id.fab_del);
        fabSave = (FloatingActionButton)findViewById(R.id.fab_save);
        fabClr = (FloatingActionButton)findViewById(R.id.fab_clear);
        fabClone = (FloatingActionButton)findViewById(R.id.fab_clone);

        fab_open = AnimationUtils.loadAnimation(App.getAppContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(App.getAppContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(App.getAppContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(App.getAppContext(),R.anim.rotate_backward);

        fabMenu.setOnClickListener(this);
        fabAdd.setOnClickListener(this);
        fabSave.setOnClickListener(this);
        fabDel.setOnClickListener(this);
        fabClr.setOnClickListener(this);
        fabClone.setOnClickListener(this);
    }

    public void animateFAB(){
        if(isFabOpen){
            fabMenu.startAnimation(rotate_backward);
            fabAdd.startAnimation(fab_close);
            fabSave.startAnimation(fab_close);
            if(linha != null && StringUtils.isNotBlank(linha.getId())) {
                fabDel.startAnimation(fab_close);
                fabClr.startAnimation(fab_close);
                fabClone.startAnimation(fab_close);
            }

            fabAdd.setClickable(false);
            fabSave.setClickable(false);
            if(linha != null && StringUtils.isNotBlank(linha.getId())) {
                fabDel.setClickable(false);
                fabClr.setClickable(false);
                fabClone.setClickable(false);
            }
            isFabOpen = false;
        } else {
            fabMenu.startAnimation(rotate_forward);
            fabAdd.startAnimation(fab_open);
            fabSave.startAnimation(fab_open);
            if(linha != null && StringUtils.isNotBlank(linha.getId())) {
                fabDel.startAnimation(fab_open);
                fabClr.startAnimation(fab_open);
                fabClone.startAnimation(fab_open);
            }

            fabAdd.setClickable(true);
            fabSave.setClickable(true);
            if(linha != null && StringUtils.isNotBlank(linha.getId())) {
                fabDel.setClickable(true);
                fabClr.setClickable(true);
                fabClone.setClickable(true);
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
                novo();
                break;
            case R.id.fab_save:
                salva();
                break;
            case R.id.fab_del:
                exclui();
                break;
            case R.id.fab_clear:
                clearMap();
                break;
            case R.id.fab_clone:
                //TODO: clonar
                break;
        }
    }

    @Override
    public void novo() {
        linha = new Linha();
        linha.setMunicipio(municipio);
        setupLinhaNaTela();
    }

    @Override
    public void edita(Bundle bundle) {
        linha = (Linha) bundle.get("linha");
        linha.setMunicipio(municipio);
        setupLinhaNaTela();
    }

    @Override
    public void exclui() {
        FirebaseUtils.getLinhasReference(linha.getMunicipio().getId(), linha.getId()).removeValue();
        Toast.makeText(App.getAppContext(), App.getAppContext().getString(R.string.linha_excluida_sucesso,
                linha.getNumero()+" - "+linha.getTitulo()), Toast.LENGTH_SHORT).show();
        novo();
    }

    @Override
    public void salva() {
        if(setupTelaNaLinha()) {
            DatabaseReference referenciaFirebase = FirebaseUtils.getLinhasReference(linha.getMunicipio().getId(), linha.getId());
            if(linha.getId() == null) {
                referenciaFirebase = referenciaFirebase.push();
                linha.setId(referenciaFirebase.getKey());
                referenciaFirebase.setValue(linha);
            } else {
                referenciaFirebase.setValue(linha);
            }
            App.toast(R.string.linha_salva_sucesso, linha.toString());
        }
    }

    private boolean setupTelaNaLinha() {
        if(StringUtils.isNotBlank(etNumero.getText().toString())) {
            linha.setNumero(etNumero.getText().toString());
        } else {
            App.toast(R.string.campo_x_obrigatorio, "Número");
            etNumero.setError(App.getAppContext().getString(R.string.campo_obrigatorio));
            return false;
        }

        if(StringUtils.isNotBlank(etTituloLinha.getText().toString())) {
            linha.setTitulo(etTituloLinha.getText().toString());
        } else {
            App.toast(R.string.campo_x_obrigatorio, "Título");
            etTituloLinha.setError(App.getAppContext().getString(R.string.campo_obrigatorio));
            return false;
        }

        linha.setSubtitulo(etSubtituloLinha.getText().toString());
        List<String> listLatLgnStr = null;
        if(CollectionUtils.isNotEmpty(rota)) {
            listLatLgnStr = new ArrayList<>();
            for (LatLng umLatLng : rota) {
                listLatLgnStr.add(GoogleMapsUtils.getLatLngToString(umLatLng));
            }
        } else {
            App.toast(R.string.campo_x_obrigatorio, "Rota");
            return false;
        }
        linha.setRota(listLatLgnStr);

        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        if(linha.getMunicipio() != null && Geocoder.isPresent()) {
            setupLocationsOnMap();
        }
        if(CollectionUtils.isNotEmpty(rota)) {
            gMap.addPolyline(GoogleMapsUtils.desenhaRota(rota));
        }
        gMap.setOnMapClickListener(getOnMapClickListenerAddMarker());
    }

    private void setupLocationsOnMap() {
        try {
            Geocoder gc = new Geocoder(this);
            List<Address> addresses = gc.getFromLocationName(linha.getMunicipio().getNome(), 1);

            if(CollectionUtils.isNotEmpty(addresses)) {
                for(Address a : addresses){
                    if(a.hasLatitude() && a.hasLongitude()){
                        gMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                        gMap.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(a.getLatitude(), a.getLongitude())));
                    }
                }
            }
        } catch (IOException e) {
            Toast.makeText(App.getAppContext(), App.getAppContext().getString(R.string.nao_achou_municipio_no_mapa, linha.getMunicipio().getNome()), Toast.LENGTH_SHORT).show();
        }
    }

    private GoogleMap.OnMapClickListener getOnMapClickListenerAddMarker() {
        return new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                MarkerOptions markerOptions = new MarkerOptions().position(point);
                if(markers == null) {
                    markers = new ArrayList<>();
                }

                Marker lastMarker = gMap.addMarker(markerOptions);
                markers.add(lastMarker);
                if(CollectionUtils.isNotEmpty(rota) || markers.size() > 1) {
                    traceRoute(lastMarker);
                }
            }
        };
    }

    private void traceRoute(Marker lastMarker) {
        if(CollectionUtils.isNotEmpty(markers)) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(App.getAppContext().getString(R.string.carregando));
            progressDialog.show();

            for(Marker umMarker : markers) {
                if(umMarker.equals(lastMarker)) {
                    String srcParam = GoogleMapsUtils.getLatLngToString(CollectionUtils.isNotEmpty(rota) ? rota.get(rota.size()-1) : markers.get(markers.indexOf(umMarker)-1).getPosition());
                    String destParam = GoogleMapsUtils.getLatLngToString(umMarker.getPosition());
                    String urlRequestRoute = GoogleMapsUtils.getUrlSearchRoute(srcParam, destParam);

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlRequestRoute, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    MapDirectionsParser parser = new MapDirectionsParser();
                                    List<List<HashMap<String, String>>> routes = parser.parse(response);
                                    LatLng position = null;
                                    ArrayList<LatLng> points = new ArrayList<>();
                                    for (int i = 0; i < routes.size(); i++) {
                                        List<HashMap<String, String>> path = routes.get(i);
                                        for (int j = 0; j < path.size(); j++) {
                                            HashMap<String, String> point = path.get(j);

                                            double lat = Double.parseDouble(point.get("lat"));
                                            double lng = Double.parseDouble(point.get("lng"));
                                            position = new LatLng(lat, lng);

                                            points.add(position);
                                        }
                                    }
                                    if(CollectionUtils.isEmpty(rota)) rota = new ArrayList<>();
                                    rota.addAll(points);
                                    gMap.addPolyline(GoogleMapsUtils.desenhaRota(points));
                                    points.clear();
                                    progressDialog.dismiss();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    progressDialog.dismiss();
                                }
                            });
                    App.addToReqQueue(jsonObjectRequest);
                }
            }
        }
    }

    private void clearMap() {
        if(gMap != null) gMap.clear();
        if(CollectionUtils.isNotEmpty(rota)) rota.clear();
        if(CollectionUtils.isNotEmpty(markers)) markers.clear();
        if(linha != null && CollectionUtils.isNotEmpty(linha.getRota()));
    }
}