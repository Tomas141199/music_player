package com.tomas.music_player.BaseDatos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class BaseDatos extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION=1;
    private static final String DATABASE_NOMBRE="MusicPlayer.db";
    public static final String TABLE_IMAGENES= "t_imagenes";
    public static final String TABLE_CANCIONES="t_canciones";
    public static final String TABLE_LISTAS="t_lista";



    public BaseDatos(@Nullable Context context){
        super(context,DATABASE_NOMBRE,null,DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table "+ TABLE_IMAGENES+
                        "("+"id integer primary key autoincrement, "
                        +"nombre text not null, "
                        +"ruta blob not null,"
                        +"tipo int not null);"); //Int 0=Artista, 1=album

        db.execSQL(
                "create table "+ TABLE_CANCIONES+
                        "("+"id integer primary key autoincrement, "
                        +"identificador TEXT not null, "
                        +"titulo TEXT not null, "
                        +"album TEXT not null, "
                        +"artista TEXT not null, "
                        +"duracion TEXT not null, "
                        +"path TEXT not null, "
                        +"fecha TEXT, "
                        +"favorito integer not null);");

        db.execSQL(
                "create table "+ TABLE_LISTAS+
                        "("+"id integer primary key autoincrement, "
                        +"nombre TEXT not null, "
                        +"total int );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE "+TABLE_IMAGENES);
        db.execSQL("DROP TABLE "+TABLE_CANCIONES);
        db.execSQL("DROP TABLE "+TABLE_LISTAS);

        onCreate(db);
    }
}
