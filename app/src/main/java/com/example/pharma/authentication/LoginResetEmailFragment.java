package com.example.pharma.authentication;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.pharma.R;
import com.example.pharma.firebase.FirebaseCustomAuth;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

public class LoginResetEmailFragment extends Fragment {
   private EditText editTextEmail;
    private Button bt;
    private ProgressBar progressBar;
//    private FirebaseApp firebaseApp;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.login_email_fragment_reset_email, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        firebaseApp=new FirebaseCustomAuth().loadFirebase(getContext(),String.valueOf(System.currentTimeMillis()));
        bt=view.findViewById(R.id.actionButton);
        editTextEmail=view.findViewById(R.id.editEmail);
        progressBar= Objects.requireNonNull(getView()).findViewById(R.id.action_button_progressbar);
        bt.setText(R.string.request_password_reset);
        bt.setOnClickListener(view1 -> {
            String email= editTextEmail.getText().toString();
            Button bt=(Button) view1;
            if (email.isEmpty()){
                editTextEmail.requestFocus();
                showAlertDialog(false,"Please enter the email id to reset your password.");
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showAlertDialog(false,"Please enter a valid email id");
                editTextEmail.requestFocus();
                return;
            }

                bt.setText("");
                progressBar.setVisibility(View.VISIBLE);
                view1.setClickable(false);
                FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnSuccessListener(aVoid -> {
                    view1.setClickable(true);
                    progressBar.setVisibility(View.GONE);
                    bt.setText(R.string.reset_mail_send);
                    editTextEmail.setText("");
                    showAlertDialog(true," A mail is sent to your email id "+email+". Please reset your password and try to login again." );
                }).addOnFailureListener(e -> {
                    view1.setClickable(true);
                    progressBar.setVisibility(View.GONE);
                    bt.setText(R.string.request_password_reset);
                    e.printStackTrace();
                    showAlertDialog(false,"Error in sending password reset mail to your email address. \n"+e.getMessage() );
                });

        });
    }

    private void showAlertDialog(boolean sendEmail, String msg) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Reset Password");
        alert.setMessage(msg);
        if (!sendEmail){
            alert.setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss());
        }else{
            if (msg.contains("mail is sent")){
                alert.setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss());
            }else {
                alert.setPositiveButton("Send", (dialogInterface, i) -> dialogInterface.dismiss());
                alert.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            }
        }



        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

}
