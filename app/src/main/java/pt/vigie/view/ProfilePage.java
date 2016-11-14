package pt.vigie.view;

import android.app.Activity;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import pt.vigie.controller.DBAdapter;
import pt.vigie.model.Profile;

import pt.vigie.vigiego.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfilePage.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ProfilePage extends Fragment {

    private OnFragmentInteractionListener mListener;

    private Profile mProfile;

    /* Inputs */
    private EditText mProfileName;
    private EditText mSamplingInput;
    private EditText mMinInput;
    private EditText mNoKMinInput;
    private EditText mMaxInput;
    private EditText mNOkMaxInput;

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
        mProfileName= (EditText) rootView.findViewById(R.id.mProfileNameInput);
        mProfileName.setText(mProfile.getName());
        mSamplingInput= (EditText) rootView.findViewById(R.id.measureTimeField);
        mSamplingInput.setText(Long.toString(mProfile.getMeasureLenght()));
        mMinInput= (EditText) rootView.findViewById(R.id.minField);
        mMinInput.setText(Integer.toString(mProfile.getMinTemp()));
        mNoKMinInput= (EditText) rootView.findViewById(R.id.nOkMinField);
        mNoKMinInput.setText(Integer.toString(mProfile.getNOkMinTime()));
        mMaxInput= (EditText) rootView.findViewById(R.id.maxField);
        mMaxInput.setText(Integer.toString(mProfile.getMaxTemp()));
        mNOkMaxInput= (EditText) rootView.findViewById(R.id.NokMaxField);
        mNOkMaxInput.setText(Integer.toString(mProfile.getNOkMaxTime()));

        final ImageButton saveBtn = (ImageButton)rootView.findViewById(R.id.saveButton);
        saveBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                saveProfile(v);
            }
        });

        final ImageButton deleteBtn = (ImageButton)rootView.findViewById(R.id.deleteButton);
        deleteBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                deleteProfile(v);
            }
        });

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
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
        void onDeleteProfile();
    }

    public void saveProfile(View v)
    {
        DBAdapter.getInstance().updateProfile(mProfile.getId(),
                mProfileName.getText().toString(),
                Integer.parseInt(mSamplingInput.getText().toString()),
                Integer.parseInt(mMinInput.getText().toString()),
                Integer.parseInt(mNoKMinInput.getText().toString()),
                Integer.parseInt(mMaxInput.getText().toString()),
                Integer.parseInt(mNOkMaxInput.getText().toString()),
                false
        );
        mProfile.setMinTemp(Integer.parseInt(mMinInput.getText().toString()));
        mProfile.setmMaxTemp( Integer.parseInt(mMaxInput.getText().toString()));
        mProfile.setMeasureLenght(Integer.parseInt(mSamplingInput.getText().toString()));
        mProfile.setNOkMaxTime(Integer.parseInt(mNOkMaxInput.getText().toString()));
        mProfile.setNOkMinTime(Integer.parseInt(mNoKMinInput.getText().toString()));
        mProfile.setName(mProfileName.getText().toString());

        Toast.makeText(v.getContext(), getString(R.string.profile_saved), Toast.LENGTH_SHORT).show();
    }
    public void deleteProfile(View v)
    {
        DBAdapter.getInstance().deleteProfileByName(mProfile.getName());
        Toast.makeText(v.getContext(), getString(R.string.profile_deleted), Toast.LENGTH_SHORT).show();
        mListener.onDeleteProfile();
    }

    /////////////////////////////////////////
    ///////////// SETTERS
    public void setProfile(Profile profile){
        mProfile = profile;
    }
}
