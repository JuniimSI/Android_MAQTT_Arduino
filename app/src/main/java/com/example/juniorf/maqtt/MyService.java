package com.example.juniorf.maqtt;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.database.ContentObservable;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.example.juniorf.maqtt.data.CountListener;
import com.example.juniorf.maqtt.data.Programacao;
import com.example.juniorf.maqtt.data.ProgramacaoDAO;

import java.util.ArrayList;
import java.util.List;


public class MyService extends Service implements CountListener{

    public List<Programacao> prog = new ArrayList<Programacao>();
    Bundle b;

    private int toL;
    private int toD;
    public int count = 0;
    public boolean ativo = true;
    private Controller controller= new Controller();

    public MyService() {
    }

    public class Controller extends Binder{
        public CountListener getCountListener(){
            return (MyService.this);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return controller;
    }

    public void onCreate(){
        setThread();
        super.onCreate();
    }

    public int onStartCommand(Intent intent, int flags, int startId){

        ProgramacaoDAO programacaoDAO = new ProgramacaoDAO(getApplicationContext());
        prog = programacaoDAO.find();
        for(Programacao p : prog){
            Log.i("Toligar", String.valueOf(p.getToLigar()));
            Log.i("Todesligar", String.valueOf(p.getToDesligar()));
        }

       setThread();
        return (super.onStartCommand(intent, flags, startId));
    }


    public void onDestroy(){
        super.onDestroy();
       ativo = false;
    }

    public void setThread(){
        new Thread(){
            public void run(){
                while(ativo && count>=100){
                    try {
                        Thread.sleep(500);
                        count++;
                        Log.i("COUNT" , ""+count);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    public int getCount() {
        return count;
    }
}
