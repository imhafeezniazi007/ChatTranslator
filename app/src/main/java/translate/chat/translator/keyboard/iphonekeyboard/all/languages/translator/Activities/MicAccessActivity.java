package translate.chat.translator.keyboard.iphonekeyboard.all.languages.translator.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.example.chattranslator.databinding.ActivityMicAccessBinding;
import com.example.chattranslator.databinding.ActivitySubscriptionBinding;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class MicAccessActivity extends AppCompatActivity {

    ActivityMicAccessBinding activityMicAccessBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMicAccessBinding = ActivityMicAccessBinding.inflate(getLayoutInflater());
        setContentView(activityMicAccessBinding.getRoot());

        if (ContextCompat.checkSelfPermission(MicAccessActivity.this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
        {
            startActivity(new Intent(MicAccessActivity.this, KeyboardAccessActivity.class));
            finish();
        }
        else {
            activityMicAccessBinding.micSwitchOne.setChecked(false);
        }

        activityMicAccessBinding.micSwitchOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestNecessaryPermissions();
            }
        });

        activityMicAccessBinding.btnMicCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    private void requestNecessaryPermissions() {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.RECORD_AUDIO)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        activityMicAccessBinding.micSwitchOne.setChecked(true);
                        startActivity(new Intent(MicAccessActivity.this, KeyboardAccessActivity.class));
                        finish();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        activityMicAccessBinding.micSwitchOne.setChecked(false);
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }
}