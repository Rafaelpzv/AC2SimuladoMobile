package com.example.ac2simulado;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ac2simulado.data.FilmeRepository;
import com.example.ac2simulado.model.Filme;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FormActivity extends AppCompatActivity {

    private static final String EXTRA_ID = "id";
    private static final String EXTRA_NOME = "nome";
    private static final String EXTRA_TIPO = "tipo";
    private static final String EXTRA_GENERO = "genero";
    private static final String EXTRA_ANO = "ano";
    private static final String EXTRA_NOTA = "nota";
    private static final String EXTRA_ASSISTIDO = "assistido";
    private static final String EXTRA_CRIADO_EM = "criadoEm";

    private EditText etNome;
    private EditText etAno;
    private EditText etNota;
    private Spinner spinnerTipo;
    private Spinner spinnerGenero;
    private CheckBox checkAssistido;
    private Button btnSalvar;

    private FilmeRepository repositorio;
    private String[] tipos;
    private String[] generos;
    private String id;
    private long criadoEm;

    public static Intent novaIntent(@NonNull Context context, Filme filme) {
        Intent intent = new Intent(context, FormActivity.class);
        if (filme != null) {
            intent.putExtra(EXTRA_ID, filme.getId());
            intent.putExtra(EXTRA_NOME, filme.getNome());
            intent.putExtra(EXTRA_TIPO, filme.getTipo());
            intent.putExtra(EXTRA_GENERO, filme.getGenero());
            intent.putExtra(EXTRA_ANO, filme.getAno());
            intent.putExtra(EXTRA_NOTA, filme.getNota());
            intent.putExtra(EXTRA_ASSISTIDO, filme.isAssistido());
            intent.putExtra(EXTRA_CRIADO_EM, filme.getCriadoEm());
        }
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        repositorio = new FilmeRepository();
        tipos = getResources().getStringArray(R.array.tipos);
        generos = getResources().getStringArray(R.array.generos);

        etNome = findViewById(R.id.etNome);
        etAno = findViewById(R.id.etAno);
        etNota = findViewById(R.id.etNota);
        spinnerTipo = findViewById(R.id.spinnerTipo);
        spinnerGenero = findViewById(R.id.spinnerGenero);
        checkAssistido = findViewById(R.id.checkAssistido);
        btnSalvar = findViewById(R.id.btnSalvar);

        configurarSpinners();

        id = getIntent().getStringExtra(EXTRA_ID);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(id == null ? R.string.novo_titulo : R.string.editar_titulo);
        }

        if (id != null) {
            preencherFormulario();
            btnSalvar.setText(R.string.atualizar);
        }

        btnSalvar.setOnClickListener(v -> salvar());
    }

    private void configurarSpinners() {
        List<String> opcoesTipo = new ArrayList<>();
        opcoesTipo.add(getString(R.string.selecione_tipo));
        Collections.addAll(opcoesTipo, tipos);

        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                opcoesTipo
        );
        adapterTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipo.setAdapter(adapterTipo);

        List<String> opcoesGenero = new ArrayList<>();
        opcoesGenero.add(getString(R.string.selecione_genero));
        Collections.addAll(opcoesGenero, generos);

        ArrayAdapter<String> adapterGenero = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                opcoesGenero
        );
        adapterGenero.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenero.setAdapter(adapterGenero);
    }

    private void preencherFormulario() {
        Intent intent = getIntent();
        etNome.setText(intent.getStringExtra(EXTRA_NOME));
        etAno.setText(String.valueOf(intent.getIntExtra(EXTRA_ANO, 0)));
        etNota.setText(String.valueOf(intent.getDoubleExtra(EXTRA_NOTA, 0)).replace('.', ','));
        selecionarValor(spinnerTipo, intent.getStringExtra(EXTRA_TIPO));
        selecionarValor(spinnerGenero, intent.getStringExtra(EXTRA_GENERO));
        checkAssistido.setChecked(intent.getBooleanExtra(EXTRA_ASSISTIDO, false));
        criadoEm = intent.getLongExtra(EXTRA_CRIADO_EM, 0);
    }

    private void selecionarValor(Spinner spinner, String valor) {
        if (valor == null) {
            return;
        }
        for (int i = 0; i < spinner.getCount(); i++) {
            Object item = spinner.getItemAtPosition(i);
            if (item != null && valor.equals(item.toString())) {
                spinner.setSelection(i);
                return;
            }
        }
    }

    private void salvar() {
        etNome.setError(null);
        etAno.setError(null);
        etNota.setError(null);

        boolean valido = true;
        String erroSpinner = "";

        String nome = etNome.getText().toString().trim();
        if (nome.isEmpty()) {
            etNome.setError(getString(R.string.erro_nome));
            etNome.requestFocus();
            valido = false;
        }

        int posicaoTipo = spinnerTipo.getSelectedItemPosition();
        if (posicaoTipo == 0) {
            erroSpinner = getString(R.string.erro_tipo);
            valido = false;
        }

        int posicaoGenero = spinnerGenero.getSelectedItemPosition();
        if (posicaoGenero == 0) {
            erroSpinner = erroSpinner.isEmpty()
                    ? getString(R.string.erro_genero)
                    : erroSpinner + "\n" + getString(R.string.erro_genero);
            valido = false;
        }

        int ano = 0;
        String anoTexto = etAno.getText().toString().trim();
        if (anoTexto.isEmpty()) {
            etAno.setError(getString(R.string.erro_ano));
            valido = false;
        } else {
            try {
                ano = Integer.parseInt(anoTexto);
                if (ano <= 0) {
                    etAno.setError(getString(R.string.erro_ano));
                    valido = false;
                }
            } catch (NumberFormatException e) {
                etAno.setError(getString(R.string.erro_ano));
                valido = false;
            }
        }

        double nota = 0;
        String notaTexto = etNota.getText().toString().trim().replace(',', '.');
        if (notaTexto.isEmpty()) {
            etNota.setError(getString(R.string.erro_nota));
            valido = false;
        } else {
            try {
                nota = Double.parseDouble(notaTexto);
                if (nota < 0 || nota > 10) {
                    etNota.setError(getString(R.string.erro_nota));
                    valido = false;
                }
            } catch (NumberFormatException e) {
                etNota.setError(getString(R.string.erro_nota));
                valido = false;
            }
        }

        if (!valido) {
            if (!erroSpinner.isEmpty()) {
                Toast.makeText(this, erroSpinner, Toast.LENGTH_LONG).show();
            }
            return;
        }

        final boolean editando = id != null;
        Filme filme = new Filme(
                nome,
                tipos[posicaoTipo - 1],
                generos[posicaoGenero - 1],
                ano,
                nota,
                checkAssistido.isChecked()
        );
        if (editando) {
            filme.setId(id);
            filme.setCriadoEm(criadoEm);
        }

        btnSalvar.setEnabled(false);
        btnSalvar.setText(R.string.salvando);

        repositorio.salvar(filme, new FilmeRepository.OnResultadoListener() {
            @Override
            public void onSucesso() {
                Toast.makeText(
                        FormActivity.this,
                        editando ? R.string.atualizado : R.string.salvo,
                        Toast.LENGTH_SHORT
                ).show();
                finish();
            }

            @Override
            public void onErro(@NonNull Exception erro) {
                btnSalvar.setEnabled(true);
                btnSalvar.setText(editando ? R.string.atualizar : R.string.salvar);
                Toast.makeText(
                        FormActivity.this,
                        R.string.erro_salvar,
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
