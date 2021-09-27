package com.example.android.bookfinder;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.ArrayList;
import java.util.List;

public class likedlistActivity extends AppCompatActivity {
    private BookAdapter mAdapter;

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    private SQLiteHelper database;
    public String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.likedlist);

        System.out.println("likedlistActivity================");
        Intent data = getIntent();
        name = data.getStringExtra("id");

        // 左侧弹出框
        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //生成数据
        database = new SQLiteHelper(this);
        SQLiteDatabase db = database.getWritableDatabase();
        ArrayList<Book> books = (ArrayList<Book>) database.getList(name,"likedlist");

        System.out.println(books.size()+"========================================");
        for (int i = 0; i < books.size(); i++) {
            System.out.println(books.get(i).getTitle()+"=============================");
        }

        //列表页
        System.out.println("SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS" + name);
        mAdapter = new BookAdapter(this, books);
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(mAdapter);
        System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh" + name);

        //详情页
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Book currentBook = mAdapter.getItem(position);
                //数据
                String bookTitle = currentBook.getTitle();
                String bookAuthor = currentBook.getAuthor();
                String bookDescription = currentBook.getDescription();
                String bookImage = currentBook.getImageUrl();
                String bookLink = currentBook.getUrl();
                String bookPublisher = currentBook.getPublisher();
                String bookPublishDate = currentBook.getPublishedDate();
                String bookWebReaderLink = currentBook.getWebReaderLink();
                int bookPages = currentBook.getPages();
                String idName = name;

                Intent intent = new Intent(likedlistActivity.this,InfoActivity.class);
                intent.putExtra("Title",bookTitle);
                intent.putExtra("Author",bookAuthor);
                intent.putExtra("Description",bookDescription);
                intent.putExtra("Image",bookImage);
                intent.putExtra("infoLink",bookLink);
                intent.putExtra("publisher",bookPublisher);
                intent.putExtra("publishDate",bookPublishDate);
                intent.putExtra("webReaderLink",bookWebReaderLink);
                intent.putExtra("pages",bookPages);
                intent.putExtra("id",idName);
                System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh" + idName);

                startActivity(intent);
                System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh" + idName);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.page_empty, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return super.onOptionsItemSelected(item);
        }
        switch (item.getItemId()) {
            case R.id.clear:
                database.deletaList("likedlist");
                mAdapter = new BookAdapter(this, new ArrayList<Book>());
                ListView listView = (ListView) findViewById(R.id.list);
                listView.setAdapter(mAdapter);
                Toast.makeText(this, "User Favourite books empty finished", Toast.LENGTH_SHORT).show();;
                break;
        }
        return true;
    }

    //likedlist 展示 new activity ，sida
    public void likedlist (MenuItem item){

    }

    //shown history page layout
    public void historyList (MenuItem item) {
        Intent hList = new Intent(this, HistoryListActivity.class);
        hList.putExtra("id", name);
        this.startActivity(hList);
    }

    public void search (MenuItem item){
        Intent MainActivity = new Intent(this, MainActivity.class);
        MainActivity.putExtra("id", name);
        this.startActivity(MainActivity);
    }

    //shown my account page layout
    public void account (MenuItem item){
        Intent AccountActivity = new Intent(this, AccountActivity.class);
        AccountActivity.putExtra("id", name);
        this.startActivity(AccountActivity);
    }
}
