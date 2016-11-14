package pt.vigie.view;

/**
 * Created by Paulo on 07/06/2016.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import pt.vigie.controller.DBAdapter;
import pt.vigie.model.ProfileList;
import pt.vigie.model.ScreenStatus;
import pt.vigie.vigiego.R;

import java.util.ArrayList;


public class Profiles extends Fragment {

    SimpleCursorAdapter mAdapter;

    private ProfileList mProfiles;

   // private static final String ARG_PARAM1 = "mProfiles";

    public Profiles(){}

    public static Profiles newInstance(){
        Profiles fragment = new Profiles();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.fragment_profiles, container, false);

        mProfiles = DBAdapter.getInstance().getProfiles();

        //********************************************************** DESIGN

        //******************************************************** LIST VIEW
        ListView profileList = (ListView)rootView.findViewById(R.id.listView);
        profileList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                gotoProfile(view, position);
            }
        });

        String[] values = new String[mProfiles.getList().size()];
        final ArrayList<String> list = new ArrayList<String>();

        for (int i = 0; i < mProfiles.getList().size(); ++i) {
            list.add(mProfiles.getList().elementAt(i).getName());
            values[i] = mProfiles.getList().elementAt(i).getName();
        }

        ArrayAdapter adapterList = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, values);

        profileList.setAdapter(adapterList);

        if(mProfiles.getList().isEmpty())
            Toast.makeText(rootView.getContext(), getString(R.string.no_profiles), Toast.LENGTH_LONG).show();


        return(rootView);
    }
    //******************************************** LISTENER HELPERS *****************************************//
    public void gotoProfile(View v, int id)
    {

        FragmentManager fm = this.getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
        ProfilePage profile = new ProfilePage();
        profile.setProfile(mProfiles.getList().elementAt(id));
        ft.replace(R.id.container, profile).commit();

        ScreenStatus.getInstance().setStatus(getString(R.string.profile));
        //Toast.makeText(v.getContext(), "Click ListItem Number " + id, Toast.LENGTH_LONG).show();
    }

    ///////////////////////////////////////////
    //////////////////////////// SETTERS
    public void setProfileList(ProfileList list){mProfiles = list;}

}
