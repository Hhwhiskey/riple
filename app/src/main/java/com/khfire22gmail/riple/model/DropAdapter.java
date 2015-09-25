package com.khfire22gmail.riple.model;

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
 * Created by Kevin on 9/16/2015.
 */

public class DropAdapter extends RecyclerView.Adapter <DropAdapter.MyViewHolder> {

    private LayoutInflater inflater;
    List<DropItem> data = Collections.emptyList();


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

    /*public DropAdapter() {
        inflater = LayoutInflater.from(context);
        this.data = data;
    }*/

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = inflater.inflate(R.layout.card_drop, parent, false);
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
        public TextView firstName;
        public TextView lastName;
        public TextView dropTitle;
        public TextView dropDescription;
        public TextView dropRipleCount;
        public TextView dropCommentCount;


        public MyViewHolder(View itemView) {

            super(itemView);
            //userId = (TextView) itemView.findViewById(R.id.userId);
            profilePic = (ImageView) itemView.findViewById(R.id.profilePic);
            firstName = (TextView) itemView.findViewById(R.id.firstName);
            lastName = (TextView) itemView.findViewById(R.id.lastName);
            dropTitle = (TextView) itemView.findViewById(R.id.dropTitle);
            dropDescription = (TextView) itemView.findViewById(R.id.dropDescription);
            dropRipleCount = (TextView) itemView.findViewById(R.id.dropRipleCount);
            dropCommentCount = (TextView) itemView.findViewById(R.id.dropCommentCount);

            itemView.setOnClickListener(this);
        }

        public void update(int position){

            DropItem current = data.get(position);
            //userId.setText(current.userId);
            //profilePic.setImage(current.profilePic);
            firstName.setText(current.firstName);
            lastName.setText(current.lastName);
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


/*
public class DropAdapter extends RecyclerView.Adapter<DropAdapter.ViewHolder> {

    List<DropItem> mItems;

    public DropAdapter() {
        super();
        mItems = new ArrayList<DropItem>();
        DropItem nature = new DropItem();
        nature.setName("The Great Barrier Reef");
        nature.setDes("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt" +
                "ut labore et dolore magna aliqua. Ut enim ad minim veniam.");
        nature.setThumbnail(R.drawable.ic_user_default);
        mItems.add(nature);

        nature = new DropItem();
        nature.setName("Grand Canyon");
        nature.setDes("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt" +
                "ut labore et dolore magna aliqua.");
        nature.setThumbnail(R.drawable.ic_user_default);
        mItems.add(nature);

        nature = new DropItem();
        nature.setName("Baltoro Glacier");
        nature.setDes("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt" +
                "ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis.");
        nature.setThumbnail(R.drawable.ic_user_default);
        mItems.add(nature);

        nature = new DropItem();
        nature.setName("Iguazu Falls");
        nature.setDes("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt" +
                "ut labore et dolore magna aliqua. Ut enim ad minim veniam.");
        nature.setThumbnail(R.drawable.ic_user_default);
        mItems.add(nature);


        nature = new DropItem();
        nature.setName("Aurora Borealis");
        nature.setDes("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt" +
                "ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud.");
        nature.setThumbnail(R.drawable.ic_user_default);
        mItems.add(nature);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_drop, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        DropItem nature = mItems.get(i);
        viewHolder.tvNature.setText(nature.getName());
        viewHolder.tvDesNature.setText(nature.getDes());
        viewHolder.imgThumbnail.setImageResource(nature.getThumbnail());
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public profilePic;
        public TextView tvNature;
        public TextView tvDesNature;

        public ViewHolder(View itemView) {
            super(itemView);
            profilePic = (ImageView)itemView.findViewById(R.id.profilePic);
            tvNature = (TextView)itemView.findViewById(R.id.profilePic);
            tvDesNature = (TextView)itemView.findViewById(R.id.profilePic);
        }
    }
}*/
