package com.example.juniorf.maqtt.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by juniorf on 07/12/16.
 */

public class ProgramacaoDAO extends AbstractDAO<Programacao> {

    public ProgramacaoDAO(Context context) {
        super(context);
    }

    @Override
    public void insert(Programacao programacao) {
        SQLiteDatabase database = this.mySQLiteOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        int s = programacao.getToLigar();
        int x = programacao.getToDesligar();
        values.put("toLigar", s);
        values.put("toDesligar", x);
        database.insert("programacao", null, values);
        Log.i("kaksdkasd", "TUDO CERTO");
        database.close();
    }


    @Override
    public void remove(int id) {

    }

    public ArrayList<Programacao> find(){
        ArrayList<Programacao> locations = new ArrayList<>();
        SQLiteDatabase database = this.mySQLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select toLigar, toDesligar from programacao", null);
        Log.i("CURSOR", ""+cursor.toString());
        while(cursor.moveToNext()){
            Programacao p = new Programacao();
            p.setToLigar(cursor.getInt(0));
            p.setToDesligar(cursor.getInt(1));

            locations.add(p);
        }
        cursor.close();
        database.close();
        return locations;
    }

    public void update(Programacao programacao) {

        SQLiteDatabase database = this.mySQLiteOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        int s = programacao.getToLigar();
        int x = programacao.getToDesligar();
        values.put("toLigar", s);
        values.put("toDesligar", x);
        Log.i("VALORES do update" , s+" "+x+" "+ String.valueOf(programacao.getId()));
        database.update("programacao", values, "id=?", new String[]{"1"});
        Log.i("KK", "UPDAEITAE");
        database.close();
    }

    @Override
    public Programacao findById(int id) {
        return null;
    }

    @Override
    public ArrayList findAll() {
        return null;
    }
}
