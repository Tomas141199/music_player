package com.tomas.music_player.adapters;

import static com.tomas.music_player.MainActivity.albums;
import static com.tomas.music_player.MainActivity.musicFiles;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
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
import com.google.android.material.snackbar.Snackbar;
import com.tomas.music_player.BaseDatos.BDCanciones;
import com.tomas.music_player.BaseDatos.BDImagenes;
import com.tomas.music_player.R;
import com.tomas.music_player.models.Imagen;
import com.tomas.music_player.models.MusicFiles;
import com.tomas.music_player.screens.ArtistDetails;
import com.tomas.music_player.screens.ModificarMetadata;
import com.tomas.music_player.screens.PlayerActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class AlbumDetailsAdapter extends RecyclerView.Adapter<AlbumDetailsAdapter.MyHolder> {

    private Context mContext;
    private int posicionCancion=-1;
    private int posicionReferencia;
    public static ArrayList<MusicFiles> albumFiles;
    public static MusicFiles cancionEditarAlbum;

    BDImagenes bd_;
    View view;

    int contador;
    public AlbumDetailsAdapter(Context mContext, ArrayList<MusicFiles> albumFiles,int posicionCancion){
        this.mContext=mContext;
        this.albumFiles=albumFiles;
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


        holder.album_titulo.setText(albumFiles.get(position).getTitle());

        //Buscamos la imagen de la canción
        //Cargamos la canción ya sea una modificada o la original
        Imagen aux= bd_.buscarImagen(albumFiles.get(position).getAlbum().replace(" ","").toLowerCase(Locale.ROOT));
        if(aux!=null){
            //Imagen encontrada
            Glide.with(mContext).asBitmap().load(aux.getRuta()).into(holder.album_imagen);
        }else{
            byte [] image = getAlbumImagen(albumFiles.get(position).getPath());
            System.out.println(image);
            if(image != null){
                Glide.with(mContext).asBitmap().load(image).into(holder.album_imagen);
            }else {
                Glide.with(mContext).asBitmap().load(R.drawable.ic_record_vinyl_solid).into(holder.album_imagen);
            }
        }

        if(albumFiles.get(position).getArtist().compareTo("<unknown>")==0)
            holder.album_artista.setText(R.string.artistaDesconocido);
        else
            holder.album_artista.setText(albumFiles.get(position).getArtist());

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
        return albumFiles.size();
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

    private void showDialog(int posicionCancion){
        final Dialog dialog=new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_cancion);

        //Dados que estamos en la sección del album esa opcion no se desplegara aquí
        dialog.findViewById(R.id.iconoIrAlbum).setVisibility(View.GONE);
        dialog.findViewById(R.id.textoIrAlbum).setVisibility(View.GONE);

        BDCanciones bd=new BDCanciones(mContext);

        MusicFiles cancion=albumFiles.get(posicionCancion);

        ImageView imagen=dialog.findViewById(R.id.music_img);
        TextView titulo=dialog.findViewById(R.id.music_file_name);
        TextView artista=dialog.findViewById(R.id.music_artista_name);
        ImageView favorito=dialog.findViewById(R.id.btnIconoFavorito);
        if(cancion.getArtist().compareTo("<unknown>")==0)
            artista.setText(R.string.artistaDesconocido);
        else
            artista.setText(cancion.getArtist());

        //Obtenemos los datos de la canción
        Imagen i=bd_.buscarImagen(cancion.getAlbum().toLowerCase(Locale.ROOT).replace(" ",""));
        if(i!=null){
            Glide.with(mContext).asBitmap().load(i.getRuta()).into(imagen);
        }else{
            byte [] image = getAlbumImagen(cancion.getPath());
            if(image != null){
                Glide.with(mContext).asBitmap().load(image).into(imagen);
            }else {
                Glide.with(mContext).asBitmap().load(R.drawable.ic_record_vinyl_solid).into(imagen);
            }
        }

        //Verificamos si la cancion es favorita
        if(cancion.getFavorito()==1){
            favorito.setImageResource(R.drawable.ic_heart_solid);
        }

        titulo.setText(cancion.getTitle());

        dialog.findViewById(R.id.btnAddFavoritos).setOnClickListener((v)-> {
            if (cancion.getFavorito() == 1) {
                cancion.setFavorito(0);
                favorito.setImageResource(R.drawable.ic_heart_regular);
            } else {
                favorito.setImageResource(R.drawable.ic_heart_solid);
                cancion.setFavorito(1);
            }

            ContentValues valores = new ContentValues();
            valores.put("identificador", cancion.getId());
            valores.put("titulo", cancion.getTitle());
            valores.put("album", cancion.getAlbum());
            valores.put("artista", cancion.getArtist());
            valores.put("duracion", cancion.getDuration());
            valores.put("path", cancion.getPath());
            valores.put("fecha", cancion.getFecha());
            valores.put("favorito", cancion.getFavorito());

            if (bd.actualizarCancion(valores, cancion.getId()) > 0) {
                System.out.println("Estado de favorito actualizado");
            }

        });

        //Agregamos los listeners a las funciones
        dialog.findViewById(R.id.btnIrArtista).setOnClickListener((v)->{
            Log.i("Navegar a ", "Ir a la sección del artista");
            Intent intent=new Intent(mContext, ArtistDetails.class);
            System.out.println(cancion.getArtist());
            intent.putExtra("artistNombre",cancion.getArtist());
            try{
                mContext.startActivity(intent);

            }catch (Exception e){
                System.out.println(e);
            }
            dialog.dismiss();


        });

        dialog.findViewById(R.id.btnEditarDatos).setOnClickListener((v)->{
            cancionEditarAlbum=albumFiles.get(posicionCancion);//Obtenemos la canción

            Intent intent=new Intent(mContext, ModificarMetadata.class);
            intent.putExtra("modo","album");
            //Cargamos el intent donde se creara la imagen
            try{
                mContext.startActivity(intent);
            }catch (Exception e){
                System.out.println(e);
            }
            //dialogoEditarDatos(posicionCancion);

            dialog.dismiss();
        });

        dialog.findViewById(R.id.btnEliminar).setOnClickListener((v)->{
            new AlertDialog.Builder(mContext).setTitle("Confirmar operación")
                    .setMessage("¿Esta seguro de eliminar la canción?")
                    .setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            //Continuamos con la eliminación
                            eliminarCancion(posicionCancion);
                        }
                    }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    }).setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

            dialog.dismiss();
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations=R.style.dialoAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void eliminarCancion(int position){
        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.parseLong(albumFiles.get(position).getId()));
        MusicFiles c=albumFiles.get(position);
        File file = new File(albumFiles.get(position).getPath());

        boolean deleted = file.delete();
        if(deleted){
            mContext.getContentResolver().delete(contentUri, null, null);
            musicFiles.remove(c);
            albumFiles.remove(c);

            notifyItemRemoved(position);
            notifyItemRangeChanged(position, albumFiles.size());
            Snackbar.make(view, "Archivo Eliminado", Snackbar.LENGTH_LONG).show();

            if(albumFiles.isEmpty()){
                albums.remove(posicionReferencia);
                //MainActivity.getAllAudio(mContext);

                ((Activity) mContext).finish();
            }

        }else{
            Snackbar.make(view, "No se pudo eliminar", Snackbar.LENGTH_LONG).show();
        }

    }
}
