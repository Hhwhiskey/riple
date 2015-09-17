package com.khfire22gmail.riple.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.khfire22gmail.riple.R;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

/**
 * Created by Kevin on 9/16/2015.
 */
public class DropAdapter extends RecyclerView.Adapter <DropAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    List<Drop> data = Collections.emptyList();

    public static interface DropAdapterDelegate {
        public void itemSelected(Object item);
    }

    WeakReference<DropAdapterDelegate> delegate;

    public DropAdapterDelegate getDelegate() {
        if (delegate == null) {
            return null;
        }
        return delegate.get();
    }

    public void setDelegate(DropAdapterDelegate delegate) {
        this.delegate = new WeakReference<>(delegate);
    }


    public Object getItem(int position){
        return data.get(position);
    }

    public DropAdapter(Context context, List<Drop> data){
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

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView dropTitle;
        public TextView dropDescription;
        public TextView dropRipleCount;
        public TextView dropCommentCount;


        public MyViewHolder(View itemView) {

            super(itemView);
//          id = (TextView) itemView.findViewById(R.id.id);
            dropTitle = (TextView) itemView.findViewById(R.id.dropTitle);
            dropDescription = (TextView) itemView.findViewById(R.id.dropDescription);
            dropRipleCount = (TextView) itemView.findViewById(R.id.dropRipleCount);
            dropCommentCount = (TextView) itemView.findViewById(R.id.dropCommentCount);

            itemView.setOnClickListener(this);
        }

        public void update(int position){

            Drop current = data.get(position);
            //id.setText(current.id);
            dropTitle.setText(current.dropTitle);
            dropDescription.setText(current.dropDescription);
            dropRipleCount.setText(current.dropRipleCount);
            dropCommentCount.setText(current.dropCommentCount);
        }

        @Override
        public void onClick(View v) {

            getDelegate().itemSelected(data.get(getPosition()));
        }
    }
}
