package com.marcosevaristo.trackusadmin.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marcosevaristo.trackusadmin.App;
import com.marcosevaristo.trackusadmin.R;
import com.marcosevaristo.trackusadmin.adapters.LinhasAdapter;
import com.marcosevaristo.trackusadmin.adapters.NumericKeyBoardTransformationMethod;
import com.marcosevaristo.trackusadmin.database.firebase.FirebaseUtils;
import com.marcosevaristo.trackusadmin.model.Linha;
import com.marcosevaristo.trackusadmin.model.Municipio;
import com.marcosevaristo.trackusadmin.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ConsultaLinhasActivity extends AppCompatActivity {

    private ListView lView;
    private LinhasAdapter adapter;
    private Municipio municipio;
    private List<Linha> lLinhas;
    private ProgressBar progressBar;

    public ConsultaLinhasActivity() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_linhas);
        Bundle bundle = getIntent().getExtras();
        municipio = (Municipio) bundle.get("municipio");

        setupComponents(StringUtils.emptyString());
        setupFloatingActionButton();
    }

    private void setupComponents(String argBusca) {
        progressBar = (ProgressBar) findViewById(R.id.progressBarAbaLinhas);
        progressBar.setVisibility(View.VISIBLE);
        lView = (ListView) findViewById(R.id.listaLinhas);
        lView.setAdapter(null);
        lView.setOnItemClickListener(getOnItemClickListenerAbreCadastro());

        FirebaseUtils.getLinhasReference(municipio.getId(), argBusca).getRef()
                .addListenerForSingleValueEvent(getEventoBuscaLinhasFirebase());
    }

    private ValueEventListener getEventoBuscaLinhasFirebase() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> mapValues = (Map<String, Object>) dataSnapshot.getValue();
                if (mapValues != null) {
                    lLinhas = new ArrayList<>();
                    //lLinhas.addAll(Linha.converteMapParaListaLinhas(mapValues));
                    for(Linha umaLinha : lLinhas) {
                        umaLinha.setMunicipio(municipio);
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
        adapter = new LinhasAdapter(R.layout.linha_item, lLinhas);
        adapter.notifyDataSetChanged();
        lView.setAdapter(adapter);
    }

    private AdapterView.OnItemClickListener getOnItemClickListenerAbreCadastro() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(App.getAppContext(), CadastroLinhaActivity.class);
                Bundle bundleAux = new Bundle();
                bundleAux.putSerializable("linha", (Linha)parent.getItemAtPosition(position));
                intent.putExtras(bundleAux);
                startActivity(intent);
            }
        };
    }

    private void setupFloatingActionButton() {
         findViewById(R.id.fab_search_linhas).setOnClickListener(getOnClickListenerFAB());
    }

    private View.OnClickListener getOnClickListenerFAB() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView busca = (TextView) findViewById(R.id.etBuscaLinhas);
                InputMethodManager imm = (InputMethodManager) App.getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if(busca.getVisibility() == View.GONE) {
                    exibeComponenteDeBusca(busca, imm);
                } else {
                    String arg = busca.getText().toString();
                    setupComponents(arg);
                    escondeComponenteDeBusca(busca, imm);
                }
            }

            private void exibeComponenteDeBusca(TextView busca, InputMethodManager imm) {
                busca.setVisibility(View.VISIBLE);
                busca.requestFocus();
                busca.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                busca.setTransformationMethod(new NumericKeyBoardTransformationMethod());
                busca.setTypeface(Typeface.SANS_SERIF);
                imm.showSoftInput(busca, InputMethodManager.SHOW_IMPLICIT);
            }

            private void escondeComponenteDeBusca(TextView busca, InputMethodManager imm) {
                busca.setText("");
                busca.setVisibility(View.GONE);
                imm.hideSoftInputFromWindow(busca.getWindowToken(), 0);
            }
        };
    }
}