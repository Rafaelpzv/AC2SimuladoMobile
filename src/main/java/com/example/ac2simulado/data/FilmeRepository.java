package com.example.ac2simulado.data;

import androidx.annotation.NonNull;

import com.example.ac2simulado.model.Filme;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilmeRepository {

    private final CollectionReference colecao;

    public FilmeRepository() {
        colecao = FirebaseFirestore.getInstance().collection("midias");
    }

    public interface OnFilmesListener {
        void onFilmes(@NonNull List<Filme> filmes);

        void onErro(@NonNull Exception erro);
    }

    public interface OnResultadoListener {
        void onSucesso();

        void onErro(@NonNull Exception erro);
    }

    public ListenerRegistration observarFilmes(@NonNull OnFilmesListener callback) {
        return colecao.orderBy("criadoEm", Query.Direction.DESCENDING)
                .addSnapshotListener((snapshots, erro) -> {
                    if (erro != null) {
                        callback.onErro(erro);
                        return;
                    }

                    List<Filme> filmes = new ArrayList<>();
                    if (snapshots != null) {
                        for (DocumentSnapshot documento : snapshots.getDocuments()) {
                            Filme filme = converter(documento);
                            if (filme != null) {
                                filmes.add(filme);
                            }
                        }
                    }
                    callback.onFilmes(filmes);
                });
    }

    public void salvar(@NonNull Filme filme, @NonNull OnResultadoListener callback) {
        Map<String, Object> dados = new HashMap<>();
        dados.put("nome", filme.getNome());
        dados.put("tipo", filme.getTipo());
        dados.put("genero", filme.getGenero());
        dados.put("ano", filme.getAno());
        dados.put("nota", filme.getNota());
        dados.put("assistido", filme.isAssistido());
        dados.put("criadoEm", filme.getCriadoEm() > 0
                ? filme.getCriadoEm()
                : System.currentTimeMillis());

        if (filme.getId() == null) {
            colecao.add(dados)
                    .addOnSuccessListener(ref -> callback.onSucesso())
                    .addOnFailureListener(callback::onErro);
        } else {
            colecao.document(filme.getId())
                    .set(dados)
                    .addOnSuccessListener(ref -> callback.onSucesso())
                    .addOnFailureListener(callback::onErro);
        }
    }

    public void excluir(@NonNull String id, @NonNull OnResultadoListener callback) {
        colecao.document(id)
                .delete()
                .addOnSuccessListener(ref -> callback.onSucesso())
                .addOnFailureListener(callback::onErro);
    }

    private Filme converter(@NonNull DocumentSnapshot documento) {
        String nome = documento.getString("nome");
        if (nome == null) {
            return null;
        }

        Long ano = documento.getLong("ano");
        Double nota = documento.getDouble("nota");
        Boolean assistido = documento.getBoolean("assistido");
        Long criadoEm = documento.getLong("criadoEm");

        Filme filme = new Filme(
                nome,
                valorOuVazio(documento.getString("tipo")),
                valorOuVazio(documento.getString("genero")),
                ano == null ? 0 : ano.intValue(),
                nota == null ? 0 : nota,
                assistido != null && assistido
        );
        filme.setId(documento.getId());
        filme.setCriadoEm(criadoEm == null ? 0 : criadoEm);
        return filme;
    }

    private String valorOuVazio(String valor) {
        return valor == null ? "" : valor;
    }
}
