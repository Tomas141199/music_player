package com.tomas.music_player.services;

import static com.tomas.music_player.screens.PlayerActivity.listsong;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.tomas.music_player.interfaces.ActionPlaying;
import com.tomas.music_player.models.MusicFiles;
import com.tomas.music_player.screens.PlayerActivity;

import java.util.ArrayList;

public class MusicService extends Service implements  MediaPlayer.OnCompletionListener {
    IBinder mBinder = new MyBinder();
    MediaPlayer mediaPlayer;
    public ArrayList<MusicFiles> musicFiles = new ArrayList<>();
    Uri uri;
    public int position = -1;
    private ActionPlaying actionPlaying;
    public static final String MUSIC_LAST_PLAYED = "LAST_PLAYED";
    public static final String MUSIC_FILE = "STORED_MUSIC";
    public static final String ARTIST_NAME = "ARTIST NAME";
    public static final String SONG_NAME = "SONG NAME";
    public static final String ALBUM_NAME = "ALBUM NAME";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }



    public class MyBinder extends Binder{
        public MusicService getService(){
            return MusicService.this;
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int myPosition = intent.getIntExtra("servicePosition",-1);
        String actionName = intent.getStringExtra("ActionName");
        if(myPosition != -1){
            playMedia(myPosition);
        }

        if(actionName != null){
            switch (actionName){
                case "playPause":
                    if(actionPlaying != null){
                        actionPlaying.playPauseBtnClicked();
                    }
                    break;
                case "next":
                    if(actionPlaying != null){
                        actionPlaying.nextBtnClicked();
                    }
                    break;
                case "previous":
                    if(actionPlaying != null){
                        actionPlaying.prevBtnClicked();
                    }
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + actionName);
            }
        }
        return START_STICKY;
    }

    private void playMedia(int StartPosition) {
        musicFiles = listsong;
        position = StartPosition;
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            if(listsong != null){
                createMediaPlayer(position);
                mediaPlayer.start();
            }
        }else{
            createMediaPlayer(position);
            mediaPlayer.start();
        }
    }

    public void start(){
        mediaPlayer.start();
    }

    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }

    public void stop(){
        mediaPlayer.stop();
    }

    public void release(){
        mediaPlayer.release();
    }

    public int getDuration(){
        return mediaPlayer.getDuration();
    }

    public void seekTo(int position){
        mediaPlayer.seekTo(position);
    }

    public int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }

    public void createMediaPlayer(int positionInner){
        position = positionInner;
        uri = Uri.parse(listsong.get(position).getPath());
        SharedPreferences.Editor editor = getSharedPreferences(MUSIC_LAST_PLAYED, MODE_PRIVATE)
                .edit();
        editor.putString(MUSIC_FILE, uri.toString());
        editor.putString(ARTIST_NAME, listsong.get(position).getArtist());
        editor.putString(SONG_NAME, listsong.get(position).getTitle());
        editor.putString(ALBUM_NAME, listsong.get(position).getAlbum());
        editor.apply();
        mediaPlayer = MediaPlayer.create(getBaseContext(),uri);
    }

    public void pause(){
        mediaPlayer.pause();
    }

    public void OnCompleted(){
        mediaPlayer.setOnCompletionListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if(actionPlaying != null){
            actionPlaying.nextBtnClicked();
            if(mediaPlayer != null){
                createMediaPlayer(position);
                mediaPlayer.start();
                OnCompleted();
            }
        }
    }

    public void setCallBack(ActionPlaying actionPlaying){
        this.actionPlaying = actionPlaying;
    }

    public void playPauseBtnClicked(){
        if(actionPlaying != null){
            actionPlaying.playPauseBtnClicked();
        }
    }

    public void nextBtnClicked(){
        if(actionPlaying != null){
            actionPlaying.nextBtnClicked();
        }
    }

    public void previousBtnClicked(){
        if(actionPlaying != null){
            actionPlaying.prevBtnClicked();
        }
    }



}
