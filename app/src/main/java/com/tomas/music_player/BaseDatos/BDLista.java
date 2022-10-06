package com.tomas.music_player.BaseDatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import com.tomas.music_player.models.BaseDatosInstancia;
import com.tomas.music_player.models.Imagen;
import com.tomas.music_player.models.Lista;

import java.util.ArrayList;

public class BDLista extends BaseDatos{

    Context context;
    BaseDatosInstancia bd;
    SQLiteDatabase db;

    public BDLista(@Nullable Context context) {
        super(context);
        this.context=context;

        /**Establecemos la conexión con la bd*/
        bd=new BaseDatosInstancia(this.context);
        db=bd.getInstancia();
        if(db!=null){
            Log.i("Esatad cargar image","Base de datos creada");
        }else{
            Log.i("Esatad cargar image","Base de datos no creada");
        }
    }

    public long insertarLista(ContentValues valores){
        long estado=-1;

        try {
            estado = db.insert(TABLE_LISTAS, null, valores);
        }catch (Exception e){
            System.out.println("Error de inserción:"+e);
            e.toString();
        }
        return estado;
    }

    public ArrayList<Lista> mostrarListas(){
        int tipo=0;
        ArrayList<Lista> listas=new ArrayList<>();
        Lista lista=null;
        Cursor cursor=null;

        cursor=db.rawQuery("SELECT * FROM "+TABLE_LISTAS ,null);
        if(cursor.moveToFirst()){
            do{
                lista=new Lista();
                lista.setId(cursor.getInt(0));
                lista.setNombre(cursor.getString(1));
                lista.setTotal(cursor.getInt(2));

                listas.add(lista);
            }while(cursor.moveToNext());
        }

        cursor.close();
        return listas;
    }

    public Imagen buscarImagen(String cadena){
        Imagen imagen=null;
        Cursor cursor=null;

        System.out.println("Imagen buscada");

        System.out.println(cadena);
        try {
            cursor = db.rawQuery( "select * from "+TABLE_IMAGENES+" WHERE nombre = '"+cadena+"'", null ); }catch (Exception e){
            Log.i("Excepcion al buscar imagen", String.valueOf(e));
        }
        try {

            if (cursor.moveToFirst()) {
                do {
                    imagen = new Imagen();
                    imagen.setId(cursor.getInt(0));
                    imagen.setNombre(cursor.getString(1));
                    imagen.setRuta(cursor.getBlob(2));
                    imagen.setTipo(cursor.getInt(3));

                    Log.i("Id de la imagen buscada", String.valueOf(imagen.getId()));


                } while (cursor.moveToNext());
            }
        }catch (Exception e){
            Log.i("Error en la imagen",String.valueOf(e));
        }


        return imagen;
    }

}
