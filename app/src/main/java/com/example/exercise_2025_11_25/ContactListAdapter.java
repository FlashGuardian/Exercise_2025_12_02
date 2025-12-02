package com.example.exercise_2025_11_25;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class ContactListAdapter extends ArrayAdapter {

    int resource;
    private List<ContactItem> contacts;
    TextView phoneTextView;
    TextView positionTextView;
    TextView nameTextView;
    ToggleableRadioButton selectedRadioButton;

    public ContactListAdapter (Context context, int resource, List<ContactItem> contacts) {
        super(context, resource, contacts);
        this.resource = resource;
        this.contacts = contacts;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null){
            LayoutInflater vi;
            vi = LayoutInflater.from(this.getContext());
            v = vi.inflate(this.resource,null);
        }
        ContactItem c = (ContactItem) getItem(position);

        if (c!=null){
            nameTextView = (TextView) v.findViewById(R.id.contactName);
            phoneTextView = (TextView) v.findViewById(R.id.contactPhone);
            positionTextView = (TextView) v.findViewById(R.id.contactPosition);
            selectedRadioButton = (ToggleableRadioButton) v.findViewById(R.id.itemSelected);


            if(nameTextView!=null)
                nameTextView.setText(c.getName());
            if(phoneTextView!=null)
                phoneTextView.setText(String.valueOf(c.getPhone()));
            if(positionTextView!= null) {
                int temp = position + 1;
                positionTextView.setText(String.valueOf(temp));
            }
            if(selectedRadioButton!=null){
                selectedRadioButton.setChecked(c.isSelected());
                selectedRadioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    c.setSelected(isChecked);
                });
            }
        }
        return v;
    }
}
