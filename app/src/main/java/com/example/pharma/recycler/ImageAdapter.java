package com.example.pharma.recycler;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.pharma.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter. ViewHolder> {
    private ViewPager2 viewpager;
    private String from;
    private Context context;
    private ArrayList<String> imgLinks;

    public ImageAdapter(Context context, ArrayList<String> imgLinks){
        this.context=context;
        this.imgLinks=imgLinks;
        this.from="zoom";
    }

    public ImageAdapter(Context context, ArrayList<String> imgLinks, ViewPager2 viewPager) {
        this.context=context;
        this.imgLinks=imgLinks;
        this.from="simple";
        this.viewpager=viewPager;
    }

    public ImageAdapter(Context context, ArrayList<String> imgLinks, String from) {
        this.context=context;
        this.imgLinks=imgLinks;
        this.from=from;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (from.equalsIgnoreCase("zoom"))
            return    new ViewHolder(LayoutInflater.from(context).inflate(R.layout.zoomage,parent,false));
        else //if (from.equalsIgnoreCase("simple"))
            return    new ViewHolder(LayoutInflater.from(context).inflate(R.layout.simple_imageview,parent,false));
        //else return    new ViewHolder(LayoutInflater.from(context).inflate(R.layout.edit_image_list,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ImageView imageView=holder.itemView.findViewById(R.id.imageView);
        String img=imgLinks.get(position);
        holder.setImage(img,imageView);
             if (!from.equalsIgnoreCase("edit_img"))
                   holder.setBoxListener(viewpager,position);
//             else{
//                 if (!img.startsWith("https")){
//                 TextView textView=holder.itemView.findViewById(R.id.img_text);
//                 textView.setText(R.string.waiting_to_upload);
//                 textView.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.ic_waiting),null,null,null);
//                textView.setTextColor(holder.itemView.getResources().getColor(R.color.gray));
//             }
//                 ImageView homeImage=holder.itemView.findViewById(R.id.mainimage);
//                 if (position==0){
//                    Glide.with(holder.itemView).load(R.drawable.ic_home_blue_24dp).into(homeImage);
//                 }
//                 homeImage.setOnClickListener(v -> {
//                     imgLinks.remove(img);
//                     imgLinks.add(0,img);
//                     notifyDataSetChanged();
//                 });
//                 holder.setDelListener(position,img);
//             }

    }

    @Override
    public int getItemCount() {
        return imgLinks.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void setImage(String s, ImageView imageView) {
            Glide.with(itemView).load(s).into(imageView);
        }
        void setBoxListener(ViewPager2 viewpager, int position) {
           try {
               itemView.findViewById(R.id.card_view).setOnClickListener(v -> viewpager.setCurrentItem(position));
           }catch (Exception ignored){}
        }

        public void setDelListener(int position,String img) {
//            itemView.findViewById(R.id.cancel).setOnClickListener(v -> {
//                imgLinks.remove(img);
//                notifyDataSetChanged();
//            });
        }
    }

}

