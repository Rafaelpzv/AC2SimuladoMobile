package com.example.ac2simulado;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ac2simulado.data.FilmeRepository;
import com.example.ac2simulado.model.Filme;
import com.example.ac2simulado.ui.FilmeAdapter;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final List<Filme> todosOsFilmes = new ArrayList<>();

    private FilmeRepository repositorio;
    private FilmeAdapter adapter;
    private ListenerRegistration listenerRegistration;

    private Spinner spinnerFiltroTipo;
    private CheckBox checkSomenteAssistidos;
    private String[] tipos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repositorio = new FilmeRepository();
        tipos = getResources().getStringArray(R.array.tipos);

        spinnerFiltroTipo = findViewById(R.id.spinnerFiltroTipo);
        checkSomenteAssistidos = findViewById(R.id.checkSomenteAssistidos);
        TextView tvVazio = findViewById(R.id.tvVazio);
        ListView listaFilmes = findViewById(R.id.listaFilmes);

        adapter = new FilmeAdapter(this);
        listaFilmes.setAdapter(adapter);
        listaFilmes.setEmptyView(tvVazio);

        findViewById(R.id.btnAdicionar).setOnClickListener(
                v -> startActivity(FormActivity.novaIntent(this, null))
        );

        listaFilmes.setOnItemClickListener((parent, view, position, id) -> {
            Filme filme = adapter.getItem(position);
            if (filme != null) {
                startActivity(FormActivity.novaIntent(this, filme));
            }
        });

        listaFilmes.setOnItemLongClickListener((parent, view, position, id) -> {
            Filme filme = adapter.getItem(position);
            if (filme != null) {
                confirmarExclusao(filme);
                return true;
            }
            return false;
        });

        configurarFiltro();
    }

    private void configurarFiltro() {
        List<String> opcoes = new ArrayList<>();
        opcoes.add(getString(R.string.todos_os_tipos));
        Collections.addAll(opcoes, tipos);

        ArrayAdapter<String> adapterFiltro = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                opcoes
        );
        adapterFiltro.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltroTipo.setAdapter(adapterFiltro);

        spinnerFiltroTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                aplicarFiltro();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        checkSomenteAssistidos.setOnCheckedChangeListener(
                (buttonView, isChecked) -> aplicarFiltro()
        );
    }

    private void aplicarFiltro() {
        int posicao = spinnerFiltroTipo.getSelectedItemPosition();
        String tipoSelecionado = posicao > 0 && posicao <= tipos.length
                ? tipos[posicao - 1]
                : null;
        boolean somenteAssistidos = checkSomenteAssistidos.isChecked();

        List<Filme> filtrados = new ArrayList<>();
        for (Filme filme : todosOsFilmes) {
            if (tipoSelecionado != null && !tipoSelecionado.equals(filme.getTipo())) {
                continue;
            }
            if (somenteAssistidos && !filme.isAssistido()) {
                continue;
            }
            filtrados.add(filme);
        }

        adapter.atualizarLista(filtrados);
    }

    private void confirmarExclusao(Filme filme) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.confirmar_exclusao_titulo)
                .setMessage(getString(R.string.confirmar_exclusao_mensagem, filme.getNome()))
                .setNegativeButton(R.string.cancelar, null)
                .setPositiveButton(R.string.excluir, (dialog, which) -> excluir(filme))
                .show();
    }

    private void excluir(Filme filme) {
        repositorio.excluir(filme.getId(), new FilmeRepository.OnResultadoListener() {
            @Override
            public void onSucesso() {
                Toast.makeText(
                        MainActivity.this,
                        getString(R.string.excluido, filme.getNome()),
                        Toast.LENGTH_SHORT
                ).show();
            }

            @Override
            public void onErro(@NonNull Exception erro) {
                Toast.makeText(
                        MainActivity.this,
                        R.string.erro_excluir,
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        listenerRegistration = repositorio.observarFilmes(new FilmeRepository.OnFilmesListener() {
            @Override
            public void onFilmes(@NonNull List<Filme> filmes) {
                todosOsFilmes.clear();
                todosOsFilmes.addAll(filmes);
                aplicarFiltro();
            }

            @Override
            public void onErro(@NonNull Exception erro) {
                Toast.makeText(
                        MainActivity.this,
                        R.string.erro_carregar,
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    @Override
    protected void onStop() {
        if (listenerRegistration != null) {
            listenerRegistration.remove();
            listenerRegistration = null;
        }
        super.onStop();
    }
}
