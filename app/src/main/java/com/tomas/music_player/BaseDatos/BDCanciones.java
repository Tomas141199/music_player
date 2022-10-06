package com.tomas.music_player.BaseDatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.Nullable;

import com.tomas.music_player.models.BaseDatosInstancia;
import com.tomas.music_player.models.MusicFiles;

import java.util.ArrayList;

public class BDCanciones extends BaseDatos {

    Context context;
    BaseDatosInstancia bd;
    SQLiteDatabase db;

    public BDCanciones(@Nullable Context context) {
        super(context);
        this.context=context;

        /**Establecemos la conexión con la bd*/
        bd=new BaseDatosInstancia(this.context);
        db=bd.getInstancia();
        if(db!=null){
            Log.i("Esatad cargar cancion","Base de datos creada");
        }else{
            Log.i("Esatad cargar cancion","Base de datos no creada");
        }
    }

    public long insertarCancion(ContentValues valores){
        long id=-1;
        System.out.println("INSERTAR");

        System.out.println(valores.get("identificador"));
        System.out.println(valores.get("titulo"));
        System.out.println(valores.get("album"));
        System.out.println(valores.get("artista"));
        System.out.println(valores.get("duracion"));
        System.out.println(valores.get("path"));
        System.out.println(valores.get("fecha"));

        try {
            id = db.insert(TABLE_CANCIONES, null, valores);
        }catch (Exception e){
            e.toString();
        }
        return id;
    }

    public MusicFiles buscarCancion(String id){
        Cursor cursor=null;
        MusicFiles cancion=null;
        try {
            cursor = db.rawQuery( "select * from "+TABLE_CANCIONES+" WHERE identificador = '"+id+"'", null );
        }catch (Exception e){Log.i("Excepcion al buscar la cancion", String.valueOf(e));}

        try {
            if(cursor.moveToFirst()) {

                //Se econtro la canción
                do{
                    String identificador=cursor.getString(1);
                    String titulo=cursor.getString(2);
                    String album=cursor.getString(3);
                    String artista=cursor.getString(4);
                    String duracion=cursor.getString(5);
                    String path=cursor.getString(6);
                    String fecha=cursor.getString(7);

                    cancion = new MusicFiles(path, titulo, artista, album, duracion, identificador);
                    cancion.setFecha(fecha);
                    cancion.setFavorito(cursor.getInt(8));

                }while(cursor.moveToNext());

                System.out.println("dato:"+cursor.getString(0));
            }
        }catch (Exception e){
            Log.i("Error en la imagen",String.valueOf(e));
        }
        return cancion;
    }


    public ArrayList<MusicFiles> mostrarCanciones(){
        ArrayList<MusicFiles> canciones=new ArrayList<>();
        MusicFiles cancion=null;
        Cursor cursor=null;

        cursor=db.rawQuery("SELECT * FROM "+TABLE_CANCIONES ,null);
        if(cursor.moveToFirst()){
            do{
                String identificador=cursor.getString(1);
                String titulo=cursor.getString(2);
                String album=cursor.getString(3);
                String artista=cursor.getString(4);
                String duracion=cursor.getString(5);
                String path=cursor.getString(6);
                String fecha=cursor.getString(7);

                cancion = new MusicFiles(path, titulo, artista, album, duracion, identificador);
                cancion.setFecha(fecha);
                cancion.setFavorito(cursor.getInt(8));

                canciones.add(cancion);
            }while(cursor.moveToNext());
        }

        return canciones;
    }

    public int actualizarCancion(ContentValues valores,String id){


        int in=db.update(TABLE_CANCIONES,valores," identificador = '"+ id +"' ",null);

        return  in;
    }
}
