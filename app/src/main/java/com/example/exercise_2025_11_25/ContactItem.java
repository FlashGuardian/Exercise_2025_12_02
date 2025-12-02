package com.example.exercise_2025_11_25;

public class ContactItem {

    private String name;
    private String phone;
    private boolean isSelected;

    public ContactItem(String name, String phone) {
        this.name = name;
        this.phone = phone;
        this.isSelected = false;
    }

    public ContactItem() {
        this("", "");
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public boolean isSelected() {
        return  isSelected;
    }
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
}
