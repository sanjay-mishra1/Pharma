package com.example.pharma;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jsibbold.zoomage.ZoomageView;

import java.util.ArrayList;
import java.util.Objects;

public class view_image_layout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        setTitle("");
        ViewPager2 viewPager=findViewById(R.id.viewPager);
        ArrayList<String> images=new ArrayList<>();
        images=getIntent().getStringArrayListExtra("DATA");
//        images.add("https://images.pexels.com/photos/806427/pexels-photo-806427.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940");
//        images.add("https://images.pexels.com/photos/3652097/pexels-photo-3652097.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940");
//        images.add("https://images.pexels.com/photos/208512/pexels-photo-208512.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940");
//        images.add("https://images.pexels.com/photos/593451/pexels-photo-593451.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940");
//        images.add("https://images.pexels.com/photos/606506/pexels-photo-606506.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940");

        if (images!=null) {
            ImageAdapter adapter = new ImageAdapter(this, images);
            viewPager.setAdapter(adapter);

            ImageAdapter adapter1 = new ImageAdapter(this, images, viewPager);
            RecyclerView recyclerView = findViewById(R.id.recycle);
            LinearLayoutManager mLayoutManager =
                    new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setAdapter(adapter1);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getWindow().setStatusBarColor(Color.WHITE);
            }
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    class ImageAdapter extends RecyclerView.Adapter<ViewHolder> {
        private  ViewPager2 viewpager;
        private String from;
        private Context context;
        private ArrayList<String> imgLinks;

        ImageAdapter(Context context, ArrayList<String> imgLinks){
            this.context=context;
            this.imgLinks=imgLinks;
            this.from="zoom";
        }

        ImageAdapter(Context context, ArrayList<String> imgLinks, ViewPager2 viewPager) {
            this.context=context;
            this.imgLinks=imgLinks;
            this.from="simple";
            this.viewpager=viewPager;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (from.equalsIgnoreCase("zoom"))
            return    new ViewHolder(LayoutInflater.from(context).inflate(R.layout.zoomage,parent,false));
            else return    new ViewHolder(LayoutInflater.from(context).inflate(R.layout.simple_imageview,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ImageView imageView=holder.itemView.findViewById(R.id.imageView);
                holder.setImage(imgLinks.get(position),imageView);
                if (!from.equalsIgnoreCase("zoom"))
                    holder.setBoxListener(viewpager,position);
        }

        @Override
        public int getItemCount() {
            return imgLinks.size();
        }
    }
    class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void setImage(String s, ImageView imageView) {
            Glide.with(itemView).load(s).into(imageView);
        }
        void setBoxListener(ViewPager2 viewpager, int position) {
            itemView.findViewById(R.id.card_view).setOnClickListener(v -> viewpager.setCurrentItem(position));
        }
    }
}
