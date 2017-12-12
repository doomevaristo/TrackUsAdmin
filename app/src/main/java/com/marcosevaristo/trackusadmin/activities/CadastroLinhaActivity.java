package com.marcosevaristo.trackusadmin.activities;

import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.marcosevaristo.trackusadmin.R;
import com.marcosevaristo.trackusadmin.app.App;
import com.marcosevaristo.trackusadmin.model.Linha;
import com.marcosevaristo.trackusadmin.model.Municipio;
import com.marcosevaristo.trackusadmin.utils.CollectionUtils;
import com.marcosevaristo.trackusadmin.utils.FirebaseUtils;
import com.marcosevaristo.trackusadmin.utils.GoogleMapsUtils;
import com.marcosevaristo.trackusadmin.utils.MapDirectionsParser;
import com.marcosevaristo.trackusadmin.utils.StringUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CadastroLinhaActivity extends AppCompatActivity implements ICrud, View.OnClickListener, OnMapReadyCallback {
    private Boolean isFabOpen = false;
    private TextInputEditText etNumero, etTituloLinha, etSubtituloLinha;
    private FloatingActionButton fabMenu,fabAdd,fabSave,fabDel,fabUndoLast,fabClr;
    private TextView labelFabAdd, labelFabSave, labelFabDel, labelFabUndoLast, labelFabClear;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private GoogleMap gMap;

    private List<Marker> markers;
    private List<Polyline> lPolylines;
    private List<ArrayList<LatLng>> rota;

    private Municipio municipio;
    private Linha linha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro_linha);
        App.showLoadingDialog(this);
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

    @Override
    public void novo() {
        linha = new Linha();
        linha.setMunicipio(municipio);
        clearMap();
        setupLinhaNaTela();
    }

    @Override
    public void edita(Bundle bundle) {
        linha = (Linha) bundle.get("linha");
        setupLinhaNaTela();
    }

    @Override
    public void exclui() {
        FirebaseUtils.getLinhasReference(linha.getMunicipio().getId(), linha.getId()).removeValue();
        App.toast(R.string.linha_excluida_sucesso, linha.toString());
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
                Map<String, Object> mapValues = new HashMap<>();
                mapValues.put("numero", linha.getNumero());
                mapValues.put("titulo", linha.getTitulo());
                mapValues.put("subtitulo", linha.getSubtitulo());
                mapValues.put("rota", linha.getRota());
                referenciaFirebase.updateChildren(mapValues);
            }
            App.toast(R.string.linha_salva_sucesso, linha.toString());
        }
    }

    private void setupLinhaNaTela(){
        if(etNumero == null) etNumero = (TextInputEditText) findViewById(R.id.etNumeroLinha);
        if(etTituloLinha == null) etTituloLinha = (TextInputEditText) findViewById(R.id.etTituloLinha);
        if(etSubtituloLinha == null) etSubtituloLinha = (TextInputEditText) findViewById(R.id.etSubtituloLinha);

        etNumero.setText(linha != null ? linha.getNumero() : null);
        etTituloLinha.setText(linha != null ? linha.getTitulo() : null);
        etSubtituloLinha.setText(linha != null ? linha.getSubtitulo() : null);
        etNumero.requestFocus();

        if(linha != null && StringUtils.isNotBlank(linha.getId())) {
            FirebaseUtils.getLinhasReference(municipio.getId(), linha.getId()).child("rota").addListenerForSingleValueEvent(getEventoBuscaRotaFirebase());
        } else {
            App.hideLoadingDialog();
        }
    }

    @SuppressWarnings("unchecked")
    private ValueEventListener getEventoBuscaRotaFirebase() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    rota = new ArrayList<>();
                    List<String> lRotasStr = new ArrayList<>();
                    for(DataSnapshot umDataSnapshot : dataSnapshot.getChildren()) {
                        lRotasStr.add(umDataSnapshot.getValue().toString());
                    }
                    rota.add((ArrayList<LatLng>) GoogleMapsUtils.getListLatLngFromListString(lRotasStr));
                    if(CollectionUtils.isNotEmpty(rota)) {
                        lPolylines = new ArrayList<>();
                        lPolylines.add(gMap.addPolyline(GoogleMapsUtils.desenhaRota(rota.get(0))));
                    }
                }
                App.hideLoadingDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                App.hideLoadingDialog();
            }
        };
    }

    private void setupToolbar() {
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
    }

    private void setupFloatingActionButtons() {
        fabMenu = (FloatingActionButton)findViewById(R.id.fab_menu);
        fabAdd = (FloatingActionButton)findViewById(R.id.fab_add);
        fabDel = (FloatingActionButton)findViewById(R.id.fab_del);
        fabSave = (FloatingActionButton)findViewById(R.id.fab_save);
        fabUndoLast = (FloatingActionButton)findViewById(R.id.fab_undo_last);
        fabClr = (FloatingActionButton)findViewById(R.id.fab_clear);

        labelFabAdd = (TextView) findViewById(R.id.labelFabAdd);
        labelFabSave = (TextView) findViewById(R.id.labelFabSave);
        labelFabDel = (TextView) findViewById(R.id.labelFabDel);
        labelFabUndoLast = (TextView) findViewById(R.id.labelFabUndoLast);
        labelFabClear = (TextView) findViewById(R.id.labelFabClear);

        fab_open = AnimationUtils.loadAnimation(App.getAppContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(App.getAppContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(App.getAppContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(App.getAppContext(),R.anim.rotate_backward);

        fabMenu.setOnClickListener(this);
        fabAdd.setOnClickListener(this);
        fabSave.setOnClickListener(this);
        fabDel.setOnClickListener(this);
        fabClr.setOnClickListener(this);
        fabUndoLast.setOnClickListener(this);

        labelFabAdd.setOnClickListener(this);
        labelFabSave.setOnClickListener(this);
        labelFabDel.setOnClickListener(this);
        labelFabUndoLast.setOnClickListener(this);
        labelFabClear.setOnClickListener(this);
    }

    public void animateFAB(){
        if(isFabOpen){
            fabMenu.startAnimation(rotate_backward);

            fabAdd.startAnimation(fab_close);
            labelFabAdd.startAnimation(fab_close);

            fabSave.startAnimation(fab_close);
            labelFabSave.startAnimation(fab_close);

            fabClr.startAnimation(fab_close);
            labelFabClear.startAnimation(fab_close);

            fabUndoLast.startAnimation(fab_close);
            labelFabUndoLast.startAnimation(fab_close);
            if(linha != null && StringUtils.isNotBlank(linha.getId())) {
                fabDel.startAnimation(fab_close);
                labelFabDel.startAnimation(fab_close);
            }

            fabAdd.setClickable(false);
            labelFabAdd.setClickable(false);

            fabSave.setClickable(false);
            labelFabSave.setClickable(false);

            fabClr.setClickable(false);
            labelFabClear.setClickable(false);

            fabUndoLast.setClickable(false);
            labelFabUndoLast.setClickable(false);
            if(linha != null && StringUtils.isNotBlank(linha.getId())) {
                fabDel.setClickable(false);
                labelFabDel.setClickable(false);
            }
            isFabOpen = false;
        } else {
            fabMenu.startAnimation(rotate_forward);

            fabAdd.startAnimation(fab_open);
            labelFabAdd.startAnimation(fab_open);

            fabSave.startAnimation(fab_open);
            labelFabSave.startAnimation(fab_open);

            fabClr.startAnimation(fab_open);
            labelFabClear.startAnimation(fab_open);

            fabUndoLast.startAnimation(fab_open);
            labelFabUndoLast.startAnimation(fab_open);
            if(linha != null && StringUtils.isNotBlank(linha.getId())) {
                fabDel.startAnimation(fab_open);
                labelFabDel.startAnimation(fab_open);
            }

            fabAdd.setClickable(true);
            labelFabAdd.setClickable(true);

            fabSave.setClickable(true);
            labelFabSave.setClickable(true);

            fabClr.setClickable(true);
            labelFabClear.setClickable(true);

            fabUndoLast.setClickable(true);
            labelFabUndoLast.setClickable(true);
            if(linha != null && StringUtils.isNotBlank(linha.getId())) {
                fabDel.setClickable(true);
                labelFabDel.setClickable(true);
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
            case R.id.fab_clear:
            case R.id.labelFabClear:
                clearMap();
                break;
            case R.id.fab_undo_last:
            case R.id.labelFabUndoLast:
                clearLastMarker();
                break;
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
        List<String> listLatLgnStr;
        if(CollectionUtils.isNotEmpty(rota)) {
            listLatLgnStr = new ArrayList<>();
            for(ArrayList<LatLng> umListLatLng: rota) {
                for (LatLng umLatLng : umListLatLng) {
                    listLatLgnStr.add(GoogleMapsUtils.getLatLngToString(umLatLng));
                }
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
            lPolylines = new ArrayList<>();
            lPolylines.add(gMap.addPolyline(GoogleMapsUtils.desenhaRota(rota.get(0))));
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
            App.toast(R.string.nao_achou_municipio_no_mapa, linha.getMunicipio().getNome());
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
                    traceRoute(lastMarker, CollectionUtils.isNotEmpty(rota) ? rota.get(rota.size()-1) : null);
                }
            }
        };
    }

    private void traceRoute(Marker lastMarker, ArrayList<LatLng> ultimaRotaDesenhada) {
        if(CollectionUtils.isNotEmpty(markers)) {
            App.showLoadingDialog(CadastroLinhaActivity.this);

            for(Marker umMarker : markers) {
                if(umMarker.equals(lastMarker)) {
                    String srcParam = GoogleMapsUtils.getLatLngToString(CollectionUtils.isNotEmpty(ultimaRotaDesenhada) ?
                            ultimaRotaDesenhada.get(ultimaRotaDesenhada.size()-1) : markers.get(markers.indexOf(umMarker)-1).getPosition());
                    String destParam = GoogleMapsUtils.getLatLngToString(umMarker.getPosition());
                    String urlRequestRoute = GoogleMapsUtils.getUrlSearchRoute(srcParam, destParam);

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlRequestRoute, null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    MapDirectionsParser parser = new MapDirectionsParser();
                                    List<List<HashMap<String, String>>> routes = parser.parse(response);
                                    ArrayList<LatLng> points = new ArrayList<>();
                                    for (int i = 0; i < routes.size(); i++) {
                                        List<HashMap<String, String>> path = routes.get(i);
                                        for (int j = 0; j < path.size(); j++) {
                                            HashMap<String, String> point = path.get(j);

                                            double lat = Double.parseDouble(point.get("lat"));
                                            double lng = Double.parseDouble(point.get("lng"));

                                            points.add(new LatLng(lat, lng));
                                        }
                                    }
                                    if(CollectionUtils.isEmpty(rota)) rota = new ArrayList<>();
                                    rota.add(new ArrayList<>(points));
                                    if(CollectionUtils.isEmpty(lPolylines)) lPolylines = new ArrayList<>();
                                    lPolylines.add(gMap.addPolyline(GoogleMapsUtils.desenhaRota(points)));
                                    App.hideLoadingDialog();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    App.hideLoadingDialog();
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
        if(CollectionUtils.isNotEmpty(lPolylines)) lPolylines.clear();
    }

    private void clearLastMarker() {
        if(CollectionUtils.isNotEmpty(rota)) rota.remove(rota.size()-1);
        if(CollectionUtils.isNotEmpty(lPolylines)) {
            lPolylines.get(lPolylines.size()-1).remove();
            lPolylines.remove(lPolylines.size()-1);
        }
        if(CollectionUtils.isNotEmpty(markers)){
            markers.get(markers.size()-1).remove();
            markers.remove(markers.size()-1);
        }
    }
}