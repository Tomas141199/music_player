package com.tomas.music_player.screens;

import static com.tomas.music_player.adapters.ArtistDetailsAdapter.cancionEditar;
import static com.tomas.music_player.adapters.AlbumDetailsAdapter.cancionEditarAlbum;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.tomas.music_player.BaseDatos.BDCanciones;
import com.tomas.music_player.BaseDatos.BDImagenes;
import com.tomas.music_player.MainActivity;
import com.tomas.music_player.R;
import com.tomas.music_player.models.Imagen;
import com.tomas.music_player.models.MusicFiles;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class ModificarMetadata extends AppCompatActivity {

    byte[] imagenn;

    EditText tTitulo,tArtista,tAlbum,tFecha;
    ImageView imagenAlbum;
    BDCanciones bd;
    BDImagenes bd_;
    String modo="";

    MusicFiles cancionEditar_;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modificar_metadata);

        this.modo=getIntent().getStringExtra("modo");

        System.out.println("Modo:"+this.modo);

        if(this.modo.equals("artista")){
            //Se entro en modod artisya
            System.out.println("Modo artista");
            cancionEditar_=cancionEditar;
        }else{
            cancionEditar_=cancionEditarAlbum;
        }



        bd=new BDCanciones(this);
        bd_=new BDImagenes(this);
        tTitulo=findViewById(R.id.tituloCancion);
        tArtista=findViewById(R.id.nombre_artista);
        tAlbum=findViewById(R.id.nombre_album);
        tFecha=findViewById(R.id.editTextNumber);
        imagenAlbum=findViewById(R.id.imagenCargada);

        tTitulo.setText(cancionEditar_.getTitle());
        tArtista.setText(cancionEditar_.getArtist());
        tAlbum.setText(cancionEditar_.getAlbum());

        tAlbum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                try {
                    Imagen i = bd_.buscarImagen(tAlbum.getText().toString().replace(" ", "").toLowerCase(Locale.ROOT));
                    if(i!=null){
                        Toast.makeText(ModificarMetadata.this,"Imagen disponible",Toast.LENGTH_SHORT).show();
                        Glide.with(ModificarMetadata.this).asBitmap().load(i.getRuta()).into((ImageView) findViewById(R.id.imagenCargada));
                    }else{
                        findViewById(R.id.imagenCargada).setBackgroundDrawable(null);
                    }
                }catch (Exception e){
                    Log.i("Error","Error en la sección buscar.");
                }
            }
        });

        try{
            tFecha.setText(cancionEditar_.getFecha());
        }catch (Exception e){
            Log.i("Error","Error metadatos");
        }
        findViewById(R.id.btnVolver).setOnClickListener((v)->{
            finish();
        });

        findViewById(R.id.contenedorImagen).setOnClickListener((v)->{
            cargarImagen();
        });

        findViewById(R.id.btnGuardarCambios).setOnClickListener((v)->{guardarCambios();});

        //Cargamos la canción
        Imagen aux= bd_.buscarImagen(cancionEditar_.getAlbum().replace(" ","").toLowerCase(Locale.ROOT));
        if(aux!=null){
            //Imagen encontrada
            Glide.with(this).asBitmap().load(aux.getRuta()).into(imagenAlbum);
        }


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

                Glide.with(this).asBitmap().load(imagenn).into((ImageView) findViewById(R.id.imagenCargada));
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    private void guardarCambios(){
        //Se guardaran los metadatos en la base de datos
        ContentValues valores = new ContentValues();

        valores.put("identificador",cancionEditar_.getId());
        valores.put("titulo",tTitulo.getText().toString());
        valores.put("album",tAlbum.getText().toString());
        valores.put("artista",tArtista.getText().toString());
        valores.put("duracion",cancionEditar_.getDuration());
        valores.put("path",cancionEditar_.getPath());
        valores.put("fecha",tFecha.getText().toString());

        if(this.bd.actualizarCancion(valores,cancionEditar_.getId())>0){
            long r=-1;
            if(imagenn!=null){
                //Guardamos la imagen en la bd
                Imagen aux=null;
                try{
                    aux=bd_.buscarImagen(tAlbum.getText().toString().replace(" ","").toLowerCase());
                }catch (Exception e){
                    Log.i("Error","Error");
                }
                if(aux==null){
                    //Si la imagen no existe se insertara
                    System.out.println("Nombre del album"+tAlbum.getText().toString().replace(" ","").toLowerCase());
                    String album=tAlbum.getText().toString().replace(" ","").toLowerCase();
                    try{
                        r=bd_.insertarImagenes(album,imagenn,1);
                    }catch (Exception e){
                        Log.i("Excepción", String.valueOf(e));
                    }
                }else{
                    //Si la imagen existe se actualiza
                    r=bd_.actualizarImagen(aux.getId(),aux.getNombre(),imagenn);
                }

                if(r<=0){
                    Log.i("Estado inserción/Actualización","Imagen del album no guardada");
                    Toast.makeText(this,"Ha ocurrido un problema.",Toast.LENGTH_LONG).show();
                    return;
                }
            }
            Toast.makeText(this,"La canción se ha actualizado.",Toast.LENGTH_LONG).show();
            MainActivity.getAllAudio(this);
        }else{
            Toast.makeText(this,"Ha ocurrido un problema.",Toast.LENGTH_LONG).show();
        }
    }

}