package pt.vigie.view;

import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import pt.vigie.controller.DBAdapter;
import pt.vigie.model.IntentOption;
import pt.vigie.model.ProfileList;
import pt.vigie.model.RecProfile;

import java.util.ArrayList;
import java.util.List;

import pt.vigie.vigiego.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StartFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class StartFragment extends Fragment {

    private View    mView;
    private OnFragmentInteractionListener mListener;

    private ProfileList mProfiles;

    //private static final String ARG_PARAM1 = "mProfiles";

    private String newProfileName = "";

    /* Inputs */
    private EditText mSamplingInput;
    private EditText mMinInput;
    private EditText mNoKMinInput;
    private EditText mMaxInput;
    private EditText mNOkMaxInput;
    private CheckBox mStartButton;

    /* SPINNER RELATED */
    private ArrayAdapter mAdapterList;
    private Spinner mSpinner;
    private List<String> mProfileList;

    public StartFragment() {
        // Required empty public constructor
    }

    public static StartFragment newInstance(){
        StartFragment fragment = new StartFragment();
        //fragment.setProfileList(profilesList);
        Bundle args = new Bundle();
        //args.putSerializable(ARG_PARAM1, profilesList);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mProfiles = (ProfileList) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_start, container, false);

        mView = rootView;

        mProfiles = DBAdapter.getInstance().getProfiles();

        //********************************************************** DESIGN
        Typeface font = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/sui-generis-rg.ttf");

        TextView spinnerLabel=(TextView) rootView.findViewById(R.id.mProfileSpinnerLabel);
        spinnerLabel.setTypeface(font);

        TextView samplingLabel=(TextView) rootView.findViewById(R.id.mSamplingLabel);
        samplingLabel.setTypeface(font);

        TextView startByPushingLabel=(TextView) rootView.findViewById(R.id.mStartByPushingLabel);
        startByPushingLabel.setTypeface(font);

        mStartButton = (CheckBox) rootView.findViewById(R.id.startByBtn);

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

        /* INPUTS */
        mSamplingInput = (EditText) rootView.findViewById(R.id.samplingInput);
        mMinInput = (EditText) rootView.findViewById(R.id.minInput);
        mNoKMinInput = (EditText) rootView.findViewById(R.id.minNOkInput);
        mMaxInput = (EditText) rootView.findViewById(R.id.maxInput);
        mNOkMaxInput = (EditText) rootView.findViewById(R.id.maxNOkInput);

        /* Spinner */
        mProfileList = new ArrayList<>();
        mProfileList.add(getString(R.string.select_profile));
        for(int i=0; i<mProfiles.getList().size(); i++){
            mProfileList.add(mProfiles.getList().elementAt(i).getName());
        }

        mAdapterList = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, mProfileList);
        mAdapterList.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner = (Spinner) rootView.findViewById(R.id.profileSpinner);
        mSpinner.setAdapter(mAdapterList);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // position-1 because the first position is the label "choose profile"
                populateProfile(rootView, position-1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

            }
        });

        /* Save Button */
        final ImageButton saveBtn = (ImageButton)rootView.findViewById(R.id.saveButton);
        saveBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                saveProfile(v);
            }
        });

        /* Go Button */
        final ImageButton goBtn = (ImageButton)rootView.findViewById(R.id.goButton);
        goBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                startRecording(v);
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

    /*
     * Populate profile inputs by selected spinner item
     */
    private void populateProfile(View v, int id){

        if(id>-1) // -1 is reserved for label "choose profile"
        {

            mSamplingInput.setText(Long.toString(mProfiles.getList().elementAt(id).getMeasureLenght()));
            mMinInput.setText(Integer.toString(mProfiles.getList().elementAt(id).getMinTemp()));
            mNoKMinInput.setText(Integer.toString(mProfiles.getList().elementAt(id).getNOkMinTime()));
            mMaxInput.setText(Integer.toString(mProfiles.getList().elementAt(id).getMaxTemp()));
            mNOkMaxInput.setText(Integer.toString(mProfiles.getList().elementAt(id).getNOkMaxTime()));
            if(mProfiles.getList().elementAt(id).getStartByButton()) {
                mStartButton.setChecked(true);
            }
            else{
                mStartButton.setChecked(false);
            }
        }
    }

    /*
     * Save Profile with values of the inputs
     */
    private void saveProfile(View v)
    {

       if(validateInputs(v)) {

            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle(getString(R.string.type_name));

            final EditText input = new EditText(v.getContext());
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);


        /* Buttons */
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    newProfileName = input.getText().toString();

                    boolean startBtn = mStartButton.isChecked();

                    long id = DBAdapter.getInstance().insertProfile(newProfileName,
                            Integer.parseInt(mSamplingInput.getText().toString()),
                            Integer.parseInt(mMinInput.getText().toString()),
                            Integer.parseInt(mNoKMinInput.getText().toString()),
                            Integer.parseInt(mMaxInput.getText().toString()),
                            Integer.parseInt(mNOkMaxInput.getText().toString()),
                            startBtn
                            );

                    mProfiles.addProfile(
                            (int)id,
                            newProfileName,
                            Integer.parseInt(mSamplingInput.getText().toString()),
                            Integer.parseInt(mMinInput.getText().toString()),
                            Integer.parseInt(mMaxInput.getText().toString()),
                            Integer.parseInt(mNoKMinInput.getText().toString()),
                            Integer.parseInt(mNOkMaxInput.getText().toString()),
                            startBtn);

                    mProfileList.add(newProfileName);
                    mAdapterList.notifyDataSetChanged();

                    Toast.makeText(mView.getContext(), getString(R.string.profile_saved), Toast.LENGTH_SHORT).show();
                    mSpinner.setSelection(mSpinner.getAdapter().getCount());
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
        }
    }
    /*
     * Set Profile to record and Start Recording flag in IntentOption
     */
    private void startRecording(View v){

       if(validateInputs(v)) {
           TextView textView = (TextView)mSpinner.getSelectedView();
           String profileName = textView.getText().toString();
           boolean startBtn = mStartButton.isChecked();
           RecProfile.getInstance().setProfile(profileName, Integer.parseInt(mSamplingInput.getText().toString()), Integer.parseInt(mMinInput.getText().toString()), Integer.parseInt(mNoKMinInput.getText().toString()), Integer.parseInt(mMaxInput.getText().toString()), Integer.parseInt(mNOkMaxInput.getText().toString()), startBtn);
           IntentOption.getInstance().setOption(IntentOption.Operations.START_RECORDING);
           Toast.makeText(v.getContext(), getString(R.string.place_smartphone), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInputs(View v)
    {
        if(mSamplingInput.getText().toString().matches("")) {
            Toast.makeText(v.getContext(), getString(R.string.no_sampling_msg), Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(mMinInput.getText().toString().matches("")) {
            Toast.makeText(v.getContext(), getString(R.string.no_min_msg), Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(mMaxInput.getText().toString().matches("")) {
            Toast.makeText(v.getContext(), getString(R.string.no_max_msg), Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(mNoKMinInput.getText().toString().matches("")) {
            Toast.makeText(v.getContext(), getString(R.string.no_nok_max_msg), Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(mNOkMaxInput.getText().toString().matches("")) {
            Toast.makeText(v.getContext(), getString(R.string.no_nok_max_msg), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    ///////////////////////////////////////////
    //////////////////////////// SETTERS
    public void setProfileList(ProfileList list){mProfiles = list;}
}
