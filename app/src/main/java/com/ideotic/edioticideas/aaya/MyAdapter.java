package com.ideotic.edioticideas.aaya;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Shubham on 16-05-2016.
 */


public class MyAdapter extends BaseAdapter {

    public static List<SingleRow> list;
    Context context;

    public static String result = "";


    public MyAdapter(Context contxt) {
        this.context = contxt;
        DataBaseNotes db = new DataBaseNotes(context);
        try {

            list = db.getROWs();
        }catch (IndexOutOfBoundsException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public int getCount() {
        try {
            return list.size();
        }
        catch(Exception e){
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.row, null);
        TextView tv_title = (TextView) view.findViewById(R.id.n_title);
        TextView tv_body = (TextView) view.findViewById(R.id.n_body);
        ImageView iv_p = (ImageView) view.findViewById(R.id.n_pri);

        SingleRow sr = list.get(position);


        tv_title.setText(sr.title);
        tv_body.setText(sr.body);

        switch (sr.priority) {
            case "1":
                iv_p.setImageResource(R.drawable.h);
                break;
            case "2":
                iv_p.setImageResource(R.drawable.m);
                break;
            case "3":
                iv_p.setImageResource(R.drawable.l);
                break;
            default:
                iv_p.setImageResource(R.drawable.n);
                break;
        }

        return view;
    }

}