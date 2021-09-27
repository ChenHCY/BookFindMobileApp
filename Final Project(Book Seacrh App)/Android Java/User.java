package com.example.android.bookfinder;

import java.io.Serializable;

public class User implements Serializable {
    private String uName;
    private String uPword;

    public User(String userName,String passWord)
    {
        uName = userName;
        uPword = passWord;
    }

    public String getuName() {
        return uName;
    }

    public String getuPword() {
        return uPword;
    }
}
