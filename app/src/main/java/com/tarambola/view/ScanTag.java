package com.tarambola.view;

/**
 * Created by Paulo on 07/06/2016.
 */

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

import java.util.ArrayList;

public class ScanTag extends Fragment{

    private LineChart mChart;
    private SeekBar mSeekBarX, mSeekBarY;
    private TextView tvX, tvY;

    public ScanTag(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.scan_tag, container, false);

        Typeface font = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/sui-generis-rg.ttf");

        TextView tv=(TextView) rootView.findViewById(R.id.mKineticLabel);
        tv.setTypeface(font);
        TextView lastMeasure=(TextView) rootView.findViewById(R.id.mLastMeasureLabel);
        lastMeasure.setTypeface(font);

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


        mChart = new LineChart(rootView.getContext());
        mChart.setLayoutParams(new FrameLayout.LayoutParams(DrawerLayout.LayoutParams.FILL_PARENT,DrawerLayout.LayoutParams.FILL_PARENT));

        mChart.setTouchEnabled(false);
        // enable scaling and dragging
       // mChart.setDragEnabled(true);
        //mChart.setScaleEnabled(true);
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);
        // if disabled, scaling can be done on x- and y-axis separately
        //mChart.setPinchZoom(true);
        mChart.setDescription("");
        mChart.setDrawBorders(false);

        LimitLine llXAxis = new LimitLine(10f, "Index 10");
        llXAxis.setLineWidth(4f);
        llXAxis.enableDashedLine(10f, 10f, 0f);
        llXAxis.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        llXAxis.setTextSize(10f);

        XAxis xAxis = mChart.getXAxis();


        LimitLine ll1 = new LimitLine(30f, "MAX NOT OK");
        ll1.setLineWidth(2f);
        ll1.enableDashedLine(10f, 10f, 0f);
        ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        ll1.setTextSize(9f);
        ll1.setTypeface(font);
        ll1.setTextColor(Color.GRAY);
        ll1.setLineColor(Color.rgb(255, 210, 77));

        LimitLine ll2 = new LimitLine(5f, "MIN NOT OK");
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
        leftAxis.setAxisMaxValue(40f);
        leftAxis.setAxisMinValue(-5f);
        //leftAxis.setYOffset(20f);
       // leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);
        leftAxis.setDrawAxisLine(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        mChart.getAxisRight().setEnabled(false);
        //mChart.getXAxis().setEnabled(false);
        mChart.getXAxis().setDrawAxisLine(false);
        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mChart.getXAxis().setDrawGridLines(false);

        //mChart.getViewPortHandler().setMaximumScaleY(2f);
        //mChart.getViewPortHandler().setMaximumScaleX(2f);

        // add data
        setData(45, 20);

//        mChart.setVisibleXRange(20);
//        mChart.setVisibleYRange(20f, AxisDependency.LEFT);
//        mChart.centerViewTo(20, 50, AxisDependency.LEFT);

        mChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);
//        mChart.invalidate();

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);


        RelativeLayout chartLayout = (RelativeLayout) rootView.findViewById(R.id.mChartLayout);
        chartLayout.addView(mChart);


        chartLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoChart(v);
            }
        });

        return(rootView);
    }


    //****************************** LISTENERS Helpers
    public void gotoChart(View v)
    {
        FragmentManager fm = this.getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left);
        Chart bigChart = new Chart();
        ft.replace(R.id.container, bigChart).commit();
    }


    // ***************************************************** SET DATA CHART
    private void setData(int count, float range) {

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add((i) + "");
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {

            float mult = (range + 1);
            float val = (float) (Math.random() * mult) + 3;// + (float)
            // ((mult *
            // 0.1) / 10);x
            yVals.add(new Entry(val, i));
        }

        LineDataSet set1;

        if (mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
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
}
