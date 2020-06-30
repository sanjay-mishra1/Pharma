package com.example.pharma.orders.prescription;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pharma.R;
import com.example.pharma.model.ImageBasedModel;
import com.example.pharma.recycler.ImageBasedListAdapter;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class LoadMyDataFragment extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.empty_recycler_layout, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayList<ImageBasedModel> data= null;
        if (getArguments() != null) {
            data = (ArrayList<ImageBasedModel>) getArguments().getSerializable("DATA");
            String from = getArguments().getString("FROM");
            RecyclerView recyclerView = getView().findViewById(R.id.recycle);
            LinearLayoutManager mLayoutManager =
                    new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(mLayoutManager);
            ImageBasedListAdapter adapter = new ImageBasedListAdapter(getContext(), data, from);
            recyclerView.setAdapter(adapter);
        }
    }

}
