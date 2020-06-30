package com.example.pharma.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class ImageBasedModel implements Serializable {
    private  String icon;
    private String name;
    private Object note;
    private String ID;
    private String url;
    private String order;
    private String uid;
    private String status_name;
    private long status;
    private long time;
    private long category_id;
    private long completion_time;
    private long total_view;
    private long original_price;
    private ArrayList<String> images;
    private ArrayList<HashMap<String,Object>> medicine_list;
    public static int Pending=0;
    public static int Approved=1;
    public static int Cancelled=2;
    public static int Completed=3;
    private String description;
    private String requirement;
    private Object lab;
    private  long price;
    private boolean isFav;
    public ImageBasedModel(){

    }
    public ImageBasedModel(String name, long time,String icon) {
        this.name=name;
        this.time=time;
        this.icon=icon;
    }

    public boolean isFav() {
        return isFav;
    }

    public Object getLab() {
        return lab;
    }

    public void setLab(Object lab) {
        this.lab = lab;
    }

    public void setFav(boolean fav) {
        isFav = fav;
    }

    public ImageBasedModel(String name, long category_id, String description, String requirement, long price) {
        this.name = name;
        this.description = description;
        this.requirement = requirement;
        this.price = price;
        this.category_id=category_id;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public long getOriginal_price() {
        return original_price;
    }

    public void setOriginal_price(long original_price) {
        this.original_price = original_price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getStatus_name() {
        return status_name;
    }
    public void setStatus_name(String status_name) {
        this.status_name = status_name;
    }
    public String getName() {
        return name;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getID() {
        return ID;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public ArrayList<HashMap<String, Object>> getMedicine_list() {
        if (medicine_list==null)
            return new ArrayList<>();
        else return medicine_list;
    }

    public void setMedicine_list(ArrayList<HashMap<String, Object>> medicine_list) {
        this.medicine_list = medicine_list;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getNote() {
        return note;
    }

    public void setNote(Object note) {
        this.note = note;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public ArrayList<String> getImages() {
        return images;
    }

    public void setImages(ArrayList<String> images) {
        this.images = images;
    }

    public long getCompletion_time() {
        return completion_time;
    }

    public void setCompletion_time(long completion_time) {
        this.completion_time = completion_time;
    }


}



//
//public class ImageBasedModel implements Serializable {
//    private String name;
//    private String note;
//    private String ID;
//    private String url;
//    private String order;
//    private long status;
//    private ArrayList<String> images;
//    public static int Approved=1;
//    public static int Pending=0;
//    public static int Cancelled=2;
//    public String getName() {
//        return name;
//    }
//
//    public String getOrder() {
//        return order;
//    }
//
//    public void setOrder(String order) {
//        this.order = order;
//    }
//
//    public String getID() {
//        return ID;
//    }
//
//    public void setID(String ID) {
//        this.ID = ID;
//    }
//
//    public String getUrl() {
//        return url;
//    }
//
//    public void setUrl(String url) {
//        this.url = url;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }
//
//    public String getNote() {
//        return note;
//    }
//
//    public void setNote(String note) {
//        this.note = note;
//    }
//
//    public long getStatus() {
//        return status;
//    }
//
//    public void setStatus(long status) {
//        this.status = status;
//    }
//
//    public ArrayList<String> getImages() {
//        return images;
//    }
//
//    public void setImages(ArrayList<String> images) {
//        this.images = images;
//    }
//}
