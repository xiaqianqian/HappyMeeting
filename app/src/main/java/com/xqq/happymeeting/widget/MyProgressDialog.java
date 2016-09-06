package com.xqq.happymeeting.widget;

import android.app.ProgressDialog;

/**
 * Created by xqq on 2016/2/28.
 */
public class MyProgressDialog implements Runnable {
    public int currentPro;
    private ProgressDialog pd;

    public MyProgressDialog(ProgressDialog pd) {
        this.pd = pd;
        pd.setMax(100);
        pd.show();
    }

    public void setPd(ProgressDialog pd) {
        this.pd = pd;
    }

    public void setCurrentPro(int currentPro) {
        this.currentPro = currentPro;
    }

    @Override
    public void run() {
        while (true) {
            pd.setProgress(currentPro);
            if (currentPro >= 100) {

                break;
            }
        }
    }
}
