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
        LinhaHolder linhaHolder = null;
        boolean ehBusca = false;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) App.getAppContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layoutResId, parent, false);
            linhaHolder = new LinhaHolder();

            TextView linhaBuscadaText = (TextView)view.findViewById(R.id.linhaBuscadaText);
            TextView linhaBuscadaSubText = (TextView)view.findViewById(R.id.linhaBuscadaSubText);
            TextView linhaFavoritaText = (TextView)view.findViewById(R.id.linhaFavoritaText);
            TextView linhaFavoritaSubText = (TextView)view.findViewById(R.id.linhaFavoritaSubText);

            ehBusca = linhaBuscadaText != null;

            linhaHolder.texto = linhaBuscadaText != null ? linhaBuscadaText : linhaFavoritaText;
            linhaHolder.subTexto = linhaBuscadaSubText != null ? linhaBuscadaSubText : linhaFavoritaSubText;

            view.setTag(linhaHolder);
        } else {
            linhaHolder = (LinhaHolder) view.getTag();
        }

        Linha linha = lLinhas.get(position);
        if(linha != null) {
            linhaHolder.texto.setText(linha.getNumero()+" - "+linha.getTitulo());
            linhaHolder.subTexto.setText(linha.getSubtitulo());
            if(ehBusca) {
                linhaHolder.botaoFavorito = setupBotaoFavorito(view, position);
            } else {
                linhaHolder.botaoExcluir = setupBotaoExcluir(view, position);
            }
        }
        return view;
    }

    private BotaoFavorito setupBotaoFavorito(View view, final int posicao) {
       BotaoFavorito botaoFavorito = (BotaoFavorito) view.findViewById(R.id.botaoFavorito);
       if(botaoFavorito != null) {
           botaoFavorito.setFavorite(lLinhas.get(posicao).ehFavorito(), true);
           botaoFavorito.setOnFavoriteChangeListener(new BotaoFavorito.OnFavoriteChangeListener() {
               @Override
               public void onFavoriteChanged(BotaoFavorito buttonView, boolean favorite) {
                   Linha linha = lLinhas.get(posicao);
                   linha.setEhFavorito(favorite);
                   QueryBuilder.updateFavorito(linha);
               }
           });
       }
        return botaoFavorito;
    }

    private Button setupBotaoExcluir(View view, final int posicao) {
        Button botaoExcluir = (Button) view.findViewById(R.id.btExcluir);
        if(botaoExcluir != null) botaoExcluir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Linha linha = lLinhas.get(posicao);
                linha.setEhFavorito(false);
                QueryBuilder.updateFavorito(linha);
                lLinhas.remove(linha);
                Toast.makeText(App.getAppContext(), App.getAppContext().getString(R.string.favorito_excluido, linha.getNumero()), Toast.LENGTH_SHORT).show();
                notifyDataSetChanged();
            }
        });

        return botaoExcluir;
    }

    private static class LinhaHolder {
        TextView texto;
        TextView subTexto;
        BotaoFavorito botaoFavorito;
        Button botaoExcluir;
    }
}
