package com.example.pharma.medicine;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pharma.R;
import com.example.pharma.medicine.ui.main.SectionsPagerAdapter;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
public class MedicineMainFragment extends Fragment {
    private View rootview;
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
            rootview= inflater.inflate(R.layout.fragment_medicine_main, container, false);
            return rootview;
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getContext(),
                Objects.requireNonNull(getActivity()). getSupportFragmentManager());
        ViewPager viewPager = Objects.requireNonNull(rootview). findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs =getView(). findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

    }
    }
