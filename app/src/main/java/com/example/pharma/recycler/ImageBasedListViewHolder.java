package com.example.pharma.recycler;

import android.content.Intent;
import android.graphics.Paint;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pharma.Constants;
import com.example.pharma.R;
import com.example.pharma.WebArticleActivity;
import com.example.pharma.model.ImageBasedModel;
import com.example.pharma.orders.ImageDataDetailActivity;
import com.example.pharma.test.ShowTestDetails;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ImageBasedListViewHolder extends RecyclerView.ViewHolder {
    private SimpleDateFormat dateFormat;
    private Calendar calendar;
    private TextView idTest;
    private TextView time;
    private TextView desc;
    private ImageView imageView;
    private View button;
    ImageBasedListViewHolder(@NonNull View itemView) {
        super(itemView);
        idTest=itemView.findViewById(R.id.title);
        desc=itemView.findViewById(R.id.Desc);
        time=itemView.findViewById(R.id.time);
        button=itemView.findViewById(R.id.actionButton);
        imageView=itemView.findViewById(R.id.imageView);
        dateFormat=new SimpleDateFormat("dd MMM yy hh:mm aa", Locale.UK);
        calendar= Calendar.getInstance();
    }

    public void setId(String id) {
        idTest.setText(id);
    }

    void setTime(String t) {
        time.setText(getFormattedTime(Long.parseLong(t)));
    }
    private String getFormattedTime(long time){
        calendar.setTimeInMillis(time);
        return "Added on "+dateFormat.format(calendar.getTime());
    }
    void setImage(ArrayList<String> images) {
        if (images!=null)
        Glide.with(itemView).load(images.get(0)).into(imageView);
    }

    void setStatus(long status) {
        if (ImageBasedModel.Approved==status) {
            desc.setText(R.string.approved);
            desc.setTextColor(itemView.getResources().getColor(R.color.colorAccent));
        }else if (ImageBasedModel.Pending==status) {
            desc.setText(R.string.pending);
            desc.setTextColor(itemView.getResources().getColor(R.color.colorPrimary));
        }else if (ImageBasedModel.Cancelled==status) {
            desc.setText(R.string.cancelled);
            desc.setTextColor(itemView.getResources().getColor(R.color.red));
        }

    }

    public void setName(String name) {
        desc.setText(name);
    }

    void setNote(String note) {
        desc.setText(note);
    }

    void setListeners(ImageBasedModel images, String from,String type) {
       button.setOnClickListener(v -> {
           Intent intent;
           if (type.contains("Test")) {
               intent = new Intent(itemView.getContext(), ShowTestDetails.class);
               intent.putExtra("DATA", from);
               intent.putExtra("MODEL", images);
           }
           else{
               intent=new Intent(itemView.getContext(), ImageDataDetailActivity.class);
               intent.putExtra("DATA",images);
               intent.putExtra("FROM",from);
               intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           }
           itemView.getContext().startActivity(intent);
       });

    }

    void setCardName(String name) {
        TextView textView=itemView.findViewById(R.id.card_title);
        textView.setText(name);
    }

    void setCardIcon(String icon) {
        Glide.with(itemView.getContext()).load(icon).into((ImageView) itemView.findViewById(R.id.card_img));
    }

    void setFromatedTime(String id) {
        time.setText(Constants.getDateInNumber(Long.parseLong(id)));
    }
    void setShare(String name, String url) {
        itemView.findViewById(R.id.share).setOnClickListener(v -> {
            Intent shareIntent=new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT,name);
            shareIntent.putExtra(Intent.EXTRA_TEXT,url);
            itemView.getContext().startActivity(Intent.createChooser(shareIntent,"Select App"));
        });
    }


    void setListeners(String name, String url) {
        button.setOnClickListener(v -> {
            Intent intent=new Intent(itemView.getContext(), WebArticleActivity.class);
            intent.putExtra("NAME",name);
            intent.putExtra("URL",url);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            itemView.getContext().startActivity(intent);
        });

    }

    void setPrice(long original_price, long price) {
        TextView priceText=itemView.findViewById(R.id.price);
        TextView originalText=itemView.findViewById(R.id.original_price);
        TextView discountText=itemView.findViewById(R.id.discount);
        priceText.setText(String.format(Locale.UK,"₹%d", price));
        if (original_price!=0&& price!=original_price) {
            originalText.setText(String.format(Locale.UK, "₹%d", original_price));
            originalText.setPaintFlags(originalText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            discountText.setText(String.format(Locale.UK, "Saved ₹%d", original_price-price));
        }
    }

    void setCardListeners(String name, String url,String id) {
        itemView.setOnClickListener(v -> {
            Intent intent=new Intent(itemView.getContext(), WebArticleActivity.class);
            intent.putExtra("NAME",name);
            intent.putExtra("URL",url);
            intent.putExtra("SAVE",true);
            intent.putExtra("ID",id);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            itemView.getContext().startActivity(intent);
        });
    }
}
