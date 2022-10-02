package com.tomas.music_player.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tomas.music_player.R;
import com.tomas.music_player.models.MusicFiles;
import com.tomas.music_player.screens.AlbumDetails;
import com.tomas.music_player.screens.PlayerActivity;

import java.util.ArrayList;

public class ArtistDetailsAdapter extends RecyclerView.Adapter<ArtistDetailsAdapter.MyHolder>{
    private Context mContext;
    public static ArrayList<MusicFiles> artistDetails;
    View view;
    private int posicionCancion=-1;


    int contador;
    public ArtistDetailsAdapter(Context mContext, ArrayList<MusicFiles> albumFiles){
        this.mContext=mContext;
        this.artistDetails=albumFiles;
        contador=0;
    }

    @NonNull
    @Override
    public ArtistDetailsAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view= LayoutInflater.from(mContext).inflate(R.layout.music_item_dos,parent,false);

        return new ArtistDetailsAdapter.MyHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ArtistDetailsAdapter.MyHolder holder, @SuppressLint("RecyclerView") int position) {


        holder.album_titulo.setText(artistDetails.get(position).getTitle());

        byte [] image = getAlbumImagen(artistDetails.get(position).getPath());
        System.out.println(image);
        if(image != null){
            Glide.with(mContext).asBitmap().load(image).into(holder.album_imagen);
        }else {
            Glide.with(mContext).asBitmap().load(R.drawable.ic_record_vinyl_solid).into(holder.album_imagen);
        }

        if(artistDetails.get(position).getArtist().compareTo("<unknown>")==0)
            holder.album_artista.setText(R.string.artistaDesconocido);
        else
            holder.album_artista.setText(artistDetails.get(position).getArtist());

        holder.numero_cancion.setText(String.valueOf((position+1)));
        holder.masOpciones.setContentDescription(String.valueOf((position)));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, PlayerActivity.class);
                intent.putExtra("sender","artistDetails");
                intent.putExtra("position",position);
                mContext.startActivity(intent);
            }
        });

        holder.masOpciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("Id", (String) holder.masOpciones.getContentDescription());
                posicionCancion=Integer.valueOf((String) holder.masOpciones.getContentDescription());
                showDialog(posicionCancion);

            }
        });

    }

    @Override
    public int getItemCount() {
        return artistDetails.size();
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
        MediaMetadataRetriever retriever=new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] imagen=retriever.getEmbeddedPicture();
        retriever.release();
        return imagen;
    }

    //Mostramos el bottom sheet
    private void showDialog(int posicionCancion){
        final Dialog dialog=new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_cancion);

        //Dado que estamos en la sección del artista esa opción no se desplegara
        dialog.findViewById(R.id.iconoIrArtista).setVisibility(View.GONE);
        dialog.findViewById(R.id.textoIrArtista).setVisibility(View.GONE);

        MusicFiles cancion=artistDetails.get(posicionCancion);

        ImageView imagen=dialog.findViewById(R.id.music_img);
        TextView titulo=dialog.findViewById(R.id.music_file_name);
        TextView artista=dialog.findViewById(R.id.music_artista_name);

        if(cancion.getArtist().compareTo("<unknown>")==0)
            artista.setText(R.string.artistaDesconocido);
        else
            artista.setText(cancion.getArtist());

        //Obtenemos los datos de la canción
        byte [] image = getAlbumImagen(cancion.getPath());
        if(image != null){
            Glide.with(mContext).asBitmap().load(image).into(imagen);
        }else {
            Glide.with(mContext).asBitmap().load(R.drawable.ic_record_vinyl_solid).into(imagen);
        }
        titulo.setText(cancion.getTitle());

        //Agregamos los listeners a las funciones
        dialog.findViewById(R.id.btnIrAlbum).setOnClickListener((v)->{
            Log.i("Navegar a ", "Ir a la sección del album");
            Intent intent=new Intent(mContext, AlbumDetails.class);
            System.out.println(cancion.getAlbum());
            intent.putExtra("albumNombre",cancion.getAlbum());
            try{
                mContext.startActivity(intent);
            }catch (Exception e){
                System.out.println(e);
            }

            dialog.dismiss();
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations=R.style.dialoAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}
