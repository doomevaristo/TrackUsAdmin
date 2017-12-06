package com.marcosevaristo.trackusadmin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.marcosevaristo.trackusadmin.app.App;
import com.marcosevaristo.trackusadmin.R;
import com.marcosevaristo.trackusadmin.model.Linha;

import java.util.ArrayList;
import java.util.List;

public class LinhasAdapter extends ArrayAdapter<Linha> {
    private List<Linha> lLinhas = new ArrayList<>();
    private int layoutResId;

    public LinhasAdapter(int layoutResId, List<Linha> lLinhas) {
        super(App.getAppContext(), layoutResId, lLinhas);
        this.layoutResId = layoutResId;
        this.lLinhas = lLinhas;
    }

    @Override
    public int getCount() {
        return lLinhas.size();
    }

    @Override
    public Linha getItem(int pos) {
        return lLinhas.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        LinhaHolder linhaHolder;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) App.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layoutResId, parent, false);
            linhaHolder = new LinhaHolder();

            TextView linhaBuscadaText = (TextView)view.findViewById(R.id.linhaBuscadaText);
            TextView linhaBuscadaSubText = (TextView)view.findViewById(R.id.linhaBuscadaSubText);
            TextView linhaBuscadaMunicipioText = (TextView)view.findViewById(R.id.linhaBuscadaMunicipioText);

            linhaHolder.texto = linhaBuscadaText;
            linhaHolder.subTexto = linhaBuscadaSubText;
            linhaHolder.municipioText = linhaBuscadaMunicipioText;

            view.setTag(linhaHolder);
        } else {
            linhaHolder = (LinhaHolder) view.getTag();
        }

        Linha linha = lLinhas.get(position);
        if(linha != null) {
            linhaHolder.texto.setText(linha.getNumero()+" - "+linha.getTitulo());
            linhaHolder.subTexto.setText(linha.getSubtitulo());
            linhaHolder.municipioText.setText(linha.getMunicipio().getNome());
        }
        return view;
    }

    private static class LinhaHolder {
        TextView texto;
        TextView subTexto;
        TextView municipioText;
    }
}
