package com.borges.listademercado;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Register extends AppCompatActivity {
    EditText mFullName, mEmail, mPassword, mCPassword;
    Button mRegisterbtn;
    TextView mLoginBtn;
    FirebaseAuth Auth;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFullName = findViewById(R.id.input_text_nome);
        mEmail = findViewById(R.id.input_text_email);
        mPassword = findViewById(R.id.input_text_password);
        mCPassword = findViewById(R.id.input_text_confirmPassword);

        mRegisterbtn = findViewById(R.id.botao_registrar);
        mLoginBtn = findViewById(R.id.input_text_nome);

        Auth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

        if(Auth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        mRegisterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                String cPassword = mCPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    mEmail.setError("Email is Required.");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is Required.");
                    return;
                }
                if(password.length() < 6){
                    mPassword.setError("Password Must be >= 6 Characters");
                    return;
                }
                if(!password.equals(cPassword)) {
                    mPassword.setError("Passwords are not the same");
                    mCPassword.setError("Passwords are not the same");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);

                //register the user in Firebase

                Auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Register.this, "User Created.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(Register.this, "Error ! " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });




        View fab = findViewById(R.id.text_to_login);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });
    }
}