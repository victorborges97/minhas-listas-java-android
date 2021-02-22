
package com.borges.listademercado;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import java.sql.Struct;
import java.util.Objects;

public class Login extends AppCompatActivity {
    private static final String TAG = "LOGIN";
    EditText mEmail, mPassword;
    Button mLogin;
    SignInButton mSignInGoogle;
    private FirebaseAuth Auth;
    private static final int RC_SIGN_IN = 1000;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;
    GoogleSignInAccount account;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.getSupportActionBar().hide();

        // Obtain the FirebaseAnalytics instance.
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Auth = FirebaseAuth.getInstance();

        verificarSeTemUsuarioLogadoComEmailSenha(Auth);

        mLogin = findViewById(R.id.botao_login);
        mSignInGoogle = findViewById(R.id.google_sign_in);
        progressBar = findViewById(R.id.progressBar);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mSignInGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmail = findViewById(R.id.input_text_email);
                mPassword = findViewById(R.id.input_text_password);

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

    @Override
    protected void onStart() {
        super.onStart();
        account = GoogleSignIn.getLastSignedInAccount(this);

        if(account != null){
            Log.i(TAG, String.valueOf(account.getEmail()));
            gotoProfile();
        }
    }

    private void verificarSeTemUsuarioLogadoComEmailSenha(FirebaseAuth Auth) {
        if(Auth.getCurrentUser() != null){
            gotoProfile();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            try {
                Task<GoogleSignInAccount> result = GoogleSignIn.getSignedInAccountFromIntent(data);
                GoogleSignInAccount signInAcc = result.getResult(ApiException.class);
                firebaseAuth(signInAcc);
            } catch (ApiException e) {
                Log.w(TAG, "ApiException Error: " + e.getStatusCode());
            }
        }

    }

    private void firebaseAuth(GoogleSignInAccount signInAcc) {
        Auth = FirebaseAuth.getInstance();
        String tokenGoogle = signInAcc.getIdToken();
        AuthCredential authCredential = GoogleAuthProvider.getCredential(tokenGoogle, null);

        Auth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Your Google Account is Connected to Our Application.", Toast.LENGTH_SHORT).show();
                    gotoProfile();
                } else {
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    Toast.makeText(getApplicationContext(), "Authentication Failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void gotoProfile() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish(); /*Esse porrinha faz não voltar para a tela anterior*/
    }


}