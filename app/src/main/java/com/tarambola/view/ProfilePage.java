package com.tarambola.view;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.tarambola.model.Profile;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfilePage.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ProfilePage extends Fragment {

    private OnFragmentInteractionListener mListener;

    private Profile mProfile;

    public ProfilePage() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_profile_page, container, false);

        Typeface font = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/sui-generis-rg.ttf");

        TextView saveBtnLab=(TextView) rootView.findViewById(R.id.mSaveBtnLabel);
        saveBtnLab.setTypeface(font);
        TextView deleteBtnLab=(TextView) rootView.findViewById(R.id.mDelBtnlabel);
        deleteBtnLab.setTypeface(font);

        TextView nameLabel=(TextView) rootView.findViewById(R.id.mProfileNameInput);
        nameLabel.setTypeface(font);

        TextView samplingLabel=(TextView) rootView.findViewById(R.id.mProfSamplingLabel);
        samplingLabel.setTypeface(font);

         /* Ballon labels */
        TextView minLabel=(TextView) rootView.findViewById(R.id.mProfMinLabel);
        minLabel.setTypeface(font);
        TextView maxLabel=(TextView) rootView.findViewById(R.id.mProfMaxLabel);
        maxLabel.setTypeface(font);
        TextView minNOkLabel=(TextView) rootView.findViewById(R.id.mProfMinNOkLabel);
        minNOkLabel.setTypeface(font);
        TextView maxNOkLabel=(TextView) rootView.findViewById(R.id.mProfMaxNOkLabel);
        maxNOkLabel.setTypeface(font);
        TextView minAfterLabel=(TextView) rootView.findViewById(R.id.mProfMinAfterLabel);
        minAfterLabel.setTypeface(font);
        TextView maxAfterLabel=(TextView) rootView.findViewById(R.id.mProfMaxAfterLabel);
        maxAfterLabel.setTypeface(font);

        // Set Content
        final EditText name= (EditText) rootView.findViewById(R.id.mProfileNameInput);
        name.setText(mProfile.getName());
        final EditText measure= (EditText) rootView.findViewById(R.id.measureTimeField);
        measure.setText(Long.toString(mProfile.getMeasureLenght()));
        final EditText min= (EditText) rootView.findViewById(R.id.minField);
        min.setText(Integer.toString(mProfile.getMinTemp()));
        final EditText NoKMin= (EditText) rootView.findViewById(R.id.nOkMinField);
        NoKMin.setText(Integer.toString(mProfile.getNOkMinTime()));
        final EditText max= (EditText) rootView.findViewById(R.id.maxField);
        max.setText(Integer.toString(mProfile.getMaxTemp()));
        final EditText NOkMax= (EditText) rootView.findViewById(R.id.NokMaxField);
        NOkMax.setText(Integer.toString(mProfile.getNOkMaxTime()));


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

    /////////////////////////////////////////
    ///////////// SETTERS
    public void setProfile(Profile profile){
        mProfile = profile;
    }
}
