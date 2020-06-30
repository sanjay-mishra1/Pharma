package com.example.pharma.orders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pharma.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 */
public class ShowOrdersFragment extends Fragment {

    public ShowOrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_show_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupTabs();
    }

    private void setupTabs(){
        ViewPager viewPager = Objects.requireNonNull(getView()). findViewById(R.id.view_pager);
        TabLayout tabs =getView(). findViewById(R.id.tabs);
        tabs.addTab(tabs.newTab().setText("Active"));
        tabs.addTab(tabs.newTab().setText("All"));
        tabs.setTabGravity(TabLayout.GRAVITY_FILL);
        try {
           ArrayList<HashMap<String,Object>> activeOrders= (ArrayList<HashMap<String, Object>>) getArguments().getSerializable("ACTIVE_ORDER");
            ArrayList<HashMap<String,Object>> allOrders= (ArrayList<HashMap<String, Object>>) getArguments().getSerializable("ALL_ORDER");
        MyAdapter sectionsPagerAdapter = new MyAdapter(
                Objects.requireNonNull(getActivity()). getSupportFragmentManager(),
                activeOrders,allOrders);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.e("Tab","Selected"+"=>"+tab.getPosition());
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        }catch (Exception e){e.printStackTrace();}

    }
    public  class MyAdapter extends FragmentPagerAdapter {
        private final ArrayList<HashMap<String, Object>> activeOrder;
        private final ArrayList<HashMap<String, Object>> allOrder;

        MyAdapter(@NonNull FragmentManager fm, ArrayList<HashMap<String, Object>> activeOrders, ArrayList<HashMap<String, Object>> allOrders) {
            super(fm);
            this.activeOrder=activeOrders;
            this.allOrder=allOrders;
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            Log.e("GetItem","Position"+position);
            if (position==0)
            {   Fragment fragment=new ActiveOrdersFragment();
                Bundle bundle=new Bundle();
                bundle.putSerializable("ACTIVE_ORDER",activeOrder);
                fragment.setArguments(bundle);
                return fragment;
            }
            else{   Fragment fragment=new AllOrdersFragment();
                Bundle bundle=new Bundle();
                bundle.putSerializable("ALL_ORDER",allOrder);
                fragment.setArguments(bundle);
                return fragment;
            }

        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}
