package com.tarambola.view;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TagInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class TagInfoFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public TagInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_tag_info, container, false);

        /* ******* Info List with two columns ******** */
        TwoColumnTable list = new TwoColumnTable(rootView.getContext()); // Initialize TwoColumnTable with current context

        /* Debug */
        /* Add dummy Info */
        list.addRow(new TwoColumnTable.Row("VIGIEGo ID number", "256255"));
        list.addRow(new TwoColumnTable.Row("Firmware Version", "256255"));
        list.addRow(new TwoColumnTable.Row("Hardware Version", "256255"));
        list.addRow(new TwoColumnTable.Row("Calibrate Date", "03 June 2016 11:58:03"));
        list.addRow(new TwoColumnTable.Row("Expiration Date", "03 June 2016 11:58:03"));
        list.addRow(new TwoColumnTable.Row("Number of Recordings", "256255"));
        list.addRow(new TwoColumnTable.Row("Recordings time left", "129d 78h 34m 22s"));
        list.addRow(new TwoColumnTable.Row("Product Description", "Teste"));
        list.addRow(new TwoColumnTable.Row("Start date of Recording", "03 June 2016 11:58:03"));

        RelativeLayout listInfoLayout = (RelativeLayout) rootView.findViewById(R.id.mTagInfoCont); // Get Fragment Relative layout to apply list
        listInfoLayout.addView(list.build());


        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
