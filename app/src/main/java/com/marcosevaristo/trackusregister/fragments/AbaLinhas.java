package com.marcosevaristo.trackusregister.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marcosevaristo.trackusregister.App;
import com.marcosevaristo.trackusregister.R;
import com.marcosevaristo.trackusregister.activities.CadastroLinhaActivity;
import com.marcosevaristo.trackusregister.adapters.LinhasAdapter;
import com.marcosevaristo.trackusregister.adapters.NumericKeyBoardTransformationMethod;
import com.marcosevaristo.trackusregister.database.firebase.FirebaseUtils;
import com.marcosevaristo.trackusregister.model.Linha;
import com.marcosevaristo.trackusregister.utils.CollectionUtils;
import com.marcosevaristo.trackusregister.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class AbaLinhas extends Fragment {

    private View view;
    private ListView lView;
    private LinhasAdapter adapter;
    private List<Linha> lLinhas;
    private ProgressBar progressBar;

    public AbaLinhas() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.aba_linhas, container, false);
        setupListLinhas(StringUtils.emptyString());
        setupFloatingActionButton(view);
        return view;
    }

    private void setupListLinhas(String argBusca) {
        progressBar = (ProgressBar) view.findViewById(R.id.progressBarAbaLinhas);
        progressBar.setVisibility(View.VISIBLE);
        lView = (ListView) view.findViewById(R.id.listaLinhas);
        lView.setAdapter(null);
        lView.setOnItemClickListener(getOnItemClickListenerAbreCadastro());

        FirebaseUtils.getLinhasReference().child(argBusca).getRef().addListenerForSingleValueEvent(getEventoBuscaLinhasFirebase());
    }

    private ValueEventListener getEventoBuscaLinhasFirebase() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> mapValues = (Map<String, Object>) dataSnapshot.getValue();
                if (mapValues != null) {
                    lLinhas = new ArrayList<>();
                    lLinhas.addAll(Linha.converteMapParaListaLinhas(mapValues));
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
        adapter = new LinhasAdapter(R.layout.linha_item, lLinhas.getlLinhas());
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

    private void setupFloatingActionButton(View view) {
         view.findViewById(R.id.fab_search_linhas).setOnClickListener(getOnClickListenerFAB());
    }

    private View.OnClickListener getOnClickListenerFAB() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView busca = (TextView) getActivity().findViewById(R.id.etBuscaLinhas);
                InputMethodManager imm = (InputMethodManager) App.getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if(busca.getVisibility() == View.GONE) {
                    exibeComponenteDeBusca(busca, imm);
                } else {
                    String arg = busca.getText().toString();
                    setupListLinhas(arg);
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

    public void atualizaBusca() {
        EditText editText = (EditText) view.findViewById(R.id.etBuscaLinhas);
        editText.setVisibility(View.GONE);
        editText.setText(StringUtils.emptyString());
    }
}