package com.tomas.music_player;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tomas.music_player.BaseDatos.BaseDatos;
import com.tomas.music_player.adapters.AlbumDetailsAdapter;
import com.tomas.music_player.fragments.AlbumsFragment;
import com.tomas.music_player.fragments.ArtistFragment;
import com.tomas.music_player.fragments.SongsFragment;
import com.tomas.music_player.models.MusicFiles;
import com.tomas.music_player.screens.ArtistDetails;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    SongsFragment songsFragment = new SongsFragment();
    AlbumsFragment albumsFragment = new AlbumsFragment();
    ArtistFragment artistFragment = new ArtistFragment();

    private static final int REQUEST_CODE = 1;
    public static ArrayList<MusicFiles> musicFiles;
    public static boolean suffleBoolean = false;
    public static boolean repeatBoolean = false;
    public static ArrayList<MusicFiles> albums=new ArrayList<>();
    public static ArrayList<MusicFiles> artists=new ArrayList<>();

    public static ArrayList<String> nombreArtistas=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permission();

        BaseDatos bd=new BaseDatos(this);//Iniciamos la base de datos

        loadFragment(songsFragment);
        BottomNavigationView navigation = findViewById(R.id.bottom_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


    }

    //MusicPlayerMethods
    public static ArrayList<MusicFiles> getAllAudio(Context context) {

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

                MusicFiles musicFiles = new MusicFiles(path, title, artist, album, duration, id);
                musicFiles.setFecha(fecha);
                tempAudioList.add(musicFiles);
                Log.e("Path: "+ path, "Album: "+album);


                if(!duplicate.contains(album)){
                    albums.add(musicFiles);
                    duplicate.add(album);
                }

                //Se crea una arreglo para separar a los artistas que se encuentran registrados
                if(artist.contains(";")){
                    System.out.println("Contienen ;");
                    String aux="";
                    for (int i=0; i<artist.length();i++){
                        if(artist.charAt(i)==';'){
                            System.out.println("res"+aux);
                            if(!duplicate.contains(aux)){
                                artists.add(musicFiles);
                                duplicate.add(aux);

                                //Agregamos el nombre del artista al arreglo
                                nombreArtistas.add(aux);
                            }
                            aux="";

                        }else{
                            aux+=artist.charAt(i);

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
                    if(!duplicate.contains(artist)){
                        artists.add(musicFiles);
                        duplicate.add(artist);

                        nombreArtistas.add(artist);
                    }
                }
            }
            cursor.close();
        }
        //-----------------------------------------------------
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
                    loadFragment(songsFragment);
                    return true;
                case R.id.albumsFragment:
                    System.out.println("Estamos en el albums fragment");
                    loadFragment(albumsFragment);
                    return true;
                case R.id.artistFragment:
                    loadFragment(artistFragment);
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



}