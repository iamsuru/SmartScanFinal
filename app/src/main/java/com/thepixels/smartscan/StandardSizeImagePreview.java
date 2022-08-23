package com.thepixels.smartscan;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class StandardSizeImagePreview extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private ImageView imageView;
    private File file;
    private final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private final String uid;

    {
        assert currentFirebaseUser != null;
        uid = currentFirebaseUser.getUid();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_standard_size_image_preview);
        imageView = findViewById(R.id.imageView);
        ImageButton backButton = findViewById(R.id.backButton);
        ImageButton dropdown_menu = findViewById(R.id.dropdown_menu);
        Button uploadButton = findViewById(R.id.uploadButton);

        progressDialog = new ProgressDialog(StandardSizeImagePreview.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Uploading...");


        Button retake_image = findViewById(R.id.retakeButton);
        retake_image.setOnClickListener(view -> onBackPressed());
        backButton.setOnClickListener(view -> onBackPressed());
        uploadButton.setOnClickListener(view -> sendToAPI());
        dropdown_menu.setOnClickListener(view -> new ThreeDotMenu(getApplicationContext(),view));


        showImage();
    }

    private void sendToAPI() {
        progressDialog.show();
        Toast.makeText(this, uid, Toast.LENGTH_SHORT).show();
        file = new File(getCacheDir()+"/document");
        System.out.println("size->>>>>>>."+(file.length())/1024*1024);
        Retrofit retrofit = NetworkClient.getRetrofit();
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"),file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file",file.getName(),requestBody);
        RequestBody requestBody1 = RequestBody.create(MediaType.parse("text/plain"),uid);
        //MultipartBody.Part part1 = MultipartBody.Part.createFormData("uid","uid",requestBody1);
        UploadAPI uploadAPI = retrofit.create(UploadAPI.class);
        Call<ResponseBody> call = uploadAPI.uploadImage(part,requestBody1);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull Call c, @NonNull Response response) {
                if(response.code()==200){
                    Toast.makeText(StandardSizeImagePreview.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    if(file.exists()){
                        file.delete();
                    }
                    onBackPressed();
                }
                if(response.code()==422){
                    progressDialog.dismiss();
                    Toast.makeText(StandardSizeImagePreview.this, response.toString(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(StandardSizeImagePreview.this, "Try again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call c, @NonNull Throwable t) {
                progressDialog.dismiss();
                System.out.println("Error->>>>>>>>>>>   "+t);
                Toast.makeText(StandardSizeImagePreview.this, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void showImage() {
            Bitmap bitmap = BitmapFactory.decodeFile(getCacheDir() + "/document");
            imageView.setImageBitmap(bitmap);
    }
}