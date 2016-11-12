package com.tarambola.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tarambola.controller.LoginController;
import com.tarambola.controller.WebServiceRequest;
import com.tarambola.model.LoginSession;
import com.tarambola.model.TagData;
import com.tarambola.vigiego.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Claim.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Claim#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Claim extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "mTagData";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private TagData mTagData;

    private OnFragmentInteractionListener mListener;

    public Claim() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param tagData TagData
     * @return A new instance of fragment Claim.
     */
    // TODO: Rename and change types and number of parameters
    public static Claim newInstance(TagData tagData) {
        Claim fragment = new Claim();
        fragment.setTagData(tagData);
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, tagData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTagData = (TagData) getArguments().getSerializable(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_claim, container, false);

        Typeface font = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/sui-generis-rg.ttf");

        TextView claimLab1=(TextView) rootView.findViewById(R.id.claimLabel1);
        claimLab1.setTypeface(font);

        TextView claimLab2=(TextView) rootView.findViewById(R.id.claimLabel2);
        claimLab2.setTypeface(font);

        final Button claimBtn = (Button)rootView.findViewById(R.id.mClaimBtn);

        if(mTagData.getIdNumber()==null) {
            claimLab1.setText(getString(R.string.no_tag_scanned));
            claimLab2.setText("");
            claimBtn.setEnabled(false);
            claimBtn.getBackground().setAlpha(50);
        } else {
            claimLab1.setText(getString(R.string.claim_tag1));
            claimLab2.setText(getString(R.string.claim_tag2));
            claimBtn.setEnabled(true);
            claimBtn.getBackground().setAlpha(100);
        }

        claimBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Click:", "OK");
                if (mTagData.getIdNumber() != null) {
                    WebServiceRequest request = new WebServiceRequest() {
                        @Override
                        public void claimTag(String email, String tagId) {
                            RequestParams params = new RequestParams();
                            params.put("email", email);
                            params.put("serial_number", tagId);

                            AsyncHttpClient client = new AsyncHttpClient();
                            Log.d("CLAIM -mail", email);
                            Log.d("CLAIM -serial", tagId);
                            client.post("http://api.vigiesolutions.com/v1/vigie-go/tag/claim", params, new AsyncHttpResponseHandler() {
                                @Override
                                public void onSuccess(String response) {
                                    try {
                                        JSONObject obj = new JSONObject(response);// JSON Object
                                        if (obj.getBoolean("status")) {// Loggin succeded
                                            Log.d("CLAIM:", "SUCCESS");
                                            Toast.makeText(rootView.getContext(), getString(R.string.claim_success), Toast.LENGTH_LONG).show();
                                            //mListener.onClaimSuccess();
                                        } else {// Login failed
                                            Log.d("CLAIM:", "Failed");
                                            //mListener.onClaimFailed();
                                            Toast.makeText(rootView.getContext(), getString(R.string.claim_failed), Toast.LENGTH_LONG).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Log.d("CLAIM:", "Error 1");
                                        //mListener.onClaimFailed();
                                        Toast.makeText(rootView.getContext(), getString(R.string.claim_failed), Toast.LENGTH_LONG).show();
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Throwable error, String content) {
                                    if (statusCode == 404) {// HTTP Res = 404
                                        //mListener.onClaimFailed();
                                        Toast.makeText(rootView.getContext(), getString(R.string.claim_failed), Toast.LENGTH_LONG).show();
                                    } else if (statusCode == 500) {// HTTP Res = 500
                                        //mListener.onClaimFailed();
                                        Toast.makeText(rootView.getContext(), getString(R.string.claim_failed), Toast.LENGTH_LONG).show();
                                    } else {// HTTP Res code other than 404, 500
                                        //mListener.onClaimFailed();
                                        Toast.makeText(rootView.getContext(), getString(R.string.claim_failed), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        }
                    };

                    request.claimTag(LoginSession.getInstance().getUsername(), mTagData.getIdNumber());
                }
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
        // ToDo, implement listener
       /* try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
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
        void onClaimSuccess();
        void onClaimFailed();
    }

    // ************************************* SETTERS ************************************* //
    public void setTagData(TagData data)
    {
        this.mTagData = data;
    }
}
