package com.example.administrator.annotationlearn.main;

public interface MainContract {

    interface View{

        void setTitle(String title);
    }

    interface Presenter {

        void setView(MainContract.View view);

        void loadData();
    }

    interface Model {

        void getTitleFromNet();

        void getTitleFromLocal();
    }
}
