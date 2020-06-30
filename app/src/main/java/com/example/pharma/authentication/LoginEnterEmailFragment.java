package com.example.pharma.authentication;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pharma.HomeActivity;
import com.example.pharma.R;
import com.example.pharma.firebase.FirebaseCustomAuth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import static android.content.Context.MODE_PRIVATE;

public class LoginEnterEmailFragment extends Fragment {
    private EditText editTextEmail;
    private EditText editTextPassword;
    private FirebaseAuth mAuth;
    private Button bt;
    private ProgressBar progressBar;
//    private FirebaseApp firebaseApp;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.login_email_fragment_enter_email, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        firebaseApp=new FirebaseCustomAuth().loadFirebase(getContext(),String.valueOf(System.currentTimeMillis()));
        mAuth=FirebaseAuth.getInstance();
        editTextEmail=view.findViewById(R.id.editEmail);
        editTextPassword=view.findViewById(R.id.editpassword);
        editTextPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handle=false;
                if(actionId== EditorInfo.IME_ACTION_GO)
                {
                    checkFields();
                    handle=true;
                }
                return handle;
            }
        });
        bt=view.findViewById(R.id.actionButton);
        progressBar=view.findViewById(R.id.action_button_progressbar);
        bt.setOnClickListener(v -> checkFields());
        view.findViewById(R.id.resetEmail).setOnClickListener(view1 -> NavHostFragment.findNavController(LoginEnterEmailFragment.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment));
        view.findViewById(R.id.signup).setOnClickListener(view1 -> startActivity(new Intent(getActivity(),SignupActivity.class)));
    }

    private void checkFields() {
        final String email = editTextEmail.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();
        if (email.isEmpty()) {
            showAlertDialog("Email is required");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showAlertDialog("Please enter a valid email");
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            showAlertDialog("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            showAlertDialog("Minimum length of password should be 6");
            editTextPassword.requestFocus();
            return;
        }
        bt.setText("");

        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    FirebaseDatabase.getInstance().getReference("Customers").orderByChild("email").equalTo(email)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            SharedPreferences sharedPreferences= Objects.requireNonNull(getContext()). getSharedPreferences("CRED",MODE_PRIVATE);
                            SharedPreferences.Editor edit=sharedPreferences.edit();
                            for(DataSnapshot d:dataSnapshot.getChildren()){
                                edit.putString("MOBILE", (String) d.child("mobile").getValue());
                                edit.putString("NAME", (String) d.child("name").getValue());
                                edit.putString("UID", d.getKey());
                                edit.putString("IMG", (String) d.child("user_img").getValue());
                                edit.apply();
                            }
                            Intent intent=new Intent(getActivity(), HomeActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                } else {
                    progressBar.setVisibility(View.GONE);
                    bt.setText("Login");
                    showAlertDialog(task.getException().getMessage());
                }
            }
        });

    }

    private void showAlertDialog( String msg) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle(R.string.signin);
        alert.setMessage(msg);
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }


}
