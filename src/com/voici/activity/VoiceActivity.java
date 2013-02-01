package com.voici.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.voici.R;
import com.voici.client.SpeechProClient;
import com.voici.data.User;
import com.voici.record.ExtAudioRecorder;
import com.voici.util.ResponseParser;
import com.voici.util.ResponseResult;
import com.voici.util.Utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: gb
 * Date: 11.01.13
 * Time: 23:19
 * To change this template use File | Settings | File Templates.
 */
public class VoiceActivity extends Activity {


    private ExtAudioRecorder extAudioRecorder = null;
    private Button buttonRecord1;
    private Button buttonRecord2;
    private Button buttonRecord3;

    private boolean record1Done;
    private boolean record2Done;
    private boolean record3Done;
    private ActionMode mMode;
    private File tempDir;

    private String[] completeRecords = new String[3];

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voice);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        tempDir = new File(Utils.getAppDir(this), String.valueOf(System.currentTimeMillis()));


        buttonRecord1 = (Button) findViewById(R.id.buttonRecord1);
        buttonRecord1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tempDir.exists()) tempDir.mkdir();
                String fileName = tempDir.getAbsolutePath() + "/record1.wav";
                showRecordDialog(fileName, R.id.buttonRecord1);
            }
        });

        buttonRecord2 = (Button) findViewById(R.id.buttonRecord2);
        buttonRecord2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tempDir.exists()) tempDir.mkdir();

                String fileName = tempDir.getAbsolutePath() + "/record2.wav";
                extAudioRecorder = ExtAudioRecorder.getInstanse(false);
                extAudioRecorder.setOutputFile(fileName);
                extAudioRecorder.prepare();
                new RecordSpeechTask(VoiceActivity.this, fileName, R.id.buttonRecord2).execute();

                //showRecordDialog(fileName, R.id.buttonRecord2);
            }
        });
        buttonRecord3 = (Button) findViewById(R.id.buttonRecord3);
        buttonRecord3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!tempDir.exists()) tempDir.mkdir();
                String fileName = tempDir.getAbsolutePath() + "/record3.wav";
                extAudioRecorder = ExtAudioRecorder.getInstanse(false);
                extAudioRecorder.setOutputFile(fileName);
                extAudioRecorder.prepare();
                new RecordSpeechTask(VoiceActivity.this, fileName, R.id.buttonRecord3).execute();
                //showRecordDialog(fileName, R.id.buttonRecord3);
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
            this.dialog.setMax(5);

        }

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Speak now...");
            dialog.show();
        }

        @Override
        protected Boolean doInBackground(Object... objects) {
            extAudioRecorder.start();
            Log.d("voici", "start record");
            long startTime = System.currentTimeMillis();
            long next = 1000;
            while (System.currentTimeMillis() - startTime < 5200) {
                if (System.currentTimeMillis() - startTime > next){
                    dialog.setProgress((int)next/1000);
                    next = next + 1000;
                }
            }
            extAudioRecorder.stop();
            extAudioRecorder.release();
            extAudioRecorder.reset();
            Log.d("voici", "stop record");
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
                    Log.d("voici", "retake record " + filePath);
                    drDialog.dismiss();
                    showRecordDialog(filePath, buttonId);
                }

                @Override
                public void onDone() {
                    drDialog.dismiss();
                    Log.d("voici", "record complete " + filePath);
                    ((Button) findViewById(buttonId)).setBackgroundResource(R.drawable.green_buttons_holo);
                    switch (buttonId) {
                        case R.id.buttonRecord1:
                            record1Done = true;
                            completeRecords[0] = filePath;
                            buttonRecord2.setEnabled(true);
                            break;
                        case R.id.buttonRecord2:
                            record2Done = true;
                            completeRecords[1] = filePath;
                            buttonRecord3.setEnabled(true);
                            break;
                        case R.id.buttonRecord3:
                            record3Done = true;
                            completeRecords[2] = filePath;
                            break;
                    }

                    if (record1Done && record2Done && record3Done) {
                        //buttonDone.setEnabled(true);
                       // buttonDone.setBackgroundResource(R.drawable.green_buttons);

                        if (mMode == null)
                            startActionMode(new RecordsCompleteMode());
                    }
                }
            });


            drDialog.show();


        }

    }

    private final class RecordsCompleteMode implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            menu.add(0, 1, 1, "Done").setIcon(android.R.drawable.ic_menu_send).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case 1:
                    new EnrollTask(VoiceActivity.this).execute(completeRecords);
                    break;
            }
            actionMode.finish();
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            mMode = null;
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

            if (!Utils.isInternetAvailable(context)){
                Utils.showMessageDialog(context, "Internet not available.", "You don't have an internet connection, check it and try again.");
                cancel(true);
                return;
            }

            dialog.setMessage("Upload...");
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

        }

        @Override
        protected ResponseResult doInBackground(String[]... strings) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            SpeechProClient client = new SpeechProClient();
            InputStream stream = client.executeEnroll(prefs.getString("key_server", "") + "/avis/vk_api2/enroll.php",  prefs.getString("key_key", ""), strings[0]);
            ResponseParser parser = new ResponseParser(context);
            return parser.getEnrollResult(stream, false);

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
            } else {
                Toast.makeText(context, "Something wrong... ", Toast.LENGTH_LONG).show();
            }

        }
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }


}