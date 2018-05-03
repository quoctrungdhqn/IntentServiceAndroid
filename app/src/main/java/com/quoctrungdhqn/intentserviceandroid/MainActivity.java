package com.quoctrungdhqn.intentserviceandroid;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_WRITE_EXTERNAL = 1;
    Button btnDownloadFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnDownloadFile = findViewById(R.id.btn_download_file);

        btnDownloadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkPermission();
            }
        });
    }

    private void checkPermission() {
        if (checkPermissionGranted()) {
            addEvents();
        } else {
            requestPermission(REQUEST_WRITE_EXTERNAL);
        }
    }

    public boolean checkPermissionGranted() {
        return ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission(int requestCode) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                if (BuildConfig.DEBUG)
                    //logDebug("requestPermission", "Should show request permission");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                    }
            } else {
                // No explanation needed, we can request the permission.
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                }
            }
        }
    }

    public boolean handlePermissionResponse(int requestCode,
                                            String permissions[], int[] grantResults, int permissionRequestCode) {
        for (String permission : permissions) {
            Log.d("MainActivity", String.format("Handling permission: %s", permission));
        }
        if (permissionRequestCode == requestCode) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                return true;

            } else {

                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                return false;
            }
        }
        return false;
    }

    private void addEvents() {
        Intent intent = new Intent(this, DownloadFileService.class);
        intent.putExtra(DownloadFileService.FILE_NAME, "android-intent-service.html");
        intent.putExtra(DownloadFileService.FILE_URL, "https://quoctrungdhqn.com/demo/android-intent-service.html");
        startService(intent);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    String filePath = bundle.getString(DownloadFileService.FILE_PATH);
                    int result = bundle.getInt(DownloadFileService.RESULT);

                    if (result == Activity.RESULT_OK) {
                        showDialogSuccess("See file at " + filePath);
                    }
                }
            }

        }
    };

    private void showDialogSuccess(String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("Download completed");
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerReceiver(broadcastReceiver, new IntentFilter(DownloadFileService.RESULT_INTENT));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isPermissionWriteGranted = handlePermissionResponse(requestCode, permissions, grantResults, REQUEST_WRITE_EXTERNAL);
        if (isPermissionWriteGranted) {
            addEvents();
        }
    }
}
