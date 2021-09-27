package com.example.android.bookfinder;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Login extends AppCompatActivity
{
    private EditText myUsername;
    private EditText myPassWord;
    String username = null;
    String password = null;
    SharedPreferences sp;
    public User user;
    LinearLayout text;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        myUsername=(EditText)findViewById(R.id.mUserName);
        myPassWord=(EditText)findViewById(R.id.mPassWord);
        final CheckBox mCheckbox=(CheckBox)findViewById(R.id.checkBox);
        sp=getPreferences(MODE_PRIVATE);
        TextView textView = (TextView)findViewById(R.id.textView3);
        textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        textView.setTextColor(Color.BLUE);
    }

    public void login(View view){
        //写死 user Name and user Passowrd
        username = myUsername.getText().toString();
        password = myPassWord.getText().toString();

        Intent intent = new Intent(Login.this, User.class);
        intent.putExtra("id", username);

        SQLiteHelper db = new SQLiteHelper(this);
        if(db.checkUser(username,password))
        {
            Intent data = new Intent();
            data.putExtra("id", username);
            System.out.println("666666666666666666666666666666666666666666666666");
            setResult(RESULT_OK, data);
            finish();
        } else {
            Toast.makeText(Login.this, "Sign Failed", Toast.LENGTH_SHORT).show();
        }
    }

    public void signUp(View view) {
        username = myUsername.getText().toString();
        password = myPassWord.getText().toString();
        SQLiteHelper db = new SQLiteHelper(this);

        if(db.insertUser(username,password))
        {
            Intent i = new Intent();
            i.putExtra("id", username);
            setResult(RESULT_OK, i);
            finish();
        } else{
            Toast.makeText(Login.this, "User exist", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor a = sp.edit();
        if(((CheckBox)findViewById(R.id.checkBox)).isChecked()){
            a.putString("id",username);
            a.putString("password",password);
        } else{
            a.clear();
        }
        a.apply();
    }

    public void forgetData(View view){
        Intent intent = new Intent(this, ForgetData.class);
        this.startActivity(intent);
    }
}