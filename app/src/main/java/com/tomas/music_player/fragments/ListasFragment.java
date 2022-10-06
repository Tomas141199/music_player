package com.tomas.music_player.fragments;

import static com.tomas.music_player.MainActivity.actualizado;
import static com.tomas.music_player.MainActivity.albums;
import static com.tomas.music_player.R.color.purple;
import static com.tomas.music_player.R.drawable.*;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.tomas.music_player.BaseDatos.BDLista;
import com.tomas.music_player.R;
import com.tomas.music_player.adapters.ArtistAdapter;
import com.tomas.music_player.adapters.ListasAdapter;
import com.tomas.music_player.adapters.MusicAdapter;
import com.tomas.music_player.models.Imagen;
import com.tomas.music_player.models.Lista;
import com.tomas.music_player.screens.CargarImagen;
import com.tomas.music_player.screens.ListaDetails;

import java.util.ArrayList;
import java.util.Locale;


public class ListasFragment extends Fragment {

    RecyclerView recyclerView;
    ArtistAdapter ListasAdapter;
    BDLista bdL;
    public static ArrayList<Lista> listas=new ArrayList<>();
    public static ListasAdapter listasAdapter;

    public ListasFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_listas, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);

        bdL=new BDLista(getContext());
        //Agregamos el listener
        view.findViewById(R.id.btnAgregarPlaylist).setOnClickListener((v)->{
            mostrarAgregar();
        });

        //Posicion de favoritos
        view.findViewById(R.id.listaFavoritos).setOnClickListener((v)->{

            Intent intent=new Intent(getContext(), ListaDetails.class);
            intent.putExtra("playlistNombre","favoritos");
            intent.putExtra("posicionCancion",-1);
            try{
                startActivity(intent);
            }catch (Exception e) {
                System.out.println(e);
            }
        });

        listas=bdL.mostrarListas();
        if(listas.isEmpty()){
            System.out.println("La lista esta vacia");
        }else{
            cargarDatosRecyclerView();
            System.out.println("Tamaño de lista:"+listas.size());
        }

        return view;
    }

    private void mostrarAgregar(){
        final Dialog dialog=new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.crear_lista);

        RelativeLayout btnGuardar=dialog.findViewById(R.id.btnGuardar);
        Button btnCancelar=dialog.findViewById(R.id.btnCancelar);
        EditText nombrePlaylist=dialog.findViewById(R.id.nombrePlaylist);
        TextView texto=dialog.findViewById(R.id.textCrear);

        nombrePlaylist.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(!nombrePlaylist.getText().toString().isEmpty()){
                    btnGuardar.setFocusable(true);
                    btnGuardar.setFocusableInTouchMode(true);
                }else{
                    btnGuardar.setFocusable(false);
                    btnGuardar.setFocusableInTouchMode(false);
                }
            }
        });

        btnCancelar.setOnClickListener((v)->{
            dialog.dismiss();
        });

        btnGuardar.setOnClickListener((v)->{
            ContentValues valores=new ContentValues();
            String nombre=nombrePlaylist.getText().toString();
            System.out.println("Nombre play list:"+nombre);
            valores.put("nombre",nombre);
            valores.put("total",0);

            try{
                Long res=bdL.insertarLista(valores);
                if(res>0){
                    Toast.makeText(getContext(),"Lista creada exitosamente",Toast.LENGTH_SHORT).show();
                    //Actualizamos los datos de las listas
                    listas=bdL.mostrarListas();
                    cargarDatosRecyclerView();
                    dialog.dismiss();
                }else{
                    Toast.makeText(getContext(),"Ha ocurrido un error",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){
                System.out.println("Ha ocurrido un error");
            }

        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations=R.style.dialoAnimation;
        dialog.getWindow().setGravity(Gravity.CENTER);
    }

    private void cargarDatosRecyclerView(){
        recyclerView.setHasFixedSize(true);
        if (!((listas.size()) <1)) {
            listasAdapter = new ListasAdapter(getContext());
            recyclerView.setAdapter(listasAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
        }
    }
}