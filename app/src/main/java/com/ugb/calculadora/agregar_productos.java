package com.ugb.calculadora;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

public class agregar_productos extends AppCompatActivity {
    String resp, accion, id, rev;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_productos);
        try {FloatingActionButton btnMostrarProductos = findViewById(R.id.btnMostrarProductos);
            btnMostrarProductos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    guardarAmigo();
                }
            });
            mostrarDatosProducto();

        }catch (Exception ex ){
            Toast.makeText(getApplicationContext(), "Error al agregar amigos: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
    }

}
    void mostrarDatosProducto(){
        try {
            Bundle recibirParametros = getIntent().getExtras();
            accion = recibirParametros.getString("accion");
            if (accion.equals("modificar")){
                JSONObject dataProducto = new JSONObject(recibirParametros.getString("dataProducto")).getJSONObject("value");

                TextView tempVal = (TextView)findViewById(R.id.txtNombre);
                tempVal.setText(dataProducto.getString("Nombre"));

                tempVal = (TextView)findViewById(R.id.txtMarca);
                tempVal.setText(dataProducto.getString("Marca"));

                tempVal = (TextView)findViewById(R.id.txtCategoria);
                tempVal.setText(dataProducto.getString("Categoria"));

                tempVal = (TextView)findViewById(R.id.txtPrecio);
                tempVal.setText(dataProducto.getString("Precio"));


                id = dataProducto.getString("_id");
                rev = dataProducto.getString("_rev");
            }
        }catch (Exception ex){
            ///
        }
    }
    private void mostrarProductos(){
        Intent mostrarProductos = new Intent(agregar_productos.this, MainActivity.class);
        startActivity(mostrarProductos);
    }
    private void guardarAmigo(){
        TextView tempVal = findViewById(R.id.txtNombre);
        String Nombre = tempVal.getText().toString();

        tempVal = findViewById(R.id.txtMarca);
        String Marca = tempVal.getText().toString();

        tempVal = findViewById(R.id.txtCategoria);
        String Categoria = tempVal.getText().toString();

        tempVal = findViewById(R.id.txtPrecio);
        String Precio = tempVal.getText().toString();


        try {
            JSONObject datosProducto = new JSONObject();
            if (accion.equals("modificar")){
                datosProducto.put("_id",id);
                datosProducto.put("_rev",rev);
            }
            datosProducto.put("Nombre del producto", Nombre);
            datosProducto.put("Marca", Marca);
            datosProducto.put("Categoria", Categoria);
            datosProducto.put("Precio", Precio);

            enviarDatosProducto objGuardarProducto = new enviarDatosProducto();
            objGuardarProducto.execute(datosProducto.toString());
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(), "Error: "+ ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private class enviarDatosProducto extends AsyncTask<String,String, String> {
        HttpURLConnection urlConnection;
        @Override
        protected String doInBackground(String... parametros) {
            StringBuilder stringBuilder = new StringBuilder();
            String jsonResponse = null;
            String jsonDatos = parametros[0];
            BufferedReader reader;
            try {
                URL url = new URL("Http://10.0.2.2:5984/db_tienda");
                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setDoInput(true);
                urlConnection.setDoOutput(true);

                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.setRequestProperty("Accept","application/json");

                Writer writer = new BufferedWriter(new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8"));
                writer.write(jsonDatos);
                writer.close();

                InputStream inputStream = urlConnection.getInputStream();
                if(inputStream==null){
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                resp = reader.toString();

                String inputLine;
                StringBuffer stringBuffer = new StringBuffer();
                while ((inputLine=reader.readLine())!= null){
                    stringBuffer.append(inputLine+"\n");
                }
                if(stringBuffer.length()==0){
                    return null;
                }
                jsonResponse = stringBuffer.toString();
                return jsonResponse;
            }catch (Exception ex){
                //
            }
            return null;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try{
                JSONObject jsonObject = new JSONObject(s);
                if(jsonObject.getBoolean("ok")){
                    Toast.makeText(getApplicationContext(), "Datos de producto guardado con exito", Toast.LENGTH_SHORT).show();
                    mostrarProductos();
                } else {
                    Toast.makeText(getApplicationContext(), "Error al intentar guardar datos de producto", Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "Error al guardar producto: "+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

}