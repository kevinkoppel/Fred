package com.example.myfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    ProgressBar progressBar;
    EditText editTextEmail, editTextPassword;
    private FirebaseAuth mAuth;
    TextView goToRegistration;

    @Override
    public void onBackPressed() {
        return;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        progressBar = (ProgressBar) findViewById(R.id.progressbar);
        goToRegistration = findViewById(R.id.go_to_signup);



        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.loginButton).setOnClickListener(this);
        findViewById(R.id.go_to_signup).setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginButton:
                String email = editTextEmail.getText().toString();
                String password = editTextPassword.getText().toString();
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d("login", "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Intent startIntent = new Intent(getApplicationContext(), MainActivity.class);
                                    startActivity(startIntent);
                                }
                                else {
                                    // If sign in fails, display a message to the user.
                                    Log.w("loginfailed", "signInWithEmail:failure", task.getException());
                                    Toast.makeText(getApplicationContext(), "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();


                                }

                            }
                        });

               /* FirebaseUser currentUser = mAuth.getCurrentUser();
                if(currentUser != null){
                    startActivity(new Intent(this, SignUpActivity.class));
                }else{
                    Toast.makeText(getApplicationContext(), "Vale email või salasõna", Toast.LENGTH_SHORT).show();
                }*/


                break;

            case R.id.go_to_signup:
                finish();
                startActivity(new Intent(this, SignUpActivity.class));
                break;



        }
    }
}
