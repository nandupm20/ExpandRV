package com.nandroid.expandablerecyclerview.ui;


import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import com.nandroid.expandablerecyclerview.listener.OnToggleListener;
import com.nandroid.expandablerecyclerview.model.ExpandableGroup;

import java.util.ArrayList;

public abstract class ExpandRecAdapter<A,B,T extends RecyclerView.ViewHolder,U extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {

    private ArrayList<ExpandableGroup<A,B>> groups;

    private OnToggleListener expandCollapseListener;

    private SparseArray<ExpandChildRecAdapter<U>> childRecAdapters;


    public ExpandRecAdapter(ArrayList<ExpandableGroup<A,B>> groups){
        this.groups = groups;
        this.childRecAdapters = new SparseArray<>();
    }



    @NonNull
    @Override
    public T onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        final int groupPos = viewType;

        final T parentHolder = this.onCreateParentViewHolder(parent);

        boolean needsOpen = this.groups.get(groupPos).needsOpen;

        if (needsOpen && parentHolder.itemView instanceof ViewGroup){

            final ExpandChildRecAdapter<U> childRecAdapter = new ExpandChildRecAdapter<U>() {
                @NonNull
                @Override
                public U onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    return ExpandRecAdapter.this.onCreateChildViewHolder(parent);
                }

                @Override
                public void onBindViewHolder(@NonNull U childHolder, int position) {
                    ExpandRecAdapter.this.onBindChildViewHolder(
                            childHolder,
                            parentHolder,
                            this,
                            groupPos,
                            position);
                }

                @Override
                public int getItemCount() {
                    return ExpandRecAdapter.this.groups.get(groupPos).childItems.size();
                }
            };

            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                    RecyclerView.LayoutParams.MATCH_PARENT,
                    RecyclerView.LayoutParams.WRAP_CONTENT
            );
            RecyclerView childRecyclerView = new RecyclerView(parent.getContext());
            childRecyclerView.setLayoutParams(params);

            ((ViewGroup) parentHolder.itemView).addView(childRecyclerView);
            childRecyclerView.setLayoutManager(new LinearLayoutManager(parent.getContext()));
            childRecyclerView.setAdapter(childRecAdapter);

            childRecAdapters.append(groupPos,childRecAdapter);

        }

        return parentHolder;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }



    @Override
    public void onBindViewHolder(@NonNull T holder, int i) {
        final int pos = i;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpandRecAdapter.this.toggleSection(pos);
            }
        });
        this.onBindParentViewHolder(holder, i);
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }




    public abstract T onCreateParentViewHolder(@NonNull ViewGroup parent);

    public abstract U onCreateChildViewHolder(@NonNull ViewGroup parent);

    public abstract void onBindParentViewHolder(@NonNull T holder, final int groupPosition);

    public abstract void onBindChildViewHolder(
            @NonNull U childHolder,
            @NonNull T parentHolder,
            ExpandChildRecAdapter<U> childRecAdapter,
            final int groupPosition,
            final int childPosition
    );




    /// methods
    public void toggleSection(int position){
        this.groups.get(position).needsOpen = !this.groups.get(position).needsOpen;
        this.notifyItemChanged(position);
        if (this.expandCollapseListener != null){
            this.expandCollapseListener.onToggle(this.groups.get(position).needsOpen,position);
        }
    }

    public void setOnToggledListener(OnToggleListener listener){
        this.expandCollapseListener = listener;
    }


    public void notifyGroupChanged(int groupPosition){
        this.notifyItemChanged(groupPosition);
        if (childRecAdapters.get(groupPosition) != null){
            childRecAdapters.get(groupPosition).notifyDataSetChanged();
        }
    }

    public void notifyChildSetChanged(){
        for (int i = 0;i<childRecAdapters.size();i++){
            if (childRecAdapters.valueAt(i) != null){
                childRecAdapters.valueAt(i).notifyDataSetChanged();
            }
        }
    }

    public void notifyGroupSetChanged(){
        this.notifyDataSetChanged();
    }

    public void notifyAllDataChanged(){
        this.notifyDataSetChanged();
        notifyChildSetChanged();
    }

    public void notifyChildItemChanged(int groupPosition,int childPosition){
        if (childRecAdapters.get(groupPosition) != null){
            childRecAdapters.get(groupPosition).notifyItemChanged(childPosition);
        }
    }

    public void notifyDataChanged(ArrayList<ExpandableGroup<A,B>> groups){
        this.groups = groups;
        this.notifyDataSetChanged();
        for (int i=0;i<childRecAdapters.size();i++){
            childRecAdapters.valueAt(i).notifyDataSetChanged();
        }
    }

}
