package com.mukesh.drawingview.example;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import utils.SerialPortUtils;

//主界面跳转到四个分界面
public class ZhuJieMian extends AppCompatActivity implements View.OnClickListener {
  public static SerialPortUtils open;
  public static int connect_status;
  private static final String FILE_NAME[] = {
          "box.STL",
          "fdmextruder.def.json",
          "fdmprinter.def.json"
  };
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_zhujiemian);
    //初始化方法
    initUI();
    connect_status = serialconnect();
  }
  public int serialconnect() {
    open = SerialPortUtils.getInstance();
    open.setSCMDataReceiveListener(new utils.SCMDataReceiveListener() {
      public void dataRecevie(byte[] buffer, int size) {
      }
    });
    return open.openSerialPort();
  }
  private void initUI(){
    findViewById(R.id.button1).setOnClickListener(this);
    findViewById(R.id.button2).setOnClickListener(this);
    findViewById(R.id.button3).setOnClickListener(this);
    findViewById(R.id.button4).setOnClickListener(this);
    findViewById(R.id.button5).setOnClickListener(this);
    //将asserts文件夹下的文件加载至SD卡中（路径可以按你们需要改）
    File testFolder = new File( Environment.getExternalStorageDirectory() + "/test");
    if(testFolder.exists() && testFolder.isDirectory() ) {
      Toast.makeText( getApplicationContext(),"资源文件已经存在",Toast.LENGTH_LONG ).show();
    } else if(!testFolder.exists()) {
      testFolder.mkdir();
// check whether the model files exist in the phone **/
// if not, copy them to there                       **/
      for (int n =0; n < FILE_NAME.length; n++) {
        File modelFile = new File(testFolder, FILE_NAME[n]);
        if (!modelFile.exists()) {
          copyAssetFilesToSDCard(modelFile, FILE_NAME[n]);
        }
      }
      Toast.makeText( getApplicationContext(),"上传资源文件成功",Toast.LENGTH_LONG ).show();
    }
  }

  private void copyAssetFilesToSDCard(final File testFileOnSdCard, final String FileToCopy) {
    new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          InputStream is = getAssets().open(FileToCopy);
          FileOutputStream fos = new FileOutputStream(testFileOnSdCard);
          byte[] buffer = new byte[8192];
          int read;
          try {
            while ((read = is.read(buffer)) != -1) {
              fos.write(buffer, 0, read);
            }
          } finally {
            fos.flush();
            fos.close();
            is.close();
          }
        } catch (IOException e) {
          Log.d("aaa", "Can't copy test file onto SD card");
        }
      }
    }).start();
  }

  //四个按钮分别实现四个分界面的跳转
  @Override
  public void onClick(View view) {
    switch (view.getId()){
      case R.id.button1:
        Intent intent1 = new Intent();
        intent1.setClass(getApplicationContext(), JianBiHua.class);
        startActivity(intent1);
        break;
      case R.id.button3:
        Intent intent2 = new Intent();
        intent2.setClass(getApplicationContext(), MoXingKu.class);
        startActivity(intent2);
        break;
      case R.id.button2:
        Intent intent3 = new Intent();
        intent3.setClass(getApplicationContext(), ZhuanYeMoShi.class);
        startActivity(intent3);
        break;
      case R.id.button4:
        Intent intent4 = new Intent();
        intent4.setClass(getApplicationContext(), SheZhi.class);
        startActivity(intent4);
        break;
      case R.id.button5:
        Intent intent5 = new Intent();
        intent5.setClass(getApplicationContext(), UPicture.class);
        startActivity(intent5);
        break;
    }
  }
  public void send_command(View v){
    String stroftempofbackbed = "M140 S55";
    String stroftempofextruder = "M105 S60";
    byte[] bytoftempofbackbed = stroftempofbackbed.getBytes();
    byte[] bytoftempofextruder = stroftempofextruder.getBytes();
    open.sendDataToSerialPort(bytoftempofbackbed);
    open.sendDataToSerialPort(bytoftempofextruder);
    Log.d("test", "一键预热成功");
  }
}
