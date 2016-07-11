package com.tarambola.view;

/**
 * Created by Paulo on 07/06/2016.
 */
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class Profiles extends Fragment {

    SimpleCursorAdapter mAdapter;

    static final String[] PROJECTION = new String[] {ContactsContract.Data._ID,
            ContactsContract.Data.DISPLAY_NAME};

    static final String SELECTION = "((" +
            ContactsContract.Data.DISPLAY_NAME + " NOTNULL) AND (" +
            ContactsContract.Data.DISPLAY_NAME + " != '' ))";



    public Profiles(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.profiles, container, false);

        //********************************************************** DESIGN

        //******************************************************** LIST VIEW
        ListView profileList = (ListView)rootView.findViewById(R.id.listView);
        profileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                gotoProfile(view, position);
            }
        });

        String[] values = new String[] { "Android", "iPhone", "WindowsMobile",
                "Blackberry", "WebOS", "Ubuntu", "Windows7", "Max OS X",
                "Linux", "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux",
                "OS/2", "Ubuntu", "Windows7", "Max OS X", "Linux", "OS/2",
                "Android", "iPhone", "WindowsMobile" };
        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < values.length; ++i) {
            list.add(values[i]);
        }
        ArrayAdapter adapterList = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, values);

        profileList.setAdapter(adapterList);




        return(rootView);
    }
    //******************************************** LISTENER HELPERS *****************************************//
    public void gotoProfile(View v, int id)
    {

        FragmentManager fm = this.getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
        ProfilePage profile = new ProfilePage();
        ft.replace(R.id.container, profile).commit();

        Toast.makeText(v.getContext(),
                "Click ListItem Number " + id, Toast.LENGTH_LONG)
                .show();
    }


}
