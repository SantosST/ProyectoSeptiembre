package com.example.proyectoseptiembre;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class navegacion extends AppCompatActivity {

    private TextView mTextMessage;
    ListView  lstvListaProf;

    List<EstructuraDatos> ListaAlmacenarProfesores= new ArrayList<EstructuraDatos>();
    List<EstructuraDatos> ListaAlmacenarProfesoresVacia= new ArrayList<EstructuraDatos>();
    ArrayList<String> ListaProfesores = new ArrayList<String>();
    Spinner Materias;
    String MateriaElegida;
    Button btnTraerLista;
    Adaptador Adaptadorcito;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    Home();

                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText("Busqueda de profesores");
                    BuscarProfesores();
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    Not();
                    return true;
                case R.id.navigation_perfil:
                    mTextMessage.setText("Perfil");
                    IrAPerfil();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navegacion);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
    public void Not(){
        ObtenerReferencias();
        SetearListener();
        Materias.setVisibility(View.INVISIBLE);
        lstvListaProf.setVisibility(View.INVISIBLE);
        btnTraerLista.setVisibility(View.INVISIBLE);
    }
    public void Home (){
        ObtenerReferencias();
        SetearListener();
        Materias.setVisibility(View.INVISIBLE);
        lstvListaProf.setVisibility(View.INVISIBLE);
        btnTraerLista.setVisibility(View.INVISIBLE);
    }
    public void BuscarProfesores(){

        ObtenerReferencias();
        SetearListener();
        Materias.setVisibility(View.VISIBLE);
        lstvListaProf.setVisibility(View.VISIBLE);
        btnTraerLista.setVisibility(View.VISIBLE);
        String[] items = new String[]{"Matematica", "Lengua", "Ingles","Base de Datos"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        Materias.setAdapter(adapter);
        Adaptadorcito = new Adaptador(getApplicationContext(),ListaAlmacenarProfesores);

    }

    public void IrAPerfil(){
        FragmentManager adminFragment;
        FragmentTransaction transacFragment;
        PerfilFragment elFragmentPerfil= new PerfilFragment();
        adminFragment = getFragmentManager();
        transacFragment = adminFragment.beginTransaction();
        transacFragment.replace(R.id.navigation, elFragmentPerfil);
        transacFragment.commit();
    }
    private void SetearListener() {
        btnTraerLista.setOnClickListener(btnTraer_Click);

    }



    private void ObtenerReferencias(){
        lstvListaProf= (ListView)findViewById(R.id.lstvListaProg);
        Materias = findViewById(R.id.spnMateria);
        btnTraerLista = findViewById(R.id.btnTraerLista);

    }
    private View.OnClickListener btnTraer_Click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ListaAlmacenarProfesores.clear();
            String text = Materias.getSelectedItem().toString();
            MateriaElegida = text;
            tareaAsincronica miTarea = new tareaAsincronica();
            miTarea.execute();
        }

    };

    private class tareaAsincronica extends AsyncTask<Void,Void,Void> {
        String strHOST = "http://10.0.2.2:80?FuncionAutilizar=profesores&numerito="+traerMateria() ;


        //String strHOST = "http://profesores.com.ar";
        protected Void doInBackground(Void... voids){
            try{
                URL miRuta= new URL (strHOST);
                HttpURLConnection miConexion=(HttpURLConnection) miRuta.openConnection();
                /*String numerito = "2";


                miConexion.setRequestMethod("GET");
                miConexion.setRequestProperty("Content-Type", "raw; charset=utf-8");
                miConexion.setRequestProperty("Charset", "UTF-8");
                miConexion.setDoOutput(true);
                miConexion.setReadTimeout(15000);
                miConexion.setConnectTimeout(15000);

                miConexion.setDoInput(true);
                miConexion.setDoOutput(true);

                DataOutputStream os = new DataOutputStream(miConexion.getOutputStream());
                os.write(("?numerito=" + traerMateria()).getBytes("UTF-8"));
                os.flush();


                os.close(); */
                Log.d("AccesoAPI","Me conecto");
                if (miConexion.getResponseCode()==200){
                    Log.d("AccesoAPI", "ConexionOK");
                    InputStream cuerpoRespuesta =miConexion.getInputStream();
                    InputStreamReader lectorRespuesta = new InputStreamReader(cuerpoRespuesta, "UTF-8");

                    procesarJSONLeido(lectorRespuesta);
                }else{
                    Log.d("AccesoApi", "ConexionOK");
                }
                miConexion.disconnect();
            }catch(MalformedURLException error){
                Log.d("AccesoAPI","Error 1 en la conexión"+error.getMessage());
            } catch (IOException error) {
                Log.d("AccesoAPI","Error 2 en la conexión"+error.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            lstvListaProf.setAdapter(Adaptadorcito);
        }
    }
    public String traerMateria(){
        String Mat = "";

        switch (MateriaElegida){
            case "Matematica":
                Mat = "1";
                break;
            case "Lengua":
                Mat = "2";
                break;
            case "Ingles":
                Mat = "3";
                break;
            case "Base de Datos":
                Mat = "4";
                break;

        }

        return Mat;
    }


    public Void procesarJSONLeido(InputStreamReader streamLeido)
    {

        JsonReader JSONLeido = new JsonReader(streamLeido);
        Log.d("LecturaJson", "El stream leido es " + JSONLeido);
        try
        {
            Log.d("LecturaJson","empezo el array 1");
            JSONLeido.beginArray();
            Log.d("LecturaJson","empezo el array 2");
            while (JSONLeido.hasNext()) {
                Log.d("LecturaJson","tiene algo");
                int ID = -1;
                String nombre = null;
                int PrecioBase = -1;
                String Localizacion_X = null;
                String NombreMateria = null;




                JSONLeido.beginObject();
                while (JSONLeido.hasNext()) {

                    //Log.d("ACCESOAPI","Estoy en: " + JSONLeido);
                    String nombreElementoActual = JSONLeido.nextName();
                    Log.d("LecturaJson", "aaaaaaaaaaaaaa: " + nombreElementoActual);

                    if (nombreElementoActual.equals("id")){
                        ID = Integer.parseInt(JSONLeido.nextString());
                        Log.d("LecturaJson","ID leido: " + ID);
                    }
                    else if (nombreElementoActual.equals("Nombre")) {
                        nombre = JSONLeido.nextString();
                        Log.d("LecturaJson","ID leido: " + nombre);
                    }

                    else if (nombreElementoActual.equals("PrecioBase")) {
                        PrecioBase = Integer.parseInt(JSONLeido.nextString());
                        Log.d("LecturaJson","ID leido: " + PrecioBase);
                    }


                    else if (nombreElementoActual.equals("Localizacion_X")) {
                        Localizacion_X = JSONLeido.nextString();
                        Log.d("LecturaJson","ID leido: " + Localizacion_X);
                    } else if (nombreElementoActual.equals("NombreMateria")) {
                        NombreMateria = JSONLeido.nextString();
                        Log.d("LecturaJson","ID leido: " + NombreMateria);
                    }
                    else {
                        JSONLeido.skipValue();
                    }

                }
                Log.d("LecturaJson", NombreMateria + "    " + MateriaElegida);
                if (
                        (ID != -1) &&
                                (nombre != null) &&
                                (Localizacion_X != null) &&
                                (PrecioBase != -1 ) &&
                                (NombreMateria != null) &&
                                NombreMateria.equals(MateriaElegida)
                ) {
                    Log.d("LecturaJson", "Agregamos");
                    ListaAlmacenarProfesores.add(new EstructuraDatos(ID,nombre,NombreMateria,PrecioBase,R.drawable.profesor,Localizacion_X));
                    Log.d("adsadsad", ""+ListaAlmacenarProfesores.size());
                }

                JSONLeido.endObject();

            }
            JSONLeido.endArray();
        } catch (IOException error)
        {
            Log.d("AccesoAPI","Error 3 en la conexión"+error.getMessage());
        }
        return null;
    }


}
