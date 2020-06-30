package com.example.pharma.medicine_info;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.example.pharma.R;
import com.example.pharma.imgeslider.SliderAdapter;
import com.example.pharma.recycler.ImageAdapter;
import com.example.pharma.recycler.ViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

public class ImageSliderFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.image_slider_layout, container, false);


        try {
            ArrayList<String> uri=new ArrayList<>();
            Adapter adapter;
            if (Objects.requireNonNull(getArguments()).getStringArrayList("DATA")!=null)
                uri=getArguments().getStringArrayList("DATA");
            if (getArguments().getBoolean("FULL"))
            {   Log.e("Viewpager","2");
                ViewPager2 viewPager = v.findViewById(R.id.viewPager2);
                viewPager.setVisibility(View.GONE);
                viewPager.setVisibility(View.VISIBLE);
                ImageAdapter sliderAdapter = new ImageAdapter(getActivity(),uri);
                viewPager.setAdapter(sliderAdapter);
                adapter=new Adapter(uri,viewPager);
            }else{
                Log.e("Viewpager","1");
                ViewPager viewPager = v.findViewById(R.id.viewPager);
                SliderAdapter sliderAdapter = new SliderAdapter(getActivity(),viewPager,uri);
                viewPager.setAdapter(sliderAdapter);
                adapter=new Adapter(uri,viewPager);
                ImageView[] imageViews=new ImageView[uri.size()];
                try {
                    LinearLayout layout=v.findViewById(R.id.pointerLinear);
                    for(int i=0;i<uri.size();i++){
                        ImageView imageView=new ImageView(getContext());
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.dot));
                        layout.addView(imageView);
                        imageViews[i]=imageView;
                    }
                }catch (Exception e){e.printStackTrace();}
                imageViews[0].setImageDrawable(getResources().getDrawable(R.drawable.dot_blue));
                viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                        //changePointerColorOnPageChange(imageViews,position);

                    }

                    @Override
                    public void onPageSelected(int position) {
                        changePointerColorOnPageChange(imageViews,position);
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });


            }
//            RecyclerView recyclerView=v.findViewById(R.id.listview);
//            LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
//            recyclerView.setLayoutManager(linearLayoutManager);
//            recyclerView.setAdapter(adapter);
        }catch (Exception e){e.printStackTrace();}
        return v;
    }

    void changePointerColorOnPageChange(ImageView[] imageViews,int position){
        try{ImageView previousImage;
            ImageView nextImage;
            if (position==0)
                previousImage=null;
            else previousImage=imageViews[position-1];
            if (position==imageViews.length-1)
                nextImage=null;
            else nextImage=imageViews[position+1];


            imageViews[position].setImageDrawable(getResources().getDrawable(R.drawable.dot_blue));
            if (previousImage!=null)
                previousImage.setImageDrawable(getResources()
                        .getDrawable(R.drawable.dot));
            if (nextImage!=null)
                nextImage.setImageDrawable(getResources().
                        getDrawable(R.drawable.dot));

        }catch (Exception e){e.printStackTrace();}

    }

    class Adapter extends RecyclerView.Adapter<ViewHolder>{
        private ViewPager2 viewPager2;
        private  List<String> imgList;
        private ViewPager viewPager;

        Adapter(List<String> imgList, ViewPager2 viewPager2){
            this.imgList=imgList;
            this.viewPager2=viewPager2;
        }

        Adapter(ArrayList<String> imgList, ViewPager viewPager) {
            this.imgList=imgList;
            this.viewPager=viewPager;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.simple_imageview, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            ImageView imageView= holder.itemView.findViewById(R.id.imageView);
            Glide.with(Objects.requireNonNull(getContext())).load(imgList.get(position)).into(imageView);
            imageView.setOnClickListener(v -> {
                if (viewPager!=null)
                viewPager.setCurrentItem(position,true);
                else viewPager2.setCurrentItem(position,true);
            });
        }

        @Override
        public int getItemCount() {
            return imgList.size();
        }
    }

}
