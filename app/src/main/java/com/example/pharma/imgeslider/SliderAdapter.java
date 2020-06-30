package com.example.pharma.imgeslider;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.pharma.Constants;
import com.google.firebase.database.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;


public class SliderAdapter extends PagerAdapter {
    private float pageSize;
    private Context mContext;
    private ArrayList<String> mImage;
    private HashMap<String,String> imgData;
    public SliderAdapter(Context context, ViewPager viewPager, ArrayList<String> imgs) {
        mContext = context;
        mImage=imgs;
        pageSize=0.93f;
        extractDetails(viewPager);
    }
    public SliderAdapter(Context context, ViewPager viewPager, ArrayList<String> imgs, HashMap<String,String>img_data) {
        mContext = context;
        mImage=imgs;
        this.imgData=img_data;
        pageSize=0.93f;
        extractDetails(viewPager);
    }

    public SliderAdapter(Context context, ViewPager viewPager, ArrayList<String> imgs, boolean isBig) {
        mContext = context;
        mImage=imgs;
        pageSize=1f;
        extractDetails(viewPager);
    }

    @Override
    public float getPageWidth(int position) {
        return pageSize;
    }

    private void extractDetails(ViewPager viewPager){
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }


    @Override
    public int getCount() {
        return mImage.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull @NotNull View view, @NonNull @NotNull Object object) {
        return view == object;
    }

    @NonNull
    @NotNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(mContext);
        String s=mImage.get(position);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Glide.with(mContext).load(s).into(imageView);
        container.addView(imageView, 0);
        Log.e("SliderAdapter","->"+s);
        if (imgData!=null ){
            String action=imgData.get(s);
            if (action!=null&& !action.isEmpty())
                loadAction(action);
        }
        return imageView;
    }

    private void loadAction(String action) {
        Class className= Constants.getActionClassName(action);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return super.getItemPosition(object);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, @NonNull @NotNull Object object) {
        container.removeView((ImageView) object);
    }
}