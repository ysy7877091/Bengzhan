package com.vanpeng.javabeen;

import java.util.List;

/**
 * Created by Administrator on 2017/4/25.
 */

public interface PublicInterface {
    void onGetDataSuccess(List<JiangYuFragmentBeen> list);
    void onGetDataError(String errmessage);
    void onEmptyData(String Emptymessage);
}
