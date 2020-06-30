package com.example.pharma.model;

import com.example.pharma.Constants;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class OrderModel implements Serializable {
    private String order_id;
    private ArrayList<HashMap<String,Object>> order_medicines;//store
    private HashMap<String,HashMap<String,String>> order_status;//store
    private HashMap<String,String> order_payment_stats;//store
    private String order_payment_ref_id;//store
    private String order_payment_time;//store
    private String order_delivery_man;//store
    private String order_delivery_time;//store
    private long order_expected_delivery_time;
    private String current_order_status;
    private String order_delivery_address_text;
    private String order_delivery_address_coordinate;
    private String order_delivery_address_landmark;
    private String order_prescription;
    public long getOrder_expected_delivery_time() {
        return order_expected_delivery_time;
    }

    public void setOrder_expected_delivery_time(long order_expected_delivery_time) {
        this.order_expected_delivery_time = order_expected_delivery_time;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public ArrayList<HashMap<String, Object>> getOrder_medicines() {
        return order_medicines;
    }

    public void setOrder_medicines(ArrayList<HashMap<String, Object>> order_medicines) {
        this.order_medicines = order_medicines;
    }

    public HashMap<String, HashMap<String, String>> getOrder_status() {
        return order_status;
    }

    public void setOrder_status(HashMap<String, HashMap<String, String>> order_status) {
        this.order_status = order_status;
    }

    public HashMap<String, String> getOrder_payment_stats() {
        return order_payment_stats;
    }

    public void setOrder_payment_stats(HashMap<String, String> order_payment_stats) {
        this.order_payment_stats = order_payment_stats;
    }

    public String getOrder_payment_ref_id() {
        return order_payment_ref_id;
    }

    public void setOrder_payment_ref_id(String order_payment_ref_id) {
        this.order_payment_ref_id = order_payment_ref_id;
    }

    public String getOrder_payment_time() {
        return order_payment_time;
    }

    public void setOrder_payment_time(String order_payment_time) {
        this.order_payment_time = order_payment_time;
    }

    public String getOrder_delivery_man() {
        return order_delivery_man;
    }

    public void setOrder_delivery_man(String order_delivery_man) {
        this.order_delivery_man = order_delivery_man;
    }

    public String getOrder_delivery_time() {
        return order_delivery_time;
    }

    public void setOrder_delivery_time(String order_delivery_time) {
        this.order_delivery_time = order_delivery_time;
    }


    public String getCurrent_order_status() {
        return current_order_status;
    }

    public void setCurrent_order_status(String current_order_status) {
        this.current_order_status = current_order_status;
    }

    public String getOrder_delivery_address_text() {
        return order_delivery_address_text;
    }

    public void setOrder_delivery_address_text(String order_delivery_address_text) {
        this.order_delivery_address_text = order_delivery_address_text;
    }

    public String getOrder_delivery_address_coordinate() {
        return order_delivery_address_coordinate;
    }

    public void setOrder_delivery_address_coordinate(String order_delivery_address_coordinate) {
        this.order_delivery_address_coordinate = order_delivery_address_coordinate;
    }

    public String getOrder_delivery_address_landmark() {
        return order_delivery_address_landmark;
    }

    public void setOrder_delivery_address_landmark(String order_delivery_address_landmark) {
        this.order_delivery_address_landmark = order_delivery_address_landmark;
    }

    public String getOrder_prescription() {
        return order_prescription;
    }

    public void setOrder_prescription(String order_prescription) {
        this.order_prescription = order_prescription;
    }

    public static class OrderModelForRealtimeDb implements Serializable{
        private long order_expected_delivery_time;
        private String current_order_status;
        private String order_delivery_address_text;
        private String order_delivery_address_coordinate;
        private String order_delivery_address_landmark;
        private String order_prescription;
        private String order_payment_from;
        private HashMap<String,Object> order_amount_stats;
        private String customer_id;
        private String order_id;
        private long order_time;

        public OrderModelForRealtimeDb() {
        }

        public OrderModelForRealtimeDb(HashMap<String,Object> total_order_amount, long order_expected_delivery_time,
                                       String current_order_status, String order_delivery_address_text,
                                       String order_delivery_address_coordinate,
                                       String order_delivery_address_landmark,
                                       String order_prescription, String order_payment_from
                                        , long order_time
        ) {
            this.order_expected_delivery_time = order_expected_delivery_time;
            this.current_order_status = current_order_status;
            this.order_delivery_address_text = order_delivery_address_text;
            this.order_delivery_address_coordinate = order_delivery_address_coordinate;
            this.order_delivery_address_landmark = order_delivery_address_landmark;
            this.order_prescription = order_prescription;
            this.order_payment_from=order_payment_from;
            this.order_amount_stats=total_order_amount;
            this.customer_id= Constants.uid;
            this.order_time=order_time;
        }

        public long getOrder_time() {
            return order_time;
        }

        public void setOrder_time(long order_time) {
            this.order_time = order_time;
        }

        public String getOrder_id() {
            return order_id;
        }

        public void setOrder_id(String order_id) {
            this.order_id = order_id;
        }

        public String getCustomer_id() {
            return customer_id;
        }

        public void setCustomer_id(String customer_id) {
            this.customer_id = customer_id;
        }

        public HashMap<String, Object> getOrder_amount_stats() {
            return order_amount_stats;
        }

        public void setOrder_amount_stats(HashMap<String, Object> order_amount_stats) {
            this.order_amount_stats = order_amount_stats;
        }

        public String getOrder_payment_from() {
            return order_payment_from;
        }

        public void setOrder_payment_from(String order_payment_from) {
            this.order_payment_from = order_payment_from;
        }

        public long getOrder_expected_delivery_time() {
            return order_expected_delivery_time;
        }

        public void setOrder_expected_delivery_time(long order_expected_delivery_time) {
            this.order_expected_delivery_time = order_expected_delivery_time;
        }

        public String getCurrent_order_status() {
            return current_order_status;
        }

        public void setCurrent_order_status(String current_order_status) {
            this.current_order_status = current_order_status;
        }

        public String getOrder_delivery_address_text() {
            return order_delivery_address_text;
        }

        public void setOrder_delivery_address_text(String order_delivery_address_text) {
            this.order_delivery_address_text = order_delivery_address_text;
        }

        public String getOrder_delivery_address_coordinate() {
            return order_delivery_address_coordinate;
        }

        public void setOrder_delivery_address_coordinate(String order_delivery_address_coordinate) {
            this.order_delivery_address_coordinate = order_delivery_address_coordinate;
        }

        public String getOrder_delivery_address_landmark() {
            return order_delivery_address_landmark;
        }

        public void setOrder_delivery_address_landmark(String order_delivery_address_landmark) {
            this.order_delivery_address_landmark = order_delivery_address_landmark;
        }

        public String getOrder_prescription() {
            return order_prescription;
        }

        public void setOrder_prescription(String order_prescription) {
            this.order_prescription = order_prescription;
        }
    }
    public static class OrderModelForFirestore implements Serializable{
        private ArrayList<HashMap<String,Object>> order_medicines;//store
        private ArrayList<HashMap<String, Object>> order_status;//store
        private  HashMap<String, Object> order_payment_stats;//store
//        private String order_payment_ref_id;//store
//        private String order_payment_time;//store
        private String order_delivery_man;//store
        private String order_delivery_time;//store
        private String customer_id;//store
        private long order_done_at;
        public OrderModelForFirestore(String order_status,
                                      String order_delivery_man,
                                      String order_delivery_time,
                                      ArrayList<HashMap<String, Object>> order_medicines,
                                      HashMap<String, Object> order_payment_stats
                                      ,long order_time) {
            this.order_medicines = order_medicines;
            ArrayList<HashMap<String,Object>> list=new ArrayList<>();
            HashMap<String,Object> statusInfo=new HashMap<>();
            statusInfo.put("order_status_name",order_status);
            statusInfo.put("order_status_note",null);
            statusInfo.put("order_status_time",System.currentTimeMillis());
            list.add(statusInfo);
            this.order_status = list;
            this.order_payment_stats = order_payment_stats;
            this.order_delivery_man = order_delivery_man;
            this.order_delivery_time = order_delivery_time;
            this.customer_id= Constants.uid;
            this.order_done_at=order_time;
        }

        public long getOrder_done_at() {
            return order_done_at;
        }

        public void setOrder_done_at(long order_done_at) {
            this.order_done_at = order_done_at;
        }

        public ArrayList<HashMap<String, Object>> getOrder_medicines() {
            return order_medicines;
        }

        public void setOrder_medicines(ArrayList<HashMap<String, Object>> order_medicines) {
            this.order_medicines = order_medicines;
        }

        public ArrayList<HashMap<String, Object>> getOrder_status() {
            return order_status;
        }


        public void setOrder_status(ArrayList<HashMap<String, Object>> order_status) {
            this.order_status = order_status;
        }

        public String getCustomer_id() {
            return customer_id;
        }

        public void setCustomer_id(String customer_id) {
            this.customer_id = customer_id;
        }

        public HashMap<String, Object> getOrder_payment_stats() {
            return order_payment_stats;
        }

        public void setOrder_payment_stats(HashMap<String, Object> order_payment_stats) {
            this.order_payment_stats = order_payment_stats;
        }
//
//        public String getOrder_payment_ref_id() {
//            return order_payment_ref_id;
//        }
//
//        public void setOrder_payment_ref_id(String order_payment_ref_id) {
//            this.order_payment_ref_id = order_payment_ref_id;
//        }
//
//        public String getOrder_payment_time() {
//            return order_payment_time;
//        }
//
//        public void setOrder_payment_time(String order_payment_time) {
//            this.order_payment_time = order_payment_time;
//        }

        public String getOrder_delivery_man() {
            return order_delivery_man;
        }

        public void setOrder_delivery_man(String order_delivery_man) {
            this.order_delivery_man = order_delivery_man;
        }

        public String getOrder_delivery_time() {
            return order_delivery_time;
        }

        public void setOrder_delivery_time(String order_delivery_time) {
            this.order_delivery_time = order_delivery_time;
        }
    }

}
