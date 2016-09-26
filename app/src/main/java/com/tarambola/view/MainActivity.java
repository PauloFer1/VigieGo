package com.tarambola.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.tarambola.controller.BackgroundTagProcessor;
import com.tarambola.controller.BackgroundUploadService;
import com.tarambola.controller.DBAdapter;
import com.tarambola.controller.WebServiceRequest;
import com.tarambola.model.DummyClass;
import com.tarambola.controller.UploadReceiver;
import com.tarambola.model.IntentOption;
import com.tarambola.model.LoginSession;
import com.tarambola.model.ProfileList;
import com.tarambola.model.RecProfile;
import com.tarambola.model.TagData;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import eu.blulog.blulib.Utils;
import eu.blulog.blulib.exceptions.BluException;
import eu.blulog.blulib.json.JSONArray;
import eu.blulog.blulib.json.JSONException;
import eu.blulog.blulib.json.JSONObject;
import eu.blulog.blulib.nfc.BackgroundExecutor;
import eu.blulog.blulib.nfc.NfcUtils;
import eu.blulog.blulib.tdl2.BlutagContent;
import eu.blulog.blulib.tdl2.BlutagHandler;
import eu.blulog.blulib.tdl2.DataDefinition;
import eu.blulog.blulib.tdl2.LogisticalProfile;
import eu.blulog.blulib.tdl2.LogisticalProfileManager;
import eu.blulog.blulib.tdl2.PredefinedLogisticalProperties;
import eu.blulog.blulib.tdl2.Recording;




 public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, Login.OnFragmentInteractionListener, Logout.OnFragmentInteractionListener{


     /**
      * Screen Fragments Vars
      */
     private Home               mHome;
     private Chart              mChart;
     private StartFragment      mStart;
     private StopFragment       mStop;
     private NoContent          mNoContent;
     private Profiles           mProfiles;
     private ProfilePage        mProfilePage;
     private ReadTag            mReadTag;
     private TagInfoFragment    mTagInfo;
     private AboutFragment      mAbout;
     private Login              mLogin;
     private Logout             mLogout;

     private Fragment           mCurrentFragment = null;
     private String             mCurrentTitle;
     private TagData            mTagData;
     private ProfileList        mProfileList;

     private boolean            mIsRecording;

     // Background Upload Vars
     private Intent             mUploadService;
     private IntentFilter       mStatusIntentFilter;
     private UploadReceiver     mUploadReceiver;



    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private BackgroundTagProcessor mBackgroundTagProcessor;
    private AtomicBoolean mBusyOnProcessNFC;

     public void onFragmentInteraction(Uri uri){

     }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentOption.Operations option = IntentOption.getInstance().getOption();

        mBackgroundTagProcessor=null;
        mBusyOnProcessNFC = new AtomicBoolean(false);

        mTagData = new TagData();

        mProfileList = new ProfileList();
        mProfileList.creatDummy();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
       //     getWindow().setStatusBarColor(0x111111);
        }

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.drawable.ic_icon);
        /*actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(R.layout.custom_actionbar);

        LayoutInflater mInflater = LayoutInflater.from(this);
        View mCustomView = mInflater.inflate(R.layout.custom_actionbar, null);
        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.mTitleTextt);
        mTitleTextView.setText("My Own Title");
*/

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();


        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        ///////////////////// Background Upload Service
        mUploadService = new Intent(MainActivity.this, BackgroundUploadService.class);
        mUploadReceiver = new UploadReceiver(new Handler());
        mUploadReceiver.setReceiver(new UploadReceiver.Receiver(){
            @Override
            public void onReceiveResult(int resultCode, Bundle resultData) {
                if (resultCode == RESULT_OK) {
                    String resultValue = resultData.getString("resultValue");
                    Toast.makeText(MainActivity.this, resultValue, Toast.LENGTH_SHORT).show();
                }
            }
        });
        mUploadService.putExtra("receiver", mUploadReceiver);

        getGPSLocation();

        DBAdapter.getInstance().init(getApplicationContext());

        TagData tags[] = DBAdapter.getInstance().getTags();

        WebServiceRequest ws = new WebServiceRequest(getApplicationContext());

        ws.constructJSON();
    }

    @Override
    public void onPause() {
        super.onPause();
        NfcUtils.disableNfcForegroundDispatch(this);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        NfcUtils.enableNfcForgroundDispatch(this);
        Intent intent = getIntent();
        onNewIntent(intent);
    }

     //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
     //////////////////////////////////////////////////////////////////// TAG PROXIMITY HANLDER

     /*
      * Handles the NFC tag intent
      * Use singleton IntentOption to choose the operation to use
      */
    @Override
    public void onNewIntent(Intent intent)
    {
        Log.d("debug", "onNewIntent: ");

                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

                if (tag == null) {
                    Log.d("operation", "Intent: " +  IntentOption.getInstance().getOption().name() + ", Tag = null");
                    return;
                }

                intent.removeExtra(NfcAdapter.EXTRA_TAG);
                if (!mBusyOnProcessNFC.compareAndSet(false, true)) { //If busyOnProcessNFC is true to return and set busyOnProcessNFC to true
                    if (IntentOption.getInstance().getOption()!= IntentOption.Operations.SHORT_READ)
                        BlutagHandler.get().processNextTag(tag); //new in 1.6.x
                  //  Toast.makeText(getApplicationContext(), "Busy Busy", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.i("start new intent", intent.toString());

        switch(IntentOption.getInstance().getOption()) {
            case FINISH_RECORDING: // To stop tag recording
                mBackgroundTagProcessor =
                        new BackgroundTagProcessor(this, R.string.downloading_nfc_data, R.string.dont_remove_tag, tag) {
                            @Override
                            protected void postExecute(String status) {
                                if (status == null) {
                                    Recording recording = BlutagContent.get().getRecordings().get(0);
                                    recording.setRegistrationFinishDate(new Date());

                                    Toast.makeText(context, R.string.recording_was_finished, Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, R.string.operation_not_completed_try_again, Toast.LENGTH_SHORT).show();
                                }
                                BackgroundExecutor be = new BackgroundExecutor() {
                                    @Override
                                    protected void postExecute(String status) {
                                        mBusyOnProcessNFC.set(false);
                                        IntentOption.getInstance().setOption(IntentOption.Operations.SHORT_READ);
                                        mBackgroundTagProcessor =null;
                                        Log.i("nfc", "setting not busy ");

                                    }
                                    @Override
                                    protected void backgroundWork() {
                                        try {Thread.sleep(WAIT_TIME);} catch (InterruptedException e) {}

                                    }
                                };
                                be.execute(null, null);
                                mBackgroundTagProcessor =null;
                            }

                            @Override
                            protected void processTag(Tag tag) throws BluException {
                                BlutagHandler.get().finishRecording(tag);
                                IntentOption.getInstance().setOption(IntentOption.Operations.NOTHING);
                            }
                        };
                break;
            case SHORT_READ: // To read main data tag
                mBackgroundTagProcessor =
                        new BackgroundTagProcessor(this, R.string.downloading_nfc_data, R.string.dont_remove_tag, tag) {
                            @Override
                            protected void postExecute(String status) {
                                if (status == null) {
                                    //contentView.removeAllViews();
                                    showInfo(); // Show Tag Info
                                    uploadTagDataHelper(); // Upload Tag Info
                                } else {
                                    Toast.makeText(context, status, Toast.LENGTH_SHORT).show();
                                }
                                BackgroundExecutor be = new BackgroundExecutor() {
                                    @Override
                                    protected void postExecute(String status) {
                                        mBusyOnProcessNFC.set(false);
                                    }

                                    @Override
                                    protected void backgroundWork() {
                                        try {
                                            Thread.sleep(WAIT_TIME);
                                        } catch (InterruptedException e) {
                                        }
                                    }
                                };
                                be.execute(null, null);
                                mBackgroundTagProcessor = null;
                            }

                            @Override
                            protected void processTag(Tag tag) throws BluException {
                                Log.i(" started", this.toString());


                                BlutagHandler.get().readBlutag(tag);
                                if (BlutagContent.get().getRecordings().size() > 0 && BlutagContent.get().getRecordings().get(0).getRegistrationStartDate().getTime() < Recording.START_BY_BUTTON)
                                    BlutagContent.get().getRecordings().get(0).computeStatistics();


                                IntentOption.getInstance().setOption(IntentOption.Operations.NOTHING);
                                Log.i("finished", this.toString());
                            }
                        };
                Log.d("debug", "onNewIntent: 2");
                break;
            case START_RECORDING: // To start tag Recording
                Log.d("debug", "Start Recording ");
                mBackgroundTagProcessor=
                        new BackgroundTagProcessor(this, R.string.downloading_nfc_data, R.string.dont_remove_tag, tag,
                                BackgroundTagProcessor.TagOperation.StartNewRecording) {
                            @Override
                            protected void postExecute(String status) {
                                if (status==null){
                                    //uaktualnij info o blutagu
                                    Toast.makeText(context, R.string.new_recording_created, Toast.LENGTH_SHORT);
                                }
                                else if (status.equals(BluException.RECORDING_ALREADY_STARTED)){
                                    Toast.makeText(context, "Recording started already", Toast.LENGTH_SHORT);
                                }
                                else
                                    Toast.makeText(context, R.string.operation_not_completed_try_again, Toast.LENGTH_SHORT);

                                BackgroundExecutor be = new BackgroundExecutor() {
                                    @Override
                                    protected void postExecute(String status) {
                                        IntentOption.getInstance().setOption( IntentOption.Operations.SHORT_READ);
                                        mBusyOnProcessNFC.set(false);
                                    }
                                    @Override
                                    protected void backgroundWork() {
                                        try {Thread.sleep(WAIT_TIME);} catch (InterruptedException e) {}

                                    }
                                };
                                be.execute(null, null);
                                mBackgroundTagProcessor =null;

                                Log.d("debug", "End post exec");
                            }

                            @Override
                            protected void processTag(Tag tag) throws BluException {
                                Log.d("debug", "Process Tag");
                                startNewRecording(tag);
                                IntentOption.getInstance().setOption(IntentOption.Operations.NOTHING);
                            }
                        };
                break;
            default:;
        }
        Log.i("post start new intent", intent.toString());
        if (mBackgroundTagProcessor!=null)
            mBackgroundTagProcessor.execute(tag);

    }

     ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
     /////////////////////////////////////////////////////////////////// MENU HANDLER

     /*
      * Handles the menu item selection
      */
    @Override
    public void onNavigationDrawerItemSelected(int position) {

        Fragment fragment = null;

        switch(position) {
            case 0:
                gotoReadTag();
                break;
            case 1:
                gotoHome();
                break;
            case 2:
                gotoStart();
                break;
            case 3:
                gotoStop();
                break;
            case 4:
               gotoTagInfo();
                break;
            case 5:
                gotoProfiles();
                break;
            case 6:
                gotoAbout();
                break;
            case 7:
                gotoLogout();
                break;
            default:
                break;
        }
        /*
        if(BlutagContent.get().getHardware()==0)
        {
            fragment = new NoContent();
            mTitle = getString(R.string.title_section8);
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(String.valueOf(mTitle));
        transaction.commit();
        */
    }

     /*
      * Switch screen name on select menu item
      */
    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
            case 5:
                mTitle = getString(R.string.title_section5);
                break;
            case 6:
                mTitle = getString(R.string.title_section6);
                break;
            case 7:
                mTitle = getString(R.string.title_section7);
                break;
            case 8:
                mTitle = getString(R.string.title_section10);
                break;
        }
    }


     ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
     ////////////////////////////////////////////////////////// DEFAULT ANDROID ACTIONS

    public void restoreActionBar() {
        Log.d("DEBUG", "restoreActionBar: "+ mTitle);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
           // getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.tarambola.vigiego/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.tarambola.vigiego/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
     ///////////////////////////////////////////////////////// SCREENS HANDLERS

     /**
      * Handle fragment change to Home Screen
      */
     private void gotoHome()
     {
         Fragment fragment;
         FragmentManager fragmentManager = getSupportFragmentManager();

         if(mHome==null)
         {
             if(BlutagContent.get().getHardware()==0)
             {
                 fragment = new NoContent();
                 mTitle = getString(R.string.title_section8);
             }
             else
             {
                 mTitle = getString(R.string.title_section2);
                 mHome = Home.newInstance(mTagData);
                 fragment = mHome;
             }

         }
         else
            fragment = mHome;

         FragmentTransaction transaction = fragmentManager.beginTransaction();
         transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
         transaction.replace(R.id.container, fragment);
         transaction.addToBackStack(String.valueOf(mTitle));
         transaction.commit();

     }

     /**
      * Handle fragment change to Read Tag Screen
      */
     private void gotoReadTag()
     {
         IntentOption.getInstance().setOption(IntentOption.Operations.SHORT_READ);
         FragmentManager fragmentManager = getSupportFragmentManager();
         if(mReadTag==null) {
             mReadTag = new ReadTag();
         }

         mTitle = getString(R.string.title_section1);

         FragmentTransaction transaction = fragmentManager.beginTransaction();
         transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
         transaction.replace(R.id.container, mReadTag);
         transaction.addToBackStack(String.valueOf(mTitle));
         transaction.commit();

     }

     /**
      * Handle fragment change to Profiles Screen
      */
     private void gotoProfiles()
     {
         Fragment fragment;
         FragmentManager fragmentManager = getSupportFragmentManager();

         if(mProfiles==null)
         {
             mProfiles = Profiles.newInstance(mProfileList);
         }
         else
             fragment = mProfiles;


         mTitle = getString(R.string.title_section6);

         FragmentTransaction transaction = fragmentManager.beginTransaction();
         transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
         transaction.replace(R.id.container, mProfiles);
         transaction.addToBackStack(String.valueOf(mTitle));
         transaction.commit();
     }
     /**
      * Handle fragment change to Profiles Item Screen
      */
     private void gotoProfilePages()
     {
         Fragment fragment;
         FragmentManager fragmentManager = getSupportFragmentManager();

         if(mProfilePage==null)
         {
             mProfilePage = new ProfilePage();
         }
         else
             fragment = mProfilePage;

         mTitle = getString(R.string.title_section6);

         FragmentTransaction transaction = fragmentManager.beginTransaction();
         transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
         transaction.replace(R.id.container, mProfilePage);
         transaction.addToBackStack(String.valueOf(mTitle));
         transaction.commit();
     }

     /**
      * Handle fragment change to StartFragment screen
      */
     private void gotoStart()
     {
         FragmentManager fragmentManager = getSupportFragmentManager();
         Fragment fragment = null;

         if(LoginSession.getInstance().isLogged()) {
             if (mStart == null) {
                 mStart = StartFragment.newInstance(mProfileList);
                 fragment = mStart;
             }
             else
                fragment = mStart;

             mTitle = getString(R.string.title_section3);
         }
         else{
             mCurrentTitle = getString(R.string.title_section3);
             fragment = new Login();
             mTitle = getString(R.string.title_section9);
         }

         FragmentTransaction transaction = fragmentManager.beginTransaction();
         transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
         transaction.replace(R.id.container, fragment);
         transaction.addToBackStack(String.valueOf(mTitle));
         transaction.commit();
     }

     /**
      * Handle fragment change to StopFragment screen
      */
     private void gotoStop()
     {
         FragmentManager fragmentManager = getSupportFragmentManager();
         Fragment fragment = null;

         if(LoginSession.getInstance().isLogged()) {
             if (mStop == null) {
                 mStop = new StopFragment();
                 fragment = mStop;
             }
             else
                fragment = mStop;

             mTitle = getString(R.string.title_section4);
         }
         else{
             mCurrentTitle = getString(R.string.title_section4);
             fragment = new Login();
             mTitle = getString(R.string.title_section9);
         }

         FragmentTransaction transaction = fragmentManager.beginTransaction();
         transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
         transaction.replace(R.id.container, fragment);
         transaction.addToBackStack(String.valueOf(mTitle));
         transaction.commit();
     }

     /**
      * Handle fragment change to Home Screen
      */
     private void gotoTagInfo()
     {
         Fragment fragment;
         FragmentManager fragmentManager = getSupportFragmentManager();

         if(mTagInfo==null)
         {
             if(BlutagContent.get().getHardware()==0)
             {
                 fragment = new NoContent();
                 mTitle = getString(R.string.title_section8);
             }
             else
             {
                 BlutagContent content = BlutagContent.get();

                 mTitle = getString(R.string.title_section5);
                 mTagInfo = new TagInfoFragment();
                 mTagInfo.setContext(getApplicationContext());
                 mTagInfo.createList();
                 mTagInfo.setIdNumber(mTagData.getIdNumber());
                 mTagInfo.setFirmwareVer(mTagData.getFirmwareVer());
                 mTagInfo.setHardwareVer(mTagData.getHardwareVer());
                 mTagInfo.setNumberRecs(mTagData.getNumberRecs());
                 mTagInfo.setProdDesc(mTagData.getProdDesc());
                 mTagInfo.setStartDateRec(mTagData.getStartDateRec());
                 mTagInfo.setEndDateRec(mTagData.getEndDateRec());
                 mTagInfo.setCalibrateDate(mTagData.getCalibrateDate());
                 mTagInfo.setExpirationDate(mTagData.getExpirationDate());
                 mTagInfo.populateList();

                 fragment = mTagInfo;
             }

         }
         else
             fragment = mTagInfo;

         FragmentTransaction transaction = fragmentManager.beginTransaction();
         transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
         transaction.replace(R.id.container, fragment);
         transaction.addToBackStack(String.valueOf(mTitle));
         transaction.commit();

     }

     /**
      * Handle fragment change to About screen
      */
     private void gotoAbout()
     {
         FragmentManager fragmentManager = getSupportFragmentManager();

         if(mStart==null)
         {
             mAbout = new AboutFragment();
         }

         mTitle = getString(R.string.title_section7);

         FragmentTransaction transaction = fragmentManager.beginTransaction();
         transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
         transaction.replace(R.id.container, mAbout);
         transaction.addToBackStack(String.valueOf(mTitle));
         transaction.commit();
     }
     /**
      * Handle fragment change to Logout Screen
      */
     private void gotoLogout()
     {
         FragmentManager fragmentManager = getSupportFragmentManager();

         if(mLogout==null)
         {
             mLogout = new Logout();
         }

         mTitle = getString(R.string.title_section10);

         FragmentTransaction transaction = fragmentManager.beginTransaction();
         transaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
         transaction.replace(R.id.container, mLogout);
         transaction.addToBackStack(String.valueOf(mTitle));
         transaction.commit();
     }

     ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
     /////////////////////////////////////////////////////////////// LOGIN RELATED

     /*
      * Handles the post sucess login function
      */
     public void onLoginSuccess()
     {

         if(mCurrentTitle.equals( getString(R.string.title_section4) ) )
             gotoStop();
         else if(mCurrentTitle.equals( getString(R.string.title_section3) ))
             gotoStart();
     }
     /*
      * Handles the post fail login function
      */
     public void onLoginFailed()
     {

     }

     /**
      * LOGOUT
      */
     public void onLogOut()
     {

     }

     ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
     ///////////////////////////////////////////////////////////// AFTER READ TAG

     /*
      * Record properties on tag
      */
     protected void freeSetupOfLogisticalProperties(JSONObject logisticalData){
         //use of predefined logistical properties
         logisticalData.put(PredefinedLogisticalProperties.Names.__productDescription.name(), RecProfile.getInstance().getName() );
         logisticalData.put(PredefinedLogisticalProperties.Names.__producerName.name(), "this is producer name");
         //use of user defined logistical properties
         logisticalData.put("user property", "value of user property");
     }

     protected void  setupOfLogisticalPropertiesUsingDefaultProfile(JSONObject logisticalData) throws BluException {
         LogisticalProfile logisticalProfile=null;
         //find build-in "Default" profile
         for (LogisticalProfile profile : LogisticalProfileManager.get(this).getProfiles()){
             if ("Default".equals(profile.getProfileName())) {
                 logisticalProfile = profile;
                 break;
             }
         }
         //populate each logistical property of "Default" profile with value "example value"
         if (logisticalProfile.getLogisticalEntries()!=null) {
             for (LogisticalProfile.LogisticalEntry entry : logisticalProfile.getLogisticalEntries()) {
                 try {
                     logisticalData.put(entry.getPropName(), "example value");
                 } catch (JSONException e) {
                     throw new BluException(e);
                 }
             }
         }
     }

     protected void  setupOfLogisticalPropertiesUsingMyOwnProfile(JSONObject logisticalData) throws BluException {

         String myProfileName="MyProfile1";
         //get instance of logistical profile manager
         LogisticalProfileManager lpm = LogisticalProfileManager.get(this);

         LogisticalProfile myProfile=null;
         //find "MyProfile" profile if previous run of this method created it
         for (LogisticalProfile profile : LogisticalProfileManager.get(this).getProfiles()){
             if (myProfileName.equals(profile.getProfileName())) {
                 myProfile = profile;
                 break;
             }
         }

         if (myProfile==null) { //if "MyProfile" was not found
             //create new logistical profile
             myProfile = new LogisticalProfile(myProfileName);

             //get collection logistical entries
             ArrayList<LogisticalProfile.LogisticalEntry> logisticalEntries = myProfile.getLogisticalEntries();

             //add new logistical entry using predefined logistical property name
             LogisticalProfile.LogisticalEntry le = new LogisticalProfile.LogisticalEntry(PredefinedLogisticalProperties.Names.__productDescription.name());
             le.setDefValue("def value of prod desc");
             logisticalEntries.add(le);

             //add new logistical entry using user defined property name
             le = new LogisticalProfile.LogisticalEntry("user property");
             le.setPredefined(false);
             le.setDefValue("def value of user property");
             logisticalEntries.add(le);

             //add new profile to logistical profile manager
             lpm.getProfiles().add(myProfile);

             //store permanently new state of logistical profile manager including new created MyProfile for future use
             lpm.writeToStore(this);
         }

         //populate each logistical property of "MyProfile" profile with its default value
         if (myProfile.getLogisticalEntries()!=null) {
             for (LogisticalProfile.LogisticalEntry entry : myProfile.getLogisticalEntries()) {
                 try {
                     logisticalData.put(entry.getPropName(), entry.getDefValue());
                 } catch (JSONException e) {
                     throw new BluException(e);
                 }
             }
         }
     }

     /**
      * Start new tag record
      */
     protected void startNewRecording(Tag tag) throws BluException {
         Recording recording = new Recording();

         JSONObject logisticalData =  new JSONObject();

         // Example1:  free setup of logistical properties
         freeSetupOfLogisticalProperties(logisticalData);

         //Example 2: setup of logistical properties using ProfileManager and build-in profile named "Default". Comment above line and uncomment bellow line please.
//        setupOfLogisticalPropertiesUsingDefaultProfile(logisticalData);

         //Example 3: setup of logistical properties using ProfileManager and user defined profile named "MyProfile". Comment above line and uncomment bellow line please.
//        setupOfLogisticalPropertiesUsingMyOwnProfile(logisticalData);

         Location location=BlutagContent.get().getLocation();
         if (location!=null) {
             JSONObject posData = new JSONObject();
             posData.put("lat", location.getLatitude());
             posData.put("lon", location.getLongitude());
             posData.put("d", (new Date()).getTime() / 1000);
             JSONArray posDataArr=new JSONArray();
             posDataArr.put(posData);

             logisticalData.put("$$pos", posDataArr);
             Log.i("logisticalData", logisticalData.toString(1));
         }


         recording.setLogisticalData(logisticalData);

         recording.setMeasurementCycle(RecProfile.getInstance().getSampling()*10);

         recording.setMinTemp(RecProfile.getInstance().getMin()*10);

         recording.setMaxTemp(RecProfile.getInstance().getMax()*10);

         recording.setDecisionParam1(RecProfile.getInstance().getMinNOk());

         recording.setDecisionParam2(RecProfile.getInstance().getMaxNOk());

         recording.setStartRecordingDelay(0);

         recording.setReadTempPin(0xFFFF);

         recording.setFinishRecordingPin(0xFFFF);

         recording.setActivationEnergy(83);


         Log.i("new recording", recording.getRecordingData().toString());

         BlutagHandler.get().startNewRecording(tag, recording);

//         Toast.makeText(getApplicationContext(), getString(R.string.tag_recorded), Toast.LENGTH_SHORT).show();

     }

     ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
     /////////////////////////////////////////////////////// NATIVE BUTTONS HANLDERS
     /*
      * Handles the native back button
      */
     @Override
     public void onBackPressed()
     {
         if(getSupportFragmentManager().getBackStackEntryCount() > 0)
             getSupportFragmentManager().popBackStack();
         else
         /* ToDo Create warning popup (Do you want to quit?) */
             super.onBackPressed();
           //  Toast.makeText(getApplicationContext(), "Back", Toast.LENGTH_SHORT).show();
     }

     //******************************************** UI HANLDERS *****************************************//

     /**
      * Go to Tag Info screen, get the tag info and set it in info screen
      */
     private void showInfo()
     {
         Log.d("Debug", "showInfo: 1");
         BlutagContent content = BlutagContent.get();


         if(content.getHardware() == 0)
             return;

         //Toast.makeText(getApplicationContext(), "ID: " + content.getDataDefinition().getGenericInfo(), Toast.LENGTH_SHORT).show();

         FragmentManager fragmentManager = getSupportFragmentManager();


         BlutagContent.get().getRecordings().get(0).computeStatistics(); // Compute statistics for min, avg, max, kinect temps, breaches...
         mTagData = new TagData();
         mTagData.setIdNumber(String.valueOf(content.getBlueTagId()));
         mTagData.setFirmwareVer(Integer.toString(content.getFirmware()));
         mTagData.setHardwareVer(Integer.toString(content.getHardware()));
         mTagData.setNumberRecs(content.getRecordings().size());
         mTagData.setTemps(BlutagContent.get().getRecordings().get(0).getTemperatures());
         mTagData.setBreaches(BlutagContent.get().getRecordings().get(0).getStatistics().getBreaches());
         mTagData.setBreachesCount(BlutagContent.get().getRecordings().get(0).getStatistics().getBreachesDuration());
         mTagData.setBreachesDuration(BlutagContent.get().getRecordings().get(0).getStatistics().getBreachesDuration());
         mTagData.setKineticTemp(BlutagContent.get().getRecordings().get(0).getStatistics().getMeanKineticTemp());
         mTagData.setmMinTempRead(BlutagContent.get().getRecordings().get(0).getStatistics().getMinTemp());
         mTagData.setMaxTempRead(BlutagContent.get().getRecordings().get(0).getStatistics().getMaxTemp());
         mTagData.setAvgTempRead(BlutagContent.get().getRecordings().get(0).getStatistics().getAvgTemp());
         mTagData.setFstDownMeasuredate(BlutagContent.get().getRecordings().get(0).getFirstDownloadedMeasurementDate());
         mTagData.setLastDownMeasureDate(BlutagContent.get().getRecordings().get(0).getLastDownloadedMeasurementDate());

         DateFormat dateFormat = DateFormat.getDateTimeInstance();
         DataDefinition dataDefinition = content.getDataDefinition();
         String propertyName;
         String propertyValueStr;
         long propertyValue;


         for ( DataDefinition.DataDefinitionEntry<DataDefinition.GenericInfoType> genericEntry:dataDefinition.getGenericInfo())
         {
             if (genericEntry.getDescription()<0)
                 continue;
             propertyName=genericEntry.getProperty().name();
             propertyValue=content.getGenericData().getLong(propertyName);
             if (genericEntry.getType()==DataDefinition.DataType.DATE)
                 propertyValueStr=dateFormat.format(new Date(propertyValue*1000));
             else
                 propertyValueStr=Long.toString(propertyValue);

             //table.addRow(new TwoColumnTable.Row(getString(genericEntry.getDescription()), propertyValueStr));
        //     fragment.addRow(getString(genericEntry.getDescription()), propertyValueStr);

             Log.d("property 0: " , getString(genericEntry.getDescription()));
             if(getString(genericEntry.getDescription()).equals("Calibration date")) {
                 Date d = new Date(propertyValue * 1000);
                 mTagData.setCalibrateDate(d);
             }
             else if(getString(genericEntry.getDescription()).equals("Expiration date")) {
                 Date d = new Date(propertyValue * 1000);
                 mTagData.setExpirationDate(d);
             }

         }

         if (dataDefinition.getGenericInfoEntry(DataDefinition.GenericInfoType.utilizedDaysCount)!=null) {
             long days;
             long lastPeriodStartDate=content.getGenericData().getLong(DataDefinition.GenericInfoType.lastRecordingStartDate.name());
             long utilizedDaysCount=content.getGenericData().getLong(DataDefinition.GenericInfoType.utilizedDaysCount.name());
             if (  lastPeriodStartDate == 0)
                 days = 0;
             else
                 days = ((new Date().getTime())/1000 -
                         lastPeriodStartDate)
                         / (60 * 60 * 24);

             //table.addRow(new TwoColumnTable.Row(getString(R.string.utilizedDaysCount), Long.toString(utilizedDaysCount + days)));
           //  fragment.addRow("Days Count", Long.toString(utilizedDaysCount + days));
         }

         if (dataDefinition.getGenericInfoEntry(DataDefinition.GenericInfoType.timeToLive)!=null) { //******************************* RECORDINGS TIME LEFT
             long timeToLive=content.getGenericData().getLong(DataDefinition.GenericInfoType.timeToLive.name());
             long heartbeatDuration=content.getGenericData().getLong(DataDefinition.GenericInfoType.heartbeatDuration.name());
             long lastPeriodStartDate=content.getGenericData().getLong(DataDefinition.GenericInfoType.lastRecordingStartDate.name());
             long lastPeriodDuration=0;
             if (lastPeriodStartDate>0)
                 lastPeriodDuration=(new Date()).getTime()/1000-lastPeriodStartDate;
             //table.addRow(new TwoColumnTable.Row(getString(R.string.time_to_live), Utils.secondsToInterval((int) (timeToLive * heartbeatDuration - lastPeriodDuration))));
         //    fragment.addRow("Time To Live", Utils.secondsToInterval((int) (timeToLive * heartbeatDuration - lastPeriodDuration)));
             mTagData.setRecTimeLeft(Utils.secondsToInterval((int) (timeToLive * heartbeatDuration - lastPeriodDuration)));
         }

         if (content.getGenericData().getLong(DataDefinition.GenericInfoType.utilizedRecordingsCount.name())>0) { //*************************** COUNT OF RECORDINGS USED


             Recording recording = content.getRecordings().get(0);

             JSONObject logisticData = recording.getLogisticalData();
             Iterator<String> iter = logisticData.keys();
             String propName;
             String propValue;
             while (iter.hasNext()) {
                 propName = iter.next();
                 if (propName.equals("$$pos"))
                     continue;
                 if ((propValue = logisticData.optString(propName)) != null) {
                     int resourceID = getResources().getIdentifier(propName, "string", this.getPackageName());
                     if (resourceID != 0)
                         propName = getString(resourceID);
                     // table.addRow(new TwoColumnTable.Row(propName, propValue));
                     //       fragment.addRow(propName, propValue);

                 }
             }


             dataDefinition = recording.getDataDefinition();

             for (DataDefinition.DataDefinitionEntry<DataDefinition.RecordingInfoType> recordingEntry : dataDefinition.getDeserialRecordingInfo()) {
                 if (recordingEntry.getDescription() < 0)
                     continue;
                 propertyName = recordingEntry.getProperty().name();
                 propertyValue = recording.getRecordingData().getLong(propertyName);
                 if (recordingEntry.getType() == DataDefinition.DataType.DATE) //************************* RECORDING DATE
                 {
                     if(getString(recordingEntry.getDescription()).equals("Start date of recording"))
                         mTagData.setStartDateRec(new Date(propertyValue * 1000));
                     else if(getString(recordingEntry.getDescription()).equals("End date of recording"))
                         mTagData.setEndDateRec(new Date(propertyValue * 1000));
                     if (propertyValue > 0)
                         propertyValueStr = dateFormat.format(new Date(propertyValue * 1000));
                     else
                         propertyValueStr = "";
                 }
                 else if (recordingEntry.getProperty() == DataDefinition.RecordingInfoType.minTemp ) { //******************************** MIN Temperature
                     mTagData.setMinTemp((int) propertyValue);
                     propertyValueStr = devideByTen((int) propertyValue) + getString(R.string.temperature_unit);
                 }
                 else if (recordingEntry.getProperty() == DataDefinition.RecordingInfoType.maxTemp) { //******************************** MAX Temperature
                     propertyValueStr = devideByTen((int) propertyValue) + getString(R.string.temperature_unit);
                     mTagData.setMaxTemp((int) propertyValue);
                 }
                 else {
                     propertyValueStr = Long.toString(propertyValue);
                     if(recordingEntry.getProperty() == DataDefinition.RecordingInfoType.activationEnergy ) // *************************** Activation Energy
                        mTagData.setActivationEnergy(propertyValue);
                     else if(recordingEntry.getProperty() == DataDefinition.RecordingInfoType.measurementCycle) //************************** Length of measurement cycle
                         mTagData.setMeasureLenght(propertyValue);
                 }

                 //table.addRow(new TwoColumnTable.Row(getString(recordingEntry.getDescription()), propertyValueStr));
              //   fragment.addRow(getString(recordingEntry.getDescription()), propertyValueStr);

                 Log.d("Property:", getString(recordingEntry.getDescription()));

             }

             if (dataDefinition.getDeserialRecordingInfoEntry(DataDefinition.RecordingInfoType.pinsInfo) != null) {
                 long pinsUsed = recording.getRecordingData().getLong(DataDefinition.RecordingInfoType.pinsInfo.name());
                 //table.addRow(new TwoColumnTable.Row(getString(R.string.readTempPinUsed), (pinsUsed & 0x02) == 0x02 ? getString(R.string.yes) : getString(R.string.no)));
             //    fragment.addRow("Protect temp reading", (pinsUsed & 0x02) == 0x02 ? "YES" : "NO");

                 //table.addRow(new TwoColumnTable.Row(getString(R.string.finishRecordingPinUsed), (pinsUsed & 0x01) == 0x01 ? getString(R.string.yes) : getString(R.string.no)));//fragment.addRow("Protect stop rec", (pinsUsed & 0x01) == 0x01 ? "YES" : "NO");
             }
         }

         mTitle = getString(R.string.title_section5);

         mTagInfo = new TagInfoFragment();
         mTagInfo.setContext(getApplicationContext());
         mTagInfo.createList();
         mTagInfo.setIdNumber(mTagData.getIdNumber());
         mTagInfo.setFirmwareVer(mTagData.getFirmwareVer());
         mTagInfo.setHardwareVer(mTagData.getHardwareVer());
         mTagInfo.setNumberRecs(mTagData.getNumberRecs());
         mTagInfo.setProdDesc(mTagData.getProdDesc());
         mTagInfo.setStartDateRec(mTagData.getStartDateRec());
         mTagInfo.setEndDateRec(mTagData.getEndDateRec());
         mTagInfo.setCalibrateDate(mTagData.getCalibrateDate());
         mTagInfo.setExpirationDate(mTagData.getExpirationDate());
         mTagInfo.populateList();

         FragmentTransaction transaction = fragmentManager.beginTransaction();
         transaction.replace(R.id.container, mTagInfo);
         transaction.addToBackStack(String.valueOf(mTitle));
         transaction.commit();


     }

     ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
     ////////////////////////////////////////////////////////////////////////////// HELPERS

     /*
      * Helper to upload tag data, check if exists internet connectio and do the upload, otherwise run intent to perform it on background
      */
     private void uploadTagDataHelper(){

         if(isNetworkConnected()){
             // ToDo, Upload tag data
             Toast.makeText(getApplicationContext(), getString(R.string.uploading_data), Toast.LENGTH_LONG).show();
         }
         else{
             // Start Intent to upload data when find network
             Toast.makeText(getApplicationContext(), getString(R.string.no_internet), Toast.LENGTH_LONG).show();
             startService(mUploadService);
         }
     }

     static protected String devideByTen(int data){
         return Integer.toString(data/10)+"."+Integer.toString(data%10);

     }

     public void askDoRetry(){
         AlertDialog.Builder alert= new AlertDialog.Builder(this);
         alert.setTitle("Operation not completed");
         alert.setMessage("Operation completed in "+BlutagHandler.get().getPercentageReadData()+"%. Do you want to continue reading of EEPROM?");

         final View v = getLayoutInflater().inflate(R.layout.activity_main, null);
         alert.setView(v);

         alert.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 mBackgroundTagProcessor=null;
                 BlutagHandler.get().setResumeRead(true);
                 mBusyOnProcessNFC.set(false);
                 Toast.makeText(MainActivity.this, getString(R.string.putBlutagInRange, getString(R.string.nfc_device_name)), Toast.LENGTH_LONG).show();
             }
         });
         alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                 IntentOption.getInstance().setOption(IntentOption.Operations.NOTHING);
                 mBackgroundTagProcessor=null;
                 BlutagHandler.get().setResumeRead(false);
                 mBusyOnProcessNFC.set(false);
             }
         });
         alert.show();
     }
     private boolean isNetworkConnected() {
         ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

         return cm.getActiveNetworkInfo() != null;
     }
     /*
      * Get GPS Coordinates
      */
     private void getGPSLocation()
     {

         Log.d("Debug:", "GPSLocation start");
         LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

         LocationListener locationListener = new LocationListener(){
             @Override
             public void onLocationChanged(final Location location){
                 Log.d("Latitude", String.valueOf(location.getLatitude()));
                 Log.d("Longitude", String.valueOf(location.getLongitude()));
                 Log.d("Altitude", String.valueOf(location.getAltitude()));
             }
             @Override
             public void onStatusChanged(String provider, int status, Bundle extras) {
                 Log.d("Latitude","status");
             }
             @Override
             public void onProviderDisabled(String provider) {
                 Log.d("Latitude","disable");
             }

             @Override
             public void onProviderEnabled(String provider) {
                 Log.d("Latitude","enable");
             }
         };
         locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

         // getting GPS status
         boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

         // getting network status
         boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

         Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

         Log.d("Last Latitude", String.valueOf(location.getLatitude()));
         Log.d("Last Longitude", String.valueOf(location.getLongitude()));
         Log.d("Last Altitude", String.valueOf(location.getAltitude()));

         Log.d("Debug:", "GPSLocation end");

     }

}
