
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

public class TrickleAdapter extends RecyclerView.Adapter<TrickleAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    List<TrickleItem> data = Collections.emptyList();

    public static interface TrickleAdapterDelegate {
        public void itemSelected(Object item);
    }

    WeakReference<TrickleAdapterDelegate> delegate;

    public TrickleAdapterDelegate getDelegate() {
        if (delegate == null) {
            return null;
        }
        return delegate.get();
    }

    public void setDelegate(TrickleAdapterDelegate delegate) {
        this.delegate = new WeakReference<>(delegate);
    }


    public Object getItem(int position){
        return data.get(position);
    }


    public TrickleAdapter(Context context, List<TrickleItem> data){
        inflater = LayoutInflater.from(context);
        this.data = data;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.card_trickle, parent, false);
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

//        public ImageView trickleProfilePic;
        public TextView trickleUserName;
        public TextView trickleTime;
        public TextView trickleTitle;
        public TextView trickleDescription;
        public TextView trickleRipleCount;
        public TextView trickleCommentCount;


        public MyViewHolder(View itemView) {

            super(itemView);
//            trickleProfilePic = (ImageView) itemView.findViewById(R.id.trickleProfilePic);
            trickleUserName = (TextView) itemView.findViewById(R.id.trickleUserName);
            trickleTime = (TextView) itemView.findViewById(R.id.trickleTime);
            trickleTitle = (TextView) itemView.findViewById(R.id.trickleTitle);
            trickleDescription = (TextView) itemView.findViewById(R.id.trickleDescription);
            trickleRipleCount = (TextView) itemView.findViewById(R.id.trickleRipleCount);
            trickleCommentCount = (TextView) itemView.findViewById(R.id.trickleCommentCount);

            itemView.setOnClickListener(this);
        }

        public void update(int position){

            TrickleItem current = data.get(position);
//            trickleProfilePic.setImage(current.trickleProfilePic);
            trickleUserName.setText(current.trickleUserName);
            trickleTime.setText(current.trickleTime);
            trickleTitle.setText(current.trickleTitle);
            trickleDescription.setText(current.trickleDescription);
            trickleRipleCount.setText(String.valueOf(current.trickleRipleCount));
            trickleCommentCount.setText(String.valueOf(current.trickleCommentCount));

        }

        @Override
        public void onClick(View v) {

            getDelegate().itemSelected(data.get(getPosition()));
        }
    }
}


