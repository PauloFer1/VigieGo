package com.tarambola.view;

import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TagInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class TagInfoFragment extends Fragment {

    static public class RecTimeLeft
    {
        public RecTimeLeft(){};
        public RecTimeLeft(int days, int hours, int min, int secs){
            this.days=days;this.hours=hours;this.minutes=minutes;this.seconds=secs;
        };

        public int days;
        public int hours;
        public int minutes;
        public int seconds;
    };

    /* TAG INFO */
    private String      mIdNumber;
    private String      mFirmwareVer;
    private String      mHardwareVer;
    private Date        mCalibrateDate;
    private Date        mExpirationDate;
    private int         mNumberRecs;
    private RecTimeLeft mRecTimeLeft;
    private String      mProdDesc;
    private Date        mStartDateRec;

    /* List Info */
    private TwoColumnTable mList;

    private Context mContext;


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

        /* ************** DESIGN ************* */
        Typeface font = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/sui-generis-rg.ttf");

        TextView recordLabel=(TextView) rootView.findViewById(R.id.mRecordingLabel);
        recordLabel.setTypeface(font);


      //  RelativeLayout listInfoLayout = (RelativeLayout) rootView.findViewById(R.id.mTagInfoCont); // Get Fragment Relative layout to apply list
        ScrollView listInfoLayout = (ScrollView) rootView.findViewById(R.id.mTagInfoCont); // Get Fragment Relative layout to apply list
        listInfoLayout.addView(mList.build());


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

    /* *********************************** GETTERS ****************************************** */


    /* *********************************** SETTERS ****************************************** */

    /**
     * Set ID Number of the Tag
     * @param id the string of id number
     */
    public void setIdNumber(String id){
        this.mIdNumber = id;
    }

    /**
     * Set firmware version of the Tag
     * @param version the string of version
     */
    public void setFirmwareVer(String version){
        this.mFirmwareVer = version;
    }

    /**
     * Set hardware version of the Tag
     * @param version the string of version
     */
    public void setHardwareVer(String version){
        this.mHardwareVer = version;
    }

    /**
     * Set calibration date of the Tag
     * @param date the Date of Calibration
     */
    public void setCalibrateDate(Date date){
        this.mCalibrateDate = date;
    }

    /**
     * Set Expiration date of the Tag
     * @param date the Date of Expiration
     */
    public void setExpirationDate(Date date){
        this.mCalibrateDate = date;
    }

    /**
     * Set Number of records of the Tag
     * @param number the int number of records
     */
    public void setNumberRecs(int number){
        this.mNumberRecs = number;
    }
    /**
     * Set the record time left of the Tag
     * @param time RecTimeLeft
     */
    public void setRecTimeLeft(RecTimeLeft time){
        this.mRecTimeLeft = time;
    }

    /**
     * Set the description of the tag
     * @param desc String of the description
     */
    public void setProdDesc(String desc){
        this.mProdDesc = desc;
    }

    /**
     * Set start date record of the Tag
     * @param date the Date of record starting
     */
    public void setStartDateRec(Date date){
        this.mStartDateRec = date;
    }

    /**
     * Set the application context to use in fragment
     */
    public void setContext(Context context)
    {
        this.mContext = context;
    }

    /* *********************************** METHODS ****************************************** */

    /**
     * Create Tag List Info
     */
    public void createList()
    {
        /* ******* Info List with two columns ******** */
        mList = new TwoColumnTable(mContext); // Initialize TwoColumnTable with current context
    }

    /**
     * Populate the tag info List
     */
    public void populateList()
    {
        mList.addRow(new TwoColumnTable.Row("VIGIEGo ID number", this.mIdNumber));
        mList.addRow(new TwoColumnTable.Row("Firmware Version", this.mFirmwareVer));
        mList.addRow(new TwoColumnTable.Row("Hardware Version", this.mHardwareVer));
        mList.addRow(new TwoColumnTable.Row("Calibrate Date", "03 June 2016 11:58:03"));
        mList.addRow(new TwoColumnTable.Row("Expiration Date", "03 June 2016 11:58:03"));
        mList.addRow(new TwoColumnTable.Row("Number of Recordings", Integer.toString(this.mNumberRecs)));
        mList.addRow(new TwoColumnTable.Row("Recordings time left", "129d 78h 34m 22s"));
        mList.addRow(new TwoColumnTable.Row("Product Description", this.mProdDesc));
        mList.addRow(new TwoColumnTable.Row("Start date of Recording", "03 June 2016 11:58:03"));

    }

    /**
     * Add new Row Regardless of the title and value
     */
    public void addRow(String name, String value)
    {
        mList.addRow(new TwoColumnTable.Row(name, value));
    }

}
