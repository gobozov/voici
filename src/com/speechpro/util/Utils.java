package com.speechpro.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import com.speechpro.record.ExtAudioRecorder;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: gb
 * Date: 14.01.13
 * Time: 0:15
 * To change this template use File | Settings | File Templates.
 */
public class Utils {

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else
            return false;
    }

    public static boolean isSdAvailable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return false;
        } else {
            return false;
        }
    }


    public static File getAppDir(Context context) {
        File appDir;
        if (isSdAvailable()) {
            File SD_DIR = Environment.getExternalStorageDirectory();
            appDir = new File(SD_DIR + "/.speechpro");
        } else {
            appDir = new File(context.getFilesDir() + "/sounds");
        }
        if (!appDir.exists())
            appDir.mkdir();
        return appDir;
    }

    public static boolean isValidAttributes(String status, String cardId) {
        if (isEmptyString(status)) return false;
        if (isEmptyString(cardId)) return false;
//
//        if (!status.equals(ResponseResult.Status.ERROR.getName()) ||
//                !status.equals(ResponseResult.Status.OK.getName()) ||
//                !status.equals(ResponseResult.Status.INCONSISTENT_COMPOSITE.getName()))
//            return false;

        return true;
    }

    public static boolean isEmptyString(String s) {
        if (s == null || s.trim().length() == 0)
            return true;
        return false;
    }

    public static void showMessageDialog(Context context, final String title, final String message) {
        final AlertDialog alert;
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int which) {

                    }
                })
                .setMessage(message);
        alert = builder.create();
        alert.show();
    }


}
