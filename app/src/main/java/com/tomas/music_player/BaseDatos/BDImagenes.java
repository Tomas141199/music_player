package com.tomas.music_player.BaseDatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import com.tomas.music_player.models.BaseDatosInstancia;
import com.tomas.music_player.models.Imagen;

import java.util.ArrayList;

public class BDImagenes extends BaseDatos{

    Context context;
    BaseDatosInstancia bd;
    SQLiteDatabase db;

    public BDImagenes(@Nullable Context context) {
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

    public long insertarImagenes(String nombre, byte[] ruta, int tipo){
        long id=-1;
        System.out.println(ruta);

        try {
            ContentValues valores = new ContentValues();
            valores.put("nombre", nombre);
            valores.put("ruta", ruta);
            valores.put("tipo", tipo);

            id = db.insert(TABLE_IMAGENES, null, valores);
        }catch (Exception e){
            e.toString();
        }
        return id;
    }

    public ArrayList<Imagen> mostrarImagenes(){
        int tipo=0;
        ArrayList<Imagen> imagenes=new ArrayList<>();
        Imagen imagen=null;
        Cursor cursor=null;

        cursor=db.rawQuery("SELECT * FROM "+TABLE_IMAGENES+" WHERE tipo= "+tipo,null);
        if(cursor.moveToFirst()){
            do{
                imagen=new Imagen();
                imagen.setId(cursor.getInt(0));
                imagen.setNombre(cursor.getString(1));
                imagen.setRuta(cursor.getBlob(2));
                imagen.setTipo(cursor.getInt(3));

                imagenes.add(imagen);

                Log.i("Id de la imagen", String.valueOf(imagen.getId()));
            }while(cursor.moveToNext());
        }

        cursor.close();
        return imagenes;
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

    public int actualizarImagen(int i, String nombre, byte [] imagen){
        ContentValues valores = new ContentValues();
        valores.put("nombre", nombre);
        valores.put("ruta", imagen);
        valores.put("tipo", i);

        boolean estado=true;

        int in=db.update(TABLE_IMAGENES,valores," nombre = '"+ nombre +"' ",null);

        return  in;
    }
}
