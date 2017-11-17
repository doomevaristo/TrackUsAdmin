package com.marcosevaristo.trackusadmin.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marcosevaristo.trackusadmin.App;
import com.marcosevaristo.trackusadmin.R;
import com.marcosevaristo.trackusadmin.activities.CadastroMunicipioActivity;
import com.marcosevaristo.trackusadmin.adapters.MunicipiosAdapter;
import com.marcosevaristo.trackusadmin.database.firebase.FirebaseUtils;
import com.marcosevaristo.trackusadmin.model.Municipio;
import com.marcosevaristo.trackusadmin.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class AbaMunicipios extends Fragment implements View.OnClickListener{
    private View view;
    private ListView lMunicipiosView;
    private MunicipiosAdapter adapter;
    private ProgressBar progressBar;
    private String ultimaBusca;
    private List<Municipio> lMunicipios;
    private FloatingActionButton fabMenu,fabAdd,fabSearch;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private Boolean isFabOpen = false;

    public AbaMunicipios() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.aba_municipios, container, false);
        setupListMunicipios(StringUtils.emptyString());
        setupFloatingActionButtons();
        return view;
    }

    private void setupFloatingActionButtons() {
        fabMenu = (FloatingActionButton) view.findViewById(R.id.fab_menu);
        fabAdd = (FloatingActionButton) view.findViewById(R.id.fab_add);
        fabSearch = (FloatingActionButton) view.findViewById(R.id.fab_search_municipios);

        fab_open = AnimationUtils.loadAnimation(App.getAppContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(App.getAppContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(App.getAppContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(App.getAppContext(),R.anim.rotate_backward);

        fabMenu.setOnClickListener(this);
        fabAdd.setOnClickListener(this);
        fabSearch.setOnClickListener(this);
    }

    private void setupListMunicipios(String argBusca) {
        progressBar = (ProgressBar) view.findViewById(R.id.progressBarAbaMunicipios);
        progressBar.setVisibility(View.VISIBLE);

        lMunicipiosView = (ListView) view.findViewById(R.id.listaMunicipiosBuscados);
        lMunicipiosView.setAdapter(null);
        lMunicipiosView.setOnItemClickListener(getOnItemClickListenerAbreCadastro());

        FirebaseUtils.getMunicipiosReference(null).getRef().addListenerForSingleValueEvent(getEventoBuscaMunicipiosFirebase());

        ultimaBusca = argBusca;
    }

    private AdapterView.OnItemClickListener getOnItemClickListenerAbreCadastro() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(App.getAppContext(), CadastroMunicipioActivity.class);
                Bundle bundleAux = new Bundle();
                bundleAux.putSerializable("municipio", (Municipio)parent.getItemAtPosition(position));
                intent.putExtras(bundleAux);
                startActivity(intent);
                if(isFabOpen) {
                    animateFAB();
                }
            }
        };
    }

    private ValueEventListener getEventoBuscaMunicipiosFirebase() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lMunicipios = new ArrayList<>();
                if(dataSnapshot == null || dataSnapshot.getChildren() != null) {
                    for (DataSnapshot municipioSnapshot : dataSnapshot.getChildren()) {
                        Municipio municipio = municipioSnapshot.getValue(Municipio.class);
                        municipio.setId(municipioSnapshot.getKey());
                        lMunicipios.add(municipio);
                    }
                    setupListAdapter();
                } else {
                    Toast.makeText(App.getAppContext(), R.string.nenhum_resultado, Toast.LENGTH_LONG).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
            }
        };
    }

    private void setupListAdapter() {
        adapter = new MunicipiosAdapter(R.layout.municipio_item, lMunicipios);
        adapter.notifyDataSetChanged();
        lMunicipiosView.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
    }

    public void atualizaBusca() {
        EditText editText = (EditText) view.findViewById(R.id.etBuscaMunicipios);
        editText.setVisibility(View.GONE);
        editText.setText(StringUtils.emptyString());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.fab_menu:
                break;
            case R.id.fab_add:
                startActivity(new Intent(App.getAppContext(), CadastroMunicipioActivity.class));
                break;
            case R.id.fab_search_municipios:
                //TODO: pesquisar
                break;
        }
        animateFAB();
    }

    public void animateFAB(){
        if(isFabOpen){
            fabMenu.startAnimation(rotate_backward);
            fabAdd.startAnimation(fab_close);
            fabSearch.startAnimation(fab_close);

            fabAdd.setClickable(false);
            fabSearch.setClickable(false);
            isFabOpen = false;
        } else {
            fabMenu.startAnimation(rotate_forward);
            fabAdd.startAnimation(fab_open);
            fabSearch.startAnimation(fab_open);

            fabAdd.setClickable(true);
            fabSearch.setClickable(false);

            isFabOpen = true;
        }
    }
}