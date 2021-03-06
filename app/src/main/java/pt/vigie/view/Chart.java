package pt.vigie.view;

import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
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
import pt.vigie.model.TagData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import pt.vigie.vigiego.R;


public class Chart extends Fragment {

    private LineChart mChart;
    private SeekBar mSeekBarX, mSeekBarY;
    private TextView tvX, tvY;
    private TagData mTagData;

    private OnFragmentInteractionListener mListener;

    private static final String ARG_PARAM1 = "mTagData";

    public Chart() {
        // Required empty public constructor
    }

    public static Chart newInstance(TagData tagData) {
        Chart fragment = new Chart();
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

        final View rootView = inflater.inflate(R.layout.fragment_chart, container, false);

        Typeface font = Typeface.createFromAsset(this.getActivity().getAssets(), "fonts/sui-generis-rg.ttf");



        mChart = new LineChart(rootView.getContext());
        mChart.setLayoutParams(new FrameLayout.LayoutParams(DrawerLayout.LayoutParams.FILL_PARENT,DrawerLayout.LayoutParams.FILL_PARENT));

        mChart.setTouchEnabled(true);
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setScaleXEnabled(true);
        mChart.setScaleYEnabled(true);
        mChart.setPinchZoom(true);
        mChart.setDescription("");
        mChart.setDrawBorders(false);

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
        setData(mTagData.getTemps());


        mChart.animateX(2500, Easing.EasingOption.EaseInOutQuart);
//        mChart.invalidate();

        Legend l = mChart.getLegend();
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);


        RelativeLayout chartLayout = (RelativeLayout) rootView.findViewById(R.id.mBigChartLayout);
        chartLayout.addView(mChart);

        return rootView;
    }


    // ***************************************************** SET DATA CHART
    private void setData(short[] temps) {

        long nSecs = temps.length * mTagData.getMeasureLength();

        Log.d("Debug Secs", Integer.toString((int)nSecs));

        ArrayList<String> xVals = new ArrayList<String>();
        SimpleDateFormat fmt = new SimpleDateFormat("dd HH:mm");
        xVals.add(fmt.format(mTagData.getFstDownMeasuredate()));
        for (int i = 1; i < temps.length; i++) {
            if(nSecs > 3600 && (i*(int)mTagData.getMeasureLength()) % 3600 == 0){
                long days = TimeUnit.SECONDS.toDays(i*mTagData.getMeasureLength());
                long hours = TimeUnit.SECONDS.toHours(i*mTagData.getMeasureLength()) - (days*24) - 1;
                long minutes = TimeUnit.SECONDS.toMinutes(i*mTagData.getMeasureLength()) - (days*24) - (hours*60);
                long seconds = i*mTagData.getMeasureLength() - (days*24) - (hours*60) - (minutes*60);

                GregorianCalendar cal = new GregorianCalendar(2016, 8, (int)days, (int)hours, (int)minutes, (int)seconds);
                Date current = cal.getTime();
                long sum = current.getTime() + mTagData.getFstDownMeasuredate().getTime();
                Date sumDate = new Date(sum);
                xVals.add(fmt.format(sumDate));
                Log.d("Debug Date", sumDate.toString());
            }
            else {
                long days = TimeUnit.SECONDS.toDays(i*mTagData.getMeasureLength());
                long hours = TimeUnit.SECONDS.toHours(i*mTagData.getMeasureLength()) - (days*24) - 1;
                long minutes = TimeUnit.SECONDS.toMinutes(i*mTagData.getMeasureLength()) - (days*24) - (hours*60);
                long seconds = i*mTagData.getMeasureLength() - (days*24) - (hours*60) - (minutes*60);

                GregorianCalendar cal = new GregorianCalendar(0, 0,(int)days+1, (int)hours+1, (int)minutes, (int)seconds);
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

    // ************************************* SETTERS ************************************* //
    public void setTagData(TagData data)
    {
        this.mTagData = data;
    }
}
