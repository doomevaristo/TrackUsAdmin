package com.marcosevaristo.trackusregister.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.marcosevaristo.trackusregister.R;
import com.marcosevaristo.trackusregister.adapters.LinhasAdapter;
import com.marcosevaristo.trackusregister.database.QueryBuilder;
import com.marcosevaristo.trackusregister.dto.ListaLinhasDTO;


public class AbaFavoritos extends Fragment{
    private View view;
    private ListView lView;
    private LinhasAdapter adapter;
    private ListaLinhasDTO lLinhas = new ListaLinhasDTO();

    public AbaFavoritos() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.aba_favoritos, container, false);
        setupListLinhas();
        return view;
    }

    private void setupListLinhas() {
        lView = (ListView) view.findViewById(R.id.listaLinhasFavoritas);
        lLinhas = new ListaLinhasDTO();
        lLinhas.addLinhas(QueryBuilder.getFavoritos(null));
        adapter = new LinhasAdapter(R.layout.item_dos_favoritos, lLinhas.getlLinhas());
        adapter.notifyDataSetChanged();
        lView.setAdapter(adapter);
    }

    public void atualizaFavoritos() {
        setupListLinhas();
    }
}