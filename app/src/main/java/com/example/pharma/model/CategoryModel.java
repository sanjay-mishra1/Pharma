package com.example.pharma.model;

import java.io.Serializable;

public class CategoryModel implements Serializable {
    private String category_id;
    private String category_name;
    private String icon;
    private String category_description;
    private String category_items;
    public CategoryModel(){
    }

    public CategoryModel(String category_name, String category_icon, String category_description) {
        this.category_name = category_name;
        this.icon = category_icon;
        this.category_description = category_description;
        this.category_id = String.valueOf(System.currentTimeMillis());
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getCategory_id() {
        return category_id;
    }

    public String getIcon() {
        return icon;
    }


    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCategory_description() {
        return category_description;
    }

    public void setCategory_description(String category_description) {
        this.category_description = category_description;
    }

    public String getCategory_items() {
        return category_items;
    }

    public void setCategory_items(String category_items) {
        this.category_items = category_items;
    }
}
