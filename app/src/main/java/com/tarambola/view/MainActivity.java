package com.tarambola.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
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

import java.util.concurrent.atomic.AtomicBoolean;

import eu.blulog.blulib.exceptions.BluException;
import eu.blulog.blulib.nfc.BackgroundExecutor;
import eu.blulog.blulib.nfc.NfcUtils;
import eu.blulog.blulib.tdl2.BlutagContent;
import eu.blulog.blulib.tdl2.BlutagHandler;
import eu.blulog.blulib.tdl2.Recording;




 public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    protected enum Operations {FINISH_RECORDING, READ_TEMPS, SHOW_TEMPS, NOTHING, RECOVER_AAR, START_RECORDING, SHORT_READ};

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBackgroundTagProcessor=null;
        mBusyOnProcessNFC = new AtomicBoolean(false);


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

        Log.w("operation", operation.name());

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
                                    Toast.makeText(context, R.string.operation_successfully_completed, Toast.LENGTH_SHORT).show();
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
                                if (BlutagContent.get().getRecordings().size() > 0
                                        && BlutagContent.get().getRecordings().get(0).getRegistrationStartDate().getTime() < Recording.START_BY_BUTTON)
                                    BlutagContent.get().getRecordings().get(0).computeStatistics();


                                Log.i("finished", this.toString());
                            }
                        };
                Log.d("debug", "onNewIntent: 2");
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
        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment fragment = null;

        //fragmentManager.beginTransaction().replace(R.id.container, PlaceholderFragment.newInstance(position + 1)).commit();

        switch(position) {
            case 0:
                fragment = new ScanTag();
                mTitle = getString(R.string.title_section1);
                break;
            case 1:
                fragment = new Profiles();
                mTitle = getString(R.string.title_section2);
                break;
            case 2:
                fragment = new Settings();
                mTitle = getString(R.string.title_section3);
                break;
            default:
                break;
        }
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            Log.d("DEBUG", "onAttach: event");
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

     //******************************************** UI HANLDERS *****************************************//
     private void showInfo()
     {
         Log.d("Debug", "showInfo: 1");
         BlutagContent content = BlutagContent.get();

         if(content.getHardware() == 0)
             return;

         Toast.makeText(getApplicationContext(), "ID: " + content.getDataDefinition().getGenericInfo(), Toast.LENGTH_SHORT).show();
     }

}
