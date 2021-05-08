package com.example.videostreamer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
Button registerButton;
EditText email,username,password;
FirebaseAuth mAuth;
DatabaseReference mDatabase;
TextView loginTxt;
private static final int STORAGE_PERMISSION_CODE = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);

        loginTxt=findViewById(R.id.loginTxtView);
        registerButton=findViewById(R.id.register);
        email=findViewById(R.id.username);
        password=findViewById(R.id.password);
        mAuth=FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Users");
        loginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "REGISTERING...", Snackbar.LENGTH_LONG).show();
                final String uname = email.getText().toString().trim();
                final String passwrd = password.getText().toString().trim();
                if(!TextUtils.isEmpty(uname) && !TextUtils.isEmpty(uname)&&!TextUtils.isEmpty(passwrd)){
                    mAuth.createUserWithEmailAndPassword(uname,passwrd).addOnCompleteListener(RegisterActivity.this,new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                String user_id = mAuth.getCurrentUser().getUid();
                                DatabaseReference dbRef = mDatabase.child(user_id);
                                dbRef.child("Username").setValue(username);
                                dbRef.child("Image").setValue("Default");
                                Toast.makeText(RegisterActivity.this, "Account Registered", Toast.LENGTH_SHORT).show();
                                Intent regIntent = new Intent(RegisterActivity.this, ProfileActivity.class);
                                regIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(regIntent);
                            }
                            else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(RegisterActivity.this,"Registration Failed either due to Weak Password(use combination of alphabets and numbers) or the Username already exists.",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else {
                    Toast.makeText(RegisterActivity.this,"InComplete Fields",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    // Function to check and request permission.
    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(RegisterActivity.this, permission) == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(RegisterActivity.this,
                    new String[]{permission},
                    requestCode);
        }
    }
    // This function is called when the user accepts or decline the permission.
    // Request Code is used to check which permission called this function.
    // This request code is provided when the user is prompt for permission.

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super
                .onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(RegisterActivity.this,
                        "Storage Permission Granted",
                        Toast.LENGTH_SHORT)
                        .show();
            }
            else {
                Toast.makeText(RegisterActivity.this,
                        "Storage Permission Denied",
                        Toast.LENGTH_SHORT).show();
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, STORAGE_PERMISSION_CODE);
            }
        }
    }
}