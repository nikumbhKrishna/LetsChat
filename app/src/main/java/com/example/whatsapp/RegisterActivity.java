package com.example.whatsapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private Button CreateAccountButton;
    private EditText UserEmail , UserPassword;
    private TextView AlreadyHaveAccountLink;

    private FirebaseAuth mAuth;
    private DatabaseReference RootRef;

    private ProgressDialog lodingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        InitializeFields();

        mAuth = FirebaseAuth.getInstance();
        RootRef = FirebaseDatabase.getInstance().getReference();

        AlreadyHaveAccountLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToLoginActivity();
            }
        });

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CreateNewAccount();
            }
            
        });

        
    }

    private void CreateNewAccount() {

        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please Enter the Email....", Toast.LENGTH_SHORT).show();
        }

        if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please Enter the Password....", Toast.LENGTH_SHORT).show();
        }

        else {
            lodingBar.setTitle("Creating New Account");
            lodingBar.setMessage("Please Wait , While we are creating new Account for you...");
            lodingBar.setCanceledOnTouchOutside(true);
            lodingBar.show();

            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()){
                        String currentUserID = mAuth.getCurrentUser().getUid();
                        RootRef.child("Users").child(currentUserID).setValue("");

                        SendUserToMainActivity();
                        Toast.makeText(RegisterActivity.this, "Account Created Sucessfully ...", Toast.LENGTH_SHORT).show();
                        lodingBar.dismiss();
                    }
                    else{
                        String message = task.getException().toString();
                        Toast.makeText(RegisterActivity.this, "Error" + message, Toast.LENGTH_SHORT).show();
                        lodingBar.dismiss();

                    }
                }
            });
        }
    }

    private void InitializeFields() {

        CreateAccountButton = (Button) findViewById(R.id.register_button);
        UserEmail = (EditText) findViewById(R.id.register_email);
        UserPassword = (EditText) findViewById(R.id.register_password);
        AlreadyHaveAccountLink = (TextView) findViewById(R.id.already_have_account_link);
        lodingBar = new ProgressDialog(this);

    }

    private void SendUserToLoginActivity() {

        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

    private void SendUserToMainActivity() {

        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}