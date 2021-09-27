package com.example.android.bookfinder;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SQLiteHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "BookFinder.db";
    // user table
    public static final String USER_TABLE_NAME = "user";
    public static final String USER_COLUMN_ID = "id";
    public static final String USER_COLUMN_PASSWORD = "password";

    // book table
    public static final String BOOK_TABLE_NAME = "book";
    public static final String BOOK_COLUMN_TITLE = "title";
    public static final String BOOK_COLUMN_AUTHOR = "author";
    public static final String BOOK_COLUMN_INTO_URL = "infoUrl";
    public static final String BOOK_COLUMN_IMAGE_URL = "imageUrl";
    public static final String BOOK_COLUMN_DESCRIPTION = "description";
    public static final String BOOK_COLUMN_PUBLISHER = "publisher";
    public static final String BOOK_COLUMN_PUBLISHED_DATE = "publishedDate";
    public static final String BOOK_COLUMN_PAGES = "pages";
    public static final String BOOK_COLUMN_WEB_READER_LINK = "webReaderLink";

    // LikedList table
    public static final String LIKEDLIST_ID = "id";
    public static final String LISEDLIST_TITLE = "title";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS user");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS book");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS likedlist");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS historylist");

        sqLiteDatabase.execSQL(
                "create table if not exists user " +
                        "(id text primary key, password text)"
        );

        sqLiteDatabase.execSQL(
                "create table if not exists book " +
                        "(title text primary key, author text, infoUrl text, imageUrl text, description text, " +
                        "publisher text, publishedDate text, pages integer, webReaderLink text)"
        );

        sqLiteDatabase.execSQL(
                "create table if not exists likedlist " +
                        "(title text primary key, id text, foreign key (id) references user (id), " +
                        "foreign key (title) references book (title))"
        );

        sqLiteDatabase.execSQL(
                "create table if not exists historylist " +
                        "(title text primary key, id text, foreign key (id) references user (id), " +
                        "foreign key (title) references book (title))"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //onCreate(sqLiteDatabase);
    }

    public boolean insertUser (String id, String pass) {
        if (ifUserExists(id))
            return false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", id);
        contentValues.put("password", pass);
        db.insert("user", null, contentValues);
        return true;
    }

    public void deleteUser(String userID, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(USER_COLUMN_ID, userID, new String[]{userID, password});
        db.delete(USER_COLUMN_PASSWORD, password, new String[]{userID, password});
    }

    public boolean insertBook (Book book) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title",  book.getTitle());
        contentValues.put("author", book.getAuthor());
        contentValues.put("infoUrl", book.getUrl());
        contentValues.put("imageUrl", book.getImageUrl());
        contentValues.put("description", book.getDescription());
        contentValues.put("publisher", book.getPublisher());
        contentValues.put("publishedDate", book.getPublishedDate());
        contentValues.put("pages", book.getPages());
        contentValues.put("webReaderLink", book.getWebReaderLink());

        Cursor c = db.query(BOOK_TABLE_NAME, new String[]{"title"}, "title = ?",
                new String[]{book.getTitle()}, null, null, null);
        if (c.getCount() == 0) {
            db.insert("book", null, contentValues);
        }
        return true;
    }

    public ArrayList<String> getAllUsers() {
        ArrayList<String> array_list = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from user", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(res.getString(res.getColumnIndex(USER_COLUMN_ID)));
            res.moveToNext();
        }
        return array_list;
    }

    public boolean ifUserExists(String id) {
        if(id != null){
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res = db.query(USER_TABLE_NAME,new String[]{"id"},"id=?",new String[]{id},null,null,null);
            return res.moveToFirst();
        } else
            return false;
    }
    public boolean checkUser(String id, String pass){
        String where = "id=? and password=?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.query(USER_TABLE_NAME,null,"id=? and password=?",new String[]{id, pass},null,null,null);
        return res.moveToFirst();
    }

    public void deleteOne(String id, Book book, String tablename) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(tablename,"id = ?  AND title = ?",new String[]{id,book.getTitle()});
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        //db.execSQL("delete from "+ tablename);
        //db.delete("book",null,null);
        db.delete("book",null,null);
        db.delete("historylist",null,null);
        db.delete("likedlist",null,null);
    }

    public void deletaList(String tablename)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(tablename,null,null);
    }

    public boolean addOne(String id, Book book, String tablename) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", id);
        contentValues.put("title", book.getTitle());
        this.insertBook(book);
        db.insert(tablename, null, contentValues);
        return true;
    }

    public boolean checkBook(String userID, String tablename, String title)
    {
        String where = "title = ? AND id = ?";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res= db.query(tablename,new String[]{"Title", "id"},where,new String[]{title,userID},null,null,null);
        return res.getCount() > 0;
    }

    public List<Book> getList(String id, String tablename) {
        List<Book> result = new ArrayList<>();
        if (!ifUserExists(id)) {
            Log.e("USER_NOT_EXIST", "user not found in database");
        }
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "select * " + "from " + tablename + " list " +
                "join user u on u.id = list.id " +
                "join book b on b.title = list.title ";
        Cursor res =  db.rawQuery( query, null );
        Set<String> set = new HashSet<>();
        if (res.getCount() > 0 && res.moveToFirst())
        {
            do {
                set.add(res.getString(res.getColumnIndex(BOOK_COLUMN_TITLE)));
            } while (res.moveToNext());


            for (String t : set) {
                String[] cols = new String[]{BOOK_COLUMN_TITLE, BOOK_COLUMN_AUTHOR, BOOK_COLUMN_INTO_URL, BOOK_COLUMN_IMAGE_URL,
                        BOOK_COLUMN_DESCRIPTION, BOOK_COLUMN_PUBLISHER, BOOK_COLUMN_PUBLISHED_DATE, BOOK_COLUMN_WEB_READER_LINK,
                        BOOK_COLUMN_PAGES};
                System.out.println("Book name: " + t);
                //t=t.replace("'","testets");
                //Cursor resBook = db.rawQuery("SELECT title, author, infoUrl, imageUrl, description, publisher, publishedDate, " +
                //        "webReaderLink, pages FROM book WHERE title= '" + t + "'", null);
                Cursor resBook = db.query("book",null,"title = ?",new String[]{t},null,null,null);

                Log.d("==================1",String.valueOf(resBook.getCount()));

                if (resBook.getCount() == 0) {
                    System.out.println("Book: " + t + " not found!");
                    continue;
                }

                if (resBook.moveToFirst()) {
                    Book book = new Book(resBook.getString(0),
                            resBook.getString(1),
                            resBook.getString(2),
                            resBook.getString(3),
                            resBook.getString(4),
                            resBook.getString(5),
                            resBook.getString(6),
                            resBook.getString(7),
                            Integer.valueOf(8));
                    result.add(book);
                    resBook.close();
                }
            }
        }
        res.close();
        return result;
    }

}
