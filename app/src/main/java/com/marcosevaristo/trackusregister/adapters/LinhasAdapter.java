package com.marcosevaristo.trackusregister.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.marcosevaristo.trackusregister.App;
import com.marcosevaristo.trackusregister.R;
import com.marcosevaristo.trackusregister.components.BotaoFavorito;
import com.marcosevaristo.trackusregister.database.QueryBuilder;
import com.marcosevaristo.trackusregister.model.Linha;

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

            linhaHolder.texto = linhaBuscadaText;
            linhaHolder.subTexto = linhaBuscadaSubText;

            view.setTag(linhaHolder);
        } else {
            linhaHolder = (LinhaHolder) view.getTag();
        }

        Linha linha = lLinhas.get(position);
        if(linha != null) {
            linhaHolder.texto.setText(linha.getNumero()+" - "+linha.getTitulo());
            linhaHolder.subTexto.setText(linha.getSubtitulo());
        }
        return view;
    }

    private static class LinhaHolder {
        TextView texto;
        TextView subTexto;
    }
}
