package com.trackme.trackme;

import android.support.v7.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    protected DAO dao;

    public BaseActivity() {
        dao = new DAO();
    }

}
