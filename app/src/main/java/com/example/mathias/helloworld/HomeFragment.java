package com.example.mathias.helloworld;

import android.app.Fragment;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mathias.helloworld.R;

public class HomeFragment extends Fragment {

    public HomeFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        MediaPlayer mediaPlayer = MediaPlayer.create(getActivity(), R.raw.welcomegame);
        mediaPlayer.start();

        return rootView;
    }
}
