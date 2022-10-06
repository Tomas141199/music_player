package com.tomas.music_player.adapters;

import static com.tomas.music_player.MainActivity.ALBUM_NAME_TO_FRAG;
import static com.tomas.music_player.MainActivity.SONG_NAME_TO_FRAG;
import static com.tomas.music_player.MainActivity.actualizado;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaMetadata;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.tomas.music_player.BaseDatos.BDImagenes;
import com.tomas.music_player.R;
import com.tomas.music_player.models.Imagen;
import com.tomas.music_player.models.MusicFiles;
import com.tomas.music_player.screens.PlayerActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MyViewHolder> {

    private Context mContext;
    public static ArrayList<MusicFiles> mFile;
    BDImagenes bd_;

    public MusicAdapter(Context mContext, ArrayList<MusicFiles> mFile){
        this.mContext = mContext;
        this.mFile = mFile;
        bd_=new BDImagenes(mContext);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.music_items,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.file_name.setText(actualizado.get(position).getTitle());

        //Buscamos la imagen de la canción
        //Cargamos la canción ya sea una modificada o la original
        Imagen aux= bd_.buscarImagen(actualizado.get(position).getAlbum().replace(" ","").toLowerCase(Locale.ROOT));
        if(aux!=null){
            //Imagen encontrada
            Glide.with(mContext).asBitmap().load(aux.getRuta()).into(holder.album_art);
        }else{
            byte[] image = getAlbumArt(actualizado.get(position).getPath());
            if (image != null) {
                Glide.with(mContext).asBitmap().load(image).into(holder.album_art);
            } else {
                Glide.with(mContext).asBitmap().load(R.drawable.ic_record_vinyl_solid).into(holder.album_art);
            }
        }

        String titulo=actualizado.get(position).getTitle();
        String album=actualizado.get(position).getAlbum();

        if(album.equals(ALBUM_NAME_TO_FRAG)&&titulo.equals(SONG_NAME_TO_FRAG)){
            holder.itemView.setBackgroundColor(Color.rgb(255,255,255));
            holder.itemView.getBackground().setAlpha(50);
        }else{
            holder.itemView.setBackgroundColor(Color.rgb(250,12,255));
            holder.itemView.getBackground().setAlpha(0);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mContext, PlayerActivity.class);
                intent.putExtra("position", position);
                mContext.startActivity(intent);
            }
        });

        holder.menu_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(mContext, view);
                popupMenu.getMenuInflater().inflate(R.menu.popup, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.delete:
                                Toast.makeText(mContext, "Delete Clicked", Toast.LENGTH_SHORT);
                                deleteFile(position, view);
                                break;

                        }
                        return true;
                    }
                });

            }
        });
    }

    private void deleteFile(int position, View view){
        Uri contentUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, Long.parseLong(mFile.get(position).getId()));
        File file = new File(mFile.get(position).getPath());

        boolean deleted = file.delete();
        if(deleted){
            mContext.getContentResolver().delete(contentUri, null, null);
            mFile.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, mFile.size());
            Snackbar.make(view, "Archivo Eliminado", Snackbar.LENGTH_LONG).show();
        }else{
            Snackbar.make(view, "No se pudo eliminar", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public int getItemCount() {
        return mFile.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{

        TextView file_name;
        ImageView album_art, menu_more;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            file_name = itemView.findViewById(R.id.music_file_name);
            album_art = itemView.findViewById(R.id.music_img);
            menu_more = itemView.findViewById(R.id.menuMore);
        }
    }

    private byte[] getAlbumArt (String uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        byte[] art = retriever.getEmbeddedPicture();
        retriever.release();
        return art;
    }

    public void updateList(ArrayList<MusicFiles> musicFilesArrayList){
        mFile = new ArrayList<>();
        mFile.addAll(musicFilesArrayList);
        notifyDataSetChanged();
    }
}
