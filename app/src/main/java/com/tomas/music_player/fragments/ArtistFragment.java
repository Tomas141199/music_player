package com.tomas.music_player.fragments;

import static com.tomas.music_player.MainActivity.artists;
import static com.tomas.music_player.MainActivity.nombreArtistas;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tomas.music_player.R;
import com.tomas.music_player.adapters.ArtistAdapter;

public class ArtistFragment extends Fragment {

    RecyclerView recyclerView;
    ArtistAdapter artistAdapter;


    public ArtistFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artists, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        //Un nuevo comentario
        Log.i("asdkja",String.valueOf(artists.size()));
        if (!(artists.size() <1)) {
            artistAdapter = new ArtistAdapter(getContext());
            recyclerView.setAdapter(artistAdapter);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        }

        //Se crea el fragmento y se muestra
        return view;

    }
}