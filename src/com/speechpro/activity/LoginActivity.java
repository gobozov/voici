package com.speechpro.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.speechpro.R;
import com.speechpro.data.User;
import com.speechpro.database.DatabaseAdapter;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: gb
 * Date: 11.01.13
 * Time: 0:44
 * To change this template use File | Settings | File Templates.
 */
public class LoginActivity extends Activity {

    public static int CODE_ADD_USER = 5;
    public static int CODE_EDIT_USER = 6;

    private DatabaseAdapter dbAdapter;
    private ArrayAdapter<User> userAdapter;
    private ListView list;
    private Button buttonAdd;
    private Button buttonDelete;
    private Button buttonEdit;
    private int site;
    private int selectedPosition = -1;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        site = getIntent().getIntExtra("site", DatabaseAdapter.VK);

        dbAdapter = new DatabaseAdapter(this);
        dbAdapter.open();

        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, AddActivity.class);
                intent.putExtra("site", site);
                startActivityForResult(intent,CODE_ADD_USER);
            }
        });


        buttonDelete = (Button) findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedPosition != -1){
                    User user = userAdapter.getItem(selectedPosition);
                    if (user != null){
                        dbAdapter.deleteUser(user);
                        userAdapter.remove(user);
                        userAdapter.notifyDataSetChanged();
                    }
                }else{
                    Toast.makeText(LoginActivity.this, "Select user to delete", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonEdit = (Button) findViewById(R.id.buttonEdit);
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedPosition != -1){
                    User user = userAdapter.getItem(selectedPosition);
                    Intent intent = new Intent(LoginActivity.this, AddActivity.class);
                    intent.putExtra("site", site);
                    intent.putExtra("user", user);
                    startActivityForResult(intent,CODE_EDIT_USER);
                }else{
                    Toast.makeText(LoginActivity.this, "Select user to edit", Toast.LENGTH_SHORT).show();
                }
            }
        });

        list = (ListView) findViewById(R.id.listUsers);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_ADD_USER) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(LoginActivity.this, "User added successfully", Toast.LENGTH_SHORT).show();
                selectedPosition = -1;
            }
        }
        if (requestCode == CODE_EDIT_USER) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(LoginActivity.this, "User edited successfully", Toast.LENGTH_SHORT).show();
                selectedPosition = -1;
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        dbAdapter.open();
        List<User> users = dbAdapter.getAllUsers(site);
        userAdapter = new ArrayAdapter<User>(this, android.R.layout.simple_list_item_single_choice, users);
        list.setAdapter(userAdapter);
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                 selectedPosition=i;
            }
        });
    }



}