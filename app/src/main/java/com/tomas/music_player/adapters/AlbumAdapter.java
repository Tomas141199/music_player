package com.tomas.music_player.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tomas.music_player.R;
import com.tomas.music_player.screens.AlbumDetails;
import com.tomas.music_player.models.MusicFiles;

import java.util.ArrayList;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.MyHolder> {

    private Context mContext;
    private ArrayList<MusicFiles> albumFiles;
    View view;

    public AlbumAdapter(Context mContext, ArrayList<MusicFiles> albumFiles){
        this.mContext=mContext;
        this.albumFiles=albumFiles;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view= LayoutInflater.from(mContext).inflate(R.layout.album_item,parent,false);

        return new MyHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.album_titulo.setText(albumFiles.get(position).getAlbum());

        if(albumFiles.get(position).getArtist().compareTo("<unknown>")==0){
            holder.album_artista.setText(R.string.artistaDesconocido);
        }else{
            holder.album_artista.setText(albumFiles.get(position).getArtist());
        }


        byte [] image = getAlbumImagen(albumFiles.get(position).getPath());
        System.out.println(image);
        if(image != null){
            Glide.with(mContext).asBitmap().load(image).into(holder.album_imagen);
        }else {
            Glide.with(mContext).asBitmap().load(R.drawable.ic_record_vinyl_solid).into(holder.album_imagen);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, AlbumDetails.class);
                System.out.println(albumFiles.get(position).getAlbum());
                intent.putExtra("albumNombre",albumFiles.get(position).getAlbum());
                try{
                    mContext.startActivity(intent);
                }catch (Exception e){
                    System.out.println(e);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return albumFiles.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        ImageView album_imagen;
        TextView album_titulo, album_artista;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            album_imagen=view.findViewById(R.id.icono_cancion);
            album_titulo=view.findViewById(R.id.tvTituloAlbum);
            album_artista=view.findViewById(R.id.tvArtistaAlbum);
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
