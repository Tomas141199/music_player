package com.tomas.music_player.fragments;

import static android.content.Context.MODE_PRIVATE;
import static com.tomas.music_player.MainActivity.ARTIST_TO_FRAG;
import static com.tomas.music_player.MainActivity.PATH_TO_FRAG;
import static com.tomas.music_player.MainActivity.SHOW_MINI_PLAYER;
import static com.tomas.music_player.MainActivity.SONG_NAME_TO_FRAG;
import static com.tomas.music_player.MainActivity.ALBUM_NAME_TO_FRAG;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.tomas.music_player.BaseDatos.BDImagenes;
import com.tomas.music_player.R;
import com.tomas.music_player.models.Imagen;
import com.tomas.music_player.services.MusicService;

import java.util.Locale;

public class NowPlayingFragmentBottom extends Fragment implements ServiceConnection {

    ImageView nextBtn, albumArt;
    TextView artist, songName;
    FloatingActionButton playPauseBtn;
    View view;
    MusicService musicService;
    BDImagenes bd_;
    public static final String MUSIC_LAST_PLAYED = "LAST_PLAYED";
    public static final String MUSIC_FILE = "STORED_MUSIC";
    public static final String ARTIST_NAME = "ARTIST NAME";
    public static final String SONG_NAME = "SONG NAME";
    public static final String ALBUM_NAME = "ALBUM NAME";

    public NowPlayingFragmentBottom() {


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_now_playing_bottom, container, false);
        artist = view.findViewById(R.id.song_artist_miniPlayer);
        songName = view.findViewById(R.id.song_name_miniPlayer);
        albumArt = view.findViewById(R.id.bottom_album_art);
        nextBtn = view.findViewById(R.id.skip_next_bottom);
        playPauseBtn = view.findViewById(R.id.play_pause_miniPlayer);


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(musicService != null){
                    musicService.nextBtnClicked();
                    if(getActivity() != null) {
                        SharedPreferences.Editor editor = getActivity().getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE)
                                .edit();
                        editor.putString(MUSIC_FILE, musicService.musicFiles.get(musicService.position).getPath());
                        editor.putString(ARTIST_NAME, musicService.musicFiles.get(musicService.position).getArtist());
                        editor.putString(SONG_NAME, musicService.musicFiles.get(musicService.position).getTitle());
                        editor.putString(ALBUM_NAME, musicService.musicFiles.get(musicService.position).getAlbum());
                        editor.apply();

                        SharedPreferences preferences = getActivity().getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE);
                        String path = preferences.getString(MUSIC_FILE, null);
                        String artistName = preferences.getString(ARTIST_NAME, null);
                        String song_name = preferences.getString(SONG_NAME, null);
                        String album_name = preferences.getString(ALBUM_NAME, null);

                        if(path != null){
                            SHOW_MINI_PLAYER = true;
                            PATH_TO_FRAG = path;
                            ARTIST_TO_FRAG = artistName;
                            SONG_NAME_TO_FRAG = song_name;
                            ALBUM_NAME_TO_FRAG = album_name;
                        }else {
                            SHOW_MINI_PLAYER = true;
                            PATH_TO_FRAG = null;
                            ARTIST_TO_FRAG = artistName;
                            SONG_NAME_TO_FRAG = song_name;
                            ALBUM_NAME_TO_FRAG = album_name;
                        }

                        if (SHOW_MINI_PLAYER) {

                            bd_= new BDImagenes(getContext());

                            try{
                                Imagen i= bd_.buscarImagen(ALBUM_NAME_TO_FRAG.replace(" ","").toLowerCase(Locale.ROOT));
                                if(i!=null){
                                    Glide.with(getContext()).asBitmap().load(i.getRuta()).into(albumArt);
                                }else{
                                    if (PATH_TO_FRAG != null) {
                                        byte[] art = getAlbumArt(PATH_TO_FRAG);
                                        Glide.with(getContext()).load(art)
                                                .load(art)
                                                .centerCrop()
                                                .placeholder(R.drawable.ic_launcher_foreground)
                                                .into(albumArt);
                                    }
                                }
                            }catch (Exception e){
                                Log.i("Erro","Now playing imagen:"+e);
                            }


                        }
                        songName.setText(SONG_NAME_TO_FRAG);

                        if(!ARTIST_TO_FRAG.equals("<unknown>")) {
                            artist.setText(ARTIST_TO_FRAG);
                        }else{
                            artist.setText(R.string.artistaDesconocido);
                        }
                    }
                }
            }
        });

        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(musicService != null){
                    musicService.playPauseBtnClicked();
                    if(musicService.isPlaying()){
                        playPauseBtn.setImageResource(R.drawable.pause);
                    }else{
                        playPauseBtn.setImageResource(R.drawable.play);
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (SHOW_MINI_PLAYER) {
            bd_= new BDImagenes(getContext());
            try{
                Imagen i= bd_.buscarImagen(ALBUM_NAME_TO_FRAG.replace(" ","").toLowerCase(Locale.ROOT));
                if(i!=null){
                    Glide.with(getContext()).asBitmap().load(i.getRuta()).into(albumArt);
                }else{
                    if (PATH_TO_FRAG != null) {
                        byte[] art = getAlbumArt(PATH_TO_FRAG);
                        Glide.with(getContext()).load(art)
                                .load(art)
                                .centerCrop()
                                .placeholder(R.drawable.ic_launcher_foreground)
                                .into(albumArt);
                    }
                }
            }catch (Exception e){
                Log.i("Erro","Now playing imagen:"+e);
            }
        }
        songName.setText(SONG_NAME_TO_FRAG);
        if(!ARTIST_TO_FRAG.equals("<unknown>")) {
            artist.setText(ARTIST_TO_FRAG);
        }else{
            artist.setText(R.string.artistaDesconocido);
        }
        Intent intent = new Intent(getContext(), MusicService.class);
        if (getContext() != null) {
            getContext().bindService(intent, this, Context.BIND_AUTO_CREATE);
        }

    }


    @Override
    public void onPause() {
        super.onPause();
        if (getContext() != null) {
            getContext().unbindService(this);
        }
    }

    private byte[] getAlbumArt(String uri) {
        try{
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(uri.toString());
            byte[] art = retriever.getEmbeddedPicture();
            retriever.release();
            return art;
        }
        catch (Exception e){

        }
        return null;
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder service) {
        MusicService.MyBinder binder = (MusicService.MyBinder) service;
        musicService = binder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        musicService = null;
    }
}