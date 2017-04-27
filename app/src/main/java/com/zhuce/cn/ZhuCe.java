package com.zhuce.cn;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.benzhanzidonghua.CommonMethod;
import com.example.administrator.benzhanzidonghua.MyProgressDialog;
import com.example.administrator.benzhanzidonghua.Path;
import com.example.administrator.benzhanzidonghua.R;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ZhuCe extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private TextView et_zhuCeSex;
    private ImageView iv_liJiZhuCe;
    private ImageView iv_sanjiao;
    private ImageView iv_shangchuna;
    private TextView tv_quxiao;
    private TextView et_zhuCe_fenzu;
    private EditText et_zhuCeName;
    private EditText phone;
    private EditText et_zhuCe_miMA;
    private EditText et_zhuCe_miMaNext;
    private PopupWindow popupWindow;
    private String[] tv;
    private String s_fenzu;
    private ImageView iv_yishangchuan;
    private ImageView iv_ceshi;
    private String name="";
    private String idCard;
    private String phone1="";
    private String mima="";
    private String mimat="";
    Bitmap myBitmap = null;
    private byte[] mContent;
    private String mIMEI = "";
    private String sex;
    private String bytes="";
    private MyProgressDialog progressDialog=null;
    private MyProgressDialog pd;
    private String groupArr [];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zhuce_main);
        CommonMethod.setStatuColor(ZhuCe.this,R.color.white);
        Log.e("warn",getFilesDir()+"");
        phone = (EditText) findViewById(R.id.et_zhuCe_phone);
        et_zhuCeName = (EditText) findViewById(R.id.et_zhuCeName);
        et_zhuCe_miMA = (EditText) findViewById(R.id.et_zhuCe_miMA);
        et_zhuCe_miMaNext = (EditText) findViewById(R.id.et_zhuCe_miMaNext);
        //et_zhuCe_IDCard = (EditText) findViewById(R.id.et_zhuCe_IDCard);
        et_zhuCeSex = (TextView) findViewById(R.id.et_zhuCeSex);
        tv_quxiao = (TextView) findViewById(R.id.tv_quxiao);
        et_zhuCe_fenzu = (TextView) findViewById(R.id.et_zhuCe_fenzu);
        iv_shangchuna = (ImageView) findViewById(R.id.iv_shangchuna);
        iv_sanjiao = (ImageView) findViewById(R.id.iv_sanjiao);
        iv_yishangchuan = (ImageView) findViewById(R.id.iv_yishangchuan);
        iv_ceshi = (ImageView) findViewById(R.id.iv_ceshi);
        iv_liJiZhuCe = (ImageView) findViewById(R.id.iv_liJiZhuCe);

        et_zhuCeSex.setOnClickListener(this);
        iv_shangchuna.setOnClickListener(this);
        iv_sanjiao.setOnClickListener(this);
        phone.setOnClickListener(this);
        tv_quxiao.setOnClickListener(this);
        iv_liJiZhuCe.setOnClickListener(this);

        //获取分组信息
        if(pd==null) {
            pd = new MyProgressDialog(ZhuCe.this, false, "获取分组信息...");
        }
        new Thread(getGroup).start();
    }
    String arr []=null;
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_quxiao:
                finish();
                break;
            case R.id.et_zhuCeSex:
                arr = new String[]{"男", "女"};
                new AlertDialog.Builder(ZhuCe.this).setTitle("请选择")
                        .setSingleChoiceItems(arr, 0,
                                new DialogInterface.OnClickListener() {

                                    public void onClick(DialogInterface dialog, int which) {
                                        sex = arr[which];
                                        et_zhuCeSex.setText(arr[which]);
                                        et_zhuCeSex.setTextColor(getResources().getColor(R.color.black));
                                        dialog.dismiss();
                                    }
                                }
                        ).setNegativeButton("取消", null).show();
                break;

            case R.id.iv_shangchuna:
                selectPhoto();
                break;

            case R.id.iv_sanjiao:
                //showPopwindow();
                if(groupArr!=null) {
                    showPopwindow(groupArr);
                }
                break;
            case R.id.iv_liJiZhuCe:

                name = et_zhuCeName.getText().toString().trim();
                phone1 = phone.getText().toString().trim();
                //idCard = et_zhuCe_IDCard.getText().toString().trim();
                mimat = et_zhuCe_miMaNext.getText().toString().trim();
                mima = et_zhuCe_miMA.getText().toString().trim();
                if(!mimat.equals(mima)){
                    Toast.makeText(getApplicationContext(),"密码不一致",Toast.LENGTH_SHORT).show();
                   return;
                }
                if (myBitmap != null) {
                    bytes = Bitmap2Base64(myBitmap);
                }
                if(name.equals("")){
                    Toast.makeText(getApplicationContext(),"姓名不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(mima.equals("")){
                    Toast.makeText(getApplicationContext(),"密码不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(et_zhuCe_fenzu.getText().toString().equals("")||et_zhuCe_fenzu.getText().toString()==null){
                    Toast.makeText(getApplicationContext(),"分组不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(phone1.equals("")||phone1==null){
                    Toast.makeText(getApplicationContext(),"手机号不能为空",Toast.LENGTH_SHORT).show();
                    return;
                }
                TelephonyManager tm = (TelephonyManager) this.getSystemService(TELEPHONY_SERVICE);
                mIMEI = tm.getDeviceId();//获取智能设备唯一编号两次密码不符合
                if(progressDialog==null) {
                    progressDialog = new MyProgressDialog(ZhuCe.this, false, "注册中...");
                }
                new Thread(networkTask).start();
                
                break;
        }
    }
    Runnable networkTask = new Runnable() {
        private Object object;
        @Override
        public void run() {
            try{
                // 命名空间
                String nameSpace = "http://tempuri.org/";
                // 调用的方法名称
                String methodName = "Insert_LoginUser";
                // EndPoint
                String endPoint = "http://beidoujieshou.sytxmap.com:5963/GPSService.asmx?wsdl";
                // SOAP Action
                String soapAction = "http://tempuri.org/Insert_LoginUser";
                // 指定WebService的命名空间和调用的方法名
                SoapObject rpc = new SoapObject(nameSpace, methodName);
                //设置需调用WebService接口需要传入的参数CarNum
                Log.e("warn",phone1);
                Log.e("warn",mimat);
                Log.e("warn",name);
                Log.e("warn",sex); Log.e("warn",mIMEI);
                //Log.e("warn",idCard);
                Log.e("warn",bytes.toString());

                rpc.addProperty("loginName",phone1);
                rpc.addProperty("loginPwd",mimat);
                rpc.addProperty("NAME",name);
                rpc.addProperty("SEX",sex);
                rpc.addProperty("IMEI",mIMEI);
                rpc.addProperty("ShenFenZhengID","");
                rpc.addProperty("PhoneNum",phone1);
                rpc.addProperty("GROUPNAME",et_zhuCe_fenzu.getText().toString());
                rpc.addProperty("PERIMG", bytes);


                // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(rpc);
                AndroidHttpTransport ht = new AndroidHttpTransport(endPoint);
                ht.debug=true;
                Log.e("warn","50");
                (new MarshalBase64()).register(envelope);
                try {
                    // 调用WebService
                    ht.call(soapAction,envelope);
                    object = envelope.getResponse();
                } catch (Exception e) {
                    Message msg = Message.obtain();
                    msg.what=0;
                    hanlder.sendMessage(msg);
                }
                    Message msg = Message.obtain();
                    msg.what=1;
                    msg.obj=object;
                    hanlder.sendMessage(msg);
            } catch (Exception e){
                Message msg = Message.obtain();
                msg.what=0;
                hanlder.sendMessage(msg);
            }
        }
    };
    private Handler hanlder = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i = msg.what;
            if(i==0){
                progressDialog.dismiss();
                progressDialog=null;
                Toast.makeText(getApplicationContext(),"注册失败",Toast.LENGTH_SHORT).show();
            }else if(i==1){
                progressDialog.dismiss();
                progressDialog=null;
                Object obj = msg.obj;
                String s = obj.toString();
                if(s.equals("0")){
                    Toast.makeText(getApplicationContext(),"注册成功" ,Toast.LENGTH_SHORT).show();
                    finish();
                }else if(s.equals("1")){

                    Toast.makeText(getApplicationContext(),"账户已存在",Toast.LENGTH_SHORT).show();
                }else if(s.equals("2")){
                    Toast.makeText(getApplicationContext(),"系统异常，通知管理员",Toast.LENGTH_SHORT).show();
                }

            }else{
                progressDialog.dismiss();
                progressDialog=null;
                Toast.makeText(getApplicationContext(),"获取信息失败",Toast.LENGTH_SHORT).show();
            }
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //相册
              if(requestCode == 1) {
                  ContentResolver resolver = getContentResolver();
                  if (data != null) {
                      if (requestCode == 1) {
                          try {
                              // 获得图片的uri
                              Uri originalUri = data.getData();
                              // 将图片内容解析成字节数组
                              mContent = readStream(resolver.openInputStream(Uri.parse(originalUri.toString())));
                              // 将字节数组转换为ImageView可调用的Bitmap对象
                              if(myBitmap!=null){
                                  myBitmap=null;
                              }
                              myBitmap = getPicFromBytes(mContent, null);
                              // //把得到的图片绑定在控件上显示
                              iv_ceshi.setImageBitmap(myBitmap);
                          } catch (Exception e) {
                              System.out.println(e.getMessage());
                          }
                      }
                  }
              }
        //照相机
        if(requestCode== Activity.RESULT_CANCELED){//未拍照返回来
            //Toast.makeText(this, "11111111", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == REQUEST_CODE_CAPTURE_CAMEIA) {//拍照之后返回来的

                FileInputStream fis = null;
                try {
                    Log.e("sdPath2", picPath);
                    //把图片转化为字节流
                    if(Build.VERSION.SDK_INT >= 24){
                        fis = new FileInputStream(getFilesDir()+"/images/"+picPath1);
                    }else{
                        fis = new FileInputStream(picPath);
                    }
                    if (myBitmap != null) {
                        myBitmap = null;
                    }
                    //把流转化图片
                    myBitmap = BitmapFactory.decodeStream(fis);
                    File file;
                    //文件大小 picPath目标图片地址
                    if (Build.VERSION.SDK_INT >= 24){
                       file = new File(getFilesDir()+"/images/"+picPath1);
                    }else{
                        file = new File(picPath);
                    }
                    Log.e("warn", file.length() / 1024 + "kb");
                    if (myBitmap != null) {
                        iv_ceshi.setImageBitmap(myBitmap);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (fis != null) {
                            fis.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            super.onActivityResult(requestCode, resultCode, data);

    }
    /*
     * 将bitmap转换为base64字节数组
     */
    public String Bitmap2Base64(Bitmap bitmap) {
        try {
            // 先将bitmap转换为普通的字节数组
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            byte[] buffer = out.toByteArray();
            // 将普通字节数组转换为base64数组
            //byte[] encode = Base64.encode(buffer, Base64.DEFAULT);
            String encode = Base64.encodeToString(buffer, Base64.DEFAULT);
            return encode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap getPicFromBytes(byte[] bytes, BitmapFactory.Options opts) {
        if (bytes != null)
            if (opts != null)
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length, opts);
            else
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return null;
    }

    public static byte[] readStream(InputStream inStream) throws Exception {
        byte[] buffer = new byte[1024];
        int len = -1;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inStream.close();
        return data;

    }

    private void closePopwindow() {
        if (popupWindow != null) {
            popupWindow.dismiss();
            popupWindow = null;
        }
    }

    private void showPopwindow(String arr []) {

        //LinearLayout ll_IDCard = (LinearLayout) findViewById(R.id.ll_IDCard);
        View addview = LayoutInflater.from(this).inflate(R.layout.poplistview, null);
        addinit(addview,arr);
        popupWindow = new PopupWindow(addview,400, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setTouchable(true);//popupWindow可触摸
        popupWindow.setOutsideTouchable(true);//点击popupWindow以外区域消失
        popupWindow.setTouchInterceptor(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                Log.i("mengdd", "onTouch : ");
                return false;
            }
        });
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.color.white));
        //popupWindow.showAtLocation(ll_IDCard, Gravity.TOP,0,0);
        popupWindow.showAsDropDown(iv_sanjiao, 0, 0);
    }

    private void addinit(View view,String arr []) {
        List<zhuCe_POPListView> list = new ArrayList<>();
        ;
        for (int i = 0; i < arr.length; i++) {
            zhuCe_POPListView bd = new zhuCe_POPListView();
            bd.setText(arr[i]);
            list.add(bd);
        }
        ListView listview = (ListView) view.findViewById(R.id.BD_carListView);
        listview.setAdapter(new zhuCe_POPListviewAdapter(ZhuCe.this, list));
        listview.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        closePopwindow();
        if(groupArr!=null) {
            s_fenzu = groupArr[position];
            et_zhuCe_fenzu.setText(groupArr[position]);
            et_zhuCe_fenzu.setTextColor(getResources().getColor(R.color.black));
        }
    }
    Runnable getGroup = new Runnable() {
        @Override
        public void run() {
            try {
                Log.e("warn", "30");
                // 命名空间
                String nameSpace = "http://tempuri.org/";
                // 调用的方法名称Get_OilAlarmInfo_List
                String methodName = "Get_GroupInfo_List";
                // EndPoint
                String endPoint = Path.get_ZanShibeidouPath();
                // SOAP Action
                String soapAction = "http://tempuri.org/Get_GroupInfo_List";
                // 指定WebService的命名空间和调用的方法名
                SoapObject rpc = new SoapObject(nameSpace, methodName);
                //设置需调用WebService接口需要传入的参数CarNum
                //rpc.addProperty("PerID",SiJiPERID);
                // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(rpc);

                AndroidHttpTransport ht = new AndroidHttpTransport(endPoint);
                ht.debug = true;
                try {
                    // 调用WebService
                    ht.call(soapAction, envelope);
                } catch (Exception e) {
                    Message msg = Message.obtain();
                    msg.what = 0;
                    SiJiHandler.sendMessage(msg);
                }
                SoapObject object;
                object = (SoapObject) envelope.getResponse();
                // 得到服务器传回的数据 数据时dataset类型的
                int count1 = object.getPropertyCount();
                if(count1==0){
                    // progressDialog.dismiss();
                    pd.dismiss();
                    pd=null;
                    Toast.makeText(ZhuCe.this,"无分组信息",Toast.LENGTH_SHORT).show();
                    return;
                }
                StringBuffer sb;
                Log.e("warn", String.valueOf(count1));
                if (count1 > 0) {
                    sb = new StringBuffer();
                    for (int i = 0; i < count1; i++) {
                        SoapObject soapProvince = (SoapObject)object.getProperty(i);
                    /*Log.e("warn", soapProvince.getProperty("Get_OilAlarmInfo_ListResult").toString() + ":返回id");//dataset数据类型
                    String str = soapProvince.getProperty("Get_OilAlarmInfo_ListResult").toString();*/
                        Log.e("warn", soapProvince.getProperty("GROUPNAME").toString() + ":");
                        if(i==count1-1) {
                            sb.append(soapProvince.getProperty("GROUPNAME").toString());
                        }else{
                            sb.append(soapProvince.getProperty("GROUPNAME").toString() + ",");
                        }
                        //lieBiao.setD_NUM(soapProvince.getProperty("D_NUM").toString());

                    }
                    Message msg = Message.obtain();
                    msg.what = 1;
                    msg.obj = sb.toString();
                    SiJiHandler .sendMessage(msg);
                }
            } catch (Exception e) {
                Message msg = Message.obtain();
                msg.what = 0;
                SiJiHandler .sendMessage(msg);
            }
        }
    };


    private Handler SiJiHandler  = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
                int i = msg.what;
            if(i==0){
                pd.dismiss();
                pd=null;
                Toast.makeText(ZhuCe.this, "网络或服务器异常，获取分组信息失败", Toast.LENGTH_SHORT).show();
            }else if(i==1){
                pd.dismiss();
                pd=null;
                 String str =(String)msg.obj;
                Log.e("warn",str+":group");
                groupArr= str.split(",");
                et_zhuCe_fenzu.setText(groupArr[0]);
            }
        }
    };
    private String [] select={"相机","相册"};
    private void selectPhoto(){

        AlertDialog.Builder builder = new AlertDialog.Builder(ZhuCe.this);
        builder.setSingleChoiceItems(select, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(select[which].equals("相机")){
                    CameraMethod();
                    dialog.dismiss();
                }else if(select[which].equals("相册")){
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 1);
                    dialog.dismiss();
                }
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
    private int REQUEST_CODE_CAPTURE_CAMEIA =0;
    private String sdPath;//sd卡路径
    private String picPath;//图片路径
    private String picPath1;//图片路径
    private void CameraMethod(){
        sdPath = Environment.getExternalStorageDirectory().getPath();
        picPath = sdPath + "/" + System.currentTimeMillis() + "temp.png";
        picPath1=System.currentTimeMillis() + "temp.png";
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            if(Build.VERSION.SDK_INT>=24) {//7.0以上调用相机
                File file = new File(getFilesDir(), "images");//getFilesDir()+"/images/"+picPath1图片地址  images必须和res/xml/file_piths中path对应
                File newFile = new File(file, picPath1);
                if (!newFile.getParentFile().exists()) newFile.getParentFile().mkdirs();
                Uri imageUri = FileProvider.getUriForFile(ZhuCe.this, "com.jph.takephoto.fileprovider", newFile);//通过FileProvider创建一个content类型的Uri
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
                startActivityForResult(intent, REQUEST_CODE_CAPTURE_CAMEIA);
            }else{//7.0以下调用相机（不包括7.0）
                Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
                Uri uri = Uri.fromFile(new File(picPath));        //为拍摄的图片指定一个存储的路径
                getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(getImageByCamera, REQUEST_CODE_CAPTURE_CAMEIA);
            }
            /*Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
            Uri uri = Uri.fromFile(new File(picPath));        //为拍摄的图片指定一个存储的路径
            getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(getImageByCamera, REQUEST_CODE_CAPTURE_CAMEIA);*/
        }
        else {
            Toast.makeText(getApplicationContext(), "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(myBitmap!=null) {
            myBitmap.recycle();
        }
    }
}
