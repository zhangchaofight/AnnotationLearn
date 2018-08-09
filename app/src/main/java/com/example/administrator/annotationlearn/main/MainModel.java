package com.example.administrator.annotationlearn.main;

import android.util.Log;

import com.example.mvpanno.anno.MVPAnno;
import com.example.mvpanno.anno.MVPAnnoType;

import static com.example.administrator.annotationlearn.main.MainConfig.TAG;

@MVPAnno(type = MVPAnnoType.MODEL, bussiness = MainConfig.bussiness)
public class MainModel implements MainContract.Model{

    public MainModel() {

    }

    @Override
    public void getTitleFromNet() {
        Log.d(TAG, "getTitleFromNet: invoke");
    }

    @Override
    public void getTitleFromLocal() {
        Log.d(TAG, "getTitleFromLocal: invoke");
    }
}
