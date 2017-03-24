package com.vanpeng.javabeen;

import android.view.View;

/**
 * Created by Administrator on 2017/3/7 0007.
 * listview条目里不同按钮点击事件回调
 */

public interface ListItemClickHelp {
    void onClick(View item, View widget, int position, int which);
}
