package com.marcosevaristo.trackusadmin.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.marcosevaristo.trackusadmin.App;
import com.marcosevaristo.trackusadmin.R;
import com.marcosevaristo.trackusadmin.activities.CadastroLinhaActivity;
import com.marcosevaristo.trackusadmin.adapters.LinhasAdapter;
import com.marcosevaristo.trackusadmin.adapters.NumericKeyBoardTransformationMethod;
import com.marcosevaristo.trackusadmin.database.firebase.FirebaseUtils;
import com.marcosevaristo.trackusadmin.model.Linha;
import com.marcosevaristo.trackusadmin.model.Municipio;
import com.marcosevaristo.trackusadmin.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;


public class AbaLinhas extends Fragment implements View.OnClickListener , EditText.OnEditorActionListener{
    private View view;
    private ListView lView;
    private LinhasAdapter adapter;
    private Municipio municipio;
    private List<Linha> lLinhas;

    private FloatingActionButton fabMenu,fabAdd,fabSearch;
    private TextView labelFabAdd, labelFabSearch;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private Boolean isFabOpen = false;

    public AbaLinhas() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.aba_linhas, container, false);
        Bundle bundle = getArguments();
        if(bundle != null) {
            municipio = (Municipio) bundle.get("municipio");
        }
        setupComponents(StringUtils.emptyString());
        setupFloatingActionButtons();
        return view;
    }

    private void setupComponents(String argBusca) {
        App.showLoadingDialog(getActivity());
        lView = (ListView) view.findViewById(R.id.listaLinhas);
        lView.setAdapter(null);
        lView.setOnItemClickListener(getOnItemClickListenerAbreCadastro());

        if(municipio != null) {
            Query query = FirebaseUtils.getLinhasReference(municipio.getId(), null).orderByChild("numero");
            if(StringUtils.isNotBlank(argBusca)) {
                query = query.equalTo(argBusca);
            }
            query.addValueEventListener(getEventoBuscaLinhasFirebase());
        } else {
            App.hideLoadingDialog();
        }
    }

    private ValueEventListener getEventoBuscaLinhasFirebase() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                lLinhas = new ArrayList<>();
                if(dataSnapshot.exists()) {
                    for(DataSnapshot umDataSnapshot : dataSnapshot.getChildren()) {
                        String id = umDataSnapshot.getKey();
                        String numero = umDataSnapshot.child("numero").getValue().toString();
                        String titulo = umDataSnapshot.child("titulo").getValue().toString();
                        String subtitulo = umDataSnapshot.child("subtitulo").getValue().toString();

                        Linha umaLinha = new Linha(id, numero, titulo, subtitulo);
                        umaLinha.setMunicipio(municipio);
                        lLinhas.add(umaLinha);
                    }
                } else {
                    App.toast(R.string.nenhum_resultado);
                }
                setupListAdapter();
                App.hideLoadingDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                App.hideLoadingDialog();
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
                bundleAux.putSerializable("municipio", municipio);
                intent.putExtras(bundleAux);
                startActivity(intent);
            }
        };
    }

    private void setupFloatingActionButtons() {
        fabMenu = (FloatingActionButton) view.findViewById(R.id.fab_menu);
        fabAdd = (FloatingActionButton) view.findViewById(R.id.fab_add);
        fabSearch = (FloatingActionButton) view.findViewById(R.id.fab_search_linhas);

        labelFabAdd = (TextView) view.findViewById(R.id.labelFabAdd);
        labelFabSearch = (TextView) view.findViewById(R.id.labelFabSearchLinhas);

        fab_open = AnimationUtils.loadAnimation(App.getAppContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(App.getAppContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(App.getAppContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(App.getAppContext(),R.anim.rotate_backward);

        fabMenu.setOnClickListener(this);
        fabAdd.setOnClickListener(this);
        fabSearch.setOnClickListener(this);
        labelFabAdd.setOnClickListener(this);
        labelFabSearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.fab_menu:
                break;
            case R.id.fab_add:
            case R.id.labelFabAdd:
                Intent intent = new Intent(App.getAppContext(), CadastroLinhaActivity.class);
                Bundle bundleAux = new Bundle();
                bundleAux.putSerializable("municipio", municipio);
                intent.putExtras(bundleAux);
                startActivity(intent);
                break;
            case R.id.fab_search_linhas:
            //case R.id.labelFabSearchLinhas:
                TextInputEditText busca = (TextInputEditText) view.findViewById(R.id.etBuscaLinhas);
                InputMethodManager imm = (InputMethodManager) App.getAppContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                ((TextInputLayout)busca.getParent().getParent()).setVisibility(View.VISIBLE);
                busca.requestFocus();
                busca.setOnEditorActionListener(this);
                busca.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                busca.setTransformationMethod(new NumericKeyBoardTransformationMethod());
                busca.setTypeface(Typeface.SANS_SERIF);
                imm.showSoftInput(busca, InputMethodManager.SHOW_IMPLICIT);
                break;
        }
        animateFAB();
    }

    public void animateFAB(){
        if(isFabOpen){
            fabMenu.startAnimation(rotate_backward);

            fabAdd.startAnimation(fab_close);
            labelFabAdd.startAnimation(fab_close);

            fabSearch.startAnimation(fab_close);
            labelFabSearch.startAnimation(fab_close);

            fabAdd.setClickable(false);
            labelFabAdd.setClickable(false);

            fabSearch.setClickable(false);
            labelFabSearch.setClickable(false);
            isFabOpen = false;
        } else {
            fabMenu.startAnimation(rotate_forward);

            fabAdd.startAnimation(fab_open);
            labelFabAdd.startAnimation(fab_open);

            fabSearch.startAnimation(fab_open);
            labelFabSearch.startAnimation(fab_open);

            fabAdd.setClickable(true);
            labelFabAdd.setClickable(true);

            fabSearch.setClickable(true);
            labelFabSearch.setClickable(true);

            isFabOpen = true;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH
                || actionId == EditorInfo.IME_ACTION_NEXT
                || actionId == EditorInfo.IME_ACTION_DONE) {
            setupComponents(v.getText().toString());
            return true;
        }
        return false;
    }
}