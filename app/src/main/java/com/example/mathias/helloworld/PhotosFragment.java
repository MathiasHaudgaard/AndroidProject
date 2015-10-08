package com.example.mathias.helloworld;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

public class PhotosFragment extends Fragment {

    SharedPreferences settings;
    CheckBox notificationCheckBox;
    SharedPreferences.Editor editor;

    boolean bNotifications;

	public PhotosFragment(){}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_photos, container, false);

        settings = getActivity().getSharedPreferences(UserStatic.getEmail(), 0);
        editor = settings.edit();

        bNotifications = settings.getBoolean("notifications", true);



        notificationCheckBox = (CheckBox) rootView.findViewById(R.id.notificationsCheckBox);
        if (bNotifications){
            notificationCheckBox.toggle();
        }
        notificationCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bNotifications = !bNotifications;
            }
        });


        return rootView;
    }

    @Override
    public void onStop() {

        super.onStop();
        editor.remove("notifications");
        editor.putBoolean("notifications", bNotifications);
        Log.d("notifications", String.valueOf(bNotifications));

        editor.commit();
        Log.d("notifications", String.valueOf(settings.getBoolean("notifications", false)));

    }
}
