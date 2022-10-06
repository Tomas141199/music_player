package com.tomas.music_player.screens;

import static com.tomas.music_player.MainActivity.actualizado;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tomas.music_player.BaseDatos.BDImagenes;
import com.tomas.music_player.R;
import com.tomas.music_player.adapters.AlbumDetailsAdapter;
import com.tomas.music_player.adapters.ArtistDetailsAdapter;
import com.tomas.music_player.adapters.ListaDetailsAdapter;
import com.tomas.music_player.models.Imagen;
import com.tomas.music_player.models.MusicFiles;

import java.util.ArrayList;

public class ListaDetails extends AppCompatActivity {

    RecyclerView recyclerView;
    String nombre;
    int posicionReferencia;
    ArrayList<MusicFiles> songLista=new ArrayList<>();
    ListaDetailsAdapter listaDetailsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_details);

        TextView titulo=findViewById(R.id.tvTituloPlaylist);
        RelativeLayout btnVolver=findViewById(R.id.btnVolver);
        this.posicionReferencia=getIntent().getIntExtra("posicionCancion",-1);
        this.recyclerView=findViewById(R.id.recyclerView);

        btnVolver.setOnClickListener((v)->{finish();});

        this.nombre=getIntent().getStringExtra("playlistNombre");

        //Cargamos los favoritos
        if(this.nombre.equals("favoritos")){

            titulo.setText("Canciones favoritas");

            int j=0;
            for(int i=0; i<actualizado.size();i++){
                if(actualizado.get(i).getFavorito()==1){

                    System.out.println(actualizado.get(i).getFavorito());
                    songLista.add(j,actualizado.get(i));
                    j++;
                }
            }
        }else{
            titulo.setText(nombre);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!(songLista.size()<1)){
            listaDetailsAdapter=new ListaDetailsAdapter(this,songLista, this.posicionReferencia);
            recyclerView.setAdapter(listaDetailsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
        }
    }
}