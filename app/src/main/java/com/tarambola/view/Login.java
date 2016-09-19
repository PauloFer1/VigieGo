package com.tarambola.view;

import android.app.Activity;
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

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.tarambola.controller.LoginController;
import com.tarambola.model.WebServiceRequest;
import com.tarambola.model.LoginSession;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Login.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Login#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Login extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private OnFragmentInteractionListener mListener;

    public Login() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Login.
     */
    // TODO: Rename and change types and number of parameters
    public static Login newInstance(String param1, String param2) {
        Login fragment = new Login();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        rootView.findViewById(R.id.loginPreLoader).setVisibility(View.INVISIBLE);
        rootView.findViewById(R.id.invalidLoginText).setVisibility(View.INVISIBLE);

        Typeface font = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/sui-generis-rg.ttf");

        TextView msgLab1=(TextView) rootView.findViewById(R.id.mEnterCredLabel1);
        msgLab1.setTypeface(font);

        TextView msgLab2=(TextView) rootView.findViewById(R.id.mEnterCredLabel2);
        msgLab2.setTypeface(font);


        TextView usernameLab=(TextView) rootView.findViewById(R.id.mUsernameLabel);
        usernameLab.setTypeface(font);

        TextView passwordLab=(TextView) rootView.findViewById(R.id.mPasswordLabel);
        passwordLab.setTypeface(font);

        TextView loginBtnLab=(TextView) rootView.findViewById(R.id.mLoginBtn);
        loginBtnLab.setTypeface(font);

        final Button loginBtn = (Button)rootView.findViewById(R.id.mLoginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                EditText username = (EditText) rootView.findViewById(R.id.usernameInput);
                EditText password = (EditText) rootView.findViewById(R.id.passwordInput);

                if(LoginController.validate(username.getText().toString()) && LoginController.isNotNull(password.getText().toString())) {
                    /* ToDo: webservice */
                    WebServiceRequest request = new WebServiceRequest(){
                        @Override
                        public void doLogin(String email, String password)
                        {
                            rootView.findViewById(R.id.loginPreLoader).setVisibility(View.VISIBLE); // SET LOADER VISIBLE
                            RequestParams params = new RequestParams();
                            params.put("email", email);
                            // params.put("password", ServiceRequestGo.md5(password));
                            params.put("password", password);

                            AsyncHttpClient client = new AsyncHttpClient();
                            client.post("http://api.vigiesolutions.com/vigiego/user/login", params, new AsyncHttpResponseHandler(){
                                @Override
                                public void onSuccess(String response) {
                                    try {
                                        JSONObject obj = new JSONObject(response);// JSON Object
                                        if(obj.getBoolean("status")){// Loggin succeded
                                            rootView.findViewById(R.id.loginPreLoader).setVisibility(View.INVISIBLE);
                                            LoginSession.getInstance().login();
                                            mListener.onLoginSuccess();
                                        }
                                        else{// Login failed
                                            Log.d("LOGIN:", "Failed");
                                            ((TextView) rootView.findViewById(R.id.invalidLoginText)).setText(getString(R.string.invalid_login));
                                            rootView.findViewById(R.id.loginPreLoader).setVisibility(View.INVISIBLE);
                                            rootView.findViewById(R.id.invalidLoginText).setVisibility(View.VISIBLE);
                                        }
                                    } catch (JSONException e) {
                                        ((TextView) rootView.findViewById(R.id.invalidLoginText)).setText(getString(R.string.invalid_login));
                                        rootView.findViewById(R.id.loginPreLoader).setVisibility(View.INVISIBLE);
                                        rootView.findViewById(R.id.invalidLoginText).setVisibility(View.VISIBLE);
                                        e.printStackTrace();
                                        Log.d("LOGIN:", "Error 1");
                                    }
                                }

                                @Override
                                public void onFailure(int statusCode, Throwable error, String content) {
                                    if(statusCode == 404){// HTTP Res = 404
                                        ((TextView) rootView.findViewById(R.id.invalidLoginText)).setText(getString(R.string.verify_connection));
                                        rootView.findViewById(R.id.invalidLoginText).setVisibility(View.VISIBLE);
                                        rootView.findViewById(R.id.loginPreLoader).setVisibility(View.INVISIBLE);
                                    }
                                    else if(statusCode == 500){// HTTP Res = 500
                                        ((TextView) rootView.findViewById(R.id.invalidLoginText)).setText(getString(R.string.verify_connection));
                                        rootView.findViewById(R.id.invalidLoginText).setVisibility(View.VISIBLE);
                                        rootView.findViewById(R.id.loginPreLoader).setVisibility(View.INVISIBLE);
                                    }
                                    else{// HTTP Res code other than 404, 500
                                        ((TextView) rootView.findViewById(R.id.invalidLoginText)).setText(getString(R.string.verify_connection));
                                        rootView.findViewById(R.id.invalidLoginText).setVisibility(View.VISIBLE);
                                        rootView.findViewById(R.id.loginPreLoader).setVisibility(View.INVISIBLE);
                                    }
                                }
                            });

                        }
                    };
                    request.doLogin(username.getText().toString(), password.getText().toString());
                }
            }
        });

        return(rootView);
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
        void onLoginSuccess();
        void onLoginFailed();
    }
}
