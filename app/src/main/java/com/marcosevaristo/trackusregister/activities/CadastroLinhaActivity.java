package com.marcosevaristo.trackusregister.activities;

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
import android.widget.AdapterView;
import android.widget.Spinner;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marcosevaristo.trackusregister.App;
import com.marcosevaristo.trackusregister.R;
import com.marcosevaristo.trackusregister.adapters.MunicipiosAdapter;
import com.marcosevaristo.trackusregister.database.firebase.FirebaseUtils;
import com.marcosevaristo.trackusregister.model.Linha;
import com.marcosevaristo.trackusregister.model.Municipio;
import com.marcosevaristo.trackusregister.utils.CollectionUtils;
import com.marcosevaristo.trackusregister.utils.GoogleMapsUtils;
import com.marcosevaristo.trackusregister.utils.MapDirectionsParser;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CadastroLinhaActivity extends AppCompatActivity implements Crud, View.OnClickListener, AdapterView.OnItemSelectedListener, OnMapReadyCallback {
    private Boolean isFabOpen = false;
    private TextInputEditText etNumero, etTituloLinha, etSubtituloLinha;
    private Spinner spinnerMunicipios;
    private FloatingActionButton fabMenu,fabAdd,fabDel,fabClr;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private GoogleMap gMap;

    private List<Marker> markers;
    private ArrayList<LatLng> points;
    private ProgressDialog progressDialog;
    private List<Municipio> lMunicipios;

    private Linha linha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_linha);

        setupToolbar();
        setupFloatingActionButtons();
        setupSpinnerMunicipios();
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
        spinnerMunicipios = (Spinner) findViewById(R.id.spinnerMunicipioLinha);
        spinnerMunicipios.setOnItemSelectedListener(this);
        FirebaseUtils.getMunicipiosReference(null).getRef().addListenerForSingleValueEvent(getEventoBuscaMunicipiosFirebase());
    }

    private ValueEventListener getEventoBuscaMunicipiosFirebase() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Map<String, Object>> lMapValues = (List<Map<String, Object>>) dataSnapshot.getValue();
                if (lMapValues != null) {
                    lMunicipios = new ArrayList<>();
                    lMunicipios.addAll(Municipio.converteListMapParaListaMunicipios(lMapValues));
                    setupMunicipiosAdapter();
                } else {
                    Toast.makeText(App.getAppContext(), R.string.nenhum_resultado, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private void setupMunicipiosAdapter() {
        MunicipiosAdapter adapter = new MunicipiosAdapter(R.layout.municipio_item, lMunicipios);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMunicipios.setAdapter(adapter);

        if(linha != null && linha.getMunicipio() != null) {
            for(int i = 0; i < lMunicipios.size(); i++) {
                if(lMunicipios.get(i).getId().equals(linha.getMunicipio().getId())) {
                    spinnerMunicipios.setSelection(i);
                    break;
                }
            }
        }
    }

    private void setupLinhaNaTela(){
        if(etNumero == null) etNumero = (TextInputEditText) findViewById(R.id.etNumeroLinha);
        if(etTituloLinha == null) etTituloLinha = (TextInputEditText) findViewById(R.id.etTituloLinha);
        if(etSubtituloLinha == null) etSubtituloLinha = (TextInputEditText) findViewById(R.id.etSubtituloLinha);

        etNumero.setText(linha != null ? linha.getNumero() : null);
        etTituloLinha.setText(linha != null ? linha.getTitulo() : null);
        etSubtituloLinha.setText(linha != null ? linha.getSubtitulo() : null);
    }

    private void setupToolbar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
    }

    private void setupFloatingActionButtons() {
        fabMenu = (FloatingActionButton)findViewById(R.id.fab_menu_linha);
        fabAdd = (FloatingActionButton)findViewById(R.id.fab_add_linha);
        fabDel = (FloatingActionButton)findViewById(R.id.fab_del_linha);
        fabClr = (FloatingActionButton)findViewById(R.id.fab_clear_map);

        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);

        fabMenu.setOnClickListener(this);
        fabAdd.setOnClickListener(this);
        fabDel.setOnClickListener(this);
        fabClr.setOnClickListener(this);
    }

    public void animateFAB(){
        if(isFabOpen){
            fabMenu.startAnimation(rotate_backward);

            fabAdd.startAnimation(fab_close);
            fabDel.startAnimation(fab_close);
            fabClr.startAnimation(fab_close);
            fabAdd.setClickable(false);
            fabDel.setClickable(false);
            fabClr.setClickable(false);

            isFabOpen = false;
        } else {
            fabMenu.startAnimation(rotate_forward);

            fabAdd.startAnimation(fab_open);
            fabDel.startAnimation(fab_open);
            fabClr.startAnimation(fab_open);
            fabAdd.setClickable(true);
            fabDel.setClickable(true);
            fabClr.setClickable(true);
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
                animateFAB();
                break;
            case R.id.fab_del_linha:
                //TODO: pedir confirmação
                exclui();
                animateFAB();
                break;
            case R.id.fab_clear_map:
                clearMap();
                animateFAB();
                break;
        }
    }

    @Override
    public void novo() {
        linha = new Linha();
        setupLinhaNaTela();
    }

    @Override
    public void edita(Bundle bundle) {
        linha = (Linha) bundle.get("linha");
        setupLinhaNaTela();
    }

    @Override
    public void exclui() {
        //TODO: verificar se está gravado antes de excluir para chamar só o novo()
        FirebaseUtils.getLinhasReference(linha.getMunicipio().getId(), linha.getNumero()).getRef().removeValue();
        Toast.makeText(App.getAppContext(), App.getAppContext().getString(R.string.linha_excluida_sucesso,
                linha.getNumero()+" - "+linha.getTitulo()), Toast.LENGTH_SHORT).show();
        novo();
    }

    @Override
    public void salva() {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;

        if(linha.getMunicipio() != null && Geocoder.isPresent()) {
            setupLocationsOnMap();
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
                if(markers.size() > 1) {
                    traceRoute(lastMarker);
                }
            }
        };
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        int componenteId = view.getId();
        switch (componenteId) {
            case R.id.spinnerMunicipioLinha:
                linha.setMunicipio((Municipio) parent.getItemAtPosition(position));
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        linha.setMunicipio(null);
    }

    private void traceRoute(Marker lastMarker) {
        if(CollectionUtils.isNotEmpty(markers)) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(App.getAppContext().getString(R.string.carregando));
            progressDialog.show();

            for(Marker umMarker : markers) {
                if(points == null) points = new ArrayList<>();
                if(umMarker.equals(lastMarker)) {
                    String srcParam = GoogleMapsUtils.getLatLngToString(markers.get(markers.indexOf(umMarker)-1).getPosition());
                    String destParam = GoogleMapsUtils.getLatLngToString(umMarker.getPosition());
                    String urlRequestRoute = GoogleMapsUtils.getUrlSearchRoute(srcParam, destParam);

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlRequestRoute, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    MapDirectionsParser parser = new MapDirectionsParser();
                                    List<List<HashMap<String, String>>> routes = parser.parse(response);
                                    LatLng position = null;
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
        gMap.clear();
        if(CollectionUtils.isNotEmpty(points)) points.clear();
        if(CollectionUtils.isNotEmpty(markers)) markers.clear();
    }
}