package com.cruzhacks20.suppcheck;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyCustomAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<StringModel> ingredientList;
    //ViewModelsHelper mViewModelsHelper;

    //Constructor
    public MyCustomAdapter(Context context, ArrayList<StringModel> modelList){
        this.context = context;
        this.ingredientList = modelList;
        //this.mViewModelsHelper = new ViewModelsHelper(context);
    }

    //Definition of adapter methods
    @Override public int getCount(){return ingredientList.size();}

    @Override
    public StringModel getItem(int position){return ingredientList.get(position);}

    @Override
    public long getItemId(int position){return position;}

    public void removeItem(int position){
        ingredientList.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){

        //Inflate the layout for each list row
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.format_row, parent, false);
        }

        //Self reference for deletion
        final MyCustomAdapter selfAdapter = this;

        //Current data values to be displayed
        final StringModel currentValues = getItem(position);

        //Get each column to be displyaed
        //ImageView imageViewPic = (ImageView) convertView.findViewById(R.id.rowImageView);
        TextView textViewTitle = (TextView) convertView.findViewById(R.id.rowNameView);

        //Set each data element to be displayed
        //imageViewPic.setImageBitmap(currentValues.getImage());
        textViewTitle.setText(Long.toString(currentValues.getId()));

        //Return the view
        return convertView;
    }
}

