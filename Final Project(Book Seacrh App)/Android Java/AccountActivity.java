package com.example.android.bookfinder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AccountActivity extends AppCompatActivity
{
    private SQLiteHelper MyDataBase;
    public DrawerLayout MyDrawerLayout;
    public ActionBarDrawerToggle MyActionBarDrawerToggle;
    private Book book;
    public String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        // 左侧弹出框  
        // drawer layout instance to toggle the menu icon to open and back button to close drawer
        MyDrawerLayout = findViewById(R.id.my_drawer_layout);
        MyActionBarDrawerToggle = new ActionBarDrawerToggle(this, MyDrawerLayout, R.string.nav_open, R.string.nav_close);
        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        MyDrawerLayout.addDrawerListener(MyActionBarDrawerToggle);
        MyActionBarDrawerToggle.syncState();
        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if(name == null ){
            Intent data = getIntent();
            name = data.getStringExtra("id");
            ((TextView)findViewById(R.id.textView2)).setText(name);
        }

        //生成数据
        MyDataBase = new SQLiteHelper(this);
        ArrayList<Book> Likebooks = (ArrayList<Book>) MyDataBase.getList(name,"likedlist");
        ArrayList<Book> Historybooks = (ArrayList<Book>) MyDataBase.getList(name,"historylist");
        System.out.println(Likebooks.size()+"========================================");
        for (int i = 0; i < Likebooks.size(); i++) {
            System.out.println(Likebooks.get(i).getTitle()+"=============================");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.log_out, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (MyActionBarDrawerToggle.onOptionsItemSelected(item)) {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    //the empty likelist button function
    public void emptyMyBook(View view){
        MyDataBase.deleteAll();
        Toast.makeText(this, "My History empty finished", Toast.LENGTH_SHORT).show();
    }

    public void emptyLikeList(View view){
        MyDataBase.deletaList("likedlist");
        Toast.makeText(this, "User Favourite books empty finished", Toast.LENGTH_SHORT).show();
    }

    public void emptyHistory(View view){
        MyDataBase.deletaList("historylist");
        Toast.makeText(this, "User Browsing History empty finished", Toast.LENGTH_SHORT).show();
    }

    //shown my account page layout
    public void account (MenuItem item){

    }

    //shown history page layout
    public void historyList (MenuItem item) {
        Intent hList = new Intent(this, HistoryListActivity.class);
        System.out.println("ggggggggggggggggggggggggg" + name);
        hList.putExtra("id", name);
        this.startActivity(hList);
    }

    //shown history page layout
    public void likedlist (MenuItem item) {
        Intent iList = new Intent(this, likedlistActivity.class);
        iList.putExtra("id", name);
        this.startActivity(iList);
    }

    public void search (MenuItem item){
        Intent MainActivity = new Intent(this, MainActivity.class);
        MainActivity.putExtra("id", name);
        this.startActivity(MainActivity);
    }

    //the logout function
    public void logoutUser(MenuItem item){
        SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
        SharedPreferences.Editor a = sp.edit();
        a.clear().apply();
        Intent lo = new Intent(this, Login.class);
        this.startActivityForResult(lo,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(requestCode+ " AAAAAAAAAAAAAAAAAAAAAAAAA "+resultCode);
        if(requestCode == 1 && resultCode == RESULT_OK){
            name = data.getStringExtra("id");
            ((TextView)findViewById(R.id.textView2)).setText(name);
            System.out.println("5555555555555555555555555555555555555555555555555" + name);
        }
    }
}