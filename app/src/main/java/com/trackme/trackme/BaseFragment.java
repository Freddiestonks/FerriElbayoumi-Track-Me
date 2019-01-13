package com.trackme.trackme;

import android.support.v4.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class BaseFragment extends Fragment {

    protected DAO dao;

    public BaseFragment() {
        dao = new DAO();
    }
}
