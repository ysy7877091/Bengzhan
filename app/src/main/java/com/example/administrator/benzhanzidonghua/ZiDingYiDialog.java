package com.example.administrator.benzhanzidonghua;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;

/**
 * Created by Administrator on 2017/2/25 0025.
 */

public class ZiDingYiDialog extends AlertDialog {

    protected ZiDingYiDialog(@NonNull Context context) {
        super(context);
    }

    protected ZiDingYiDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity_pop_window);
    }
}
