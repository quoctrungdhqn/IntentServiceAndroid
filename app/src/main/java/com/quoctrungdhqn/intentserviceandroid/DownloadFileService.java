package com.quoctrungdhqn.intentserviceandroid;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;

public class DownloadFileService extends IntentService {

    public static final String FILE_NAME = "FILE_NAME";
    public static final String FILE_URL = "FILE_URL";
    public static final String FILE_PATH = "FILE_PATH";
    public static final String RESULT = "RESULT";
    public static final String RESULT_INTENT = "com.quoctrungdhqn.intentserviceandroid";
    public int result;

    public DownloadFileService() {
        super("Download file");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            String fileURL = intent.getStringExtra(FILE_URL);
            String fileName = intent.getStringExtra(FILE_NAME);

            handleFile(fileName, fileURL);

        }
    }

    private void handleFile(String fileName, String fileURL) {
        File outPutFile = new File(Environment.getExternalStorageDirectory(), fileName);

        if (outPutFile.exists()) outPutFile.delete();

        InputStream inputStream = null;
        OutputStream outputStream = null;

        try {
            URL url = new URL(fileURL);
            inputStream = url.openConnection().getInputStream();

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            outputStream = new FileOutputStream(outPutFile.getPath());

            int next;
            while ((next = inputStreamReader.read()) != -1) {
                outputStream.write(next);
            }

            result = Activity.RESULT_OK;

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        showFileDownloaded(outPutFile.getAbsolutePath(), result);
    }

    private void showFileDownloaded(String outPutPath, int result) {
        Intent intent = new Intent(RESULT_INTENT);
        intent.putExtra(FILE_PATH, outPutPath);
        intent.putExtra(RESULT, result);
        sendBroadcast(intent);
    }
}
