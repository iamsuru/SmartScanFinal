package com.thepixels.smartscan;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaActionSound;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class CameraActivityFullScreen extends AppCompatActivity {
    private ListenableFuture<ProcessCameraProvider> cameraProviderListenableFuture;
    private PreviewView previewPane;
    private ImageButton takeImage,flash_ON,flash_OFF,flash_AUTO,moreDetails;
    private Button uploadButton;
    private AppCompatButton changeRatio;
    private ImageCapture imageCapture;
    private ImageView imageView;
    private int height=1280,width=720;
    private File photoDir;
    private String fileName = "";
    private ProcessCameraProvider cameraProvider;
    private StorageReference storageReference;
    final private uploadClass upload = new uploadClass(CameraActivityFullScreen.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //hiding title and action bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_camera);
        previewPane = findViewById(R.id.previewPane);
        takeImage = findViewById(R.id.captureButton);
        imageView = findViewById(R.id.imageView);
        flash_ON = findViewById(R.id.flash_ON);
        flash_OFF = findViewById(R.id.flash_OFF);
        flash_AUTO = findViewById(R.id.flash_AUTO);
        uploadButton = findViewById(R.id.uploadButton);
        changeRatio = findViewById(R.id.changeRatio);
        moreDetails = findViewById(R.id.moreDetails);
        changeRatio.setOnClickListener(view -> changeAspectRatio());
        uploadButton.setOnClickListener(view -> uploadToDatabase());
        flash_ON.setOnClickListener(view -> doFlashOff());
        flash_OFF.setOnClickListener(view -> doFlashOn());
        flash_AUTO.setOnClickListener(view -> doFlashAuto());
        takeImage.setOnClickListener(view -> capturePhoto());
        storageReference = FirebaseStorage.getInstance().getReference();
        cameraProviderListenableFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderListenableFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderListenableFuture.get();
                startCamera(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @SuppressLint("SetTextI18n")
    private void changeAspectRatio() {
        Intent intent = new Intent(this,CameraActivity_4_3.class);
        startActivity(intent);
        changeRatio.setText("16:9");
        finish();
    }

    private void uploadToDatabase() {
        /*upload.startLoading();
        File file = new File(fileName);
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"),file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file",file.getName(),requestBody);
        Retrofit retrofit = NetworkClient.getRetrofit();
        UploadAPI uploadAPI = retrofit.create(UploadAPI.class);
        Call call = uploadAPI.uploadImage(part);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                Toast.makeText(CameraActivityFullScreen.this, "Success", Toast.LENGTH_SHORT).show();
                System.out.println("onResponse:   ->" + response);
                System.out.println("onResponse:   ->" + call);
                upload.stopLoading();
            }
            @Override
            public void onFailure(Call call, Throwable t) {
                Toast.makeText(CameraActivityFullScreen.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                System.out.println("onFailure :   ->" + t);
                System.out.println("onFailure :   ->" + call);
                upload.stopLoading();
            }
        });*/
        upload.startLoading();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        Uri file = Uri.fromFile(new File(this.getCacheDir(),"document"));
        StorageReference sr = storageReference.child("Employee/" + uid + "/" + "document");
        sr.putFile(file).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(CameraActivityFullScreen.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                upload.stopLoading();
                startActivity(new Intent(getApplicationContext(),CameraActivity_4_3.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                upload.stopLoading();
                Toast.makeText(CameraActivityFullScreen.this, "Failed To upload", Toast.LENGTH_SHORT).show();
            }
        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                upload.stopLoading();
            }
        });
    }


    private void doFlashOn() {
        flash_OFF.setVisibility(View.INVISIBLE);
        flash_ON.setVisibility(View.VISIBLE);
        imageCapture.setFlashMode(ImageCapture.FLASH_MODE_ON);
    }
    private void doFlashOff() {
        flash_ON.setVisibility(View.INVISIBLE);
        flash_OFF.setVisibility(View.INVISIBLE);
        flash_AUTO.setVisibility(View.VISIBLE);
        imageCapture.setFlashMode(ImageCapture.FLASH_MODE_AUTO);
    }
    private void doFlashAuto() {
        flash_ON.setVisibility(View.INVISIBLE);
        flash_OFF.setVisibility(View.VISIBLE);
        flash_AUTO.setVisibility(View.INVISIBLE);
        imageCapture.setFlashMode(ImageCapture.FLASH_MODE_OFF);
    }

    private void startCamera(ProcessCameraProvider cameraProvider){
        cameraProvider.unbindAll();
        //full screen
        /*DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;*/
        Toast.makeText(this, height + "     " + width, Toast.LENGTH_SHORT).show();
        Preview preview = new Preview.Builder().setTargetResolution(new Size(width,height)).build();
        preview.setSurfaceProvider(previewPane.getSurfaceProvider());
        imageCapture = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY).setTargetResolution(new Size(width ,height)).build();
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
    }


    private void capturePhoto() {
        MediaActionSound mediaActionSound = new MediaActionSound();
        mediaActionSound.play(MediaActionSound.SHUTTER_CLICK);
        photoDir = new File(this.getCacheDir(),"document");
        fileName = photoDir.getPath();
        imageCapture.takePicture(
                new ImageCapture.OutputFileOptions.Builder(photoDir).build(),
                getExecutor(),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Toast.makeText(CameraActivityFullScreen.this, "Saved", Toast.LENGTH_SHORT).show();
                        flash_OFF.setVisibility(View.INVISIBLE);
                        flash_ON.setVisibility(View.INVISIBLE);
                        flash_AUTO.setVisibility(View.INVISIBLE);
                        takeImage.setVisibility(View.INVISIBLE);
                        previewPane.setVisibility(View.INVISIBLE);
                        uploadButton.setVisibility(View.VISIBLE);
                        changeRatio.setVisibility(View.INVISIBLE);
                        //moreDetails.setVisibility(View.INVISIBLE);
                        showImage();
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(CameraActivityFullScreen.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void showImage() {
        imageView.getLayoutParams().height = height;
        imageView.getLayoutParams().width = width;
        Bitmap bmImg = BitmapFactory.decodeFile(photoDir.getAbsolutePath());
        imageView.setImageBitmap(bmImg);
    }

    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }
}