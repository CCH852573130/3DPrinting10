package com.mukesh.drawingview.example;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import usbcontroler.USBDiskState;

import static com.mukesh.drawingview.example.DaYinJieMian.File_Path;

//要做读取U盘路径 并实现U盘文件（stl）的显示 选中文件后能点击按钮进行上传 布局界面为activity_upicture
public class UPicture extends AppCompatActivity{
    private List myBeanList; //用来存放数据的数组
    public ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upicture);
        boolean b = new USBDiskState().isMounted();
        Log.e("test", "onCreate: ------U盘是否存在-------" + b);
        if (b) {
            Toast.makeText(this, "USB设备存在", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "USB设备不存在", Toast.LENGTH_SHORT).show();
        }
        listView = (ListView)findViewById(R.id.listview);//在视图中找到ListView
        myBeanList = new ArrayList();
        File path = new File("/storage/usbhost1/");//外置U盘路径
        File[] files = path.listFiles();// 读取
        getFileName(files);
        SimpleAdapter adapter = new SimpleAdapter(this,myBeanList,R.layout.newusb,new String[]{"Name"},null);
        listView.setAdapter(adapter);
        for (int p = 0; p < myBeanList.size(); p++) {
            Log.e("test", "list.name" + myBeanList.get(p));
        }
    }

        public void slice(View view){
        File_Path ="";
        Toast.makeText(this,"切片成功",Toast.LENGTH_SHORT).show();
        }
        public void printing(View view){
            Intent intent8 = new Intent();
            intent8.setClass(getApplicationContext(), DaYinJieMian.class);
            startActivity(intent8);
        }

    private void getFileName(File[] files) {
        if (files != null) {// 先判断目录是否为空，否则会报空指针
            String fileName = null;
            for (File file : files) {
                if (file.isDirectory()) {
                    Log.e("test", "若是文件目录。继续读1" + file.getName().toString() + file.getPath().toString());
                    getFileName(file.listFiles());
                    Log.e("test", "若是文件目录。继续读2" + file.getName().toString() + file.getPath().toString());
                } else {
                    fileName = file.getName();
                    if (fileName.endsWith(".stl")||(fileName.endsWith(".STL"))) {
                        HashMap map = new HashMap();
                        String s = fileName.substring(0, fileName.lastIndexOf(".")).toString();
                        Log.i("test", "文件名stl：：  " + s);
                        map.put("Name", fileName.substring(0, fileName.lastIndexOf(".")));
                        myBeanList.add(map);
                    }
                }
            }
        }
    }
    }
