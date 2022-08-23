package com.thepixels.smartscan;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private final String[] permissions={"android.permission.CAMERA","android.permission.WRITE_EXTERNAL_STORAGE","android.permission.READ_EXTERNAL_STORAGE"};
    private EditText mEmail;
    private EditText mPassword;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hiding title and action bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_login);
        askUserPermission();
        firebaseAuthStateListener = firebaseAuth -> {
            user = FirebaseAuth.getInstance().getCurrentUser();
            if(user != null){
                startActivity(new Intent(getApplicationContext(),StandardCameraScreen.class));
                finish();
            }
        };

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Authenticating");

        mAuth = FirebaseAuth.getInstance();
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        Button loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(view -> doLogin());
    }


    private void doLogin(){
        progressDialog.show();
        final String email = mEmail.getText().toString();
        final String password = mPassword.getText().toString();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(LoginActivity.this, "Employee ID is blank", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            mEmail.requestFocus();
        }
        else if(TextUtils.isEmpty(password)){
            Toast.makeText(LoginActivity.this, "Password is blank", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            mPassword.requestFocus();
        }else{
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Authentication Successful", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                            startActivity(new Intent(getApplicationContext(),StandardCameraScreen.class));
                            finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }

    private void askUserPermission(){
        requestPermissions(permissions,1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==1){
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                //nothing to do
            }
            else{
                Toast.makeText(getApplicationContext(), "Permissions are Required", Toast.LENGTH_SHORT).show();
                askUserPermission();
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "Permissions are Required", Toast.LENGTH_SHORT).show();
            askUserPermission();
        }
    }
}