package com.example.administrator.annotationlearn.main;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.administrator.annotationlearn.R;
import com.example.mvpanno.anno.GetMVPCom;
import com.example.mvpanno.anno.MVPAnno;
import com.example.mvpanno.anno.MVPAnnoType;
import com.example.mvpanno.processor.RunTimeProcessor;

@MVPAnno(type = MVPAnnoType.VIEW, bussiness = MainConfig.bussiness)
public class MainActivity extends AppCompatActivity implements MainContract.View{

    private TextView tvTitle;

    @GetMVPCom(type = MVPAnnoType.PRESENTER, bussiness = MainConfig.bussiness)
    private MainContract.Presenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RunTimeProcessor.init(this);

        tvTitle = findViewById(R.id.title);

        presenter.setView(this);
        presenter.loadData();
    }

    @Override
    public void setTitle(final String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvTitle.setText(title);
            }
        });
    }
}
