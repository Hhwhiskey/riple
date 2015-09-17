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
 * Created by Kevin on 9/13/2015.
 */
public class IdeaAdapter extends RecyclerView.Adapter <IdeaAdapter.MyViewHolder> {
    private LayoutInflater inflater;
    List<Idea> data = Collections.emptyList();

    public static interface IdeaAdapterDelegate {
        public void itemSelected(Object item);
    }

    WeakReference<IdeaAdapterDelegate> delegate;

    public IdeaAdapterDelegate getDelegate() {
        if (delegate == null) {
            return null;
        }
        return delegate.get();
    }

    public void setDelegate(IdeaAdapterDelegate delegate) {
        this.delegate = new WeakReference<>(delegate);
    }


    public Object getItem(int position){
        return data.get(position);
    }



    public IdeaAdapter(Context context, List<Idea> data){
        inflater = LayoutInflater.from(context);
        this.data = data;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_row_idea, parent, false);
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

        TextView idea;


        public MyViewHolder(View itemView) {
            super(itemView);
            idea = (TextView) itemView.findViewById(R.id.ideaView);
            itemView.setOnClickListener(this);
        }


        public void update(int position) {
            Idea current = data.get(position);
            idea.setText(current.idea);
        }


        @Override
        public void onClick(View view) {
            //getDelegate().itemSelected(data.get(getPosition()));
        }
    }
}
