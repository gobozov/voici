package com.speechpro.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.speechpro.R;
import com.speechpro.record.ExtAudioRecorder;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: gb
 * Date: 11.01.13
 * Time: 23:19
 * To change this template use File | Settings | File Templates.
 */
public class VoiceActivity extends Activity {

    public static final File SD_DIR = Environment.getExternalStorageDirectory();
    public static final File APP_DIR = new File(SD_DIR + "/.speechpro");
    private File tempDir = new File(APP_DIR, String.valueOf(System.currentTimeMillis()));

    private ExtAudioRecorder extAudioRecorder = null;
    private Button buttonRecord1;
    private Button buttonRecord2;
    private Button buttonRecord3;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice);

        if (!APP_DIR.exists())
            APP_DIR.mkdir();

        if (!tempDir.exists())
            tempDir.mkdir();

        buttonRecord1 = (Button) findViewById(R.id.buttonRecord1);
        buttonRecord1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showStartDialog(1);
            }
        });

        buttonRecord2 = (Button) findViewById(R.id.buttonRecord2);
        buttonRecord2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showStartDialog(2);
            }
        });
        buttonRecord3 = (Button) findViewById(R.id.buttonRecord3);
        buttonRecord3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showStartDialog(3);
            }
        });
    }


//    public void prepareToRecord(int buttonId){
//
//        String fileName = tempDir.getAbsolutePath() + "/record" + buttonId + ".wav";
//        System.out.println("fileName = " + fileName);
//        extAudioRecorder = ExtAudioRecorder.getInstanse(false);
//        extAudioRecorder.setOutputFile(fileName);
//        extAudioRecorder.prepare();
//
//        showStartDialog();
//    }

    private void showStartDialog(final int buttonId) {
        final AlertDialog alert;
        AlertDialog.Builder builder = new AlertDialog.Builder(VoiceActivity.this)
                .setTitle("Are you ready?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int which) {

                        String fileName = tempDir.getAbsolutePath() + "/record" + buttonId + ".wav";
                        System.out.println("fileName = " + fileName);
                        extAudioRecorder = ExtAudioRecorder.getInstanse(false);
                        extAudioRecorder.setOutputFile(fileName);
                        extAudioRecorder.prepare();

                        new RecordSpeechTask(VoiceActivity.this, fileName).execute();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        extAudioRecorder.release();
                       // extAudioRecorder.reset();
                    }
                })
                .setMessage("Now you need to speak for 5 seconds. Click OK when you're ready");
        alert = builder.create();
        alert.show();
    }



    public class RecordSpeechTask extends AsyncTask<Object, Integer, Boolean>{

        private ProgressDialog dialog;
        private String filePath;

        public RecordSpeechTask(Context context, String filePath) {
            this.filePath = filePath;
            this.dialog = new ProgressDialog(context);
            this.dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.dialog.setCancelable(false);
            this.dialog.setMax(5000);

        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Speak now...");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(Object... objects) {
            extAudioRecorder.start();
            Log.d("speechpro", "start record");
            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < 5000){
                dialog.setProgress((int)(System.currentTimeMillis() - startTime));
            }
            extAudioRecorder.stop();
            extAudioRecorder.release();
            extAudioRecorder.reset();
            Log.d("speechpro", "stop record");
            return true;

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (dialog.isShowing())
                dialog.dismiss();
            DoneRetakeDialog drDialog = new DoneRetakeDialog(VoiceActivity.this, filePath);
            drDialog.setRetakeListener(new DoneRetakeDialog.RetakeListener() {
                @Override
                public void onRetake() {
                    Log.d("speechpro", "retake");
                }
            });
            drDialog.show();



        }




    }

}