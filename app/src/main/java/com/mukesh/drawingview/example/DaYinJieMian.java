package com.mukesh.drawingview.example;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

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
    }
    public void send_Mesg(View v) {
        File_Path = "";
        File file = new File(File_Path);
        if (file.canRead()){
            try{
                FileInputStream fis = new FileInputStream(file);
                ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
                byte[] b1 = new byte[1024];
                int n;
                while((n = fis.read(b1)) != -1){
                    bos.write(b1,0,n);
                }
                fis.close();
                byte[] data = bos.toByteArray();
                bos.close();
                open.sendDataToSerialPort(data);
                String str = "M24";
                byte[] bty1 = str.getBytes();
                open.sendDataToSerialPort(bty1);
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    public void send_M25(View view){
        String str1 = "M25";
        byte[] bty2 =str1.getBytes();
        open.sendDataToSerialPort(bty2);
    }
    public void send_M112(View view){
        String str2 = "M112";
        byte[] bty3 =str2.getBytes();
        open.sendDataToSerialPort(bty3);
    }
}
