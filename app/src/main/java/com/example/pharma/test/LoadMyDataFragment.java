package com.example.pharma.test;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pharma.GridSpacingItemDecoration;
import com.example.pharma.R;
import com.example.pharma.model.ImageBasedModel;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LoadMyDataFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.simple_recyclerview, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayList<ImageBasedModel> data;
        if (getArguments() != null) {
            data = (ArrayList<ImageBasedModel>) getArguments().getSerializable("DATA");
            if (data!=null) {
                view.findViewById(R.id.progressbar).setVisibility(View.GONE);
                String from = getArguments().getString("FROM");
                boolean type=getArguments().getBoolean("TYPE",true);
                Log.e("FROM","->"+from);
                RecyclerView recyclerView = Objects.requireNonNull(getView()).findViewById(R.id.recycler_variant);
                if (type) {
                    LinearLayoutManager gridManager =
                            new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
                    recyclerView.setLayoutManager(gridManager);
                }else{
                    RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 3);
                    recyclerView.addItemDecoration(new
                            GridSpacingItemDecoration(3, dpToPx(10), true));
                    recyclerView.setLayoutManager(mLayoutManager);
                }
                TestAdapter adapter = new TestAdapter(getActivity(), data, from);
                recyclerView.setAdapter(adapter);
            }else{
                TextView textView=view.findViewById(R.id.progressbar_title);
                textView.setText(R.string.loading);
            }
        }
    }
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }
}
