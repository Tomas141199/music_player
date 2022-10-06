package com.tomas.music_player.screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.tomas.music_player.BaseDatos.BDImagenes;
import com.tomas.music_player.R;
import com.tomas.music_player.models.Imagen;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CargarImagen extends AppCompatActivity {

    ImageView imagen;
    CardView btnCargarImagen;
    RelativeLayout btnGuardarImagen,btnVolver;
    TextView titulo;
    String nombre="";
    BDImagenes bd;
    byte imagenn[];
    Uri uriPath;
    int id=-1,tipo=-1;
    boolean actualizar;
    Imagen imagenArtista;

    //Shared preferences atributos
    int idImagen=0,tipoImagen=0;
    String nombreArtista="";
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cargar_imagen);

        this.bd=new BDImagenes(CargarImagen.this);
        this.imagen=findViewById(R.id.imagenCargada);
        this.btnVolver=findViewById(R.id.btnVolver);
        this.btnCargarImagen=findViewById(R.id.btnCargarImagen);
        this.btnGuardarImagen=findViewById(R.id.btnGuardarImagen);
        this.titulo=findViewById(R.id.tituloSeccion);

        //Obtenemos el archivo getPreferences
        SharedPreferences sp=getSharedPreferences("datosImagen",this.MODE_PRIVATE);
        this.idImagen=sp.getInt("idImagen",-1);
        this.tipoImagen=sp.getInt("tipo",-1);
        this.nombreArtista=sp.getString("nombre","");
        if(idImagen!=-1){
            Log.i("Modo de carga","Modo edición");

            this.titulo.setText(R.string.actualizarImagen);
            actualizar=true;
            //Buscamos la imagen del artista y la mostramos
            imagenArtista=bd.buscarImagen(this.nombreArtista);
            if(imagenArtista==null){
                Glide.with(this).asBitmap().load(R.drawable.ic_image_regular).into(imagen);
            } else{
                System.out.println("Id de la image"+this.idImagen);
                Glide.with(this).asBitmap().load(imagenArtista.getRuta()).into(imagen);
            }
        }else{
            actualizar=false;
            Log.i("Modo de carga","Modo inserción");
        }

        this.btnCargarImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarImagen();
            }
        });


        this.btnGuardarImagen.setOnClickListener((v)->{
            guardarImagen();
        });

        this.btnVolver.setOnClickListener((v)->{
            finish();
        });
    }

    private void cargarImagen(){
        Intent intent=new Intent (Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/");
        startActivityForResult(intent.createChooser(intent,"Selecciona la aplicación"),10);
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode==RESULT_OK){

            Uri uri = data.getData();
            InputStream iStream = null;
            try {
                iStream = getContentResolver().openInputStream(uri);
                imagenn = getBytes(iStream);

                Glide.with(this).asBitmap().load(imagenn).into(imagen);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void guardarImagen(){
        nombre=nombre.replace(" ","");
        nombre=nombre.toLowerCase();

        long r=-1;
        if(this.actualizar!=false){

            r=bd.actualizarImagen(this.idImagen,this.nombreArtista,imagenn);
        }else{
            System.out.println("Nombre artista:"+this.nombreArtista);
            r=bd.insertarImagenes(this.nombreArtista.replace(" ","").toLowerCase(),imagenn,0);
        }
        if(r>0) {
            Log.i("Estado inserción/Actualización", "Imagen guardada");
            if(actualizar==true)
                Toast.makeText(CargarImagen.this,"La imagen se ha actualizado.",Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(CargarImagen.this,"La imagen se ha guardado.",Toast.LENGTH_SHORT).show();

        }
        else{
            Log.i("Estado inserción/Actualización","Imagen no guardada");
            Toast.makeText(CargarImagen.this,"Ha surgidon un problema.",Toast.LENGTH_SHORT);
        }
    }

    //Obtenemos los bytes de la imagen para almacenarla en la bd
    public byte[] getBytes(InputStream inputStream) throws IOException, IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}