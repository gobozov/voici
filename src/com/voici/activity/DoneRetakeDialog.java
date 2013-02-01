package com.voici.activity;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.voici.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: gb
 * Date: 15.01.13
 * Time: 0:04
 * To change this template use File | Settings | File Templates.
 */
public class DoneRetakeDialog extends Dialog {

    private Context context;
    private Button buttonDone;
    private Button buttonRetake;
    private Button buttonListen;
    private String filePath;
    private DoneRetakeListener retakeListener;


    public DoneRetakeDialog(Context context, String filePath) {
        super(context);
        this.filePath = filePath;
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.done_retake);
        setTitle("Done or retake record?");
        setCanceledOnTouchOutside(false);

        buttonListen = (Button)findViewById(R.id.buttonListen);
        buttonListen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    final MediaPlayer player = new MediaPlayer();
                    player.setDataSource(new FileInputStream(new File(filePath)).getFD());
                    player.prepare();
                    player.start();
                    player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            player.release();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        });

        buttonRetake = (Button)findViewById(R.id.buttonRetake);
        buttonRetake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                retakeListener.onRetake();
            }
        });

        buttonDone = (Button)findViewById(R.id.buttonDone);
        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               retakeListener.onDone();
            }
        });
    }

    public void setRetakeListener(DoneRetakeListener retakeListener) {
        this.retakeListener = retakeListener;
    }

    public interface DoneRetakeListener{
        public void onDone();
        public void onRetake();
    }

}
