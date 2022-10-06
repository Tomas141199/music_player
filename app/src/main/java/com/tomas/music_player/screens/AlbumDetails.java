package com.tomas.music_player.screens;

import static com.tomas.music_player.MainActivity.actualizado;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tomas.music_player.BaseDatos.BDImagenes;
import com.tomas.music_player.R;
import com.tomas.music_player.adapters.AlbumDetailsAdapter;
import com.tomas.music_player.models.Imagen;
import com.tomas.music_player.models.MusicFiles;

import java.util.ArrayList;
import java.util.Locale;

public class AlbumDetails extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView albumPhoto;
    String albumName;
    TextView tituloAlbum,nombreArtista, agnoAlbum;
    ArrayList<MusicFiles> albumSongs=new ArrayList<>();
    AlbumDetailsAdapter albumDetailsAdapter;
    int posicionReferencia;
    BDImagenes bd_;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);

        bd_=new BDImagenes(this);
        this.albumPhoto=findViewById(R.id.icono_cancion);
        this.recyclerView=findViewById(R.id.recyclerView);
        this.tituloAlbum=findViewById(R.id.tvTituloAlbum);
        this.albumName=getIntent().getStringExtra("albumNombre");
        this.nombreArtista=findViewById(R.id.tvArtistaAlbum);
        this.posicionReferencia=getIntent().getIntExtra("posicionCancion",-1);
        this.agnoAlbum=findViewById(R.id.agnoAlbum);

        int j=0;
        for(int i=0; i<actualizado.size();i++){
            if(albumName.equals(actualizado.get(i).getAlbum())){
                albumSongs.add(j,actualizado.get(i));
                j++;
            }
        }

        //Buscamos la imagen de la canción
        //Cargamos la canción ya sea una modificada o la original
        Imagen aux= bd_.buscarImagen(this.albumName.replace(" ","").toLowerCase(Locale.ROOT));
        if(aux!=null){
            //Imagen encontrada
            Glide.with(this).asBitmap().load(aux.getRuta()).into(this.albumPhoto);
        }else{
            byte[] imagen=getAlbumImagen(albumSongs.get(0).getPath());
            if(imagen != null){
                Glide.with(this).asBitmap().load(imagen).into(this.albumPhoto);
            }else {
                Glide.with(this).asBitmap().load(R.drawable.ic_record_vinyl_solid).into(this.albumPhoto);
            }
        }

        this.tituloAlbum.setText(this.albumName);

        if(albumSongs.get(0).getArtist().compareTo("<unknown>")==0) {
            this.nombreArtista.setText(R.string.artistaDesconocido);
        }
        else{
            this.nombreArtista.setText(albumSongs.get(0).getArtist());
        }

        if(albumSongs.get(0).getFecha()!=null)
            this.agnoAlbum.setText(albumSongs.get(0).getFecha());
        else
            this.agnoAlbum.setText(R.string.sinFecha);

        findViewById(R.id.btnVolver).setOnClickListener((v)->{
            finish();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!(albumSongs.size()<1)){
            albumDetailsAdapter=new AlbumDetailsAdapter(this,albumSongs, this.posicionReferencia);
            recyclerView.setAdapter(albumDetailsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        }
    }

    public void actualizar(){
        onResume();
    }

    private byte[] getAlbumImagen(String uri){
        try{
            MediaMetadataRetriever retriever=new MediaMetadataRetriever();
            retriever.setDataSource(uri);
            byte[] imagen=retriever.getEmbeddedPicture();
            retriever.release();
            return imagen;
        }catch (Exception e){}

        return null;
    }
}