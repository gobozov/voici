package com.speechpro.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.speechpro.R;
import com.speechpro.client.SpeechProClient;
import com.speechpro.record.ExtAudioRecorder;
import com.speechpro.util.ResponseParser;
import com.speechpro.util.ResponseResult;
import com.speechpro.util.Utils;

import java.io.File;
import java.io.FileFilter;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: gb
 * Date: 11.01.13
 * Time: 23:19
 * To change this template use File | Settings | File Templates.
 */
public class VoiceActivity extends Activity {

    //public static final File SD_DIR = Environment.getExternalStorageDirectory();
    //public static final File APP_DIR = new File(SD_DIR + "/.speechpro");
    //private File tempDir = new File(APP_DIR, String.valueOf(System.currentTimeMillis()));



    private ExtAudioRecorder extAudioRecorder = null;
    private Button buttonRecord1;
    private Button buttonRecord2;
    private Button buttonRecord3;
    private Button buttonDone;
    private Button buttonLast;

    private boolean record1Done;
    private boolean record2Done;
    private boolean record3Done;

    private File tempDir;

    private String[] completeRecords = new String[3];

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice);

        tempDir = new File(Utils.getAppDir(this), String.valueOf(System.currentTimeMillis()));
//        if (!tempDir.exists())
//            tempDir.mkdir();


        buttonLast = (Button) findViewById(R.id.buttonLast);
        buttonLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File[] folders = Utils.getAppDir(VoiceActivity.this).listFiles(new FileFilter() {
                    @Override
                    public boolean accept(File file) {
                        if (file.isDirectory()) return true;
                        return false;
                    }
                });

                File last = folders[0];
                for (File f : folders) {
                    if (f.getName().compareTo(last.getName()) > 0) {
                        last = f;
                        Log.d("speechpro", "last = " + f.getName());
                    }
                }

                File lastFiles[] = last.listFiles();
                for (int i = 0; i < lastFiles.length; i++) {
                    completeRecords[i] = lastFiles[i].getAbsolutePath();
                }
                new EnrollTask(VoiceActivity.this).execute(completeRecords);


            }
        });

        buttonDone = (Button) findViewById(R.id.buttonDone);
        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for (int i = 0; i < completeRecords.length; i++) {
                    if (completeRecords[i] == null) {
                        // todo show notification
                    }
                }

//                Intent data = new Intent();
//                data.putExtra("records", completeRecords);
//                setResult(RESULT_OK, data);

                //              new EnrollTask(VoiceActivity.this).execute(completeRecords);


            }
        });

        buttonRecord1 = (Button) findViewById(R.id.buttonRecord1);
        buttonRecord1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fileName = tempDir.getAbsolutePath() + "/record1.wav";
                showRecordDialog(fileName, R.id.buttonRecord1);
            }
        });

        buttonRecord2 = (Button) findViewById(R.id.buttonRecord2);
        buttonRecord2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fileName = tempDir.getAbsolutePath() + "/record2.wav";
                showRecordDialog(fileName, R.id.buttonRecord2);
            }
        });
        buttonRecord3 = (Button) findViewById(R.id.buttonRecord3);
        buttonRecord3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fileName = tempDir.getAbsolutePath() + "/record3.wav";
                showRecordDialog(fileName, R.id.buttonRecord3);
            }
        });
    }


    private void showRecordDialog(final String filePath, final int buttonId) {
        final AlertDialog alert;
        AlertDialog.Builder builder = new AlertDialog.Builder(VoiceActivity.this)
                .setTitle("Are you ready?")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int which) {

                        //  String fileName = tempDir.getAbsolutePath() + "/record" + buttonId + ".wav";
                        System.out.println("filePath = " + filePath);
                        extAudioRecorder = ExtAudioRecorder.getInstanse(false);
                        extAudioRecorder.setOutputFile(filePath);
                        extAudioRecorder.prepare();

                        new RecordSpeechTask(VoiceActivity.this, filePath, buttonId).execute();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (extAudioRecorder != null)
                            extAudioRecorder.release();
                        // extAudioRecorder.reset();
                    }
                })
                .setMessage("Now you need to speak for 5 seconds. Click OK when you're ready");
        alert = builder.create();
        alert.show();
    }


    public class RecordSpeechTask extends AsyncTask<Object, Integer, Boolean> {

        private ProgressDialog dialog;
        private String filePath;
        private int buttonId;

        public RecordSpeechTask(Context context, String filePath, int buttonId) {
            this.filePath = filePath;
            this.buttonId = buttonId;
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
            while (System.currentTimeMillis() - startTime < 5000) {
                dialog.setProgress((int) (System.currentTimeMillis() - startTime));
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

            final DoneRetakeDialog drDialog = new DoneRetakeDialog(VoiceActivity.this, filePath);

            drDialog.setRetakeListener(new DoneRetakeDialog.DoneRetakeListener() {
                @Override
                public void onRetake() {
                    Log.d("speechpro", "retake record " + filePath);
                    drDialog.dismiss();
                    showRecordDialog(filePath, buttonId);
                }

                @Override
                public void onDone() {
                    drDialog.dismiss();
                    Log.d("speechpro", "record complete " + filePath);
                    ((Button) findViewById(buttonId)).setBackgroundResource(R.drawable.green_buttons);
                    switch (buttonId) {
                        case R.id.buttonRecord1:
                            record1Done = true;
                            completeRecords[0] = filePath;
                            break;
                        case R.id.buttonRecord2:
                            record2Done = true;
                            completeRecords[1] = filePath;
                            break;
                        case R.id.buttonRecord3:
                            record3Done = true;
                            completeRecords[2] = filePath;
                            break;
                    }

                    if (record1Done && record2Done && record3Done) {
                        buttonDone.setEnabled(true);
                        buttonDone.setBackgroundResource(R.drawable.green_buttons);
                    }
                }
            });


            drDialog.show();


        }

    }


    private class EnrollTask extends AsyncTask<String[], Void, ResponseResult> {

        private ProgressDialog dialog;
        private Context context;

        private EnrollTask(Context context) {
            this.context = context;
            this.dialog = new ProgressDialog(context);
        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Upload...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

        }

        @Override
        protected ResponseResult doInBackground(String[]... strings) {
            SpeechProClient client = new SpeechProClient();
            InputStream stream = client.executeEnroll("http://voicekey.speechpro-usa.com/avis/vk_api2/enroll.php", "zab", strings[0]);
            ResponseParser parser = new ResponseParser();
            return parser.getEnrollResult(stream);

        }

        @Override
        protected void onPostExecute(ResponseResult result) {
            if (dialog.isShowing())
                dialog.dismiss();

            if (result != null) {
                Intent data = new Intent();
                data.putExtra("result", result);
                setResult(RESULT_OK, data);
                finish();
            }else {
                Toast.makeText(context, "Something wrong... ", Toast.LENGTH_LONG).show();
            }

            //Log.d("speechpro", "Enroll response = " + response);
        }
    }


}