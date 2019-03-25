package com.example.annmargaret.androidblockchain.auth;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.annmargaret.androidblockchain.activities.BlockchainActivity;
import com.example.annmargaret.androidblockchain.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    EditText email,password;
    Button registerButton,loginButton, forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        email = (EditText) findViewById(R.id.regEmail);
        password = (EditText) findViewById(R.id.regPW);
        registerButton = (Button) findViewById(R.id.regButton);
        loginButton = (Button) findViewById(R.id.regLoginButton);
        forgotPassword = (Button) findViewById(R.id.regForgotPWButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email_input = email.getText().toString();
                String password_input = password.getText().toString();

                if(email.getText() == null || password.getText() == null) {
                    Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), R.string.reg_no_email, Toast.LENGTH_SHORT).show();

                }
                else if(TextUtils.isEmpty(email_input)) {
                    Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    Toast.makeText(getApplicationContext(), R.string.reg_no_email, Toast.LENGTH_SHORT).show();

                }
                else if(TextUtils.isEmpty(password_input)) {
                    Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                    Toast.makeText(getApplicationContext(), R.string.reg_no_psswd, Toast.LENGTH_SHORT).show();

                }
                else if(password_input.length() < 6) {
                    password.setText("");
                    Toast.makeText(getApplicationContext(), R.string.reg_psswd_short, Toast.LENGTH_SHORT).show();

                }
                else {
                    mAuth.createUserWithEmailAndPassword(email_input, password_input)
                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        startActivity(new Intent(getApplicationContext(), BlockchainActivity.class));
                                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                                        //updateUI(user);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(RegisterActivity.this, R.string.reg_failure,
                                                Toast.LENGTH_SHORT).show();

                                    }

                                }
                            });
                }



            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}
