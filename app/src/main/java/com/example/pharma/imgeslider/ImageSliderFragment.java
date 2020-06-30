package com.example.pharma.imgeslider;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pharma.R;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

public class ImageSliderFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.top_imageview, container, false);
        ViewPager viewPager = v.findViewById(R.id.viewPager);
        viewPager.setClipToPadding(false);
        viewPager.setPageMargin(dpToPx(15));

        try {
            ArrayList<String> list= getArguments() != null ? getArguments().getStringArrayList("DATA") : new ArrayList<String>();
            SliderAdapter adapter = new SliderAdapter(getActivity(),viewPager,list);
            viewPager.setAdapter(adapter);
        }catch (Exception ignored){}
        return v;
    }
    private int dpToPx(int dp) {
        Resources r = Objects.requireNonNull(getContext()). getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
