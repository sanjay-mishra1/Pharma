package com.example.pharma.medicine;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

public class CustomViewPager extends ViewPager {


    public CustomViewPager(@NonNull Context context) {
        super(context);
    }

    public CustomViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int mode=MeasureSpec.getMode(heightMeasureSpec);
        if(mode==MeasureSpec.UNSPECIFIED||mode==MeasureSpec.AT_MOST){
            super.onMeasure(widthMeasureSpec,heightMeasureSpec);
            int height=0;
            for(int i=0;i<getChildCount();i++){
                View child=getChildAt(i);
                child.measure(widthMeasureSpec,MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED));
                int h=child.getMeasuredHeight();
                if(h>height) height=h;
            }
            heightMeasureSpec=MeasureSpec.makeMeasureSpec(0,MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
    }
}
