package com.example.pharma.ui.user;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.pharma.R;
import com.example.pharma.authentication.LoginMobileActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class UserFragment extends Fragment {

    private UserViewModel notificationsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_user, container, false);
        root.findViewById(R.id.logout).setOnClickListener(v -> {
            logout();
        });
        TextView nameText=root.findViewById(R.id.username);
        TextView mobileText=root.findViewById(R.id.userphone);
        TextView emailText=root.findViewById(R.id.useremail);
        return root;
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent=new Intent(getActivity(), LoginMobileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        Objects.requireNonNull(getActivity()).startActivity(intent);
    }
}
