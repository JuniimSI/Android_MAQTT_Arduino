package com.example.juniorf.maqtt;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.juniorf.maqtt.data.CountListener;
import com.example.juniorf.maqtt.data.Programacao;
import com.example.juniorf.maqtt.data.ProgramacaoDAO;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;
import java.util.List;
import com.example.juniorf.maqtt.MyService.Controller;


public class MainActivity extends AppCompatActivity implements ServiceConnection{

    private int JAINSERIDO = 0;
    public static int VALOR_PARA_DESLIGAR;
    public static int VALOR_PARA_LIGAR;
    public static int count;
    private static int TEMPERATURA_DO_AR;
    private static int TEMPERATURA_DO_AMBIENTE;
    private CountListener cl;
    private ServiceConnection connection;
    private static final String TAG ="_____________" ;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private String topicReceive = "";
    private String getTopicStr = "iot/control";
    private String topicStr = "iot/sensor/temp1";
    private static String topicStr2 = "iot/control";

    static MqttAndroidClient client;
    private int last;
    TextView txTemp;
    TextView txTempL;
    ProgramacaoDAO pDAO;
    ArrayList<Programacao> pro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        connection = this;
        pDAO = new ProgramacaoDAO(getApplicationContext());
        pro = pDAO.find();
       /// Programacao p = pro.get(0);


        for(int i = 0; i < pro.size(); i++){
            VALOR_PARA_DESLIGAR =pro.get(i).getToDesligar();
            VALOR_PARA_LIGAR = pro.get(i).getToLigar();
        }

        Log.i(" VALOR_PARA_DESLIGAR",  VALOR_PARA_DESLIGAR+"______________________________________");
        Log.i(" VALOR_PARA_LIGAR",  VALOR_PARA_LIGAR+"__________________________");


        setContentView(R.layout.activity_main);
        last = 0;
        txTemp = (TextView) findViewById(R.id.textViewTemperaturaAr);
        String clientId = MqttClient.generateClientId();
        client  = new MqttAndroidClient(this.getApplicationContext(), "tcp://10.42.0.1:1883",
                        clientId);

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                    setSubscription();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        TEMPERATURA_DO_AR = getTemperaturaAr();
        TEMPERATURA_DO_AMBIENTE = getTemperaturaAmbiente();

    }

    private int getTemperaturaAmbiente() {
        return 25;
    }

    private int getTemperaturaAr() {
        return 22;
    }


    public void ligar(View view) {

        switch (last) {
            case 0: {
                pub("l");
                last = 1;
                break;
            }
            case 1: {
                pub("d");
                last = 0;
                break;
            }
            default:
                last = 1;
        }
    }


    public void aumentarTemperatura(View view){
        int n = TEMPERATURA_DO_AR+1;
        TEMPERATURA_DO_AR++;
        pub("aumentarTemperatura");

    }


    public void enviarTemperatura(View view){

        EditText s = (EditText) findViewById(R.id.enviarTemperatura);
        String n = s.getText().toString();
        if(Integer.parseInt(n) > 30 || Integer.parseInt(n) < 16){
            Toast.makeText(this, "Os valores suportado pelo ar está entre 16 e 30", Toast.LENGTH_SHORT).show();
            return;
        }else {
            pub(n);
        }
    }


    public void inserir(View view){
        ProgramacaoDAO pDAO = new ProgramacaoDAO(getApplicationContext());
        pDAO.insert(new Programacao(20, 20));
    }

    public void programar(View view){
        EditText edLigar = (EditText) findViewById(R.id.edTemperaturaLigar);
        String toLigar = edLigar.getText().toString();
        EditText edDesligar = (EditText) findViewById(R.id.edTemperaturaDesligar);
        String toDesligar = edDesligar.getText().toString();

        if(toDesligar!=null && toLigar!=null ) {
            List<Programacao> pro = new ArrayList<Programacao>();
            pro = pDAO.find();
            Programacao p = pro.get(0);
            p.setToLigar(Integer.parseInt(toLigar));
            p.setToDesligar(Integer.parseInt(toDesligar));
            pDAO.update(p);

            pro = pDAO.find();
            for(int i = 0; i < pro.size(); i++)
                Log.i("Kk", pro.get(i).getToDesligar() + " " + pro.get(i).getToLigar());
            Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Preencha os campos..", Toast.LENGTH_SHORT).show();
        }
        //CRIAR SERVIÇO PRA MONITORAR A TEMPERATURA
    }

    /*public void startServi(){
        if(JAINSERIDO == 0){
            ProgramacaoDAO pDAO = new ProgramacaoDAO(getApplicationContext());
            pDAO.insert(new Programacao(25, 20));
            JAINSERIDO = 1;
        }
        Intent i = new Intent("SERVICE");
        bindService(i, connection, Context.BIND_AUTO_CREATE);
    }*/


    /*public void stopService(){
        unbindService(connection);
    }*/
    public void diminuirTemperatura(View view){
        int n = TEMPERATURA_DO_AR-1;
        TEMPERATURA_DO_AR--;
        pub("diminuirTemperatura");
    }

    public static void pub(String mensagem){
        String topic = topicStr2;
        String payload = mensagem;
        try {
            client.publish(topic, payload.getBytes(), 0, false);
        } catch ( MqttException e) {
            e.printStackTrace();
        }
    }

    public void setSubscription(){
        try{
            client.subscribe(topicStr, 0);
        }catch (MqttException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {



        Toast.makeText(this, "START", Toast.LENGTH_SHORT).show();        super.onStart();
    }

    public void connection(String v){
        Log.i("SDLASDL", v+"NuPre");
        String clientId = MqttClient.generateClientId();
        Log.i("SDLASDL", v+"NuPos");
        client  = new MqttAndroidClient(this.getApplicationContext(), "tcp://"+v+":1883",
                clientId);
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                    setSubscription();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }

    public void disconnect(){
        try {
            IMqttToken token = client.disconnect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "DISCONECTED");
                    //setSubscription();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "FALHA NNAS DISCONEXAO");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public String showInput(){
        final String[] n = {""};

        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View pront = layoutInflater.inflate(R.layout.ip, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(pront);

        final EditText editText = (EditText) pront.findViewById(R.id.ip);
        alertDialogBuilder.setCancelable(false).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                n[0] = editText.getText().toString();
                connection(n[0]);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
        //
        return n[0];
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            showInput();

            //connection(v);
            return true;

        }
        if(id == R.id.action_desconectar){
            disconnect();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Controller c = (Controller) service;
        cl = c.getCountListener();
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {

    }



    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            final int[] n = new int[1];
            int is = getArguments().getInt(ARG_SECTION_NUMBER);
            if(is==1){
                View rootView = inflater.inflate(R.layout.fragment_control, container, false);
                return rootView;
            }
            if(is==2){
                View rootViews = inflater.inflate(R.layout.fragment_liga, container, false);

                return rootViews;
            }
            if(is==3){
                View rootViewz = inflater.inflate(R.layout.fragment_detalhe, container, false);
                final TextView txTemp = (TextView)rootViewz.findViewById(R.id.textViewTemperaturaAr);
                client.setCallback(new MqttCallback() {

                    @Override
                    public void connectionLost(Throwable cause) {

                    }

                    @Override
                    public void messageArrived(String topic, MqttMessage message) throws Exception {
                        String t1 = new String(message.toString() + "°C");
                        String ts1 = message.toString();
                        txTemp.setText(t1);
                        Log.i("T!111", ""+t1);
                        count++;
                        Log.i(" VALOR_PARA_DESLIGAR",  VALOR_PARA_DESLIGAR+"");
                        Log.i(" VALOR_PARA_LIGAR",  VALOR_PARA_LIGAR+"");
                        Log.i("COUNT", "+"+count);
                        if(Integer.parseInt(ts1) < VALOR_PARA_DESLIGAR){
                            Log.i("Entra", "ENTRA");
                             if(count % 15 == 0) {
                                 Log.i("Entra", "ENTRA NO IF do %");
                                 pub("d");

                             }
                        }
                        if(Integer.parseInt(ts1) > VALOR_PARA_LIGAR){
                            if(count % 15 == 0)
                                pub("l");

                        }
                        if(count >= 225) count = 0;
                    }

                    @Override
                    public void deliveryComplete(IMqttDeliveryToken token) {

                    }
                });

               /* TextView txAmbiente = (TextView)rootViewz.findViewById(R.id.textViewTemperaturaAmbiente);
                txAmbiente.setText(TEMPERATURA_DO_AMBIENTE+"ºC");*/
                return rootViewz;
            }
            return null;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "PROGRAMER";
                case 1:
                    return "MAIN";
                case 2:
                    return "DETAILS";
            }
            return null;
        }
    }
}
