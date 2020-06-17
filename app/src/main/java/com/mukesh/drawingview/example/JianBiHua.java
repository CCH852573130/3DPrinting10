package com.mukesh.drawingview.example;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.mukesh.DrawingView;
import com.pes.androidmaterialcolorpickerdialog.ColorPicker;
import com.pes.androidmaterialcolorpickerdialog.ColorPickerCallback;

//简笔画功能

public class JianBiHua extends AppCompatActivity
        implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private Button saveButton, penButton, eraserButton, penColorButton, clearButton;
    private DrawingView drawingView;
    private SeekBar penSizeSeekBar, eraserSizeSeekBar;
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jianbihua);
        initPython();
        initializeUI();
        setListeners();
        requestMyPermissions();//添加SD卡动态读写权力
    }
    private void initPython(){
        if (!Python.isStarted()){
            Python.start(new AndroidPlatform(this));
        }
    }
    private void requestMyPermissions() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //没有授权，编写申请权限代码
            ActivityCompat.requestPermissions(JianBiHua.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        } else {
            Log.d("TAG", "requestMyPermissions: 有写SD权限");
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //没有授权，编写申请权限代码
            ActivityCompat.requestPermissions(JianBiHua.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        } else {
            Log.d("TAG", "requestMyPermissions: 有读SD权限");
        }
    }
    private void callPythonCode(String stl_path){
        Python py = Python.getInstance();
        py.getModule("image").get("generateSceneNode").call(Environment.getExternalStorageDirectory().toString() + "/test/test4.png",120,20,0.4,100,512,false,stl_path);
    }
    private void setListeners() {
        saveButton.setOnClickListener(this);
        penButton.setOnClickListener(this);
        eraserButton.setOnClickListener(this);
        penColorButton.setOnClickListener(this);
        penSizeSeekBar.setOnSeekBarChangeListener(this);
        eraserSizeSeekBar.setOnSeekBarChangeListener(this);
        clearButton.setOnClickListener(this);
    }

    private void initializeUI() {
        drawingView = findViewById(R.id.scratch_pad);
        saveButton = findViewById(R.id.save_button);
        penButton = findViewById(R.id.pen_button);
        eraserButton = findViewById(R.id.eraser_button);
        penColorButton = findViewById(R.id.pen_color_button);
        penSizeSeekBar = findViewById(R.id.pen_size_seekbar);
        eraserSizeSeekBar = findViewById(R.id.eraser_size_seekbar);
        clearButton = findViewById(R.id.clear_button);
    }
    public native String stringFromJNI6(String stl_path, String gcode_path);

    @Override public void onClick(View view) {
        switch (view.getId()) {
            //此处保存按钮能在模型库显示的目录下保存图片（图片名字可自己命名），同时拉伸生成stl文件
            case R.id.save_button:
                drawingView.saveImage(Environment.getExternalStorageDirectory().getPath()+"/test/", "test4",
                        Bitmap.CompressFormat.PNG, 100);
                String stl_path = "mnt/sdcard/test/test4.stl";
                callPythonCode(stl_path);
                String gcode_path = stl_path.replace( "stl","gcode" );
//                stringFromJNI();//进行切片操作，需要3个文件的路径，目前路径是写死的
                stringFromJNI6(stl_path,gcode_path);//切片
                Toast.makeText( getApplicationContext(),"切片成功",Toast.LENGTH_LONG ).show();
                Intent intent9 = new Intent();
                intent9.setClass(getApplicationContext(), DaYinJieMian.class);
                Bundle mBundle = new Bundle();
//                mBundle.putString("Gcode", gcode_path);//压入数据
                mBundle.putString("stlpath",stl_path);
                intent9.putExtras(mBundle);
                startActivity(intent9);
                System.exit(0);
                break;
                //////////////////////////////////////////////////////////
            case R.id.pen_button:
                drawingView.initializePen();
                break;
            case R.id.eraser_button:
                drawingView.initializeEraser();
                break;
            case R.id.clear_button:
                drawingView.clear();
                break;
            case R.id.pen_color_button:
                final ColorPicker colorPicker = new ColorPicker(JianBiHua.this, 0, 0, 0);
                colorPicker.setCallback(
                        new ColorPickerCallback() {
                            @Override public void onColorChosen(int color) {
                                drawingView.setPenColor(color);
                                colorPicker.dismiss();
                            }
                        });
                colorPicker.show();
                break;

            default:
                break;
        }
    }

    @Override public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        int seekBarId = seekBar.getId();
        if (seekBarId == R.id.pen_size_seekbar) {
            drawingView.setPenSize(i);
        } else if (seekBarId == R.id.eraser_size_seekbar) {
            drawingView.setEraserSize(i);
        }
    }

    @Override public void onStartTrackingTouch(SeekBar seekBar) {
        //Intentionally Empty
    }

    @Override public void onStopTrackingTouch(SeekBar seekBar) {
        //Intentionally Empty
    }
}
