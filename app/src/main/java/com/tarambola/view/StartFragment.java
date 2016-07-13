package com.tarambola.view;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StartFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class StartFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    public StartFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_start, container, false);

        //********************************************************** DESIGN
        Typeface font = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/sui-generis-rg.ttf");

        TextView spinnerLabel=(TextView) rootView.findViewById(R.id.mProfileSpinnerLabel);
        spinnerLabel.setTypeface(font);

        TextView samplingLabel=(TextView) rootView.findViewById(R.id.mSamplingLabel);
        samplingLabel.setTypeface(font);

        TextView startByPushingLabel=(TextView) rootView.findViewById(R.id.mStartByPushingLabel);
        startByPushingLabel.setTypeface(font);

        TextView saveProfileLabel=(TextView) rootView.findViewById(R.id.mSaveProfileBtnlabel);
        saveProfileLabel.setTypeface(font);

        TextView goBtnlabel=(TextView) rootView.findViewById(R.id.mGoBtnLabel);
        goBtnlabel.setTypeface(font);

        /* Ballon labels */
        TextView minLabel=(TextView) rootView.findViewById(R.id.mMinStartLabel);
        minLabel.setTypeface(font);
        TextView maxLabel=(TextView) rootView.findViewById(R.id.mMaxStartLabel);
        maxLabel.setTypeface(font);
        TextView minNOkLabel=(TextView) rootView.findViewById(R.id.mMinNOkLabel);
        minNOkLabel.setTypeface(font);
        TextView maxNOkLabel=(TextView) rootView.findViewById(R.id.mMaxNOkLabel);
        maxNOkLabel.setTypeface(font);
        TextView minAfterLabel=(TextView) rootView.findViewById(R.id.mMinAfterLabel);
        minAfterLabel.setTypeface(font);
        TextView maxAfterLabel=(TextView) rootView.findViewById(R.id.mMaxAfterLabel);
        maxAfterLabel.setTypeface(font);


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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
