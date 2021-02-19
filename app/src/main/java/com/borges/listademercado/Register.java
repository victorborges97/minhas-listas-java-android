package com.borges.listademercado;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

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

        mRegisterbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String email = mEmail.getText().toString().trim();
            }
        });




        View fab = findViewById(R.id.text_to_login);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(Register.this, Login.class);
                startActivity(intent);
            }
        });
    }
}