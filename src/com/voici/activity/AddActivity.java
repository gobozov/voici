package com.voici.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.voici.R;
import com.voici.data.User;
import com.voici.database.DatabaseAdapter;
import com.voici.util.ResponseResult;
import com.voici.util.Utils;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: gb
 * Date: 11.01.13
 * Time: 1:35
 * To change this template use File | Settings | File Templates.
 */
public class AddActivity extends Activity {
    private static final int CODE_RETURN_FROM_VOICE = 5;
    private DatabaseAdapter dbAdapter;
    private Button buttonVoice;
    private EditText login;
    private EditText password;
    private Integer site;
    private User editedUser;
    private ResponseResult voiceResult;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        site = getIntent().getIntExtra("site", DatabaseAdapter.VK);
        editedUser = getIntent().getSerializableExtra("user") == null ? null : (User) getIntent().getSerializableExtra("user");

        dbAdapter = new DatabaseAdapter(this);
        dbAdapter.open();

        buttonVoice = (Button) findViewById(R.id.buttonVoice);
        buttonVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddActivity.this, VoiceActivity.class);
                startActivityForResult(intent, CODE_RETURN_FROM_VOICE);
                //startActivity(intent);
            }
        });


        login = (EditText) findViewById(R.id.login);
        password = (EditText) findViewById(R.id.password);


        // is edit action
        if (editedUser != null) {
            User user = dbAdapter.getUserById(editedUser.getId());
            login.setText(user.getName());
            password.setText(user.getPassword());
        }


        Button buttonOk = (Button) findViewById(R.id.buttonOk);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (isValidField(login) && isValidField(password)) {
                    User user = new User(login.getText().toString(), password.getText().toString());


                    //edit existing user
                    if (editedUser != null) {
                        user.setKey(voiceResult == null ? editedUser.getKey() : voiceResult.getKey());
                        if (Utils.isEmptyString(user.getKey()))
                            Toast.makeText(AddActivity.this, "User do not have voice password, you can set up it later", Toast.LENGTH_SHORT).show();
                        dbAdapter.updateUser(editedUser.getId(), user);
                        // create new user
                    } else {
                        user.setKey(voiceResult == null ? "" : voiceResult.getKey());
                        if (Utils.isEmptyString(user.getKey()))
                            Toast.makeText(AddActivity.this, "User do not have voice password, you can set up it later", Toast.LENGTH_SHORT).show();
                        dbAdapter.addUser(user, site);
                    }

                    //Utils.cleanAppDir(AddActivity.this);
                    setResult(RESULT_OK);
                    finish();

                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_RETURN_FROM_VOICE) {
            if (resultCode == RESULT_OK) {
                voiceResult = (ResponseResult) data.getSerializableExtra("result");
                if (voiceResult.getStatus().equals(ResponseResult.Status.ERROR))
                   Utils.showMessageDialog(this, "Error", voiceResult.getError());

            }
        }
    }


    @Override
    protected void onResume() {
        dbAdapter.open();
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (dbAdapter != null)
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