package com.nikhil.doit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nikhil.doit.Adapter.DoItAdapter;
import com.nikhil.doit.Model.DoItModel;
import com.nikhil.doit.Utils.DatabaseHandler;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements DialogCloseListener {

    private RecyclerView taskRV;
    private ImageView emptyStateImage;
    private DoItAdapter tasksAdapter;
    private FloatingActionButton fab;
    private List<DoItModel> taskList;
    private DatabaseHandler db;
    private TextView completedCount, pendingCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHandler(this);
        db.openDatabase();

        completedCount = findViewById(R.id.completedCount);
        pendingCount = findViewById(R.id.pendingCount);
        fab = findViewById(R.id.fab);
        taskRV = findViewById(R.id.tasksRV);
        emptyStateImage = findViewById(R.id.emptyStateImage);
        taskRV.setLayoutManager(new LinearLayoutManager(this));
        tasksAdapter = new DoItAdapter(db,MainActivity.this);

        tasksAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                if (tasksAdapter.getItemCount() == 0) {
                    showEmptyState();
                } else if(tasksAdapter.getItemCount() != 0){
                    hideEmptyState();
                }

                int arr[] = tasksAdapter.taskStatus();

                completedCount.setText(String.valueOf(arr[0]));
                pendingCount.setText(String.valueOf(arr[1]));
            }
        });

        taskRV.setAdapter(tasksAdapter);

        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        tasksAdapter.setTask(taskList);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new RecyclerItemTouchHelper(tasksAdapter));
        itemTouchHelper.attachToRecyclerView(taskRV);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(getSupportFragmentManager(), AddNewTask.TAG);
            }
        });
    }

    @Override
    public void handleDialogClose(DialogInterface dialog) {
        taskList = db.getAllTasks();
        Collections.reverse(taskList);
        tasksAdapter.setTask(taskList);
        tasksAdapter.notifyDataSetChanged();
    }

    private void showEmptyState() {
        taskRV.setVisibility(View.GONE);
        emptyStateImage.setVisibility(View.VISIBLE);
    }

    private void hideEmptyState() {
        taskRV.setVisibility(View.VISIBLE);
        emptyStateImage.setVisibility(View.GONE);
    }
}