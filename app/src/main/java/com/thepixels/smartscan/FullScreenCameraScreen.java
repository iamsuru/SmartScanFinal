package com.thepixels.smartscan;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Size;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class FullScreenCameraScreen extends AppCompatActivity {
    private ListenableFuture<ProcessCameraProvider> cameraProviderListenableFuture;
    private PreviewView previewPane;
    private ProcessCameraProvider cameraProvider;
    private ImageButton flash_ON;
    private ImageButton flash_OFF;
    private ImageButton flash_AUTO;
    private ImageButton high_quality;
    private int height,width;
    private ImageCapture imageCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(R.layout.activity_full_screen_camera_screen);

        previewPane = findViewById(R.id.previewPane);
        flash_ON = findViewById(R.id.flash_ON);
        flash_OFF = findViewById(R.id.flash_OFF);
        flash_AUTO = findViewById(R.id.flash_AUTO);
        ImageButton standard_screen = findViewById(R.id.standard_screen);
        ImageButton takeImage = findViewById(R.id.captureButton);
        high_quality = findViewById(R.id.high_quality);
        ImageButton dropdown_menu = findViewById(R.id.dropdown_menu);




        flash_ON.setOnClickListener(view -> doFlashOff());
        flash_OFF.setOnClickListener(view -> doFlashOn());
        flash_AUTO.setOnClickListener(view -> doFlashAuto());
        standard_screen.setOnClickListener(view -> closeFullScreen());
        takeImage.setOnClickListener(view -> capturePhoto());
        high_quality.setOnClickListener(view -> changeToHighQuality());
        dropdown_menu.setOnClickListener(view -> new ThreeDotMenu(getApplicationContext(),view));




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


    private void closeFullScreen() {
        startActivity(new Intent(getApplicationContext(),StandardCameraScreen.class));
        finish();
    }


    private void changeToHighQuality(){
        if(height==1280){
            high_quality.setBackgroundResource(R.drawable.ic_baseline_hdr_on_24);
            this.height = 1920;
            this.width = 1080;
            Toast.makeText(this, "Set to High Quality\nDocument Size will be increased", Toast.LENGTH_SHORT).show();
        }
        else{
            high_quality.setBackgroundResource(R.drawable.ic_baseline_hdr_off_24);
            this.height = 1280;
            this.width = 720;
            Toast.makeText(this, "Set to Standard Quality\nDocument Size will be Average", Toast.LENGTH_SHORT).show();
        }

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
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        Toast.makeText(this, height + "     " + width, Toast.LENGTH_SHORT).show();

        //Toast.makeText(this, height + "     " + width, Toast.LENGTH_SHORT).show();
        Preview preview = new Preview.Builder().setTargetResolution(new Size(width,height)).build();
        preview.setSurfaceProvider(previewPane.getSurfaceProvider());
        imageCapture = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY).setTargetResolution(new Size(width ,height)).build();
        CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
    }


    private void capturePhoto() {
        File photoDir = new File(this.getCacheDir(), "document");
        imageCapture.takePicture(
                new ImageCapture.OutputFileOptions.Builder(photoDir).build(),
                getExecutor(),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),StandardSizeImagePreview.class));

                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private Executor getExecutor() {
        return ContextCompat.getMainExecutor(this);
    }

}