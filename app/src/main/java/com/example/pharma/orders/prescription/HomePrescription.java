package com.example.pharma.orders.prescription;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pharma.ListActivityForImage;
import com.example.pharma.R;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

public class HomePrescription extends Fragment {

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.layout_for_pres_and_test, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.uploadPrescriptionFromCamera).setOnClickListener(v ->
                NavHostFragment.findNavController(HomePrescription.this)
                        .navigate(R.id.action_HomePrescription_to_cameraPrescription));
        view.findViewById(R.id.uploadPrescriptionFromGallery).setOnClickListener(v ->
                NavHostFragment.findNavController(HomePrescription.this)
                        .navigate(R.id.action_HomePrescription_to_galleryPrescription));
        view.findViewById(R.id.openMyPrescription).setOnClickListener(v -> {
            Intent intent=new Intent(getActivity(), ListActivityForImage.class);
            intent.putExtra("FROM","Prescriptions");
            Objects.requireNonNull(getActivity()).startActivity(intent);
        });

    }
    }
