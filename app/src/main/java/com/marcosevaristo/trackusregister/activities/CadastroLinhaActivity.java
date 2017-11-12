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
import com.google.android.gms.maps.model.Polyline;
import com.marcosevaristo.trackusregister.App;
import com.marcosevaristo.trackusregister.R;
import com.marcosevaristo.trackusregister.adapters.MunicipiosAdapter;
import com.marcosevaristo.trackusregister.database.QueryBuilder;
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

public class CadastroLinhaActivity extends AppCompatActivity implements Crud, View.OnClickListener, AdapterView.OnItemSelectedListener, OnMapReadyCallback {
    private Boolean isFabOpen = false;
    private FloatingActionButton fabMenu,fabAdd,fabDel;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private GoogleMap gMap;
    private List<Marker> markers;
    private Polyline rota;
    private ProgressDialog progressDialog;

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
        Spinner spinnerMunicipios = (Spinner) findViewById(R.id.spinnerMunicipioLinha);
        spinnerMunicipios.setOnItemSelectedListener(this);

        List<Municipio> lMunicipios = QueryBuilder.getMunicipios(null);
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
        TextInputEditText etNumero = (TextInputEditText) findViewById(R.id.etNumeroLinha);
        TextInputEditText etTituloLinha = (TextInputEditText) findViewById(R.id.etTituloLinha);
        TextInputEditText etSubtituloLinha = (TextInputEditText) findViewById(R.id.etSubtituloLinha);

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

        if(linha.getMunicipio() != null && Geocoder.isPresent()) {
            setupLocationsOnMap();
        }

        gMap.setOnMapClickListener(getOnMapClickListenerAddMarker());
    }

    private void setupLocationsOnMap() {
        try {
            Geocoder gc = new Geocoder(this);
            List<Address> addresses = gc.getFromLocationName(linha.getMunicipio().getNome(), 5);

            if(CollectionUtils.isNotEmpty(addresses)) {
                StringBuilder sbTitle;
                LatLng latLngAux = null;
                for(Address a : addresses){
                    if(a.hasLatitude() && a.hasLongitude()){
                        MarkerOptions marker = new MarkerOptions();
                        latLngAux = new LatLng(a.getLatitude(), a.getLongitude());
                        marker.position(latLngAux);
                        sbTitle = new StringBuilder();
                        sbTitle.append(a.getLocality()).append(" - ");
                        sbTitle.append(a.getAdminArea()).append(" (");
                        sbTitle.append(a.getCountryCode()).append(")");

                        marker.title(sbTitle.toString());
                        gMap.addMarker(marker);
                    }
                    if(latLngAux != null) {
                        gMap.moveCamera(CameraUpdateFactory.zoomTo(15));
                        gMap.animateCamera(CameraUpdateFactory.newLatLng(latLngAux));
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
                if(CollectionUtils.isEmpty(markers)) {
                    gMap.clear();
                    markers = new ArrayList<>();
                } else if(CollectionUtils.isNotEmpty(markers) && markers.size() == 2) {
                    markers.get(0).remove();
                }

                Marker markerAux = gMap.addMarker(markerOptions);
                if(CollectionUtils.isNotEmpty(markers) && markers.size() == 2) {
                    markers.set(0, markers.get(1));
                    markers.set(1, markerAux);

                    traceRoute(markers.get(0).getPosition(), markers.get(1).getPosition());
                } else if(markers != null && markers.size() < 2) {
                    markers.add(markerAux);
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

    private void traceRoute(LatLng srcLatLng, LatLng destLatLng) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading..");
        progressDialog.show();

        String srcParam = srcLatLng.latitude + "," + srcLatLng.longitude;
        String destParam = destLatLng.latitude + "," + destLatLng.longitude;

        String url = GoogleMapsUtils.getUrlSearchRoute(srcParam, destParam);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        MapDirectionsParser parser = MapDirectionsParser.getInstance();
                        List<List<HashMap<String, String>>> routes = parser.parse(response);
                        ArrayList<LatLng> points = null;

                        for (int i = 0; i < routes.size(); i++) {
                            points = new ArrayList<>();
                            List<HashMap<String, String>> path = routes.get(i);

                            for (int j = 0; j < path.size(); j++) {
                                HashMap<String, String> point = path.get(j);

                                double lat = Double.parseDouble(point.get("lat"));
                                double lng = Double.parseDouble(point.get("lng"));
                                LatLng position = new LatLng(lat, lng);

                                points.add(position);
                            }
                        }

                        if(rota != null) {
                            rota.remove();
                        }
                        rota = gMap.addPolyline(GoogleMapsUtils.desenhaRota(points));
                        progressDialog.dismiss();
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        App.addToReqQueue(jsonObjectRequest);
    }
}
