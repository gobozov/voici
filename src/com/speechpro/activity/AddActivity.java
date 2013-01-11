package com.speechpro.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.speechpro.R;
import com.speechpro.data.User;
import com.speechpro.database.DatabaseAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: gb
 * Date: 11.01.13
 * Time: 1:35
 * To change this template use File | Settings | File Templates.
 */
public class AddActivity extends Activity {

    private DatabaseAdapter dbAdapter;
    private Button buttonVoice;
    private EditText login;
    private EditText password;
    private int site;
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add);

         site = getIntent().getIntExtra("site", DatabaseAdapter.VK);

        dbAdapter = new DatabaseAdapter(this);
        dbAdapter.open();

        buttonVoice = (Button) findViewById(R.id.buttonVoice);
        buttonVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddActivity.this, VoiceActivity.class);
                startActivity(intent);
            }
        });


        login = (EditText) findViewById(R.id.login);
        password = (EditText) findViewById(R.id.password);


        Button buttonOk = (Button) findViewById(R.id.buttonOk);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValidField(login) && isValidField(password)) {
                    User user = new User(login.getText().toString(), password.getText().toString());
                    user.setKey("1234567890");
                    dbAdapter.addUser(user, site);
                    finish();
                }
            }
        });


    }


    @Override
    protected void onResume() {
        dbAdapter.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        dbAdapter.close();
        super.onPause();
    }


    private boolean isValidField(EditText text) {
        if (text.getText().toString() == null || text.getText().toString().trim().length() == 0) {
            text.setError("Fill field!");
            return false;
        }
        return true;
    }

}