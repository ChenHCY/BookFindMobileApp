package com.example.android.bookfinder;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    public static final String LOG_TAG = MainActivity.class.getName();

    String url = "https://www.googleapis.com/books/v1/volumes?q=";

    private BookAdapter mAdapter;
    private static final int BOOK_LOADER_ID = 1;
    private ImageView mEmptyStateView;

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    public String userID;
    private SQLiteHelper database;
    private SharedPreferences sp;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //生成测试数据
        database = new SQLiteHelper(this);
        sp = getSharedPreferences("Login", MODE_PRIVATE);
        //断开跳到Login页面 ==> Login Activity
        if(getIntent() != null){
            userID = getIntent().getStringExtra("id");
        }

        if(userID == null && !sp.contains("id")){
            Intent intent = new Intent(this, Login.class);
            //intent.setClass(MainActivity.this, Login.class);//从MainActivity页面跳转至LoginActivity页面
            this.startActivityForResult(intent,1);
        } else{
            userID = sp.getString("id", null);
        }

        //调用demo演示
        System.out.println("================================================================================");
        List<Book>test = database.getList(userID,"likedlist");
        for (int i = 0; i < test.size(); i++) {
            System.out.println(test.get(i).getTitle());
        }

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

        //列表页
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.INVISIBLE);
        mAdapter = new BookAdapter(this, new ArrayList<Book>());
        ListView listView = (ListView) findViewById(R.id.list);
        listView.setAdapter(mAdapter);

        //详情页
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Book currentBook = mAdapter.getItem(position);
                //插入数据demo
                //database.insertBook(currentBook);
                //database.addBookToLikedList("user_id", currentBook);

                String bookTitle = currentBook.getTitle();
                String bookAuthor = currentBook.getAuthor();
                String bookDescription = currentBook.getDescription();
                String bookImage = currentBook.getImageUrl();
                String bookLink = currentBook.getUrl();
                String bookPublisher = currentBook.getPublisher();
                String bookPublishDate = currentBook.getPublishedDate();
                String bookWebReaderLink = currentBook.getWebReaderLink();
                int bookPages = currentBook.getPages();

                Intent intent = new Intent(MainActivity.this,InfoActivity.class);
                intent.putExtra("Title",bookTitle);
                intent.putExtra("Author",bookAuthor);
                intent.putExtra("Description",bookDescription);
                intent.putExtra("Image",bookImage);
                intent.putExtra("infoLink",bookLink);
                intent.putExtra("publisher",bookPublisher);
                intent.putExtra("publishDate",bookPublishDate);
                intent.putExtra("webReaderLink",bookWebReaderLink);
                intent.putExtra("pages",bookPages);
                intent.putExtra("id",userID);

                startActivity(intent);
            }
        });

        final Button searchButton = (Button) findViewById(R.id.search_button);

        mEmptyStateView = (ImageView) findViewById(R.id.background);
        listView.setEmptyView(mEmptyStateView);

        //API列表页
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View loadingIndicator = findViewById(R.id.loading_indicator);
                loadingIndicator.setVisibility(View.VISIBLE);

                url = "https://www.googleapis.com/books/v1/volumes?q=";

                EditText searchField = (EditText) findViewById(R.id.search_bar);
                String fullString = searchField.getText().toString();

                final String LOCATION_SEPARATOR = " ";

                if (fullString.contains(LOCATION_SEPARATOR)) {
                    String[] parts = fullString.split(LOCATION_SEPARATOR);
                    url += parts[0];
                    for (int i=1;i<parts.length;i++)
                    {
                        url += "+" + parts[i];
                    }
                } else {
                    url += fullString;
                }

                url += "&maxResults=20";
                System.out.println(url);
                ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {
                    LoaderManager loaderManager = getLoaderManager();
                    loaderManager.initLoader(BOOK_LOADER_ID, null, MainActivity.this);
                }
                else {
                    mEmptyStateView.setImageResource(R.drawable.images);
                }

                getLoaderManager().restartLoader(BOOK_LOADER_ID,null,MainActivity.this);
            }
        });

    }



    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        return new BookLoader(this, url);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {

        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        mAdapter.clear();

        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
        }

    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        mAdapter.clear();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            userID = data.getStringExtra("id");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return super.onOptionsItemSelected(item);
        }
        return true;
    }

    //likedlist 展示 new activity ，sida
    public void likedlist (MenuItem item){
        SharedPreferences.Editor a = sp.edit();
        a.clear();
        Intent likedlist = new Intent(this, likedlistActivity.class);
        likedlist.putExtra("id", userID);
        this.startActivity(likedlist);
    }

    //shown history page layout
    public void historyList (MenuItem item) {
        Intent hList = new Intent(this, HistoryListActivity.class);
        hList.putExtra("id", userID);
        this.startActivity(hList);
    }

    public void search (MenuItem item){
        Intent MainActivity = new Intent(this, MainActivity.class);
        MainActivity.putExtra("id", userID);
        this.startActivity(MainActivity);
    }

    //shown my account page layout
    public void account (MenuItem item){
        Intent AccountActivity = new Intent(this, AccountActivity.class);
        AccountActivity.putExtra("id", userID);
        this.startActivity(AccountActivity);
    }
}




















