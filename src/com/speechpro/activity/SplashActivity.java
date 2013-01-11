package com.speechpro.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.speechpro.R;

public class SplashActivity extends Activity {


//    private MediaRecorder mRecorder = null;
//    private MediaPlayer mPlayer = null;
//    ExtAudioRecorder extAudioRecorder = null;
//    private Button startButton;
//    private Button stopButton;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

//        startButton = (Button) findViewById(R.id.buttonStart);
//        stopButton = (Button) findViewById(R.id.buttonStop);
//
//
//
//        startButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                extAudioRecorder = ExtAudioRecorder.getInstanse(false);
//                extAudioRecorder.setOutputFile(Environment.getExternalStorageDirectory().getAbsolutePath()+"/test2.wav");
//                extAudioRecorder.prepare();
//                extAudioRecorder.start();
//            }
//        });
//
//
//        stopButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                extAudioRecorder.stop();
//                extAudioRecorder.release();
//            }
//        });


        Button buttonVk = (Button)findViewById(R.id.buttonVK);
        buttonVk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                intent.putExtra("site", "vk");
                startActivity(intent);
            }
        });

        Button buttonGmail = (Button)findViewById(R.id.buttonGmail);
        buttonGmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                intent.putExtra("site", "gmail");
                startActivity(intent);
            }
        });

        Button buttonFacebook = (Button)findViewById(R.id.buttonFacebook);
        buttonFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                intent.putExtra("site", "facebook");
                startActivity(intent);
            }
        });




    }








}
