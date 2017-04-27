package com.example.administrator.benzhanzidonghua;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhuce.cn.ZhuCe;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.AndroidHttpTransport;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/4/1.
 * 加油记录录入
 */

public class AddOilHistoryWrite extends AppCompatActivity {
    private int REQUEST_CODE_CAPTURE_CAMEIA =0;
    private ImageView photo_car;
    private String sdPath;
    private String picPath;
    private String picPath1;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.jiayoujilu_luru);
        init();
    }
    private TextView time;//加油时间
    private  EditText Oil;//加油量
    private EditText money;//加油金额
    private TextView carNum;//车牌号
    private EditText name;//姓名
    private Bitmap bitmap;
    private String image;
    private MyProgressDialog progressDialog;
    private String [] arr;
    private void init(){
        sdPath = Environment.getExternalStorageDirectory().getPath();
        picPath = sdPath + "/" + System.currentTimeMillis() + "temp.png";
        picPath1 = System.currentTimeMillis() + "temp.png";
        //当前时间转为yyyy-MM-dd HH:mm:ss
        carNum = (TextView)findViewById(R.id.tv_jiayoujilu_carNumber);
        name = (EditText)findViewById(R.id.tv_jiayoujilu_Name);


        String information = getIntent().getStringExtra("personInformation");
        Log.e("warn",information);

        arr=information.split(";");
        for(int j=0;j<arr.length;j++){
            arr[j]=arr[j].substring(arr[j].indexOf("=")+1);
            Log.e("warn",arr[j]);
        }

        if (information.contains("CARNUM")){
            if(!arr[9].equals("anyType{}")) {
                carNum.setText(arr[9]);
            }else{
                carNum.setText("请选择");
            }
        }else{
            carNum.setText("请选择");
        }
        if(!arr[1].equals("anyType{}")){
            name.setText(arr[1]);
        }else{
            name.setText("");
        }
        Log.e("warn",String.valueOf(arr.length));

        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateString = formatter.format(currentTime);

        Button write_back = (Button)findViewById(R.id.write_back);
        write_back.setOnClickListener(new  AddOilHistoryWriteListener());
        carNum.setOnClickListener(new AddOilHistoryWriteListener());
        time = (TextView)findViewById(R.id.tv_jiayoujilu_shiJian);
        time.setText(dateString);
        Oil = (EditText)findViewById(R.id.tv_jiayoujilu_jiaYouLiang);
        money = (EditText)findViewById(R.id.tv_jiayoujilu_RMB);
        //LinearLayout photo=  (LinearLayout) findViewById(R.id.iv_fapiao_Phone);
        photo_car  = (ImageView)findViewById(R.id.photo_car); //车辆照片
        photo_car.setOnClickListener(new  AddOilHistoryWriteListener());
        ImageView submit = (ImageView)findViewById(R.id.iv_yaoujilu_baocunxinxi);
        submit.setOnClickListener(new  AddOilHistoryWriteListener());
    }
    private class AddOilHistoryWriteListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.photo_car://判断sd卡
                    selectPhoto();
                    break;
                case R.id.iv_yaoujilu_baocunxinxi: if(bitmap!=null){
                                                            image=Bitmap2Base64(bitmap);
                                                            }else{
                                                                    Toast.makeText(AddOilHistoryWrite.this, "请上传照片", Toast.LENGTH_SHORT).show();
                                                                    return;
                                                            }
                                                        if(name.getText().toString().equals("")||name.getText().toString()==null){
                                                            Toast.makeText(AddOilHistoryWrite.this, "姓名不能为空", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }
                                                        if(carNum.getText().toString().equals("请选择")||carNum.getText().toString()==null){
                                                            Toast.makeText(AddOilHistoryWrite.this, "车牌号不能为空", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }
                                                        if( Oil.getText().toString().equals("")|| Oil.getText().toString()==null){
                                                            Toast.makeText(AddOilHistoryWrite.this, "加油量不能为空", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }
                                                        if( money.getText().toString().equals("")|| money.getText().toString()==null){
                                                            Toast.makeText(AddOilHistoryWrite.this, "加油金额不能为空", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        }
                                                        progressDialog = new MyProgressDialog(AddOilHistoryWrite.this, false, "加载中...");
                                                        new Thread(insert_OilFormation).start();
                                                        break;
                case R.id.write_back:
                                        finish();
                                        break;
                case R.id.tv_jiayoujilu_carNumber:
                                                        String ALLcarNum=getIntent().getStringExtra("ALLcarNum");
                                                        if(ALLcarNum==null){
                                                            getCarNum();
                                                            if(allcarnum!=null){
                                                                CarPai(allcarnum);
                                                            }
                                                        }else {
                                                            CarPai(ALLcarNum);
                                                        }
                                                        break;
            }
        }
    }
    //从照相机获取照片
    protected void getImageFromCamera() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            if(Build.VERSION.SDK_INT>=24) {//7.0以上调用相机
                File file = new File(getFilesDir(), "images");//getFilesDir()+"/images/"+picPath1图片地址  images必须和res/xml/file_piths中path对应
                File newFile = new File(file, picPath1);
                if (!newFile.getParentFile().exists()) newFile.getParentFile().mkdirs();
                Uri imageUri = FileProvider.getUriForFile(AddOilHistoryWrite.this, "com.jph.takephoto.fileprovider", newFile);//通过FileProvider创建一个content类型的Uri
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
                startActivityForResult(intent, REQUEST_CODE_CAPTURE_CAMEIA);
            }else{
                Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
                Uri uri = Uri.fromFile(new File(picPath));        //为拍摄的图片指定一个存储的路径
                getImageByCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(getImageByCamera, REQUEST_CODE_CAPTURE_CAMEIA);
            }

        }
        else {
            Toast.makeText(getApplicationContext(), "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
        }
    }
    /*但是，有时候我们会发现用相机拍摄获取照片的时候，得到的 uri 是 null 的，这是因为android把拍摄的图片封装到bundle中传递回来，但是根据不同的机器获得相片的方式不太一样，可能有的相机能够通过
    inten.getData()获取到uri
    然后再根据uri获取数据的路径，在封装成bitmap，但有时候有的相机获取到的是null的，这时候我们该怎么办呢？
    其实这时候我们就应该从bundle中获取数据，通过
            (Bitmap) bundle.get("data")
    获取到相机图片的bitmap数据。
    为了能够同时适应上述两种情况，我们这时候就应该在获取图片时做判断了。我们可以在响应的时候做一个判断：*/
    private byte[] mContent;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode== Activity.RESULT_CANCELED){//未拍照返回来
            //Toast.makeText(this, "11111111", Toast.LENGTH_SHORT).show();

        }
       if (requestCode == REQUEST_CODE_CAPTURE_CAMEIA) {//拍照之后返回来的

           FileInputStream fis = null;
           try {  Log.e("sdPath2",picPath);
               //把图片转化为字节流
               if(Build.VERSION.SDK_INT>=24) {//7.0以上调用相机

                   fis = new FileInputStream(getFilesDir()+"/images/"+picPath1);

                   }  else{
                   fis = new FileInputStream(picPath);
               }

               //把流转化图片
               if( bitmap!=null){
                   bitmap=null;
               }
               bitmap = BitmapFactory.decodeStream(fis);

               //文件大小 picPath目标图片地址
              /* File file = new File(picPath);
               Log.e("warn",file.length()/1024+"kb");*/
                if(bitmap!=null){
                    //photo_car.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
                    photo_car.setImageBitmap(bitmap);
                }

           } catch (Exception e) {
               e.printStackTrace();
           }finally {
               try {
                   if(fis!=null) {
                       fis.close();
                   }
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
       }
        //相册
        if(requestCode == 1) {
            LinearLayout iv_fapiao_Phone = (LinearLayout)findViewById(R.id.iv_fapiao_Phone);
            LinearLayout.LayoutParams linearParams =(LinearLayout.LayoutParams) photo_car.getLayoutParams(); //取控件textView当前的布局参数 linearParams.height = 20;// 控件的高强制设成20
            linearParams.width = iv_fapiao_Phone.getHeight();// 控件的宽强制设成30
            linearParams.height = iv_fapiao_Phone.getHeight();
            ContentResolver resolver = getContentResolver();
            if (data != null) {
                if (requestCode == 1) {
                    try {
                        // 获得图片的uri
                        Uri originalUri = data.getData();
                        // 将图片内容解析成字节数组
                        mContent = readStream(resolver.openInputStream(Uri.parse(originalUri.toString())));
                        // 将字节数组转换为ImageView可调用的Bitmap对象
                        if( bitmap!=null){
                            bitmap=null;
                        }
                        bitmap = getPicFromBytes(mContent, null);
                        // //把得到的图片绑定在控件上显示
                        if(bitmap!=null){
                            photo_car.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
                            photo_car.setImageBitmap(bitmap);
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        }
            //to do find the path of pic
    }
    private Runnable insert_OilFormation = new Runnable() {
        private Object object;
        @Override
        public void run() {
            try {
                Log.e("warn", "30");
                // 命名空间
                String nameSpace = "http://tempuri.org/";
                // 调用的方法名称
                String methodName = "Insert_JYRecord";
                // EndPoint
                String endPoint = Path.get_ZanShibeidouPath();
                // SOAP Action
                String soapAction = "http://tempuri.org/Insert_JYRecord";
                // 指定WebService的命名空间和调用的方法名
                SoapObject rpc = new SoapObject(nameSpace, methodName);
                //设置需调用WebService接口需要传入的参数CarNum
                rpc.addProperty("PerID",arr[8]);
                rpc.addProperty("CARNUM",carNum.getText().toString());
                rpc.addProperty("JYTime",time.getText().toString());
                rpc.addProperty("JYLiang",Oil.getText().toString());
                rpc.addProperty("JYJinE",money.getText().toString());
                rpc.addProperty("JYIMG",image);
                // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
                envelope.dotNet = true;
                envelope.setOutputSoapObject(rpc);
                AndroidHttpTransport ht = new AndroidHttpTransport(endPoint);
                ht.debug = true;
                Log.e("warn", "50");
                //(new MarshalBase64()).register(envelope);
                try {
                    // 调用WebService
                    ht.call(soapAction, envelope);
                    object =(Object)envelope.getResponse();
                } catch (Exception e) {
                    Message msg = Message.obtain();
                    msg.what = 0;
                    hanlder.sendMessage(msg);
                }
                Message msg = Message.obtain();
                msg.what = 1;
                msg.obj = object.toString();
                hanlder.sendMessage(msg);
            } catch (Exception e) {
                Message msg = Message.obtain();
                msg.what = 0;
                hanlder.sendMessage(msg);
            }
        }
    };
    private Handler hanlder= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i  = msg.what;
            if(i==0){
                closeDialog();
                Toast.makeText(getApplicationContext(),"网络或服务器异常",Toast.LENGTH_SHORT).show();
            }
            else if(i==1){
                closeDialog();
               String s = (String) msg.obj;
                if(s.equals("0")){
                    Toast.makeText(AddOilHistoryWrite.this, "保存成功", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(AddOilHistoryWrite.this, "保存失败", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };
    private void closeDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
            progressDialog=null;
        }
    }
    public String Bitmap2Base64(Bitmap bitmap) {
        try {
            // 先将bitmap转换为普通的字节数组
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,40, out);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(bitmap!=null) {
            bitmap.recycle();
        }
    }
    private String [] FENJ_ALLcarNum(String ALLCARNUM){
        Log.e("warn",ALLCARNUM);
        //List<String> ALL_list =new ArrayList<String>();
        String arr [] = ALLCARNUM.split(",");

        return  arr;
    }
    String [] arr_ALL;
    private void CarPai(String ALLCARNUM){
        arr_ALL= FENJ_ALLcarNum(ALLCARNUM);//获取所有车牌的集合
        AlertDialog.Builder builder = new AlertDialog.Builder(AddOilHistoryWrite.this);
        builder.setTitle("请选择");
        builder.setSingleChoiceItems(arr_ALL,0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                carNum .setText(arr_ALL[i]);
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }
    private MyProgressDialog progressDialog1;
    private void getCarNum(){
        progressDialog1 = new MyProgressDialog(AddOilHistoryWrite.this,false,"");
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {  Log.e("warn", "30");
                    // 命名空间
                    String nameSpace = "http://tempuri.org/";
                    // 调用的方法名称Get_OilAlarmInfo_List
                    String methodName = "Get_CarNum_List";
                    // EndPoint
                    String endPoint = Path.get_ZanShibeidouPath();
                    // SOAP Action
                    String soapAction = "http://tempuri.org/Get_CarNum_List";
                    // 指定WebService的命名空间和调用的方法名
                    SoapObject rpc = new SoapObject(nameSpace, methodName);
                    //设置需调用WebService接口需要传入的参数CarNum
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
                        carHandler.sendMessage(msg);
                    }
                    SoapObject object;
                    object = (SoapObject) envelope.getResponse();
                    // 得到服务器传回的数据 数据时dataset类型的
                    int count1 = object.getPropertyCount();
                    if(count1 ==0){
                        progressDialog1.dismiss();
                        Toast.makeText(AddOilHistoryWrite.this,"无车辆信息",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    StringBuffer sb;
                    Log.e("warn", String.valueOf(count1));
                    if (count1 > 0) {
                        sb = new StringBuffer();
                        for (int i = 0; i < count1; i++) {
                            SoapObject soapProvince = (SoapObject)object.getProperty(i);

                            Log.e("warn", soapProvince.getProperty("CARNUM").toString() + ":");
                            if(i==count1-1){
                                sb.append(soapProvince.getProperty("CARNUM").toString());
                            }else{
                                sb.append(soapProvince.getProperty("CARNUM").toString()+",");
                            }
                        }
                        Message msg = Message.obtain();
                        msg.what = 1;
                        msg.obj = sb.toString();
                        carHandler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    Message msg = Message.obtain();
                    msg.what = 0;
                    carHandler.sendMessage(msg);
                }
            }
        }.start();
    }
    private String allcarnum=null;
    private Handler carHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int i = msg.what;
            if(i==0){
                progressDialog1.dismiss();
                Toast.makeText(AddOilHistoryWrite.this, "网络或服务器异常", Toast.LENGTH_SHORT).show();
            }else if(i==1){
                progressDialog1.dismiss();
                allcarnum = (String)msg.obj;
            }
        }
    };
    private String [] select={"相机","相册"};
    private void selectPhoto(){

        AlertDialog.Builder builder = new AlertDialog.Builder(AddOilHistoryWrite.this);
        builder.setSingleChoiceItems(select, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(select[which].equals("相机")){
                    getImageFromCamera();//调取本地相机拍照
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

}
