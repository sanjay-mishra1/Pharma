package com.example.pharma.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class MedicineModel implements Serializable {
    private  ArrayList<String> medicine_images;
    private String medicine_name;
    private String medicine_company;
    private String medicine_img;
    private long medicine_price;
    private long medicine_original_price;
    private long medicine_max_quantity;
    private long medicine_stock;
    private String medicine_description_daily_dose;
    private String medicine_chemicals;
    private String medicine_side_effects;
    private ArrayList<String> medicine_disease;
    private boolean prescription_needed;
    private long medicine_order;
    private String medicine_category;
    private String medicine_type;
    private String medicine_weight;
    private String medicine_id;
    private long price_discount;
    private long medicine_discount;
    private ArrayList<HashMap<String, Object>> medicine_variant;
/*
    public MedicineModel(String medicine_name, String medicine_company, String medicine_img, long medicine_price, String medicine_disease, boolean prescription_needed, String medicine_category, String medicine_type, String medicine_weight, long price_discount) {
        this.medicine_name = medicine_name;
        this.medicine_company = medicine_company;
        this.medicine_img = medicine_img;
        this.medicine_price = medicine_price;
        this.medicine_disease = medicine_disease;
        this.prescription_needed = prescription_needed;
        this.medicine_category = medicine_category;
        this.medicine_type = medicine_type;
        this.medicine_weight = medicine_weight;
        this.price_discount = price_discount;
    }

    public MedicineModel(String medicine_name, String medicine_company, ArrayList<String> images, long medicine_original_price, long medicine_max_quantity, long medicine_stock, String medicine_description_daily_dose, String medicine_chemicals, String medicine_side_effects, String medicine_weight) {
        this.medicine_name = medicine_name;
        this.medicine_company = medicine_company;
        this.medicine_images = images;
        this.medicine_original_price = medicine_original_price;
        this.medicine_max_quantity = medicine_max_quantity;
        this.medicine_stock = medicine_stock;
        this.medicine_description_daily_dose = medicine_description_daily_dose;
        this.medicine_chemicals = medicine_chemicals;
        this.medicine_side_effects = medicine_side_effects;
        this.medicine_weight = medicine_weight;
    }

    public MedicineModel(String medicine_name, String medicine_company, ArrayList<String> imgs, String medicine_img, long medicine_price, long medicine_original_price, long medicine_max_quantity, long medicine_stock, String medicine_description_daily_dose, String medicine_chemicals, String medicine_side_effects, String medicine_disease, boolean prescription_needed, String weight) {
        this.medicine_name = medicine_name;
        this.medicine_company = medicine_company;
        this.medicine_images = imgs;
        this.medicine_img = medicine_img;
        this.medicine_price = medicine_price;
        this.medicine_original_price = medicine_original_price;
        this.medicine_max_quantity = medicine_max_quantity;
        this.medicine_stock = medicine_stock;
        this.medicine_description_daily_dose = medicine_description_daily_dose;
        this.medicine_chemicals = medicine_chemicals;
        this.medicine_side_effects = medicine_side_effects;
        this.medicine_disease = medicine_disease;
        this.prescription_needed = prescription_needed;
        this.medicine_weight=weight;
    }
*/

    public long getMedicine_discount() {
        return medicine_discount;
    }

    public void setMedicine_discount(long medicine_discount) {
        this.medicine_discount = medicine_discount;
    }

    /* For firebase realtime db*/
    public MedicineModel(String medicine_name, ArrayList<String> images,
                         long medicine_original_price, long medicine_max_quantity, String medicine_description_daily_dose, String medicine_chemicals, String medicine_side_effects, ArrayList<HashMap<String, Object>> variantsList) {
        this.medicine_name = medicine_name;
        this.medicine_images = images;
        this.medicine_original_price = medicine_original_price;
        this.medicine_max_quantity = medicine_max_quantity;
        this.medicine_description_daily_dose = medicine_description_daily_dose;
        this.medicine_chemicals = medicine_chemicals;
        this.medicine_side_effects = medicine_side_effects;
        this.medicine_variant=variantsList;
        this.price_discount= Long.parseLong(null);
        this.price_discount= Long.parseLong(null);
        this.price_discount= Long.parseLong(null);

    }
    /*For firebase firestore db*/
    public MedicineModel(String nameTxt, String company, String img, long priceNumber, ArrayList<String> allDisease, boolean prescribe,
                         String category, String typeTxt, String weightTxt, long discount, long stock) {
        this.medicine_name = nameTxt;
        this.medicine_company = company;
        this.medicine_img = img;
        this.medicine_price = priceNumber;
        this.medicine_stock = stock;
        this.medicine_disease = allDisease;
        this.prescription_needed = prescribe;
        this.medicine_weight=weightTxt;
        this.medicine_category=category;
        this.medicine_type=typeTxt;
        price_discount=discount;
    }


    public long getMedicine_order() {
        return medicine_order;
    }

    public void setMedicine_order(long medicine_order) {
        this.medicine_order = medicine_order;
    }

    public String getMedicine_type() {
        return medicine_type;
    }

    public void setMedicine_type(String medicine_type) {
        this.medicine_type = medicine_type;
    }

    public long getPrice_discount() {
        return price_discount;
    }

    public void setPrice_discount(long price_discount) {
        this.price_discount = price_discount;
    }

    public String getMedicine_category() {
        return medicine_category;
    }

    public void setMedicine_category(String medicine_category) {
        this.medicine_category = medicine_category;
    }


    public String getMedicine_id() {
        return medicine_id;
    }

    public void setMedicine_id(String medicine_id) {
        this.medicine_id = medicine_id;
    }


    public MedicineModel() {

    }

    public String getMedicine_name() {
        return medicine_name;
    }

    public void setMedicine_name(String medicine_name) {
        this.medicine_name = medicine_name;
    }

    public String getMedicine_company() {
        return medicine_company;
    }

    public void setMedicine_company(String medicine_company) {
        this.medicine_company = medicine_company;
    }

    public ArrayList<String> getMedicine_images() {
        return medicine_images;
    }

    public void setMedicine_images(ArrayList<String> medicine_images) {
        this.medicine_images = medicine_images;
    }

    public String getMedicine_img() {
        return medicine_img;
    }

    public void setMedicine_img(String medicine_img) {
        this.medicine_img = medicine_img;
    }

    public long getMedicine_price() {
        return medicine_price;
    }

    public void setMedicine_price(long medicine_price) {
        this.medicine_price = medicine_price;
    }

    public long getMedicine_original_price() {
        return medicine_original_price;
    }

    public void setMedicine_original_price(long medicine_original_price) {
        this.medicine_original_price = medicine_original_price;
    }

    public long getMedicine_max_quantity() {
        return medicine_max_quantity;
    }

    public void setMedicine_max_quantity(long medicine_max_quantity) {
        this.medicine_max_quantity = medicine_max_quantity;
    }

    public long getMedicine_stock() {
        return medicine_stock;
    }

    public void setMedicine_stock(long medicine_stock) {
        this.medicine_stock = medicine_stock;
    }

    public String getMedicine_description_daily_dose() {
        return medicine_description_daily_dose;
    }

    public void setMedicine_description_daily_dose(String medicine_description_daily_dose) {
        this.medicine_description_daily_dose = medicine_description_daily_dose;
    }

    public String getMedicine_chemicals() {
        return medicine_chemicals;
    }

    public void setMedicine_chemicals(String medicine_chemicals) {
        this.medicine_chemicals = medicine_chemicals;
    }

    public String getMedicine_side_effects() {
        return medicine_side_effects;
    }

    public void setMedicine_side_effects(String medicine_side_effects) {
        this.medicine_side_effects = medicine_side_effects;
    }

    public ArrayList<String> getMedicine_disease() {
        return medicine_disease;
    }

    public void setMedicine_disease(ArrayList<String> medicine_disease) {
        this.medicine_disease = medicine_disease;
    }

    public boolean isPrescription_needed() {
        return prescription_needed;
    }

    public void setPrescription_needed(boolean prescription_needed) {
        this.prescription_needed = prescription_needed;
    }

    public String getMedicine_weight() {
        return medicine_weight;
    }

    public void setMedicine_weight(String medicine_weight) {
        this.medicine_weight = medicine_weight;
    }
    public static class MedicineModelForFireStore implements Serializable {
        private String medicine_name;
        private String medicine_company;
        private String medicine_img;
        private long medicine_price;
        //        private long medicine_original_price;
        private long medicine_stock;
        private ArrayList<String> medicine_disease;
        private boolean prescription_needed;
        private String medicine_category;
        private String medicine_type;
        private String medicine_weight;
        private long medicine_discount;

        /*For firebase firestore db*/
        public MedicineModelForFireStore(String nameTxt, String company, String img, long priceNumber, ArrayList<String> allDisease, boolean prescribe,
                                         String category, String typeTxt, String weightTxt, long discount,
                                         long stock
        ) {
            this.medicine_name = nameTxt;
            this.medicine_company = company;
            this.medicine_img = img;
            this.medicine_price = priceNumber;
            this.medicine_stock = stock;
            this.medicine_disease = allDisease;
            this.prescription_needed = prescribe;
            this.medicine_weight=weightTxt;
            this.medicine_category=category;
            this.medicine_type=typeTxt;
//            medicine_original_price=discount;
            this.medicine_discount=discount;
        }

        public MedicineModelForFireStore() {

        }

        public String getMedicine_name() {
            return medicine_name;
        }

        public void setMedicine_name(String medicine_name) {
            this.medicine_name = medicine_name;
        }

        public long getMedicine_discount() {
            return medicine_discount;
        }

        public void setMedicine_discount(long medicine_discount) {
            this.medicine_discount = medicine_discount;
        }

        public String getMedicine_company() {
            return medicine_company;
        }

        public void setMedicine_company(String medicine_company) {
            this.medicine_company = medicine_company;
        }

        public String getMedicine_img() {
            return medicine_img;
        }

        public void setMedicine_img(String medicine_img) {
            this.medicine_img = medicine_img;
        }

        public long getMedicine_price() {
            return medicine_price;
        }

        public void setMedicine_price(long medicine_price) {
            this.medicine_price = medicine_price;
        }

//        public long getMedicine_original_price() {
//            return medicine_original_price;
//        }

//        public void setMedicine_original_price(long medicine_original_price) {
//            this.medicine_original_price = medicine_original_price;
//        }

        public long getMedicine_stock() {
            return medicine_stock;
        }

        public void setMedicine_stock(long medicine_stock) {
            this.medicine_stock = medicine_stock;
        }

        public ArrayList<String> getMedicine_disease() {
            return medicine_disease;
        }

        public void setMedicine_disease(ArrayList<String> medicine_disease) {
            this.medicine_disease = medicine_disease;
        }

        public boolean isPrescription_needed() {
            return prescription_needed;
        }

        public void setPrescription_needed(boolean prescription_needed) {
            this.prescription_needed = prescription_needed;
        }

        public String getMedicine_category() {
            return medicine_category;
        }

        public void setMedicine_category(String medicine_category) {
            this.medicine_category = medicine_category;
        }

        public String getMedicine_type() {
            return medicine_type;
        }

        public void setMedicine_type(String medicine_type) {
            this.medicine_type = medicine_type;
        }

        public String getMedicine_weight() {
            return medicine_weight;
        }

        public void setMedicine_weight(String medicine_weight) {
            this.medicine_weight = medicine_weight;
        }
    }

    public static class MedicineModelForRealtime implements Serializable{
        private ArrayList<HashMap<String, Object>> medicine_variant;
        private  HashMap<String, String> medicine_note;
        private  ArrayList<String> medicine_images;
        private String medicine_name;
        //        private long medicine_price;
        private long medicine_max_quantity;
        private String medicine_description_daily_dose;
        private String medicine_chemicals;
        private String medicine_side_effects;
        private long medicine_order;
        private String medicine_category;
        private String medicine_company;
        /* For firebase realtime db*/

        public MedicineModelForRealtime() {
        }

        public MedicineModelForRealtime(String medicine_name, String medicine_company ,ArrayList<String> images,
                                        long medicine_max_quantity,
                                        String medicine_category,HashMap<String,String> medicine_note,ArrayList<HashMap<String,Object>> variantsList) {
//            this.medicine_price=medicine_price;
            this.medicine_name = medicine_name;
            this.medicine_images = images;
            this.medicine_max_quantity = medicine_max_quantity;
            this.medicine_company=medicine_company;
//            this.medicine_description_daily_dose = medicine_description_daily_dose;
//            this.medicine_chemicals = medicine_chemicals;
//            this.medicine_side_effects = medicine_side_effects;
            this.medicine_note=medicine_note;

            this.medicine_variant=variantsList;
            this.medicine_category=medicine_category;
        }

        public String getMedicine_company() {
            return medicine_company;
        }

        public void setMedicine_company(String medicine_company) {
            this.medicine_company = medicine_company;
        }

        public HashMap<String, String> getMedicine_note() {
            return medicine_note;
        }

        public void setMedicine_note(HashMap<String, String> medicine_note) {
            this.medicine_note = medicine_note;
        }

        public ArrayList<String> getMedicine_images() {
            return medicine_images;
        }

        public void setMedicine_images(ArrayList<String> medicine_images) {
            this.medicine_images = medicine_images;
        }

        public String getMedicine_name() {
            return medicine_name;
        }

        public void setMedicine_name(String medicine_name) {
            this.medicine_name = medicine_name;
        }

//        public long getMedicine_price() {
//            return medicine_price;
//        }
//
//        public void setMedicine_price(long medicine_price) {
//            this.medicine_price = medicine_price;
//        }

        public long getMedicine_max_quantity() {
            return medicine_max_quantity;
        }

        public void setMedicine_max_quantity(long medicine_max_quantity) {
            this.medicine_max_quantity = medicine_max_quantity;
        }

        public String getMedicine_description_daily_dose() {
            return medicine_description_daily_dose;
        }

        public void setMedicine_description_daily_dose(String medicine_description_daily_dose) {
            this.medicine_description_daily_dose = medicine_description_daily_dose;
        }

        public String getMedicine_chemicals() {
            return medicine_chemicals;
        }

        public void setMedicine_chemicals(String medicine_chemicals) {
            this.medicine_chemicals = medicine_chemicals;
        }

        public String getMedicine_side_effects() {
            return medicine_side_effects;
        }

        public void setMedicine_side_effects(String medicine_side_effects) {
            this.medicine_side_effects = medicine_side_effects;
        }

        public long getMedicine_order() {
            return medicine_order;
        }

        public void setMedicine_order(long medicine_order) {
            this.medicine_order = medicine_order;
        }

        public String getMedicine_category() {
            return medicine_category;
        }

        public void setMedicine_category(String medicine_category) {
            this.medicine_category = medicine_category;
        }

        public ArrayList<HashMap<String, Object>> getMedicine_variant() {
            return medicine_variant;
        }

        public void setMedicine_variant(ArrayList<HashMap<String, Object>> medicine_variant) {
            this.medicine_variant = medicine_variant;
        }
    }
}




/*
public class MedicineModel implements Serializable {
    private  ArrayList<String> medicine_images;
    private String medicine_name;
    private String medicine_company;
    private String medicine_img;
    private long medicine_price;
    private long medicine_original_price;
    private long medicine_max_quantity;
    private long medicine_stock;
    private String medicine_description_daily_dose;
    private String medicine_chemicals;
    private String medicine_side_effects;
    private ArrayList<String> medicine_disease;
    private boolean prescription_needed;
    private long medicine_order;
    private String medicine_category;
    private String medicine_type;
    private String medicine_weight;
    private String medicine_id;
    private long price_discount;
    private ArrayList<HashMap<String, Object>> medicine_variant;
/*
    public MedicineModel(String medicine_name, String medicine_company, String medicine_img, long medicine_price, String medicine_disease, boolean prescription_needed, String medicine_category, String medicine_type, String medicine_weight, long price_discount) {
        this.medicine_name = medicine_name;
        this.medicine_company = medicine_company;
        this.medicine_img = medicine_img;
        this.medicine_price = medicine_price;
        this.medicine_disease = medicine_disease;
        this.prescription_needed = prescription_needed;
        this.medicine_category = medicine_category;
        this.medicine_type = medicine_type;
        this.medicine_weight = medicine_weight;
        this.price_discount = price_discount;
    }

    public MedicineModel(String medicine_name, String medicine_company, ArrayList<String> images, long medicine_original_price, long medicine_max_quantity, long medicine_stock, String medicine_description_daily_dose, String medicine_chemicals, String medicine_side_effects, String medicine_weight) {
        this.medicine_name = medicine_name;
        this.medicine_company = medicine_company;
        this.medicine_images = images;
        this.medicine_original_price = medicine_original_price;
        this.medicine_max_quantity = medicine_max_quantity;
        this.medicine_stock = medicine_stock;
        this.medicine_description_daily_dose = medicine_description_daily_dose;
        this.medicine_chemicals = medicine_chemicals;
        this.medicine_side_effects = medicine_side_effects;
        this.medicine_weight = medicine_weight;
    }

    public MedicineModel(String medicine_name, String medicine_company, ArrayList<String> imgs, String medicine_img, long medicine_price, long medicine_original_price, long medicine_max_quantity, long medicine_stock, String medicine_description_daily_dose, String medicine_chemicals, String medicine_side_effects, String medicine_disease, boolean prescription_needed, String weight) {
        this.medicine_name = medicine_name;
        this.medicine_company = medicine_company;
        this.medicine_images = imgs;
        this.medicine_img = medicine_img;
        this.medicine_price = medicine_price;
        this.medicine_original_price = medicine_original_price;
        this.medicine_max_quantity = medicine_max_quantity;
        this.medicine_stock = medicine_stock;
        this.medicine_description_daily_dose = medicine_description_daily_dose;
        this.medicine_chemicals = medicine_chemicals;
        this.medicine_side_effects = medicine_side_effects;
        this.medicine_disease = medicine_disease;
        this.prescription_needed = prescription_needed;
        this.medicine_weight=weight;
    }
* /
/* For firebase realtime db* /
    public MedicineModel(String medicine_name, ArrayList<String> images,
                         long medicine_original_price, long medicine_max_quantity, String medicine_description_daily_dose, String medicine_chemicals, String medicine_side_effects, ArrayList<HashMap<String, Object>> variantsList) {
        this.medicine_name = medicine_name;
        this.medicine_images = images;
        this.medicine_original_price = medicine_original_price;
        this.medicine_max_quantity = medicine_max_quantity;
        this.medicine_description_daily_dose = medicine_description_daily_dose;
        this.medicine_chemicals = medicine_chemicals;
        this.medicine_side_effects = medicine_side_effects;
        this.medicine_variant=variantsList;
        this.price_discount= Long.parseLong(null);
        this.price_discount= Long.parseLong(null);
        this.price_discount= Long.parseLong(null);

    }
    /*For firebase firestore db* /
    public MedicineModel(String nameTxt, String company, String img, long priceNumber, ArrayList<String> allDisease, boolean prescribe,
                         String category, String typeTxt, String weightTxt, long discount, long stock) {
        this.medicine_name = nameTxt;
        this.medicine_company = company;
        this.medicine_img = img;
        this.medicine_price = priceNumber;
        this.medicine_stock = stock;
        this.medicine_disease = allDisease;
        this.prescription_needed = prescribe;
        this.medicine_weight=weightTxt;
        this.medicine_category=category;
        this.medicine_type=typeTxt;
        price_discount=discount;
    }


    public long getMedicine_order() {
        return medicine_order;
    }

    public void setMedicine_order(long medicine_order) {
        this.medicine_order = medicine_order;
    }

    public String getMedicine_type() {
        return medicine_type;
    }

    public void setMedicine_type(String medicine_type) {
        this.medicine_type = medicine_type;
    }

    public long getPrice_discount() {
        return price_discount;
    }

    public void setPrice_discount(long price_discount) {
        this.price_discount = price_discount;
    }

    public String getMedicine_category() {
        return medicine_category;
    }

    public void setMedicine_category(String medicine_category) {
        this.medicine_category = medicine_category;
    }


    public String getMedicine_id() {
        return medicine_id;
    }

    public void setMedicine_id(String medicine_id) {
        this.medicine_id = medicine_id;
    }


    public MedicineModel() {

    }

    public String getMedicine_name() {
        return medicine_name;
    }

    public void setMedicine_name(String medicine_name) {
        this.medicine_name = medicine_name;
    }

    public String getMedicine_company() {
        return medicine_company;
    }

    public void setMedicine_company(String medicine_company) {
        this.medicine_company = medicine_company;
    }

    public ArrayList<String> getMedicine_images() {
        return medicine_images;
    }

    public void setMedicine_images(ArrayList<String> medicine_images) {
        this.medicine_images = medicine_images;
    }

    public String getMedicine_img() {
        return medicine_img;
    }

    public void setMedicine_img(String medicine_img) {
        this.medicine_img = medicine_img;
    }

    public long getMedicine_price() {
        return medicine_price;
    }

    public void setMedicine_price(long medicine_price) {
        this.medicine_price = medicine_price;
    }

    public long getMedicine_original_price() {
        return medicine_original_price;
    }

    public void setMedicine_original_price(long medicine_original_price) {
        this.medicine_original_price = medicine_original_price;
    }

    public long getMedicine_max_quantity() {
        return medicine_max_quantity;
    }

    public void setMedicine_max_quantity(long medicine_max_quantity) {
        this.medicine_max_quantity = medicine_max_quantity;
    }

    public long getMedicine_stock() {
        return medicine_stock;
    }

    public void setMedicine_stock(long medicine_stock) {
        this.medicine_stock = medicine_stock;
    }

    public String getMedicine_description_daily_dose() {
        return medicine_description_daily_dose;
    }

    public void setMedicine_description_daily_dose(String medicine_description_daily_dose) {
        this.medicine_description_daily_dose = medicine_description_daily_dose;
    }

    public String getMedicine_chemicals() {
        return medicine_chemicals;
    }

    public void setMedicine_chemicals(String medicine_chemicals) {
        this.medicine_chemicals = medicine_chemicals;
    }

    public String getMedicine_side_effects() {
        return medicine_side_effects;
    }

    public void setMedicine_side_effects(String medicine_side_effects) {
        this.medicine_side_effects = medicine_side_effects;
    }

    public ArrayList<String> getMedicine_disease() {
        return medicine_disease;
    }

    public void setMedicine_disease(ArrayList<String> medicine_disease) {
        this.medicine_disease = medicine_disease;
    }

    public boolean isPrescription_needed() {
        return prescription_needed;
    }

    public void setPrescription_needed(boolean prescription_needed) {
        this.prescription_needed = prescription_needed;
    }

    public String getMedicine_weight() {
        return medicine_weight;
    }

    public void setMedicine_weight(String medicine_weight) {
        this.medicine_weight = medicine_weight;
    }
  public static class MedicineModelForFireStore{
        private String medicine_name;
        private String medicine_company;
        private String medicine_img;
        private long medicine_price;
        private long medicine_original_price;
        private long medicine_stock;
        private ArrayList<String> medicine_disease;
        private boolean prescription_needed;
        private String medicine_category;
        private String medicine_type;
        private String medicine_weight;

        /*For firebase firestore db* /
        public MedicineModelForFireStore(String nameTxt, String company, String img, long priceNumber, ArrayList<String> allDisease, boolean prescribe,
                             String category, String typeTxt, String weightTxt, long discount, long stock) {
            this.medicine_name = nameTxt;
            this.medicine_company = company;
            this.medicine_img = img;
            this.medicine_price = priceNumber;
            this.medicine_stock = stock;
            this.medicine_disease = allDisease;
            this.prescription_needed = prescribe;
            this.medicine_weight=weightTxt;
            this.medicine_category=category;
            this.medicine_type=typeTxt;
            medicine_original_price=discount;
        }

        public String getMedicine_name() {
            return medicine_name;
        }

        public void setMedicine_name(String medicine_name) {
            this.medicine_name = medicine_name;
        }

        public String getMedicine_company() {
            return medicine_company;
        }

        public void setMedicine_company(String medicine_company) {
            this.medicine_company = medicine_company;
        }

        public String getMedicine_img() {
            return medicine_img;
        }

        public void setMedicine_img(String medicine_img) {
            this.medicine_img = medicine_img;
        }

        public long getMedicine_price() {
            return medicine_price;
        }

        public void setMedicine_price(long medicine_price) {
            this.medicine_price = medicine_price;
        }

        public long getMedicine_original_price() {
            return medicine_original_price;
        }

        public void setMedicine_original_price(long medicine_original_price) {
            this.medicine_original_price = medicine_original_price;
        }

        public long getMedicine_stock() {
            return medicine_stock;
        }

        public void setMedicine_stock(long medicine_stock) {
            this.medicine_stock = medicine_stock;
        }

        public ArrayList<String> getMedicine_disease() {
            return medicine_disease;
        }

        public void setMedicine_disease(ArrayList<String> medicine_disease) {
            this.medicine_disease = medicine_disease;
        }

        public boolean isPrescription_needed() {
            return prescription_needed;
        }

        public void setPrescription_needed(boolean prescription_needed) {
            this.prescription_needed = prescription_needed;
        }

        public String getMedicine_category() {
            return medicine_category;
        }

        public void setMedicine_category(String medicine_category) {
            this.medicine_category = medicine_category;
        }

        public String getMedicine_type() {
            return medicine_type;
        }

        public void setMedicine_type(String medicine_type) {
            this.medicine_type = medicine_type;
        }

        public String getMedicine_weight() {
            return medicine_weight;
        }

        public void setMedicine_weight(String medicine_weight) {
            this.medicine_weight = medicine_weight;
        }
    }

   public static class MedicineModelForRealtime{
        private  ArrayList<String> medicine_images;
        private String medicine_name;
        private long medicine_price;
        private long medicine_max_quantity;
        private String medicine_description_daily_dose;
        private String medicine_chemicals;
        private String medicine_side_effects;
        private long medicine_order;
        private String medicine_category;
        private ArrayList<HashMap<String, Object>> medicine_variant;
        public MedicineModelForRealtime(){

        }
        /* For firebase realtime db* /
        public MedicineModelForRealtime(String medicine_name, ArrayList<String> images,
                             long medicine_price, long medicine_max_quantity, String medicine_description_daily_dose, String medicine_chemicals, String medicine_side_effects, ArrayList<HashMap<String, Object>> variantsList,String medicine_category) {
            this.medicine_price=medicine_price;
            this.medicine_name = medicine_name;
            this.medicine_images = images;
            this.medicine_max_quantity = medicine_max_quantity;
            this.medicine_description_daily_dose = medicine_description_daily_dose;
            this.medicine_chemicals = medicine_chemicals;
            this.medicine_side_effects = medicine_side_effects;
            this.medicine_variant=variantsList;
            this.medicine_category=medicine_category;
        }

        public ArrayList<String> getMedicine_images() {
            return medicine_images;
        }

        public void setMedicine_images(ArrayList<String> medicine_images) {
            this.medicine_images = medicine_images;
        }

        public String getMedicine_name() {
            return medicine_name;
        }

        public void setMedicine_name(String medicine_name) {
            this.medicine_name = medicine_name;
        }

        public long getMedicine_price() {
            return medicine_price;
        }

        public void setMedicine_price(long medicine_price) {
            this.medicine_price = medicine_price;
        }

        public long getMedicine_max_quantity() {
            return medicine_max_quantity;
        }

        public void setMedicine_max_quantity(long medicine_max_quantity) {
            this.medicine_max_quantity = medicine_max_quantity;
        }

        public String getMedicine_description_daily_dose() {
            return medicine_description_daily_dose;
        }

        public void setMedicine_description_daily_dose(String medicine_description_daily_dose) {
            this.medicine_description_daily_dose = medicine_description_daily_dose;
        }

        public String getMedicine_chemicals() {
            return medicine_chemicals;
        }

        public void setMedicine_chemicals(String medicine_chemicals) {
            this.medicine_chemicals = medicine_chemicals;
        }

        public String getMedicine_side_effects() {
            return medicine_side_effects;
        }

        public void setMedicine_side_effects(String medicine_side_effects) {
            this.medicine_side_effects = medicine_side_effects;
        }

        public long getMedicine_order() {
            return medicine_order;
        }

        public void setMedicine_order(long medicine_order) {
            this.medicine_order = medicine_order;
        }

        public String getMedicine_category() {
            return medicine_category;
        }

        public void setMedicine_category(String medicine_category) {
            this.medicine_category = medicine_category;
        }

        public ArrayList<HashMap<String, Object>> getMedicine_variant() {
            return medicine_variant;
        }

        public void setMedicine_variant(ArrayList<HashMap<String, Object>> medicine_variant) {
            this.medicine_variant = medicine_variant;
        }
    }
}
*/
