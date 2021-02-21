
package com.borges.listademercado;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.GoogleSignatureVerifier;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Objects;

public class Login extends AppCompatActivity {
    private static final String TAG = "LOGIN";
    EditText mEmail, mPassword;
    Button mLogin;
    SignInButton mSignInGoogle;
    private FirebaseAuth Auth;
    private static final int RC_SIGN_IN = 1;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount account;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.getSupportActionBar().hide();
        Auth = FirebaseAuth.getInstance();

        verificarSeTemUsuarioLogadoComEmailSenha(Auth);

        mEmail = findViewById(R.id.input_text_email);
        mPassword = findViewById(R.id.input_text_password);
        mLogin = findViewById(R.id.botao_login);
        mSignInGoogle = findViewById(R.id.google_sign_in);
        progressBar = findViewById(R.id.progressBar);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        account = GoogleSignIn.getLastSignedInAccount(this);

        if(account != null){
            Log.i(TAG, String.valueOf(account.getEmail()));
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish(); /*Esse porrinha faz não voltar para a tela anterior*/
        }

        mSignInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });


        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();
                isSignInGoing(true);
                blocksFields(true);

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

                progressBar.setVisibility(View.VISIBLE);

                //login the user in Firebase

                Auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){
                            Toast.makeText(Login.this, "Logged in Successfully.", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish(); /*Esse porrinha faz não voltar para a tela anterior*/
                        } else {
                            isSignInGoing(false);
                            blocksFields(false);
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(Login.this, "Error ! " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }

                });
            }
        });

        View fab = findViewById(R.id.text_to_login);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Register.class));
            }
        });

    }

    private void verificarSeTemUsuarioLogadoComEmailSenha(FirebaseAuth Auth) {
        if(Auth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish(); /*Esse porrinha faz não voltar para a tela anterior*/
            return;
        }

    }

    private void blocksFields(boolean b) {
        mLogin.setEnabled(!b);
        mPassword.setEnabled(!b);
        mEmail.setEnabled(!b);
    }

    private void isSignInGoing(boolean b) {
        String email = "";
        if(b) {
            email = "Entrando...";
        } else {
            email = "Entrar";
        }
        mLogin.setText(email);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (RC_SIGN_IN == requestCode) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish(); /*Esse porrinha faz não voltar para a tela anterior*/
            }
        }
    }


}