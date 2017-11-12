package com.marcosevaristo.trackusregister.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marcosevaristo.trackusregister.App;
import com.marcosevaristo.trackusregister.R;
import com.marcosevaristo.trackusregister.activities.CadastroMunicipioActivity;
import com.marcosevaristo.trackusregister.adapters.MunicipiosAdapter;
import com.marcosevaristo.trackusregister.database.firebase.FirebaseUtils;
import com.marcosevaristo.trackusregister.model.Municipio;
import com.marcosevaristo.trackusregister.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class AbaMunicipios extends Fragment{
    private View view;
    private ListView lMunicipiosView;
    private MunicipiosAdapter adapter;
    private ProgressBar progressBar;
    private String ultimaBusca;
    private List<Municipio> lMunicipios;

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
        return view;
    }

    private void setupListMunicipios(String argBusca) {
        progressBar = (ProgressBar) view.findViewById(R.id.progressBarAbaMunicipios);
        progressBar.setVisibility(View.VISIBLE);

        lMunicipiosView = (ListView) view.findViewById(R.id.listaMunicipiosBuscados);
        lMunicipiosView.setAdapter(null);
        lMunicipiosView.setOnItemClickListener(getOnItemClickListenerAbreCadastro());

        FirebaseUtils.getMunicipiosReference().getRef().addListenerForSingleValueEvent(getEventoBuscaMunicipiosFirebase());

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
            }
        };
    }

    private ValueEventListener getEventoBuscaMunicipiosFirebase() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Map<String, Object>> lMapValues = (List<Map<String, Object>>) dataSnapshot.getValue();
                if (lMapValues != null) {
                    lMunicipios = new ArrayList<>();
                    lMunicipios.addAll(Municipio.converteListMapParaListaMunicipios(lMapValues));
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
}