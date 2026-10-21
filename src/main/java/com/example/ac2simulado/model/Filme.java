package com.example.ac2simulado.model;

public class Filme {

    private String id;
    private String nome;
    private String tipo;
    private String genero;
    private int ano;
    private double nota;
    private boolean assistido;
    private long criadoEm;

    public Filme(String nome, String tipo, String genero, int ano, double nota, boolean assistido) {
        this.nome = nome;
        this.tipo = tipo;
        this.genero = genero;
        this.ano = ano;
        this.nota = nota;
        this.assistido = assistido;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public String getTipo() {
        return tipo;
    }

    public String getGenero() {
        return genero;
    }

    public int getAno() {
        return ano;
    }

    public double getNota() {
        return nota;
    }

    public boolean isAssistido() {
        return assistido;
    }

    public long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(long criadoEm) {
        this.criadoEm = criadoEm;
    }
}
