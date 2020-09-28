package com.mukesh.drawingview.example;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class 人物角色 extends AppCompatActivity  {
    private GridView mGv;
    private static final String FILE_NAME[] = {
            "a.png",
            "b.png",
            "c.png","d.png","e.png","p2.png"
    };
    private static final String FILE_NAME2[] = {
            "a.STL",
            "b.STL",
            "c.STL","d.STL","e.STL","p2.STL"
    };
    static {
        System.loadLibrary("native-lib");
    }

//    private static final String FILE_NAME[] = {
//            "a.png", "b.png",
//            "c.png", "d.png",
//            "e.png", "p2.png",
//    };
//    private static final String stl_NAME[] = {
//            "a.STL", "b.STL",
//            "c.STL", "d.STL",
//            "e.STL", "p2.STL",
//
//
//    };
//    private volatile boolean flag = true;//同一时刻只有一个线程能修改值
    private Thread thread;
    private Handler handler;
    private ProgressDialog progressDialog;
    private String gcode_path2,stl_path2;
    private static List<String> imagePath=new ArrayList<String>();//图片文件的路径
    private static String[] imageFormatSet=new String[]{"jpg","png","gif"};//合法的图片文件格式
    /*
     * 方法:判断是否为图片文件
     * 参数:String path图片路径
     * 返回:boolean 是否是图片文件，是true，否false
     * */
    private static boolean isImageFile(String path){
        for(String format:imageFormatSet){//遍历数组
            if(path.contains(format)){//判断是否为合法的图片文件
                return true;
            }
        }
        return false;
    }
    /*
     * 方法:用于遍历指定路径
     * 参数:String url遍历路径
     * 无返回值
     * */
    private void getFiles(String url){
        File files=new File(url);//创建文件对象
        File[] file=files.listFiles();
        try {
            for(File f:file){//通过for循环遍历获取到的文件数组
                if(f.isDirectory()){//如果是目录，也就是文件夹
                    getFiles(f.getAbsolutePath());//递归调用有点狠
                }else{
                    if(isImageFile(f.getPath())){//如果是图片文件
                        imagePath.add(f.getPath());//将文件的路径添加到List集合中
                    }
                }
            }
            for (int i = 0;i<imagePath.size();i++){
                int j = imagePath.lastIndexOf( imagePath.get(i) );
                if (i != j){
                    imagePath.remove( j );
                    i--;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();//输出异常信息
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


    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_moxingku );
        Log.d( "MoXing","oncreate" );
        Log.d("MoXing","taskid:"+getTaskId()+"  ,hash:"+hashCode());
        logtaskName();
        //此handler与主UI线程绑定
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage( msg );
                //关闭对话框并跳转
                progressDialog.dismiss();
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), DaYinJieMian.class);
                Bundle mBundle = new Bundle();
                mBundle.putString("Gcode", gcode_path2);//压入数据
                mBundle.putString("stlpath",stl_path2);
                intent.putExtras(mBundle);
                startActivity(intent);
                System.exit( 0 );//退出的是跳转前的界面
            }
        };
        String sdpath3 = Environment.getExternalStorageDirectory() + "/renwujuese";
        String sdpath = Environment.getExternalStorageDirectory() + "/renwujuese/picture";
        String sdpath1 = Environment.getExternalStorageDirectory() + "/renwujuese/stl_file";
        String sdpath2 = Environment.getExternalStorageDirectory() + "/renwujuese/gcode_file";
        File testFolder1 = new File( sdpath);
        File testFolder2 = new File( sdpath1);
        File testFolder3 = new File( sdpath2);
        File testFolder4 = new File( sdpath3);
        if(testFolder3.exists() && testFolder1.isDirectory() ) {

        } else if(!testFolder3.exists()) {
            testFolder3.mkdir();

        }
        if(testFolder1.exists() && testFolder1.isDirectory() ) {

        } else if(!testFolder1.exists()) {
            testFolder1.mkdir();

        }
        if(testFolder2.exists() && testFolder2.isDirectory() ) {

        } else if(!testFolder2.exists()) {
            testFolder2.mkdir();

        }
        if(testFolder4.exists() && testFolder4.isDirectory() ) {

        } else if(!testFolder4.exists()) {
            testFolder4.mkdir();

        }

        for (int n =0; n < FILE_NAME.length; n++) {
            File modelFile = new File(testFolder1, FILE_NAME[n]);
            if (!modelFile.exists()) {
                copyAssetFilesToSDCard(modelFile, FILE_NAME[n]);
            }
        }
        for (int n =0; n < FILE_NAME2.length; n++) {
            File modelFile = new File(testFolder2, FILE_NAME2[n]);
            if (!modelFile.exists()) {
                copyAssetFilesToSDCard(modelFile, FILE_NAME2[n]);
            }
        }
        getFiles( sdpath );//调用getFiles()方法获取SD卡上的全部图片
        if (imagePath.size() < 1) {//如果不存在文件图片
            return;
        }
        mGv = (GridView) findViewById( R.id.gv );
        mGv.setAdapter( new BaseAdapter() {
            @Override
            public int getCount() {
                return imagePath.size();
            }

            class ViewHolder {
                public ImageView imageView;
                public TextView textView;
            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int position, View view, ViewGroup viewGroup) {
                ViewHolder holder = null;
                if (view == null) {
                    LayoutInflater mLayoutInflater = LayoutInflater.from( 人物角色.this );
                    view = mLayoutInflater.inflate( R.layout.layout_grid_item, null );
                    holder = new ViewHolder();
                    holder.imageView = (ImageView) view.findViewById( R.id.iv_grid );
                    holder.textView = (TextView) view.findViewById( R.id.tv_title );
                    view.setTag( holder );
                } else {
                    holder = (ViewHolder) view.getTag();
                }
                //赋值
                String fName = imagePath.get(position).trim();
                String fileName = fName.substring(fName.lastIndexOf("/")+1,fName.lastIndexOf("."));
                holder.textView.setText( fileName );
                //为ImageView设置要显示的图片
                Bitmap bm= BitmapFactory.decodeFile(imagePath.get(position));
                holder.imageView.setImageBitmap(bm);
//        Glide.with(mContext).load("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1503734793030&di=10ea8e49217f7a2054f4febf94a164af&imgtype=0&src=http%3A%2F%2Fpic.58pic.com%2F58pic%2F15%2F24%2F57%2F34s58PICQEq_1024.jpg").into(holder.imageView);
                return view;
            }
        } );
        mGv.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
                //点击图片，先执行show()弹出对话框，同时开启子线程
                progressDialog = new ProgressDialog( 人物角色.this );
                progressDialog.setTitle( "提示" );
                progressDialog.setMessage( "正在切片..." );
                progressDialog.setButton( DialogInterface.BUTTON_POSITIVE, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit( 0 );
                    }
                } );
                progressDialog.setCancelable( false );
                progressDialog.show();
                thread = new Thread( new Runnable() {
                    @Override
                    public void run() {
                        String fPath = imagePath.get(position).trim();
                        String fileName2 = fPath.substring(fPath.lastIndexOf("/")+1);
                        String stl_path1 = fPath.replace( "png","STL" );
                        stl_path2 = stl_path1.replace( "picture","stl_file" );
                        String gcode_path1 = fPath.replace( "png","gcode" );
                        gcode_path2 = gcode_path1.replace( "picture","gcode_file" );
                        stringFromJNI2(stl_path2,gcode_path2);//进行切片操作，接口需要传入stl和gcode路径
                        //当切片完成后向handler发消息
                        handler.sendEmptyMessage(0);
                    }
                } );
                thread.start();//为什么两部分不能连在一起写？
//              Toast.makeText( getApplicationContext(),fileName2+"切片成功",Toast.LENGTH_LONG ).show()
            }
        } );
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d( "MoXing","onStart" );
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d( "MoXing","onresume" );
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d( "MoXing","onpause" );
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d( "MoXing","onStop" );
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d( "MoXing","onrestart" );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d( "MoXing","ondestroy" );
    }


    public native String stringFromJNI2(String stl_path, String gcode_path);



//    private void initUI() {
//
//
//        //将drawable文件夹下的文件加载至SD卡中（路径可以按你们需要改）[有难度，好像只有asserts下的才能用IO流]
//        File testFolder = new File( Environment.getExternalStorageDirectory() + "/picture");
//        if(testFolder.exists() && testFolder.isDirectory() ) {
//        Toast.makeText( getApplicationContext(),"图片已经存在",Toast.LENGTH_LONG ).show();
//    } else if(!testFolder.exists()) {
//        testFolder.mkdir();
//// check whether the model files exist in the phone **/
//// if not, copy them to there                       **/
//        for (int n =0; n < FILE_NAME.length; n++) {
//            File modelFile = new File(testFolder, FILE_NAME[n]);
//            if (!modelFile.exists()) {
//                copyAssetFilesToSDCard(modelFile, FILE_NAME[n]);
//            }
//        }
//        Toast.makeText( getApplicationContext(),"上传图片成功",Toast.LENGTH_LONG ).show();
//    }
//
//        //将drawable文件夹下的文件加载至SD卡中（路径可以按你们需要改）[有难度，好像只有asserts下的才能用IO流]
//        File testFolder2 = new File( Environment.getExternalStorageDirectory() + "/stl_file");
//        File testFolder3 = new File( Environment.getExternalStorageDirectory() + "/gcode_file");
//        if(testFolder2.exists() && testFolder2.isDirectory() ) {
//            Toast.makeText( getApplicationContext(),"stl已经存在",Toast.LENGTH_LONG ).show();
//        } else if(!testFolder2.exists()) {
//            testFolder2.mkdir();
//// check whether the model files exist in the phone **/
//// if not, copy them to there                       **/
//            for (int n =0; n < stl_NAME.length; n++) {
//                File modelFile2 = new File(testFolder2, stl_NAME[n]);
//                if (!modelFile2.exists()) {
//                    copyAssetFilesToSDCard(modelFile2, stl_NAME[n]);
//                }
//            }
//            Toast.makeText( getApplicationContext(),"上传stl成功",Toast.LENGTH_LONG ).show();
//        }
//        if(testFolder3.exists() && testFolder3.isDirectory() ) {
//            Toast.makeText( getApplicationContext(),"gcode已经存在",Toast.LENGTH_LONG ).show();
//        } else if(!testFolder3.exists()) {
//            testFolder3.mkdir();}
//}
    private void logtaskName(){
        try {
            ActivityInfo info = getPackageManager().getActivityInfo(getComponentName(), PackageManager.GET_META_DATA);
            Log.d("MoXing",info.taskAffinity);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
//    private void copyAssetFilesToSDCard(final File testFileOnSdCard, final String FileToCopy) {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {//lastIndexOf去掉后缀名，说实话有点天才
////                    int id =getResources().getIdentifier(FileToCopy, "drawable", getPackageName()); //name:图片的名，defType：资源类型（drawable，string。。。），defPackage:工程的包名
//
////                    InputStream is = getResources().getDrawable(id);
//                    InputStream is = getAssets().open(FileToCopy);
//                    FileOutputStream fos = new FileOutputStream(testFileOnSdCard);
//                    byte[] buffer = new byte[8192];
//                    int read;
//                    try {
//                        while ((read = is.read(buffer)) != -1) {
//                            fos.write(buffer, 0, read);
//                        }
//                    } finally {
//                        fos.flush();
//                        fos.close();
//                        is.close();
//                    }
//                } catch (IOException e) {
//                    Log.d("aaa", "Can't copy test file onto SD card");
//                }
//            }
//        }).start();
//    }
}