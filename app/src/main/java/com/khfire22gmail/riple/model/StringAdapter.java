package com.khfire22gmail.riple.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.khfire22gmail.riple.R;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

/**
 * Created by Kevin on 9/15/2015.
 */
public class StringAdapter extends RecyclerView.Adapter <StringAdapter.MyViewHolder> {
    private LayoutInflater inflater;
    List<String> data = Collections.emptyList();

    public static interface StringAdapterDelegate {
        public void itemSelected(Object item);
    }

    WeakReference<StringAdapterDelegate> delegate;

    public StringAdapterDelegate getDelegate() {
        if (delegate == null) {
            return null;
        }
        return delegate.get();
    }

    public void setDelegate(StringAdapterDelegate delegate) {
        this.delegate = new WeakReference<>(delegate);
    }


    public Object getItem(int position){
        return data.get(position);
    }



    public StringAdapter(Context context, List<String> data){
        inflater = LayoutInflater.from(context);
        this.data = data;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_row_drop, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        viewHolder.update(position);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {

        TextView name;
        TextView lastName;
        ImageView profilePic;

        public MyViewHolder(View itemView) {
            super(itemView);
            //number = (TextView) itemView.findViewById(R.id.itemListTextView);
            name = (TextView) itemView.findViewById(R.id.userName);
            //lastName = (TextView) itemView.findViewById(R.id.nameView2);
            //profilePic = (ImageView) itemView.findViewById(R.id.profilePic);
            itemView.setOnClickListener(this);
        }


        public void update(int position){
            String current = data.get(position);
            //number.setText(current.number);
            name.setText(current);
            //lastName.setText(current.lastName);
            //profilePic.setImage(current.profilePic);
        }

        @Override
        public void onClick(View v) {
            //getDelegate().itemSelected(data.get(getPosition()));
        }
    }
}
