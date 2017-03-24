package com.example.administrator.benzhanzidonghua;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.TextView;

/**
 * 加载画面
 */
public class MyProgressDialog extends ProgressDialog {

	private Dialog progressDialog;
	private TextView msg;

	/**
	 * 创建默认框，不可取消，
	 * 
	 * @param context
	 */
	public MyProgressDialog(Context context, boolean cancelable, String msgStr) {
		super(context);
		// TODO 自动生成的构造函数存根


		progressDialog = new Dialog(context,R.style.progress_dialog);//样式

		progressDialog.setContentView(R.layout.wait_dialog);//布局
		progressDialog.getWindow().setBackgroundDrawableResource(
				android.R.color.transparent);//背景
		progressDialog.setCancelable(cancelable);//代表dialog点击屏幕是否消失false代表不消失
		msg = (TextView) progressDialog.findViewById(R.id.id_tv_loadingmsg);

		if (msgStr.isEmpty()) {
			msg.setText("加载中...");
		} else {
			msg.setText(msgStr);
		}
		progressDialog.show();
	}

	@Override
	public void dismiss() {
		// TODO 自动生成的方法存根
		progressDialog.dismiss();
	}
}
