package com.example.juniorf.maqtt.data;

/**
 * Created by juniorf on 07/12/16.
 */
public class Programacao {

    private int id;
    private int toLigar;
    private int toDesligar;

    public Programacao(){

    }
    public Programacao(int ligar, int desligar){
        this.toLigar = ligar;
        this.toDesligar = desligar;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getToDesligar() {
        return toDesligar;
    }

    public void setToDesligar(int toDesligar) {
        this.toDesligar = toDesligar;
    }

    public int getToLigar() {
        return toLigar;
    }

    public void setToLigar(int toLigar) {
        this.toLigar = toLigar;
    }
}
