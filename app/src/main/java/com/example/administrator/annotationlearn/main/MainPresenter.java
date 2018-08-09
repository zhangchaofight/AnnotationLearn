package com.example.administrator.annotationlearn.main;

import android.util.Log;

import com.example.mvpanno.anno.GetMVPCom;
import com.example.mvpanno.anno.MVPAnno;
import com.example.mvpanno.anno.MVPAnnoType;
import com.example.mvpanno.processor.RunTimeProcessor;

@MVPAnno(type = MVPAnnoType.PRESENTER, bussiness = MainConfig.bussiness)
public class MainPresenter implements MainContract.Presenter {

    private static final String TAG = MainPresenter.class.getSimpleName();

    @GetMVPCom(type = MVPAnnoType.VIEW, bussiness = MainConfig.bussiness)
    private MainContract.View view;

    @GetMVPCom(type = MVPAnnoType.MODEL, bussiness = MainConfig.bussiness)
    private MainContract.Model model;

    public MainPresenter() {
        RunTimeProcessor.init(this);
    }

    @Override
    public void setView(MainContract.View view) {
        this.view = view;
    }

    @Override
    public void loadData() {
        Log.d(TAG, "loadData: invoke");
        model.getTitleFromLocal();
        model.getTitleFromNet();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                view.setTitle("hello inject");
            }
        }).start();
    }
}
