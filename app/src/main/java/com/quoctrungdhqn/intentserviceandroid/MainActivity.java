package com.quoctrungdhqn.intentserviceandroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button btnDownloadFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnDownloadFile = findViewById(R.id.btn_download_file);

        btnDownloadFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addEvents();
            }
        });
    }

    private void addEvents() {
        Intent intent = new Intent(this, DownloadFileService.class);
        intent.putExtra(DownloadFileService.FILE_NAME, "index.html");
        intent.putExtra(DownloadFileService.FILE_URL, "https://quoctrungdhqn.com/maintenance/index.html");
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
}
