package com.tomas.music_player.adapters;

import static com.tomas.music_player.MainActivity.albums;
import static com.tomas.music_player.MainActivity.nombreArtistas;
import static com.tomas.music_player.fragments.ListasFragment.listas;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tomas.music_player.BaseDatos.BDImagenes;
import com.tomas.music_player.BaseDatos.BDLista;
import com.tomas.music_player.R;
import com.tomas.music_player.screens.AlbumDetails;
import com.tomas.music_player.screens.ListaDetails;

public class ListasAdapter extends RecyclerView.Adapter<ListasAdapter.MyHolder> {
    Context mContext;
    View view;

    public ListasAdapter(Context mContext){
        this.mContext=mContext;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view= LayoutInflater.from(mContext).inflate(R.layout.item_lista,parent,false);
        return new ListasAdapter.MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.tituloLista.setText(listas.get(position).getNombre());

        //Agregamos el listener al presionar
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, ListaDetails.class);
                System.out.println(albums.get(position).getAlbum());
                intent.putExtra("playlistNombre","favoritos");
                intent.putExtra("posicionCancion",position);
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
        return listas.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {
        TextView tituloLista;
        ImageView imagenPlaylist;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            tituloLista=view.findViewById(R.id.nombrePlaylist);
            imagenPlaylist=view.findViewById(R.id.iconoPlaylist);
        }

    }
}
