package com.example.pharma.recycler;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pharma.R;
import com.example.pharma.medicine.ShowMedicinesActivity;
import com.example.pharma.medicine_info.MedicineDetailsActivity;
import com.example.pharma.model.MedicineModel;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

public class CategoryViewHolder extends RecyclerView.ViewHolder {
    CategoryViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setText(String categoryName) {
        TextView textView=itemView.findViewById(R.id.title);
        textView.setText(categoryName);
    }

    void setCategoryImage(String categoryIcon,int position) {
        ImageView imageView= (ImageView) itemView.findViewById(R.id.img);


        switch (position){
            case 0:imageView.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.gradient_0));
                break;
            case 1:imageView.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.gradient_4));
                break;
            case 2:imageView.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.gradient_2));
                break;
            case 3:imageView.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.gradient_3));
                break;
            case 4:imageView.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.gradient_4));
                break;
            case 6:imageView.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.gradient_6));
                break;
            case 7:imageView.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.gradient_7));
                break;
            case 8:imageView.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.gradient_8));
                break;
            case 9:imageView.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.gradient_9));
                break;
            case 10:imageView.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.gradient_10));
                break;
/*
            case 11:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_11));
                break;
            case 12:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_12));
                break;
            case 13:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_13));
                break;
            case 14:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_14));
                break;
            case 15:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_15));
                break;
            case 16:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_16));
                break;
            case 17:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_17));
                break;
            case 18:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_18));
                break;
            case 19:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_19));
                break;
            case 20:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_20));
                break;
            case 21:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_21));
                break;
            case 22:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_22));
                break;
            case 23:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_23));
                break;
            case 24:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_24));
                break;
            case 25:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_25));
                break;
            case 26:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_26));
                break;
            case 27:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_27));
                break;
            case 28:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_28));
                break;
            case 29:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_29));
                break;
            case 30:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_30));
                break;
            case 31:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_31));
                break;
            case 32:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_32));
                break;
            case 33:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_33));
                break;
            case 34:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_34));
                break;
            case 35:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_35));
                break;
            case 36:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_36));
                break;
            case 37:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_37));
                break;
            case 38:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_38));
                break;
            case 39:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_39));
                break;
            case 40:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_40));
                break;
            case 41:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_41));
                break;
            case 42:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_42));
                break;
            case 43:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_43));
                break;
            case 44:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_44));
                break;
            case 45:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_45));
                break;
            case 46:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_46));
                break;
            case 47:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_47));
                break;
            case 48:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_48));
                break;
            case 49:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_49));
                break;
            case 50:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_50));
                break;
            case 51:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_51));
                break;
            case 52:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_52));
                break;
            case 53:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_53));
                break;
            case 54:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_54));
                break;
            case 55:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_55));
                break;
            case 56:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_56));
                break;
            case 57:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_57));
                break;
            case 58:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_58));
                break;
            case 59:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_59));
                break;
            case 60:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_60));
                break;
            case 61:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_61));
                break;
            case 62:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_62));
                break;
            case 63:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_63));
                break;
            case 64:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_64));
                break;
            case 65:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_65));
                break;
            case 66:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_66));
                break;
            case 67:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_67));
                break;
            case 68:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_68));
                break;
            case 69:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_69));
                break;
            case 70:imageView.setBackground(itemView.getContext().getDrawable(R.drawable.gradient_70));
                break;
*/
            case 5:

            default:imageView.setBackground(itemView.getContext().getResources().getDrawable(R.drawable.gradient_5));
        }
        try {
            if (categoryIcon.equals("Category"))
                Glide.with(itemView).load(R.drawable.ic_all_category_white_24dp).into(imageView);
            else Glide.with(itemView).load(categoryIcon).into(imageView);
        }catch (Exception e){e.printStackTrace();}
    }

    void setBoxListener(String key,String name) {
        itemView.findViewById(R.id.parent).setOnClickListener(v -> {
    Intent intent=new Intent(itemView.getContext(), ShowMedicinesActivity.class);
//    intent.putExtra("DATA_ID",key);
//    intent.putExtra("DATA_NAME",name);

    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    itemView.getContext().startActivity(intent);
        });
    }

    void setImage(String medicine_img) {
        Glide.with(itemView).load(medicine_img).into((ImageView)itemView.findViewById(R.id.img) );
    }

    void setMedicine_name(String medicine_name) {
        TextView textView=itemView.findViewById(R.id.medicineName);
        textView.setText(medicine_name);
    }

    void setPrice(long medicine_price) {
        TextView textView=itemView.findViewById(R.id.price);
        textView.setText("₹ "+medicine_price);
    }

    void setCompanyName(String medicine_company) {
        TextView textView=itemView.findViewById(R.id.companyName);
        textView.setText("By "+medicine_company);
    }


    void setMedicineBoxListener(MedicineModel model, Fragment fragment) {
        Bundle bundle=new Bundle();
        bundle.putSerializable("DATA",model);
        itemView.findViewById(R.id.color).setOnClickListener(v -> {
            Intent intent=new Intent(itemView.getContext(), MedicineDetailsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("MEDICINE_ID",model.getMedicine_id());
            intent.putExtra("MEDICINE_NAME",model.getMedicine_name());
            itemView.getContext().startActivity(intent);
        });

    }

    void OpenAllCategory(Fragment fragment) {
        itemView.findViewById(R.id.parent).setOnClickListener(v -> {
            NavHostFragment.findNavController(fragment)
                    .navigate(R.id.action_navigation_home_to_navigation_category);
        });
    }

    public void setBoxListener2(String name,String key,String value) {
        itemView.findViewById(R.id.parent).setOnClickListener(v -> {
            Intent intent=new Intent(itemView.getContext(), ShowMedicinesActivity.class);
            intent.putExtra("DATA",key);
            intent.putExtra("name",name);
            intent.putExtra("key",key);
            intent.putExtra("value",value);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            itemView.getContext().startActivity(intent);
        });
    }
}
