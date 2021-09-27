package com.example.android.bookfinder;

import java.io.Serializable;

public class Book implements Serializable {

    private String mTitle;
    private String mAuthor;
    private String mInfoUrl;
    private String mImageUrl;
    private String mDescription;
    private String mPublisher;
    private String mPublishedDate;
    private int mPages;
    private String mWebReaderLink;

    public Book(String title,String author,String infoUrl,String imageUrl,String description,String publisher,String publishedDate,
         String webReaderLink, int pages){

        mTitle = title;
        mAuthor = author;
        mInfoUrl = infoUrl;
        mImageUrl = imageUrl;
        mDescription = description;
        mPublisher = publisher;
        mPublishedDate = publishedDate;
        mPages = pages;
        mWebReaderLink = webReaderLink;

    }

    public String getTitle() {
        return mTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getUrl() { return mInfoUrl; }

    public String getImageUrl() {return mImageUrl; }

    public String getDescription() { return mDescription; }

    public String getPublisher() { return mPublisher; }

    public String getPublishedDate() { return mPublishedDate; }

    public int getPages() { return mPages; }

    public String getWebReaderLink() { return mWebReaderLink; }
}
