package com.mukesh.drawingview.example;

import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import utils.SerialPortUtils;

import static com.mukesh.drawingview.example.ZhuJieMian.open;

//开始打印 将gcode文件传给打印机

public class DaYinJieMian extends AppCompatActivity {
    public String File_Path;
    public volatile int currentProgress = 0;
    public ProgressBar mPbLoading;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dayinjiemian);

    }

    protected void onStart(){
        super.onStart();
        Bundle bundle=getIntent().getExtras();
        File_Path =bundle.getString("Data");
       try{
            String abc ="123";
            byte[] data11 = abc.getBytes();
            open.sendDataToSerialPort(data11);
        }catch (Exception e){
            open = SerialPortUtils.getInstance();
           open.setSCMDataReceiveListener(new utils.SCMDataReceiveListener() {
               public void dataRecevie(byte[] buffer, int size) {
               }
           });
            open.openSerialPort();
        }finally {

        }
    }

    protected void onStop(){
        super.onStop();
        handler2.removeCallbacks(runnable);
    }

    public void send_Mesg(View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("注意");
        builder.setMessage("是否开始打印该STL模型？文件传输过程中请不要触摸其他按钮");
        builder.setCancelable(true);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                File file = new File(File_Path);
                Log.d("test", "aaaaa");
                if (file.canRead()) {
                    try {
                        Log.d("test", "bbbbb");
                        FileInputStream fis = new FileInputStream(file);
                        ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
                        byte[] b1 = new byte[1024];
                        int n;
                        while ((n = fis.read(b1)) != -1) {
                            bos.write(b1, 0, n);
                        }
                        fis.close();
                        byte[] data = bos.toByteArray();
                        bos.close();
                        open.sendDataToSerialPort(data);
                        String str = "M24";
                        byte[] bty1 = str.getBytes();
                        open.sendDataToSerialPort(bty1);
                        Toast.makeText(getApplicationContext(),"打印任务已开始",Toast.LENGTH_SHORT).show();
                        Log.d("Test", "开始打印");
                        handler2.postDelayed(runnable, 5000);
/*                        new Thread()
                        {
                            public void run()
                            {
                                double Znum = Double.parseDouble(SerialPortUtils.Z_posi_num1);
                                double per = Znum/100.0;
                                currentProgress = Double.valueOf(per).intValue();
                                handler3.sendEmptyMessage(123);
                            }
                        }.start();

 */
                     new Thread(Runnableysn).start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
        AlertDialog dialog = builder.create();
        //对话框显示的监听事件
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Log.e("test","对话框显示了");
            }
        });
        //对话框消失的监听事件
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Log.e("test","对话框消失了");
            }
        });
        //显示对话框
        dialog.show();
    }
//            Bundle bundle = getIntent().getExtras();   //得到传过来的bundle
//            Toast.makeText(this, File_Path, Toast.LENGTH_SHORT).show();
/*
            File file = new File(File_Path);
            Log.d("test","aaaaa");
            if (file.canRead()) {
                try {
                    Log.d("test","bbbbb");
                    FileInputStream fis = new FileInputStream(file);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
                    byte[] b1 = new byte[1024];
                    int n;
                    while ((n = fis.read(b1)) != -1) {
                        bos.write(b1, 0, n);
                    }
                    fis.close();
                    byte[] data = bos.toByteArray();
                    bos.close();
                    open.sendDataToSerialPort(data);
                    String str = "M24";
                    byte[] bty1 = str.getBytes();
                    open.sendDataToSerialPort(bty1);
                    Log.d("Test", "开始打印");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

 */
    public void send_M25(View view){
        String str1 = "M25";
        byte[] bty2 =str1.getBytes();
        open.sendDataToSerialPort(bty2);
        Toast.makeText(this,"打印任务已经暂停",Toast.LENGTH_SHORT).show();
        Log.d("Test", "暂停打印" + str1);
    }

    public void send_M112(View view){
        String str2 = "M112";
        byte[] bty3 =str2.getBytes();
        open.sendDataToSerialPort(bty3);
        Toast.makeText(this,"打印任务已紧急停止",Toast.LENGTH_SHORT).show();
        Log.d("Test", "停止打印" + str2);
    }

/*
    Handler handler3 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 123) {
//                mPbLoading.setProgress(currentProgress);
                mPbLoading.setProgress(currentProgress);
            }
        }
    };

 */

    public Runnable Runnableysn = new Runnable() {
        @Override
        public void run() {
            while(true){
                try {
                    Thread.sleep(2000);
                    Handlerysn.sendMessage(Handlerysn.obtainMessage());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    //
//    /*
//    作者：蔚晟楠
//    函数作用：mHandler对象创建实例，super表示继承
//     */
    Handler Handlerysn = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            refreshUI();
        }
    };

    //    /*
//    作者：蔚晟楠
//    函数作用：更新UI，首先findViewById方法定位，然后用settext方法更新数值，记得数值在SerialPortUtils里，关键字不要变
//     */
    public void refreshUI(){
        //这里是算法实现部分，具体算法实现有待考究
        double Znum = Double.parseDouble(SerialPortUtils.Z_posi_num1);
        double per = 0.1 * Znum;
        currentProgress = Double.valueOf(per).intValue();
        mPbLoading = findViewById(R.id.progressBar);
        mPbLoading.setProgress(currentProgress);
//        Log.d("Test","test" + currentProgress);
    }

    Handler handler2 =new Handler();
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            handler2.postDelayed(this, 5000);
            String str3 = "M114";
            byte[] bty4 =str3.getBytes();
            open.sendDataToSerialPort(bty4);
        }
    };//定时器初始化
}