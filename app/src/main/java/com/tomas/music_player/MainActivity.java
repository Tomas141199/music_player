package com.tomas.music_player;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tomas.music_player.BaseDatos.BDCanciones;
import com.tomas.music_player.BaseDatos.BaseDatos;
import com.tomas.music_player.adapters.AlbumDetailsAdapter;
import com.tomas.music_player.fragments.AlbumsFragment;
import com.tomas.music_player.fragments.ArtistFragment;
import com.tomas.music_player.fragments.ListasFragment;
import com.tomas.music_player.fragments.SongsFragment;
import com.tomas.music_player.models.MusicFiles;
import com.tomas.music_player.screens.ArtistDetails;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    //Los fragmentos que se utilizan
    SongsFragment songsFragment = new SongsFragment();
    AlbumsFragment albumsFragment = new AlbumsFragment();
    ArtistFragment artistFragment = new ArtistFragment();
    ListasFragment listasFragment = new ListasFragment();
    Toolbar toolbar;
    private static final int REQUEST_CODE = 1;
    public static boolean suffleBoolean = false;
    public static boolean repeatBoolean = false;

    public static ArrayList<MusicFiles> musicFiles;
    public static ArrayList<MusicFiles> albums;
    public static ArrayList<MusicFiles> artists;
    public static ArrayList<String> nombreArtistas;
    public static ArrayList<MusicFiles> cancionesBD;
    public static ArrayList<MusicFiles> actualizado;


    public static String  PATH_TO_FRAG = null;
    public static  String ARTIST_TO_FRAG = null;
    public static  String SONG_NAME_TO_FRAG = null;
    public static  String ALBUM_NAME_TO_FRAG = null;

    public static boolean  SHOW_MINI_PLAYER = false;
    public static final String SONG_NAME = "SONG NAME";
    public static final String ARTIST_NAME = "ARTIST NAME";
    public static final String ALBUM_NAME = "ALBUM NAME";
    public static final String MUSIC_FILE = "STORED_MUSIC";
    public static final String MUSIC_LAST_PLAYED = "LAST_PLAYED";

    static BDCanciones bd;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Inicio");

        this.bd=new BDCanciones(MainActivity.this);

        musicFiles=new ArrayList<>();
        albums=new ArrayList<>();
        artists=new ArrayList<>();
        nombreArtistas=new ArrayList<>();

        permission();
        BaseDatos bd=new BaseDatos(this);//Iniciamos la base de datos
        loadFragment(songsFragment);
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    //MusicPlayerMethods
    public static ArrayList<MusicFiles> getAllAudio(Context context) {

        MusicFiles musicFiles=null;
        albums.clear();
        artists.clear();
        nombreArtistas.clear();

        //Contenedor de valores para la inserción
        ContentValues valores = new ContentValues();
        ArrayList<String> duplicate=new ArrayList<>();

        ArrayList<MusicFiles> tempAudioList = new ArrayList<>();
        Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.YEAR
        };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String duration = cursor.getString(2);
                String path = cursor.getString(3);
                String artist = cursor.getString(4);
                String id = cursor.getString(5);
                String fecha=cursor.getString(6);

                MusicFiles c=bd.buscarCancion(id);
                if(c==null){
                    valores.put("identificador",id);
                    valores.put("titulo",title);
                    valores.put("album",album);
                    valores.put("artista",artist);
                    valores.put("duracion",duration);
                    valores.put("path",path);
                    valores.put("fecha",fecha);
                    valores.put("fecha",fecha);
                    valores.put("favorito",0);
                    //Si no existe la canción se va a insertar en la bd
                    if(bd.insertarCancion(valores)>0){
                        System.out.println("Canción insertada en la bd");
                    }

                    c=new MusicFiles(path, title, artist, album, duration, id);
                    c.setFecha(fecha);

                    musicFiles = new MusicFiles(path, title, artist, album, duration, id);
                    musicFiles.setFecha(fecha);

                }else{
                    //Si existe entonces la canción ya esta insertada
                    System.out.println("No la da");
                    musicFiles=c;
                }

                System.out.println("Favorito estado:"+c.getFavorito());

                tempAudioList.add(musicFiles);

                if(!duplicate.contains(musicFiles.getAlbum())){
                    albums.add(musicFiles);
                    duplicate.add(musicFiles.getAlbum());
                }

                //Se crea una arreglo para separar a los artistas que se encuentran registrados
                if(musicFiles.getArtist().contains(";")){
                    System.out.println("Contienen ;");
                    String aux="";
                    for (int i=0; i<musicFiles.getArtist().length();i++){
                        if(musicFiles.getArtist().charAt(i)==';'){
                            System.out.println("res"+aux);
                            if(!duplicate.contains(aux)){
                                artists.add(musicFiles);
                                duplicate.add(aux);

                                //Agregamos el nombre del artista al arreglo
                                nombreArtistas.add(aux);
                            }
                            aux="";

                        }else{
                            aux+=musicFiles.getArtist().charAt(i);

                        }

                    }
                    if(!duplicate.contains(aux)){
                        artists.add(musicFiles);
                        duplicate.add(aux);
                        nombreArtistas.add(aux);
                    }
                    aux="";
                }else{
                    System.out.println("No contienen");
                    if(!duplicate.contains(musicFiles.getArtist())){
                        artists.add(musicFiles);
                        duplicate.add(musicFiles.getArtist());

                        nombreArtistas.add(musicFiles.getArtist());
                    }
                }
            }
            cursor.close();
        }
        //-----------------------------------------------------
        actualizado=tempAudioList;

        return tempAudioList;
    }

    private void permission() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        } else {
            musicFiles = getAllAudio(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            musicFiles = getAllAudio(this);
        } else {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
    }
    //BottomNavigationMethods

    private final BottomNavigationView.OnNavigationItemSelectedListener  mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener(){
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item){
            switch (item.getItemId()){
                case R.id.songsFragment:
                    getSupportActionBar().setTitle("Inicio");
                    loadFragment(songsFragment);
                    return true;
                case R.id.albumsFragment:
                    getSupportActionBar().setTitle("Albumes");
                    loadFragment(albumsFragment);
                    return true;
                case R.id.artistFragment:
                    getSupportActionBar().setTitle("Artistas");
                    loadFragment(artistFragment);
                    return true;
                case R.id.playlistFragment:
                    getSupportActionBar().setTitle("Listas");
                    loadFragment(listasFragment);
                    return true;
            }
            return false;
        }
    };

    public void loadFragment(Fragment fragment){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }

    //----------------------------------METODOS NUEVOS
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search,menu);
        MenuItem menuItem = menu.findItem(R.id.Search_option);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String userInput = newText.toLowerCase();
        ArrayList<MusicFiles> myFiles = new ArrayList<>();
        for(MusicFiles song: musicFiles){
            if(song.getTitle().toLowerCase().contains(userInput)){
                myFiles.add(song);
            }
        }
        SongsFragment.musicAdapter.updateList(myFiles);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preferences = getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE);
        String path = preferences.getString(MUSIC_FILE, null);
        String artist = preferences.getString(ARTIST_NAME, null);
        String song_name = preferences.getString(SONG_NAME, null);
        String album_name = preferences.getString(ALBUM_NAME, null);

        System.out.println("PAHT;"+ path);
        if(path != null){
            SHOW_MINI_PLAYER = true;
            PATH_TO_FRAG = path;
            ARTIST_TO_FRAG = artist;
            SONG_NAME_TO_FRAG = song_name;
            ALBUM_NAME_TO_FRAG = album_name;
        }else {
            SHOW_MINI_PLAYER = true;
            PATH_TO_FRAG = null;
            ARTIST_TO_FRAG = artist;
            SONG_NAME_TO_FRAG = song_name;
            ALBUM_NAME_TO_FRAG = album_name;
        }
    }
}