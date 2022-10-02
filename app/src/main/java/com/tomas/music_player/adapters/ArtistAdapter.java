package com.tomas.music_player.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.tomas.music_player.BaseDatos.BDImagenes;
import com.tomas.music_player.R;
import com.tomas.music_player.models.Imagen;
import com.tomas.music_player.models.MusicFiles;
import com.tomas.music_player.screens.ArtistDetails;
import com.tomas.music_player.screens.CargarImagen;

import java.util.ArrayList;
import java.util.Locale;

public class ArtistAdapter extends RecyclerView.Adapter<ArtistAdapter.MyHolder> {

    private Context mContext;
    private ArrayList<MusicFiles> artistFiles;
    private ArrayList<String> nombres=new ArrayList<>();
    private ArrayList<String> nombresArtist=new ArrayList<>();

    private ArrayList<Imagen> imagenes;
    View view;
    BDImagenes bd;
    Imagen imagen=null;
    public ArtistAdapter(Context mContext, ArrayList<MusicFiles> artistFiles, ArrayList<String> nombresArtistas){
        this.mContext=mContext;
        this.nombresArtist=nombresArtistas;
        nombres.clear();
        bd=new BDImagenes(mContext);
        this.artistFiles=artistFiles;

        imagenes=bd.mostrarImagenes();

        for(int i=0; i< imagenes.size(); i++){
            System.out.println("Insertado en el arreglo:"+imagenes.get(i).getNombre());
            nombres.add(imagenes.get(i).getNombre());
        }
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view= LayoutInflater.from(mContext).inflate(R.layout.artist_item,parent,false);
        return new MyHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, @SuppressLint("RecyclerView") int position) {


        if(nombresArtist.get(position).equals("<unknown>"))
            holder.artista_nombre.setText(R.string.artistaDesconocido);
        else
            holder.artista_nombre.setText(nombresArtist.get(position));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, ArtistDetails.class);
                System.out.println(nombresArtist.get(position));
                intent.putExtra("artistNombre",nombresArtist.get(position));
                try{
                    mContext.startActivity(intent);
                }catch (Exception e){
                    System.out.println(e);
                }
            }
        });

        /*-Buscamos la imagen del artista--*/
        imagen=bd.buscarImagen(nombresArtist.get(position).toLowerCase(Locale.ROOT).replace(" ",""));
        if(imagen==null) {
        Log.i("Estado busqueda imagen","Imagen no encontrada");
        Glide.with(mContext).asBitmap().load(R.drawable.ic_user_solid).into(holder.icono_artista);
        } else{
            Log.i("Estado busqueda imagen","Imagen encontrada");
            Log.i("Estado busqueda imagen id:", String.valueOf(imagen.getId()));
            Glide.with(mContext).asBitmap().load(imagen.getRuta()).into(holder.icono_artista);
        }
        /*--------------------------------*/

        holder.btnMasOpciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*-Buscamos la imagen del artista--*/
                imagen=bd.buscarImagen(nombresArtist.get(position).toLowerCase(Locale.ROOT).replace(" ",""));
                if(imagen==null) {
                    Log.i("Estado busqueda imagen","Imagen no encontrada");
                    System.out.println("Imagen no conseguida");
                    Glide.with(mContext).asBitmap().load(R.drawable.ic_user_solid).into(holder.icono_artista);
                } else{
                    Log.i("Estado busqueda imagen show dialog","Imagen encontrada");
                    Log.i("Estado busqueda imagen id:", String.valueOf(imagen.getId()));                    Glide.with(mContext).asBitmap().load(imagen.getRuta()).into(holder.icono_artista);
                }
                /*--------------------------------*/

                String nombre=nombresArtist.get(position);

                //Creamos un shared preferences para almacenar la informaciómn
                SharedPreferences sp= mContext.getSharedPreferences("datosImagen",mContext.MODE_PRIVATE);
                SharedPreferences.Editor editor=sp.edit();
                editor.putInt("idImagen", imagen==null ? -1 : imagen.getId() );
                editor.putString("nombre",imagen==null ? nombre : imagen.getNombre());
                editor.putInt("tipo",imagen==null ? -1 : imagen.getTipo());

                if(editor.commit()){
                    System.out.println("Los datos se han almacenado");
                }else {
                    System.out.println("Los datos no se han almacenado");
                }

                Log.i("Nombre artista",nombresArtist.get(position));
                showDialog();
            }
        });

    }

    @Override
    public int getItemCount() {
        return nombresArtist.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        TextView artista_nombre;
        ImageView btnMasOpciones,icono_artista;
        public MyHolder(@NonNull View itemView) {
            super(itemView);
            artista_nombre=view.findViewById(R.id.nombre_artista);
            btnMasOpciones=view.findViewById(R.id.menuMore);
            icono_artista=view.findViewById(R.id.icono_artista);
        }

    }

    private void showDialog(){
        final Dialog dialog=new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout);
        LinearLayout layoutAgregarFoto=dialog.findViewById(R.id.layoutAgregarFoto);

        layoutAgregarFoto.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

                Intent intent=new Intent(mContext, CargarImagen.class);

                /*Verificamos si la imagen es nula, con la finalidad
                * de establcer que se entrara a actualizar la imagen*/
                /*if(imagen!=null){

                    intent.putExtra("id",imagen.getId());
                    intent.putExtra("nombreArtista",imagen.getNombre());
                    intent.putExtra("tipo",imagen.getTipo());
                    try{
                        mContext.startActivity(intent);
                    }catch (Exception e){
                        System.out.println(e);
                    }

                }else{
                    System.out.println("Imagen nula");
                    intent.putExtra("nombreArtista",nombre);
                    try{
                        mContext.startActivity(intent);
                    }catch (Exception e){
                        System.out.println(e);
                    }
                }*/

                //Cargamos el intent donde se creara la imagen
                try{
                    mContext.startActivity(intent);
                }catch (Exception e){
                    System.out.println(e);
                }
                dialog.dismiss();
            }
        });


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations=R.style.dialoAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);


    }
}
