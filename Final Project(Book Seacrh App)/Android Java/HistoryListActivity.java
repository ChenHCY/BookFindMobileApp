package com.example.android.bookfinder;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
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

public class HistoryListActivity extends AppCompatActivity {
    private BookAdapter HistoryAdapter;
    public DrawerLayout Historylayout;
    public ActionBarDrawerToggle actionToggle;
    private SQLiteHelper Hdatabase;
    public String name;
    public User user;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_list);

        Intent data = getIntent();
        name = data.getStringExtra("id");

        //左侧抽屉显示
        // The left layout instance to toggle the menu icon to open
        Historylayout = findViewById(R.id.my_drawer_layout);
        actionToggle = new ActionBarDrawerToggle(this, Historylayout, R.string.nav_open, R.string.nav_close);
        //shown the icon to open layout
        Historylayout.addDrawerListener(actionToggle);
        actionToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //create the data of book
        Hdatabase = new SQLiteHelper(this);
        SQLiteDatabase db = Hdatabase.getWritableDatabase();
        ArrayList<Book> books = (ArrayList<Book>) Hdatabase.getList(name,"historylist");
        Log.d("==================2",String.valueOf(books.size()));
        for (int i = 0; i < books.size(); i++) {
            System.out.println(books.get(i).getTitle());
            Log.d("==================3","test2");
            Log.d("====================4",books.get(i).getTitle());
        }

        //the list layout page
        HistoryAdapter = new BookAdapter(this, books);
        ListView historyView = (ListView) findViewById(R.id.list);
        historyView.setAdapter(HistoryAdapter);

        // the information layout page
        historyView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Define create the read book history
                Book bookHistory = HistoryAdapter.getItem(position);
                //add the book data value to the list
                Intent historyIntent = new Intent(HistoryListActivity.this,InfoActivity.class);
                historyIntent.putExtra("Title",bookHistory.getTitle()); //the book title
                historyIntent.putExtra("Author",bookHistory.getAuthor()); //the book Author
                historyIntent.putExtra("Description",bookHistory.getDescription()); //the book Description
                historyIntent.putExtra("Image",bookHistory.getImageUrl()); //the book image
                historyIntent.putExtra("infoLink",bookHistory.getUrl()); //the book information link
                historyIntent.putExtra("publisher",bookHistory.getPublishedDate()); //the book Published Date
                historyIntent.putExtra("webReaderLink",bookHistory.getWebReaderLink());//the book web link
                historyIntent.putExtra("pages",bookHistory.getPages());//the book History page
                historyIntent.putExtra("id", name);
                //start to shown the History Activity page
                startActivity(historyIntent);
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
        if (actionToggle.onOptionsItemSelected(item)) {
            return super.onOptionsItemSelected(item);
        }
        switch (item.getItemId()) {
            case R.id.clear:
                Hdatabase.deletaList("historylist");
                HistoryAdapter = new BookAdapter(this, new ArrayList<Book>());
                ListView listView = (ListView) findViewById(R.id.list);
                listView.setAdapter(HistoryAdapter);
                Toast.makeText(this, "User Browsing History empty finished", Toast.LENGTH_SHORT).show();;
                break;
        }
        return true;
    }

    //shown the historyList page
    public void historyList (MenuItem item){

    }

    //shown history page layout
    public void likedlist (MenuItem item) {
        Intent hList = new Intent(this, likedlistActivity.class);
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
