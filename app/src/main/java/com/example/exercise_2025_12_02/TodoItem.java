package com.example.exercise_2025_12_02;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class TodoItem {
    static final boolean FINISHED = true;
    static final boolean IN_PROGRESS = false;
    private int itemID;
    private String title;
    private String description;
    private LocalDateTime deadline;
    private ArrayList<String> relatedContacts;
    private boolean status;
    private boolean isSelected;
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

    public TodoItem(int itemID, String Title, String Description, LocalDateTime deadline) {
        this(itemID, Title, Description, deadline, IN_PROGRESS, new ArrayList<>());
    }
    public TodoItem(int itemID, String Title, String Description , LocalDateTime deadline,Boolean Status, ArrayList<String> relatedContacts) {
        this.itemID = itemID;
        title = Title;
        description = Description;
        this.deadline = deadline;
        status = Status;
        this.relatedContacts = relatedContacts;
    }

    public  String getTitle() {
        return title;
    }

    public void setTitle(String Title) {
        title = Title;
    }

    public  String getDescription() {
        return description;
    }

    public void setDescription(String Description) {
        description = Description;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean Status) {
        status = Status;
    }

    public LocalDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.deadline = deadline;
    }

    public int getItemID(){
        return itemID;
    }

    public void setItemID(int itemID){
        this.itemID = itemID;
    }
    public boolean isSelected() {
        return  isSelected;
    }
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
    public ArrayList<String> getRelatedContacts() {
        return  relatedContacts;
    }
    public void setRelatedContacts(ArrayList<String> relatedContacts) {
        this.relatedContacts = relatedContacts;
    }
    public DateTimeFormatter getDateTimeFormatter() {
        return dateTimeFormatter;
    }

    public void setDateTimeFormatter(DateTimeFormatter dateTimeFormatter) {
            this.dateTimeFormatter = dateTimeFormatter;
    }
}
