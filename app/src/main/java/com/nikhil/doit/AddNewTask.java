package com.nikhil.doit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.nikhil.doit.Model.DoItModel;
import com.nikhil.doit.Utils.DatabaseHandler;

import java.util.Objects;

public class AddNewTask extends BottomSheetDialogFragment {

    public static final String TAG = "ActionBottomDialog";
    private EditText newTaskText;
    private Button newTaskSaveBtn;
    private DatabaseHandler db;
    public static AddNewTask newInstance(){
        return new AddNewTask();
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.DialogStyle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.new_task, container, false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return view;
    }

    @SuppressLint("UseRequireInsteadOfGet")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        newTaskText = Objects.requireNonNull(getView()).findViewById(R.id.newtasktext);
        newTaskSaveBtn = getView().findViewById(R.id.newTaskbtn);
        db = new DatabaseHandler(getActivity());
        db.openDatabase();
        boolean isUpdate = false;
        final Bundle bundle = getArguments();
        if(bundle != null){
            isUpdate = true;
            String task = bundle.getString("task");
            newTaskText.setText(task);
            if(task.length() > 0){
                newTaskSaveBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
            }
        }
        newTaskText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().equals("")){
                    newTaskSaveBtn.setClickable(false);
                    newTaskSaveBtn.setTextColor(Color.GRAY);
                } else{
                    newTaskSaveBtn.setClickable(true);
                    newTaskSaveBtn.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        boolean finalIsUpdate = isUpdate;
        newTaskSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = newTaskText.getText().toString();
                if(text.length() != 0){
                    if(finalIsUpdate){
                        db.updateTask(bundle.getInt("id"), text);
                    } else{
                        DoItModel task = new DoItModel();
                        task.setTask(text);
                        task.setStatus(0);
                        db.insertTask(task);
                    }
                } else{
                    Toast.makeText(getContext(), "Please enter the Task...", Toast.LENGTH_SHORT).show();
                }
                dismiss();
            }
        });
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        Activity activity = getActivity();
        if(activity instanceof DialogCloseListener){
            ((DialogCloseListener)activity).handleDialogClose(dialog);
        }
    }
}
