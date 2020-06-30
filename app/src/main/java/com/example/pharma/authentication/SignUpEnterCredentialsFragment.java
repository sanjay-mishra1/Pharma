package com.example.pharma.authentication;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.pharma.R;
import com.example.pharma.firebase.FirebaseCustomAuth;
import com.example.pharma.model.SignUpModel;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

public class SignUpEnterCredentialsFragment extends Fragment {
    private EditText editFName;
    private EditText editLName;
    private EditText editEmail;
    private EditText editPass;
    private ProgressBar progressbar;
    private Button button;
//    private FirebaseApp firebaseApp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.signup_fragment_enter_credentials, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        firebaseApp=new FirebaseCustomAuth().loadFirebase(getContext(),String.valueOf(System.currentTimeMillis()));
        editFName = Objects.requireNonNull(getView()). findViewById(R.id.editFName);
        editLName = Objects.requireNonNull(getView()). findViewById(R.id.editLName);
        editEmail = Objects.requireNonNull(getView()). findViewById(R.id.editEmail);
        editPass = Objects.requireNonNull(getView()). findViewById(R.id.editpassword);
        progressbar= Objects.requireNonNull(getView()). findViewById(R.id.action_button_progressbar);
        button=view.findViewById(R.id.actionButton);
        button.setText("Next");
        getView().findViewById(R.id.signup_bar).setVisibility(View.GONE);
        button.setOnClickListener(view1 -> checkFields());
        view.findViewById(R.id.signin).setOnClickListener(v -> {
            startActivity(new Intent(getActivity(),LoginMobileActivity.class));
        });
    }

    private void checkFields() {
         String email = editEmail.getText().toString().trim();
         String password = editPass.getText().toString().trim();
         String fName=editFName.getText().toString().trim();
         String lName=editLName.getText().toString().trim();
        if (fName.isEmpty()) {
            showAlertDialog("Enter your first name");
            editFName.requestFocus();
            return;
        }
        if (lName.isEmpty()) {
            showAlertDialog("Enter your last name");
            editLName.requestFocus();
            return;
        }

         if (email.isEmpty()) {
            showAlertDialog("Email is required");
            editEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showAlertDialog("Please enter a valid email");
            editEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            showAlertDialog("Password is required");
            editPass.requestFocus();
            return;
        }

        if (password.length() < 6) {
            showAlertDialog("Minimum length of password should be 6");
            editPass.requestFocus();
            return;
        }
        chekUser(email,fName+" "+lName,password);
    }
    private void chekUser(String email,String name,String password) {
        button.setText("");
        progressbar.setVisibility(View.VISIBLE);
        FirebaseDatabase.getInstance().getReference("Customers").
                orderByChild("Email").startAt(email).endAt(email+"\uf8ff").
                addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Objects.requireNonNull(getView()). findViewById(R.id.action_button_progressbar).setVisibility(View.GONE);
                            if (dataSnapshot.getChildrenCount() == 0) {
                                Bundle bundle = new Bundle();
                                SignUpModel model=new SignUpModel();
                                model.setEmail(email);
                                model.setName(name);
                                model.pwd=password;
                                bundle.putSerializable("SIGNUP_CRED",model);
                                NavHostFragment.findNavController(SignUpEnterCredentialsFragment.this)
                                        .navigate(R.id.action_FirstFragment_to_SecondFragment,bundle);
                            }else showAlertDialog("Email id already exist.");
                        progressbar.setVisibility(View.GONE);
                        button.setText(R.string.signup);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        progressbar.setVisibility(View.GONE);
                        button.setText(R.string.signup);
                        showAlertDialog("An error occurred");
                    }
                });
    }


    private void showAlertDialog(String msg) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setMessage(msg);
        alert.setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss());
        //alert.setOnDismissListener(dialogInterface -> Log.e("Alert dialog","Dismiss***********************"));

        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

}
