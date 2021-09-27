package com.example.android.bookfinder;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class InfoActivity extends AppCompatActivity
{
    private SQLiteHelper database;
    private boolean check_liked;
    private Book book;
    private String name;
    
    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);

        Intent data = getIntent();
        name = data.getStringExtra("id");
        String title = data.getStringExtra("Title");
        String author = data.getStringExtra("Author");
        String description = data.getStringExtra("Description");
        String image = data.getStringExtra("Image");
        final String infolink = data.getStringExtra("infoLink");
        String publisher = data.getStringExtra("publisher");
        String publishedDate = data.getStringExtra("publishDate");
        final String webReaderLink = data.getStringExtra("webReaderLink");
        int pages = data.getIntExtra("pages",0);
        setTitle(title);

        //书对象
        this.book = new Book(title,author,infolink,image,description,publisher,publishedDate,webReaderLink,0);

        //查询是否加入过喜欢
        database = new SQLiteHelper(this);
        this.check_liked = database.checkBook(name,"likedlist",title);
        TextView likeTextView = (TextView) findViewById(R.id.like);
        System.out.println(check_liked+"==============================check_liked 1");
        if(this.check_liked){
            likeTextView.setText("remove from liked list");
        }else{
            likeTextView.setText("add to liked list");
        }

        //查询是否加入过
        boolean check_history = database.checkBook(name,"historylist",title);
        if(!check_history){
            database.addOne(String.valueOf(name),book,"historylist");
        }

        ImageView imageView = findViewById(R.id.imageview);

        if(image!="") {
            Glide.with(this)
                    .load(image)
                    .into(imageView);
        }
        else {
            imageView.setImageResource(R.drawable.bookicon);
        }

        TextView titleTextView = (TextView) findViewById(R.id.title);
        titleTextView.setText(title);

        TextView authorTextView = (TextView) findViewById(R.id.author);
        authorTextView.setText(author);

        TextView publisherTextView = (TextView) findViewById(R.id.publisher);
        publisherTextView.setText(publisher);

        TextView publishedDateTextView = (TextView) findViewById(R.id.date);
        publishedDateTextView.setText(publishedDate);

        TextView pagesTextView = (TextView) findViewById(R.id.pages);
        if(pages == 0) {
            pagesTextView.setText("Info not available.");
        }
        else {
            pagesTextView.setText(String.valueOf(pages));
        }

        TextView descriptionTextView = (TextView) findViewById(R.id.description);
        descriptionTextView.setText(description);

        TextView infoLinkTextView = (TextView) findViewById(R.id.infolink);
        infoLinkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Uri bookUri = Uri.parse(infolink);

                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                websiteIntent.putExtra("id", name);
                startActivity(websiteIntent);

            }
        });

        TextView webReaderLinkTextView = (TextView) findViewById(R.id.readerlink);
        webReaderLinkTextView.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View view) {

                    if(webReaderLink!="")
                    {
                        Uri readerUri = Uri.parse(webReaderLink);

                        Intent websiteIntent = new Intent(Intent.ACTION_VIEW, readerUri);

                        startActivity(websiteIntent);
                    }
                    else {
                        Toast.makeText(getBaseContext(),"Web Link Unavailable",Toast.LENGTH_SHORT);
                    }
                }
            });
    }

    //likelist 按钮事件 触发插入
    public void like(View v){
        if(this.check_liked){
            System.out.println("==============================check_liked 2");
            database.deleteOne(name,this.book,"likedlist");

            //回到likedlist页
            Intent likedlist = new Intent(this, likedlistActivity.class);
            likedlist.putExtra("id",name);
            this.startActivity(likedlist);
        }else{
            System.out.println("==============================check_liked 3");
            database.addOne(name,this.book,"likedlist");

            //回到likedlist页
            Intent search = new Intent(this, MainActivity.class);
            search.putExtra("id",name);
            this.startActivity(search);
        }
    }
}
