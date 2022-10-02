package com.tomas.music_player.screens;

import static com.tomas.music_player.MainActivity.musicFiles;

import com.bumptech.glide.Glide;
import com.tomas.music_player.BaseDatos.BDImagenes;
import com.tomas.music_player.R;
import com.tomas.music_player.adapters.ArtistDetailsAdapter;
import com.tomas.music_player.models.Imagen;
import com.tomas.music_player.models.MusicFiles;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

public class ArtistDetails extends AppCompatActivity {
    RecyclerView recyclerView;
    ImageView imageView;
    String artistaNombre;
    TextView nombreArtista;
    ArrayList<MusicFiles> artistSong=new ArrayList<>();
    ArtistDetailsAdapter artistDetailsAdapter;
    BDImagenes bd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artist_details);

        //Vinculamos la tabla de las imagenes
        bd=new BDImagenes(ArtistDetails.this);

        recyclerView=findViewById(R.id.recyclerView);
        artistaNombre=getIntent().getStringExtra("artistNombre");
        nombreArtista=findViewById(R.id.tvArtistaCanciones);

        if(this.artistaNombre.compareTo("<unknown>")==0)
            nombreArtista.setText(R.string.artistaDesconocido);
        else
            nombreArtista.setText(this.artistaNombre);

        imageView=findViewById(R.id.icono_cancion);

        //String nombreBusqueda=artistaNombre.replace(" ","");
        //nombreBusqueda.toLowerCase();
        Imagen c=null;
         c=bd.buscarImagen(artistaNombre.toLowerCase(Locale.ROOT).replace(" ",""));

        if(c==null) {
            System.out.println("Imagen no conseguida");
            Glide.with(this).asBitmap().load(R.drawable.ic_user_solid).into(imageView);
        }
        else{
            System.out.println("Imagen conseguida");
            Glide.with(this).asBitmap().load(c.getRuta()).into(imageView);
        }

        int j=0;
        String aux;
        for(int i=0; i<musicFiles.size();i++){

            aux=musicFiles.get(i).getArtist();
            aux=aux.replace(";","");
            aux=aux.replace(" ","");
            aux=aux.toLowerCase();

            System.out.println(aux+":"+artistaNombre.replace("\\s","").toLowerCase());
            if(aux.contains(artistaNombre.replace(" ","").toLowerCase())){
                artistSong.add(j,musicFiles.get(i));
                j++;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!(artistSong.size()<1)){

            artistDetailsAdapter=new ArtistDetailsAdapter(this,artistSong);
            recyclerView.setAdapter(artistDetailsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        }
    }

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