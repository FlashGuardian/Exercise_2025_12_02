package com.example.exercise_2025_12_02;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ToDoItemAdapter extends ArrayAdapter {
    int resource;
    private List<TodoItem> todoItems;
    TextView positionTextView;
    TextView titleTextView;
    CheckBox statusCheckBox;
    TextView statusTextView;
    RadioButton selectedRadioButton;
    public ToDoItemAdapter (Context context, int resource, List<TodoItem> todoItems){
        super(context, resource, todoItems);
        this.resource = resource;
        this.todoItems = todoItems;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        Context context = this.getContext();
        if (v == null){
            LayoutInflater vi;
            vi = LayoutInflater.from(context);
            v = vi.inflate(this.resource,null);
        }
        TodoItem todoItem = (TodoItem) getItem(position);

        if (todoItem!=null){
            TextView positionTextView = (TextView) v.findViewById(R.id.itemPosition);
            TextView titleTextView = (TextView) v.findViewById(R.id.itemTitle);
            CheckBox statusCheckBox = (CheckBox) v.findViewById(R.id.itemStatus);
            TextView statusTextView = (TextView) v.findViewById(R.id.itemStatusTextView);
            ToggleableRadioButton selectedRadioButton = v.findViewById(R.id.itemSelected);


            if(positionTextView!=null) {
                positionTextView.setText("#" + (position + 1));
            }
            if(titleTextView!=null) {
                titleTextView.setText(todoItem.getTitle());
            }
            if(statusCheckBox!=null){
                final boolean finished = todoItem.getStatus();
                statusCheckBox.setChecked(finished);
                if(finished) {
                    statusTextView.setText("Finished");
                }
                else {
                    statusTextView.setText("In Progress");
                }
                statusCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if(isChecked) {
                        statusTextView.setText("Finished");
                        todoItem.setStatus(TodoItem.FINISHED);
                    }
                    else {
                        statusTextView.setText("In Progress");
                        todoItem.setStatus(TodoItem.IN_PROGRESS);
                    }
                });
            }
            if(selectedRadioButton!=null){
                selectedRadioButton.setChecked(todoItem.isSelected());
                selectedRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    todoItem.setSelected(isChecked);
                });
            }
        }

        return v;
    }
}
