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
 * Created by Kevin on 9/30/2015.
 */
public class RipleAdapter extends RecyclerView.Adapter<RipleAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    List<DropItem> data = Collections.emptyList();

    public static interface RipleAdapterDelegate {
        public void itemSelected(Object item);
    }

    WeakReference<RipleAdapterDelegate> delegate;

    public RipleAdapterDelegate getDelegate() {
        if (delegate == null) {
            return null;
        }
        return delegate.get();
    }

    public void setDelegate(RipleAdapterDelegate delegate) {
        this.delegate = new WeakReference<>(delegate);
    }


    public Object getItem(int position){
        return data.get(position);
    }


    public RipleAdapter(Context context, List<DropItem> data){
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

        public ImageView profilePic;
        public TextView userName;
        public TextView dropTime;
        public TextView dropTitle;
        public TextView dropDescription;
        public TextView dropRipleCount;
        public TextView dropCommentCount;


        public MyViewHolder(View itemView) {

            super(itemView);
            //profilePic = (ImageView) itemView.findViewById(R.id.profilePic);
//            userName = (TextView) itemView.findViewById(R.id.name);
            //dropTime = (TextView) itemView.findViewById(R.id.dropTime);
            //dropTitle = (TextView) itemView.findViewById(R.id.dropTitle);
            dropDescription = (TextView) itemView.findViewById(R.id.description);
            dropRipleCount = (TextView) itemView.findViewById(R.id.riple_count);
            //dropCommentCount = (TextView) itemView.findViewById(R.id.dropCommentCount);

            itemView.setOnClickListener(this);
        }

        public void update(int position){

            DropItem current = data.get(position);
//            trickleProfilePic.setImage(current.trickleProfilePic);
            /*author.setText(current.author);
            trickletime.setText(current.trickletime);
            title.setText(current.title);
            description.setText(current.description);
            ripleCount.setText(String.valueOf(current.ripleCount));
            commentCount.setText(current.commentCount);*/
        }

        @Override
        public void onClick(View v) {

            getDelegate().itemSelected(data.get(getPosition()));
        }
    }
}


