package com.thepixels.smartscan;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ThreeDotMenu extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private Context context;
    ThreeDotMenu(Context context,View v){
        this.context = context;
        PopupMenu popupMenu = new PopupMenu(context,v);
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.inflate(R.menu.menu_option);
        popupMenu.show();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.send_feedback:
                sendFeedBack();
                return true;
            case R.id.privacy_policy:
                openPrivacy();
                return true;
            case R.id.terms_of_service:
                openTermsAndServices();
                return true;
            case R.id.log_out:
                doLogOut();
                return true;
            default:
                return false;
        }
    }

    private void doLogOut() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(context,SplashScreen.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        context.startActivity(intent);

    }

    private void openPrivacy() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.google.android.browser","com.google.android.browser.BrowserActivity"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("android.intent.action.VIEW");
        intent.setComponent(new ComponentName("com.android.chrome", "com.google.android.apps.chrome.IntentDispatcher"));
        Uri uri = Uri.parse("https://iamsuru.github.io/thepixels-privacy-policy/");
        intent.setData(uri);
        context.startActivity(intent);
    }

    private void sendFeedBack() {
        String addresses[] = new String[]{"thepixelssih@gmail.com"};
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL,addresses);

        if(intent.resolveActivity(context.getPackageManager())!= null){
            context.startActivity(intent);
        }
        else{
            Toast.makeText(context, "No App Found", Toast.LENGTH_SHORT).show();
        }
    }

    private void openTermsAndServices() {
/*        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://iamsuru.github.io/thepixels-privacy-policy/"));
        browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(browserIntent);*/

        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.google.android.browser","com.google.android.browser.BrowserActivity"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("android.intent.action.VIEW");
        intent.setComponent(new ComponentName("com.android.chrome", "com.google.android.apps.chrome.IntentDispatcher"));
        Uri uri = Uri.parse("https://iamsuru.github.io/thepixels-privacy-policy/");
        intent.setData(uri);
        context.startActivity(intent);
    }
}
