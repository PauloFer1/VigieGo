package com.tarambola.view;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by Juliusz on 2015-02-17.
 */
public class TwoColumnTable {

    private Context context;
    protected int oddRowBackgroundColor;
    protected int evenRowBackgroundColor;
    protected float firstColumnWeigth=1;
    protected float secondColumnWeigth=1;
    protected int padding;
    protected String font="MYRIADPRO-REGULAR.OTF";
    protected TableLayout table;
    protected ArrayList<Row> rows = new ArrayList<>();


    public TwoColumnTable(Context context) {
        this.context=context;
        oddRowBackgroundColor = context.getResources().getColor(android.R.color.darker_gray);
        evenRowBackgroundColor = context.getResources().getColor(android.R.color.background_light);
        //padding=(int)context.getResources().getDimension(R.dimen.inputTextPadding);
        padding=20;
    }

    public TableLayout build(){
        table =  new TableLayout(context);
        table.setLayoutParams(new TableLayout.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        table.setPadding(padding, padding, padding, padding);

        TableRow tr;
        TextView firstColumn;
        TextView secondColumn;
        TableRow.LayoutParams firstColumnLayoutParams=(new TableRow.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                firstColumnWeigth));
        TableRow.LayoutParams secondColumnLayoutParams=(new TableRow.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                secondColumnWeigth));
        TableRow.LayoutParams rowLayoutParams=(new TableRow.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        for (int i=0; i<rows.size(); i++){
            tr = new TableRow(context);
            tr.setLayoutParams(rowLayoutParams);
            tr.setBackgroundColor( i%2==0 ? oddRowBackgroundColor : evenRowBackgroundColor);
            tr.setPadding(padding, padding, padding, padding);

            firstColumn=new TextView(context);
            firstColumn.setText(rows.get(i).firstColumn);
            firstColumn.setLayoutParams(firstColumnLayoutParams);
            tr.addView(firstColumn);

            secondColumn=new TextView(context);
            secondColumn.setText(rows.get(i).secondColumn);
            secondColumn.setLayoutParams(secondColumnLayoutParams);
            secondColumn.setPadding(3, 0, 0, 0);
            tr.addView(secondColumn);

            table.addView(tr);
        }
        return table;
    }

    public TwoColumnTable addRow(Row row){
        rows.add(row);
        return this;
    }


    public static class Row{
        protected String firstColumn;
        protected String secondColumn;

        public Row(String firstColumn, String secondColumn) {
            this.firstColumn = firstColumn;
            this.secondColumn = secondColumn;
        }
    }

    public void setOddRowBackgroundColor(int oddRowBackgroundColor) {
        this.oddRowBackgroundColor = oddRowBackgroundColor;
    }

    public void setEvenRowBackgroundColor(int evenRowBackgroundColor) {
        this.evenRowBackgroundColor = evenRowBackgroundColor;
    }

    public void setFirstColumnWeigth(float firstColumnWeigth) {
        this.firstColumnWeigth = firstColumnWeigth;
    }

    public void setSecondColumnWeigth(float secondColumnWeigth) {
        this.secondColumnWeigth = secondColumnWeigth;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    public void setFont(String font) {
        this.font = font;
    }
}
