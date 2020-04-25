package com.mukesh.drawingview.example;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

import utils.SerialPortUtils;

import static com.mukesh.drawingview.example.ZhuJieMian.connect_status;
import static com.mukesh.drawingview.example.ZhuJieMian.open;

//开始打印 将gcode文件传给打印机

public class DaYinJieMian extends AppCompatActivity {
    public String File_Path;
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
            open.openSerialPort();
        }finally {

        }
    }

    public void send_Mesg(View v) {
//            Bundle bundle = getIntent().getExtras();   //得到传过来的bundle
//            Toast.makeText(this, File_Path, Toast.LENGTH_SHORT).show();
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
    }
    public void send_M25(View view){
        String str1 = "M25";
        byte[] bty2 =str1.getBytes();
        open.sendDataToSerialPort(bty2);
        Log.d("Test", "暂停打印" + str1);
    }
    public void send_M112(View view){
        String str2 = "M112";
        byte[] bty3 =str2.getBytes();
        open.sendDataToSerialPort(bty3);
        Log.d("Test", "停止打印" + str2);
    }

    public void back(View v){
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), ZhuJieMian.class);
        startActivity(intent);
    }
}