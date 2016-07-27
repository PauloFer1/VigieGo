package com.tarambola.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;


import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.tarambola.controller.BackgroundTagProcessor;
import com.tarambola.controller.LoginSession;
import com.tarambola.model.TagData;

import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import eu.blulog.blulib.Utils;
import eu.blulog.blulib.exceptions.BluException;
import eu.blulog.blulib.json.JSONObject;
import eu.blulog.blulib.nfc.BackgroundExecutor;
import eu.blulog.blulib.nfc.NfcUtils;
import eu.blulog.blulib.tdl2.BlutagContent;
import eu.blulog.blulib.tdl2.BlutagHandler;
import eu.blulog.blulib.tdl2.DataDefinition;
import eu.blulog.blulib.tdl2.Recording;




 public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, Login.OnFragmentInteractionListener, Logout.OnFragmentInteractionListener{

    protected enum Operations {FINISH_RECORDING, READ_TEMPS, SHOW_TEMPS, NOTHING, RECOVER_AAR, START_RECORDING, SHORT_READ};

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


     private Operations operation=Operations.SHORT_READ;

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

        mBackgroundTagProcessor=null;
        mBusyOnProcessNFC = new AtomicBoolean(false);

        mTagData = new TagData();


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
        BlutagContent mBlutagContent;

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

    @Override
    public void onNewIntent(Intent intent)
    {
        Log.d("debug", "onNewIntent: ");

        Log.w("************ operation", operation.name());

              //  Toast.makeText(getApplicationContext(), "Put Tag", Toast.LENGTH_SHORT).show();


                Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

                if (tag == null) {
                    Log.d("operation", "Intent: " +  operation.name() + ", Tag = null");
                    return;
                }

                intent.removeExtra(NfcAdapter.EXTRA_TAG);
                if (!mBusyOnProcessNFC.compareAndSet(false, true)) { //If busyOnProcessNFC is true to return and set busyOnProcessNFC to true
                    if (operation!= Operations.SHORT_READ)
                        BlutagHandler.get().processNextTag(tag); //new in 1.6.x
                    Toast.makeText(getApplicationContext(), "Busy Busy", Toast.LENGTH_SHORT).show();
                    return;
                }

                Log.i("start new intent", intent.toString());

        switch(operation) {
            case SHORT_READ:
                mBackgroundTagProcessor =
                        new BackgroundTagProcessor(this, R.string.downloading_nfc_data, R.string.dont_remove_tag, tag) {
                            @Override
                            protected void postExecute(String status) {
                                if (status == null) {
                                    //contentView.removeAllViews();
                                    showInfo();
                                    //Toast.makeText(context, R.string.operation_successfully_completed, Toast.LENGTH_SHORT).show();
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



                                Log.i("finished", this.toString());
                            }
                        };
                Log.d("debug", "onNewIntent: 2");
                break;

            case READ_TEMPS:
                mBackgroundTagProcessor =
                        new BackgroundTagProcessor(this, R.string.downloading_nfc_data, R.string.dont_remove_tag, tag, ProgressDialog.STYLE_HORIZONTAL) {
                            @Override
                            protected void postExecute(String status) {
                                if (status == null) {
                                    Toast.makeText(context, R.string.operation_successfully_completed, Toast.LENGTH_SHORT).show();
                                    BackgroundExecutor be = new BackgroundExecutor() {

                                        @Override
                                        protected void postExecute(String status) {
                                            operation=Operations.SHORT_READ;
                                            mBusyOnProcessNFC.set(false);
                                        }
                                        @Override
                                        protected void backgroundWork() {
                                            try {Thread.sleep(WAIT_TIME);} catch (InterruptedException e) {}
                                        }
                                    };
                                    be.execute(null, null);
                                    mBackgroundTagProcessor=null;
                                } else {
                                    askDoRetry();
                                }
                            }

                            @Override
                            protected void processTag(Tag tag) throws BluException {
                                BlutagHandler.get().readTempsFromEeprom(tag, 0);
                            }
                        };
                break;
            default:;
        }
        Log.i("post start new intent", intent.toString());
        if (mBackgroundTagProcessor!=null)
            mBackgroundTagProcessor.execute(tag);

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
      //  FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment fragment = null;

        //fragmentManager.beginTransaction().replace(R.id.container, PlaceholderFragment.newInstance(position + 1)).commit();

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



     /* ***************************** SCREENS HANDLERS ***************************************** */

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
             mProfiles = new Profiles();
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
                 mStart = new StartFragment();
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
                 mTagInfo.setIdNumber(String.valueOf(content.getBlueTagId()));
                 mTagInfo.setFirmwareVer(Integer.toString(content.getFirmware()));
                 mTagInfo.setHardwareVer(Integer.toString(content.getHardware()));
                 mTagInfo.setNumberRecs(content.getRecordings().size());
                 mTagInfo.setProdDesc("sasasa");
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

     /* ********************* LOGIN RELATED ************************ */

     public void onLoginSuccess()
     {

         if(mCurrentTitle.equals( getString(R.string.title_section4) ) )
             gotoStop();
         else if(mCurrentTitle.equals( getString(R.string.title_section3) ))
             gotoStart();
     }
     public void onLoginFailed()
     {

     }

     /**
      * LOGOUT
      */
     public void onLogOut()
     {

     }

     /* ******************************************** NATIVE BUTTONS HANDLER **************************************************** */
     @Override
     public void onBackPressed()
     {
         if(getFragmentManager().getBackStackEntryCount() > 0)
             getFragmentManager().popBackStack();
         else
         /* ToDo Create warning popup (Do you want to quit?) */
           //  super.onBackPressed();
         Toast.makeText(getApplicationContext(), "Back", Toast.LENGTH_SHORT).show();
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

         TagInfoFragment fragment = null;
         fragment = new TagInfoFragment();
         fragment.setContext(getApplicationContext());
         fragment.createList();
         fragment.setIdNumber(String.valueOf(content.getBlueTagId()));
         fragment.setFirmwareVer(Integer.toString(content.getFirmware()));
         fragment.setHardwareVer(Integer.toString(content.getHardware()));
         fragment.setNumberRecs(content.getRecordings().size());
         fragment.setProdDesc("sasasa");
         fragment.populateList();

         mTagData = new TagData();
         mTagData.setIdNumber(String.valueOf(content.getBlueTagId()));
         mTagData.setFirmwareVer(Integer.toString(content.getFirmware()));
         mTagData.setHardwareVer(Integer.toString(content.getHardware()));
         mTagData.setNumberRecs(content.getRecordings().size());
         mTagData.setTemps(BlutagContent.get().getRecordings().get(0).getTemperatures());

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
             fragment.addRow(getString(genericEntry.getDescription()), propertyValueStr);

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
             fragment.addRow("Days Count", Long.toString(utilizedDaysCount + days));
         }

         if (dataDefinition.getGenericInfoEntry(DataDefinition.GenericInfoType.timeToLive)!=null) { //******************************* RECORDINGS TIME LEFT
             long timeToLive=content.getGenericData().getLong(DataDefinition.GenericInfoType.timeToLive.name());
             long heartbeatDuration=content.getGenericData().getLong(DataDefinition.GenericInfoType.heartbeatDuration.name());
             long lastPeriodStartDate=content.getGenericData().getLong(DataDefinition.GenericInfoType.lastRecordingStartDate.name());
             long lastPeriodDuration=0;
             if (lastPeriodStartDate>0)
                 lastPeriodDuration=(new Date()).getTime()/1000-lastPeriodStartDate;
             //table.addRow(new TwoColumnTable.Row(getString(R.string.time_to_live), Utils.secondsToInterval((int) (timeToLive * heartbeatDuration - lastPeriodDuration))));
             fragment.addRow("Time To Live", Utils.secondsToInterval((int) (timeToLive * heartbeatDuration - lastPeriodDuration)));
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
                     fragment.addRow(propName, propValue);

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
                 fragment.addRow(getString(recordingEntry.getDescription()), propertyValueStr);


             }

             if (dataDefinition.getDeserialRecordingInfoEntry(DataDefinition.RecordingInfoType.pinsInfo) != null) {
                 long pinsUsed = recording.getRecordingData().getLong(DataDefinition.RecordingInfoType.pinsInfo.name());
                 //table.addRow(new TwoColumnTable.Row(getString(R.string.readTempPinUsed), (pinsUsed & 0x02) == 0x02 ? getString(R.string.yes) : getString(R.string.no)));
                 fragment.addRow("Protect temp reading", (pinsUsed & 0x02) == 0x02 ? "YES" : "NO");

                 //table.addRow(new TwoColumnTable.Row(getString(R.string.finishRecordingPinUsed), (pinsUsed & 0x01) == 0x01 ? getString(R.string.yes) : getString(R.string.no)));
                 fragment.addRow("Protect stop rec", (pinsUsed & 0x01) == 0x01 ? "YES" : "NO");
             }
         }

         mTitle = getString(R.string.title_section5);

         FragmentTransaction transaction = fragmentManager.beginTransaction();
         transaction.replace(R.id.container, fragment);
         transaction.addToBackStack(String.valueOf(mTitle));
         transaction.commit();


     }

     //************************ HELPERS ****************************//
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
                 operation=Operations.NOTHING;
                 mBackgroundTagProcessor=null;
                 BlutagHandler.get().setResumeRead(false);
                 mBusyOnProcessNFC.set(false);
             }
         });
         alert.show();
     }

}
