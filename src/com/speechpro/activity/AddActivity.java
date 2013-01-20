package com.speechpro.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.speechpro.R;
import com.speechpro.data.User;
import com.speechpro.database.DatabaseAdapter;
import com.speechpro.util.ResponseResult;

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
    private Integer editedUser;
    private ResponseResult voiceResult;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add);

        site = getIntent().getIntExtra("site", DatabaseAdapter.VK);
        editedUser = getIntent().getIntExtra("user", -1);

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
        if (editedUser != null && editedUser != -1) {
            User user = dbAdapter.getUserById(editedUser);
            login.setText(user.getName());
            password.setText(user.getPassword());
        }


        Button buttonOk = (Button) findViewById(R.id.buttonOk);
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (isValidField(login) && isValidField(password)) {
                    User user = new User(login.getText().toString(), password.getText().toString());
                    user.setKey("1234567890");

                    boolean result;

                    //edit existing user
                    if (editedUser != null && editedUser != -1) {
                        result = dbAdapter.updateUser(editedUser, user);
                        setResult(RESULT_OK);

                    } else {
                        // create new user
                        result = dbAdapter.addUser(user, site);
                        setResult(RESULT_OK);
                    }

                    if (result)
                        finish();
                    else
                        Toast.makeText(AddActivity.this, "Can't add or edit user, something wrong :(", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_RETURN_FROM_VOICE) {
            if (resultCode == RESULT_OK) {

                voiceResult = data.getParcelableExtra("result");
                Log.d("speechpro", "result 2 = " + voiceResult);
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