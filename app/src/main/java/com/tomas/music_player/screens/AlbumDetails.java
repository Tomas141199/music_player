package com.tomas.music_player.screens;

import static com.tomas.music_player.MainActivity.musicFiles;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tomas.music_player.R;
import com.tomas.music_player.adapters.AlbumDetailsAdapter;
import com.tomas.music_player.models.MusicFiles;

import java.util.ArrayList;

public class AlbumDetails extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView albumPhoto;
    String albumName;
    TextView tituloAlbum,nombreArtista, agnoAlbum;
    ArrayList<MusicFiles> albumSongs=new ArrayList<>();
    AlbumDetailsAdapter albumDetailsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);

        this.albumPhoto=findViewById(R.id.icono_cancion);
        this.recyclerView=findViewById(R.id.recyclerView);
        this.tituloAlbum=findViewById(R.id.tvTituloAlbum);
        this.albumName=getIntent().getStringExtra("albumNombre");
        this.nombreArtista=findViewById(R.id.tvArtistaAlbum);
        this.agnoAlbum=findViewById(R.id.agnoAlbum);

        int j=0;
        for(int i=0; i<musicFiles.size();i++){
            if(albumName.equals(musicFiles.get(i).getAlbum())){
                albumSongs.add(j,musicFiles.get(i));
                j++;
            }
        }

        byte[] imagen=getAlbumImagen(albumSongs.get(0).getPath());
        if(imagen != null){
            Glide.with(this).asBitmap().load(imagen).into(this.albumPhoto);
        }else {
            Glide.with(this).asBitmap().load(R.drawable.ic_record_vinyl_solid).into(this.albumPhoto);
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
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!(albumSongs.size()<1)){
            albumDetailsAdapter=new AlbumDetailsAdapter(this,albumSongs);
            recyclerView.setAdapter(albumDetailsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));

        }
    }

    private byte[] getAlbumImagen(String uri){
        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] imagen=retriever.getEmbeddedPicture();
        retriever.release();
        return imagen;
    }
}