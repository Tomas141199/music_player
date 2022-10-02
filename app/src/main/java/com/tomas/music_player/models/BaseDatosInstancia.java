package com.tomas.music_player.models;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.tomas.music_player.BaseDatos.BaseDatos;
public class BaseDatosInstancia {

    SQLiteDatabase db;
    BaseDatos bd;

    public BaseDatosInstancia(Context c){
        bd=new BaseDatos(c);
    }

    public SQLiteDatabase getInstancia(){
        if(db!=null){
            return db;
        }else{
            db=bd.getWritableDatabase();
            if(db!=null)
                Log.i("Estad bd","Base de datos creada");
            else
                Log.i("Estad bd","Base de datos no creada");
        }
        return db;
    }

}
