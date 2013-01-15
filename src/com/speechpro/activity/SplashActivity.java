package com.speechpro.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.speechpro.R;
import com.speechpro.database.DatabaseAdapter;

public class SplashActivity extends Activity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);


        Button buttonVk = (Button)findViewById(R.id.buttonVK);
        buttonVk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                intent.putExtra("site", DatabaseAdapter.VK);
                startActivity(intent);
            }
        });

        Button buttonGmail = (Button)findViewById(R.id.buttonGmail);
        buttonGmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                intent.putExtra("site", DatabaseAdapter.GMAIL);
                startActivity(intent);
            }
        });

        Button buttonFacebook = (Button)findViewById(R.id.buttonFacebook);
        buttonFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                intent.putExtra("site", DatabaseAdapter.FACEBOOK);
                startActivity(intent);
            }
        });




    }








}
