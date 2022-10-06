package com.tomas.music_player.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tomas.music_player.BaseDatos.BDImagenes;
import com.tomas.music_player.R;
import com.tomas.music_player.models.Imagen;
import com.tomas.music_player.models.MusicFiles;
import com.tomas.music_player.screens.PlayerActivity;

import java.util.ArrayList;
import java.util.Locale;

public class ListaDetailsAdapter extends RecyclerView.Adapter<ListaDetailsAdapter.MyHolder> {

    private Context mContext;
    private int posicionCancion=-1;
    private int posicionReferencia;
    public static ArrayList<MusicFiles> playListSong;
    public static MusicFiles cancionEditarAlbum;

    BDImagenes bd_;
    View view;
    int contador;
    public ListaDetailsAdapter(Context mContext, ArrayList<MusicFiles> albumFiles, int posicionCancion){
        this.mContext=mContext;
        this.playListSong=albumFiles;
        System.out.println(playListSong.size());
        contador=0;
        posicionReferencia=posicionCancion;
        bd_=new BDImagenes(mContext);
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view= LayoutInflater.from(mContext).inflate(R.layout.music_item_dos,parent,false);
        return new MyHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.album_titulo.setText(playListSong.get(position).getTitle());
        System.out.println(playListSong.get(position).getTitle());
        //Buscamos la imagen de la canción
        //Cargamos la canción ya sea una modificada o la original
        Imagen aux= bd_.buscarImagen(playListSong.get(position).getAlbum().replace(" ","").toLowerCase(Locale.ROOT));
        if(aux!=null){
            //Imagen encontrada
            Glide.with(mContext).asBitmap().load(aux.getRuta()).into(holder.album_imagen);
        }else{
            byte [] image = getAlbumImagen(playListSong.get(position).getPath());
            System.out.println(image);
            if(image != null){
                Glide.with(mContext).asBitmap().load(image).into(holder.album_imagen);
            }else {
                Glide.with(mContext).asBitmap().load(R.drawable.ic_record_vinyl_solid).into(holder.album_imagen);
            }
        }

        if(playListSong.get(position).getArtist().compareTo("<unknown>")==0)
            holder.album_artista.setText(R.string.artistaDesconocido);
        else
            holder.album_artista.setText(playListSong.get(position).getArtist());

        holder.numero_cancion.setText(String.valueOf((position+1)));

        holder.masOpciones.setContentDescription(String.valueOf((position)));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PlayerActivity.class);
                intent.putExtra("sender","albumDetails");
                intent.putExtra("position",position);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.playListSong.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        ImageView album_imagen,masOpciones;
        TextView album_titulo,album_artista,numero_cancion;
        public MyHolder(@NonNull View itemView) {
            super(itemView);

            album_imagen=view.findViewById(R.id.music_img);
            album_titulo=view.findViewById(R.id.music_file_name);
            album_artista=view.findViewById(R.id.music_artista_name);
            numero_cancion=view.findViewById(R.id.nCancion);
            masOpciones=view.findViewById(R.id.menuMore);
        }
    }

    private byte[] getAlbumImagen(String uri){
        try{
            MediaMetadataRetriever retriever=new MediaMetadataRetriever();

            retriever.setDataSource(uri);
            byte[] imagen=retriever.getEmbeddedPicture();
            retriever.release();
            return imagen;
        }catch (Exception e){
            System.out.println(e);
        }
        return null;
    }
}
