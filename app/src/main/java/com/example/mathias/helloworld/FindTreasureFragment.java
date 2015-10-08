package com.example.mathias.helloworld;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.mathias.helloworld.MapsActivity;
import com.example.mathias.helloworld.R;

public class FindTreasureFragment extends Fragment {
	
	public FindTreasureFragment(){}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_find_treasure, container, false);
        Button btnToMap = (Button) rootView.findViewById(R.id.ToMapButton);
        btnToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),MapsActivity.class);
                startActivity(intent);
            }
        });
         
        return rootView;
    }
}
