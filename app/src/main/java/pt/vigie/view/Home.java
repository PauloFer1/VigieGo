package pt.vigie.view;

/**
 * Created by Paulo on 07/06/2016.
 */

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.Utils;
import pt.vigie.model.PDFDownloader;
import pt.vigie.model.ScreenStatus;
import pt.vigie.model.TagData;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import pt.vigie.vigiego.R;

public class Home extends Fragment {

    private LineChart       mChart;
    private SeekBar         mSeekBarX, mSeekBarY;
    private TextView        tvX, tvY;
    private TagData         mTagData;
    private ProgressDialog  mProgressDialog;
    private View            mRootView;
    private ArrayList<String> xVals;
    private ProgressBar     mChartProgressBar;

    private static final String ARG_PARAM1 = "mTagData";

    public Home(){}


    public static Home newInstance(TagData tagData) {
        Home fragment = new Home();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.fragment_home, container, false);

        mRootView = rootView;

        /* ******** DESIGN ********** */
        Typeface font = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/sui-generis-rg.ttf");

        mChartProgressBar = (ProgressBar) rootView.findViewById(R.id.chartProgressBar);

        if(mTagData.isEmpty())
            return rootView;

        TextView tv=(TextView) rootView.findViewById(R.id.mKineticLabel);
        tv.setTypeface(font);
        TextView lastMeasure=(TextView) rootView.findViewById(R.id.mLastMeasureLabel);
        lastMeasure.setTypeface(font);
        TextView lastMeasureVal=(TextView) rootView.findViewById(R.id.mLastMeasureValLabel);
        lastMeasureVal.setTypeface(font);

        TextView minLabel = (TextView) rootView.findViewById(R.id.mMinTempLabel);
        minLabel.setTypeface(font);
        TextView avgLabel = (TextView) rootView.findViewById(R.id.mAvgTempLabel);
        avgLabel.setTypeface(font);
        TextView maxLabel = (TextView) rootView.findViewById(R.id.mMaxTempLabel);
        maxLabel.setTypeface(font);

        TextView breachesTime = (TextView) rootView.findViewById(R.id.mBreachesTime);
        breachesTime.setTypeface(font);
        TextView breachesNumber = (TextView) rootView.findViewById(R.id.mBreachesNumber);
        breachesNumber.setTypeface(font);

        TextView downloadBtnLabel = (TextView) rootView.findViewById(R.id.mDownloadBtnLabel);
        downloadBtnLabel.setTypeface(font);

        TextView kinectVal=(TextView) rootView.findViewById(R.id.mKineticValue);

        TextView lastMeasureDate = (TextView) rootView.findViewById(R.id.mLastMeasureDate);

        /* Set params */
        float min = (float) (((float)mTagData.getMinTempRead())/10.0);
        if(min >= 80 || min <= -50)min = 0;
        float max = (float)(((float)mTagData.getMaxtempRead())/10.0);
        if(max >= 80 || max <= -50)max = 0;
        float avg = (float)(((float)mTagData.getAverageTemp())*10.0);
        avg = Math.round(avg);
        avg = avg/10;
        float last=0;
        if(mTagData.getTemps()!=null) {
            last = (float) (((float) mTagData.getLastMeasure()) / 10.0);
        }
        float kinet = (float) (mTagData.getKineticTemp());

        BigDecimal kinetRound2 = new BigDecimal(Float.toString(kinet));
        kinetRound2 = kinetRound2.setScale(2, BigDecimal.ROUND_HALF_UP);

        minLabel.setText(Float.toString(min)+"ºc");
        maxLabel.setText(Float.toString(max)+"ºc");
        avgLabel.setText(Float.toString(avg)+"ºc");
        lastMeasureVal.setText(Float.toString(last)+"ºc");
        kinectVal.setText(kinetRound2.toString()+"ºc");
        TagData.RecTimeLeft breachT = new TagData.RecTimeLeft();
        breachT.convertFromSecs(mTagData.getBreachesDuration());

        breachesTime.setText(Integer.toString(breachT.hours)+":"+Integer.toString(breachT.minutes)+":"+Integer.toString(breachT.seconds));
        breachesNumber.setText(Integer.toString(mTagData.getBreachesCount()));

        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        if(mTagData.getLastDownMeasureDate()!=null)
            lastMeasureDate.setText(fmt.format(mTagData.getLastDownMeasureDate()));
        else
            lastMeasureDate.setText("-");


        final ImageButton downloadBtn = (ImageButton)rootView.findViewById(R.id.mDownloadBtn);
        downloadBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                downloadPDF(v);
            }
        });

        return(rootView);
    }

    @Override
    public void onStart(){
        super.onStart();;
        ////////////////////////////////////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////////// CHART

        Typeface font = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/sui-generis-rg.ttf");

        mChart = new LineChart(mRootView.getContext());
        mChart.setLayoutParams(new FrameLayout.LayoutParams(DrawerLayout.LayoutParams.FILL_PARENT,DrawerLayout.LayoutParams.FILL_PARENT));

        mChart.setTouchEnabled(false);
        mChart.setDescription("");
        mChart.setDrawBorders(false);
        mChart.setNoDataText("");

        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        XAxis xAxis = mChart.getXAxis();

        LimitLine ll1 = new LimitLine(mTagData.getMaxtemp()/10, "MAX NOT OK");
        ll1.setLineWidth(2f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(9f);
        ll1.setTypeface(font);
        ll1.setTextColor(Color.GRAY);
        ll1.setLineColor(Color.rgb(255, 210, 77));

        LimitLine ll2 = new LimitLine(mTagData.getMinTemp()/10, "MIN NOT OK");
        ll2.setLineWidth(2f);
        ll2.enableDashedLine(10f, 10f, 0f);
        ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        ll2.setTextSize(9f);
        ll2.setTypeface(font);
        ll2.setTextColor(Color.GRAY);
        ll2.setLineColor(Color.rgb(115, 220, 255));

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(ll1);
        leftAxis.addLimitLine(ll2);
        leftAxis.setAxisMaxValue(mTagData.getMaxtempRead()/10 + 10);
        leftAxis.setAxisMinValue(mTagData.getMinTempRead()/10 - 10);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setDrawAxisLine(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        mChart.getAxisRight().setEnabled(false);
        mChart.getXAxis().setDrawAxisLine(false);
        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mChart.getXAxis().setDrawGridLines(false);

        // add data
       // setData(mTagData.getTemps());

        ChartTask asyncTask = new ChartTask(mTagData){
            @Override
            protected  void onPostExecute(ArrayList<String> vals){
                xVals = vals;
                ArrayList<Entry> yVals = this.mYVals;

                LineDataSet set1;

                if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0) {
                    set1 = (LineDataSet)mChart.getData().getDataSetByIndex(0);
                    mChart.getData().notifyDataChanged();
                    mChart.notifyDataSetChanged();
                } else {
                    // create a dataset and give it a type
                    set1 = new LineDataSet(yVals, "");
                    set1.setColor(Color.rgb(2, 115, 100));
                    set1.setCircleColor(Color.argb(100, 3, 181, 158));
                    set1.setDrawCircles(false);
                    set1.setLineWidth(1f);
                    set1.setCircleRadius(2f);
                    set1.setDrawCircleHole(true);
                    set1.setDrawValues(false);

                    if (Utils.getSDKInt() >= 18) {
                        // fill drawable only supported on api level 18 and above
                        // Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
                        //set1.setFillDrawable(drawable);
                    } else {
                        set1.setFillColor(Color.BLACK);
                    }

                    ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                    dataSets.add(set1); // add the datasets

                    // create a data object with the datasets
                    LineData data = new LineData(xVals, dataSets);

                    mChartProgressBar.setVisibility(View.INVISIBLE);

                    // set data
                    mChart.setData(data);

                    mChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);
                }
            }
        };

        asyncTask.execute(mChart);


        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        l.setForm(Legend.LegendForm.LINE);


        RelativeLayout chartLayout = (RelativeLayout) mRootView.findViewById(R.id.mChartLayout);
        chartLayout.addView(mChart);


        chartLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoChart(v);
            }
        });
    }

    //****************************** LISTENERS Helpers
    public void gotoChart(View v)
    {
        FragmentManager fm = this.getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
        Chart bigChart = Chart.newInstance(mTagData);
    //    ft.addToBackStack("Chart");
        ft.replace(R.id.container, bigChart).commit();

        ScreenStatus.getInstance().setStatus(getString(R.string.chart));
    }

    private void downloadPDF(View v)
    {
        // instantiate it within the onCreate method
        mProgressDialog = new ProgressDialog(v.getContext());
        mProgressDialog.setMessage("Downloading PDF");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);


        final PDFDownloader downloadHandler = new PDFDownloader(v.getContext(), mProgressDialog);
        downloadHandler.execute("http://api.vigiesolutions.com/v1/vigie-go/report/"+mTagData.getIdNumber());


        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                downloadHandler.cancel(true);
            }
        });

    }

    // ***************************************************** SET DATA CHART
    private void setData(short[] temps) {

        long nSecs = temps.length * mTagData.getMeasureLength();

       // Log.d("Debug Secs", Integer.toString((int)nSecs));

        ArrayList<String> xVals = new ArrayList<String>();
        SimpleDateFormat fmt = new SimpleDateFormat("dd HH:mm");
        xVals.add(fmt.format(mTagData.getFstDownMeasuredate()));
        for (int i = 1; i < temps.length; i++) {
            if(nSecs > 3600 && (i*(int)mTagData.getMeasureLength()) % 3600 == 0){
                long days = TimeUnit.SECONDS.toDays(i*mTagData.getMeasureLength());
                long hours = TimeUnit.SECONDS.toHours(i*mTagData.getMeasureLength()) - (days*24);
                long minutes = TimeUnit.SECONDS.toMinutes(i*mTagData.getMeasureLength()) - (days*24) - (hours*60);
                long seconds = i*(mTagData.getMeasureLength()/60) - (days*24) - (hours*60) - (minutes*60);

                GregorianCalendar cal = new GregorianCalendar(2016, 8, (int)days, (int)hours, (int)minutes, (int)seconds);
                Date current = cal.getTime();
                long sum = current.getTime() + mTagData.getFstDownMeasuredate().getTime();
                Date sumDate = new Date(sum);
                xVals.add(fmt.format(sumDate));
            //    Log.d("Debug Date", sumDate.toString());
            }
            else {
                long days = TimeUnit.SECONDS.toDays(i*mTagData.getMeasureLength());
                long hours = TimeUnit.SECONDS.toHours(i*mTagData.getMeasureLength()) - (days*24);
                long minutes = TimeUnit.SECONDS.toMinutes(i*mTagData.getMeasureLength()) - (days*24) - (hours*60);
                long seconds = i*(mTagData.getMeasureLength()/60) - (days*24) - (hours*60) - (minutes*60);

                GregorianCalendar cal = new GregorianCalendar(0, 0, (int)days, (int)hours, (int)minutes, (int)seconds);
                Date current = cal.getTime();
                long sum = current.getTime() + mTagData.getFstDownMeasuredate().getTime();
                Date sumDate = new Date(sum);
                xVals.add(fmt.format(sumDate));
            }
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < temps.length; i++) {

            float val = (float) temps[i]/10;
            yVals.add(new Entry(val, i));
        }

        LineDataSet set1;

        if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet)mChart.getData().getDataSetByIndex(0);
            //set1.setYVals(yVals);
            //mChart.getData().setXVals(xVals);
            mChart.getData().notifyDataChanged();
            mChart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(yVals, "");

            // set1.setFillAlpha(110);
            // set1.setFillColor(Color.RED);

            // set the line to be drawn like this "- - - - - -"
           // set1.enableDashedLine(10f, 5f, 0f);
            //et1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(Color.rgb(2,115,100));
            set1.setCircleColor(Color.argb(100,3,181,158));
            set1.setDrawCircles(false);
            set1.setLineWidth(1f);
            set1.setCircleRadius(2f);
            set1.setDrawCircleHole(true);
            //set1.setValueTextSize(8f);
            set1.setDrawValues(false);
           // set1.setDrawFilled(true);

            if (Utils.getSDKInt() >= 18) {
                // fill drawable only supported on api level 18 and above
               // Drawable drawable = ContextCompat.getDrawable(this, R.drawable.fade_red);
                //set1.setFillDrawable(drawable);
            }
            else {
                set1.setFillColor(Color.BLACK);
            }

            ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
            dataSets.add(set1); // add the datasets

            // create a data object with the datasets
            LineData data = new LineData(xVals, dataSets);

            // set data
            mChart.setData(data);
        }
    }

    // ************************************* SETTERS ************************************* //
    public void setTagData(TagData data)
    {
        this.mTagData = data;
    }
}
