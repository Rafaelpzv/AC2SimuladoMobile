package com.example.ac2simulado.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.ac2simulado.R;
import com.example.ac2simulado.model.Filme;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FilmeAdapter extends ArrayAdapter<Filme> {

    private final LayoutInflater inflater;
    private final DecimalFormat formatoNota =
            new DecimalFormat("0.#", DecimalFormatSymbols.getInstance(new Locale("pt", "BR")));

    public FilmeAdapter(@NonNull Context context) {
        super(context, 0, new ArrayList<>());
        inflater = LayoutInflater.from(context);
    }

    public void atualizarLista(@NonNull List<Filme> filmes) {
        setNotifyOnChange(false);
        clear();
        addAll(filmes);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_filme, parent, false);
            holder = new ViewHolder();
            holder.nome = convertView.findViewById(R.id.tvNomeItem);
            holder.info = convertView.findViewById(R.id.tvInfoItem);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Filme filme = getItem(position);
        if (filme != null) {
            preencherItem(holder, filme);
        }
        return convertView;
    }

    private void preencherItem(ViewHolder holder, Filme filme) {
        Context context = getContext();
        String status = context.getString(
                filme.isAssistido() ? R.string.assistido : R.string.nao_assistido
        );
        String info = context.getString(
                R.string.info_filme,
                filme.getTipo(),
                filme.getGenero(),
                filme.getAno(),
                formatoNota.format(filme.getNota()),
                status
        );

        holder.nome.setText(filme.getNome());
        holder.info.setText(info);
    }

    static class ViewHolder {
        TextView nome;
        TextView info;
    }
}
