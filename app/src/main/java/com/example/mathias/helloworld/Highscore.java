package com.example.mathias.helloworld;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

public class Highscore extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mainView = inflater.inflate(R.layout.fragment_highscore, container, false);

        WebView mywebView = (WebView) mainView.findViewById(R.id.webView); // Laver et webView
        mywebView.loadUrl("http://hjem.andersholm.eu:32500/android_login_api/treasures.php"); // Henter hjemmesiden med HighScore

        return mainView;
    }
}
