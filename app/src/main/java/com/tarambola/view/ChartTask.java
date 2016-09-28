package com.tarambola.view;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

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
import com.tarambola.model.TagData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

/**
 * Created by paulofernandes on 27/09/16.
 */
public class ChartTask extends AsyncTask<LineChart, Void, ArrayList<String>> {

    private TagData mTagData;
    private Typeface mFont;
    private ArrayList<ILineDataSet> mDataSets;

    public ArrayList<Entry> mYVals;

    public ChartTask(TagData tag)
    {
        mTagData = tag;
    }

    @Override
    protected ArrayList<String> doInBackground(LineChart... chart)
    {
        LineChart mChart = chart[0];

        // add data
        short temps[] = mTagData.getTemps();
        long nSecs = temps.length * mTagData.getMeasureLength();

        mYVals = new ArrayList<Entry>();

        for (int i = 0; i < temps.length; i++) {

            float val = (float) temps[i]/10;
            mYVals.add(new Entry(val, i));
        }

        ArrayList<String> xVals = new ArrayList<String>();
        SimpleDateFormat fmt = new SimpleDateFormat("dd HH:mm");
        xVals.add(fmt.format(mTagData.getFstDownMeasuredate()));
        for (int i = 1; i < temps.length; i++) {
            if(nSecs > 3600 && (i*(int)mTagData.getMeasureLength()) % 3600 == 0){
                long days = TimeUnit.SECONDS.toDays(i*mTagData.getMeasureLength());
                long hours = TimeUnit.SECONDS.toHours(i*mTagData.getMeasureLength()) - (days*24);
                long minutes = TimeUnit.SECONDS.toMinutes(i*mTagData.getMeasureLength()) - (days*24) - (hours*60);
                long seconds = i*mTagData.getMeasureLength() - (days*24) - (hours*60) - (minutes*60);

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
                long seconds = i*mTagData.getMeasureLength() - (days*24) - (hours*60) - (minutes*60);

                GregorianCalendar cal = new GregorianCalendar(0, 0, (int)days, (int)hours, (int)minutes, (int)seconds);
                Date current = cal.getTime();
                long sum = current.getTime() + mTagData.getFstDownMeasuredate().getTime();
                Date sumDate = new Date(sum);
                xVals.add(fmt.format(sumDate));
            }
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < temps.length; i++) {

            float val = (float) temps[i] / 10;
            yVals.add(new Entry(val, i));
        }

        return xVals;
    }
}
