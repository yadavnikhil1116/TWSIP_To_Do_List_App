package com.nikhil.doit.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.nikhil.doit.AddNewTask;
import com.nikhil.doit.MainActivity;
import com.nikhil.doit.Model.DoItModel;
import com.nikhil.doit.R;
import com.nikhil.doit.Utils.DatabaseHandler;

import java.util.List;

public class DoItAdapter extends RecyclerView.Adapter<DoItAdapter.ViewHolder> {

    private List<DoItModel> DoItList;
    private MainActivity activity;
    private DatabaseHandler db;

    public DoItAdapter(DatabaseHandler db, MainActivity activity) {
        this.db = db;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        db.openDatabase();
        final DoItModel item = DoItList.get(position);
        holder.task.setText(item.getTask());
        holder.task.setChecked(toBoolean(item.getStatus()));
        holder.task.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    db.updateStatus(item.getId(), 1);
                } else{
                    db.updateStatus(item.getId(), 0);
                }
            }
        });
    }

    private boolean toBoolean(int n){
        return n!=0;
    }

    @Override
    public int getItemCount() {
        return DoItList.size();
    }

    public Context getContext(){
        return activity;
    }

    public void setTask(List<DoItModel> DoItList){
        this.DoItList = DoItList;
        notifyDataSetChanged();
    }

    public void deleteItem(int position){
        DoItModel item = DoItList.get(position);
        db.deleteTask(item.getId());
        DoItList.remove(position);
        notifyDataSetChanged();
    }

    public void editItem(int position){
        DoItModel item = DoItList.get(position);
        Bundle bundle = new Bundle();
        bundle.putInt("id", item.getId());
        bundle.putString("task", item.getTask());
        AddNewTask fragment = new AddNewTask();
        fragment.setArguments(bundle);
        fragment.show(activity.getSupportFragmentManager(), AddNewTask.TAG);
    }

    public int[] taskStatus(){
        int comcnt = 0, pencnt = 0;
        for(int i = 0; i < DoItList.size(); i++) {
            final DoItModel item = DoItList.get(i);
            if(item.getStatus() == 1){
                comcnt++;
            } else{
                pencnt++;
            }
        }
        return new int[] {comcnt, pencnt};
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        CheckBox task;
        ViewHolder(View view){
            super(view);
            task = view.findViewById(R.id.todochechBox);
        }
    }
}
