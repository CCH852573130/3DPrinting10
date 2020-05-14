package com.mukesh.drawingview.example;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
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


public class 动物植物 extends AppCompatActivity  {
    private GridView mGv;
    static {
        System.loadLibrary("native-lib");
    }

    private Handler mHandler;
    private Runnable mBackgroundRunnable;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_moxingku );
        HandlerThread thread = new HandlerThread("MyHandlerThread");
        thread.start();//创建一个HandlerThread并启动它
        mHandler = new Handler(thread.getLooper());//使用HandlerThread的looper对象创建Handler，如果使用默认的构造方法，很有可能阻塞UI线程
        String sdpath = Environment.getExternalStorageDirectory() + "/dongwuzhiwu/picture";//获得SD卡中图片的路径
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
                    LayoutInflater mLayoutInflater = LayoutInflater.from( 动物植物.this );
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
                mBackgroundRunnable = new Runnable() {
                    @Override
                    public void run() {
                        String fPath = imagePath.get(position).trim();
                        String fileName2 = fPath.substring(fPath.lastIndexOf("/")+1);
                        String stl_path1 = fPath.replace( "png","STL" );
                        String stl_path2 = stl_path1.replace( "picture","stl_file" );
                        String gcode_path1 = fPath.replace( "png","gcode" );
                        String gcode_path2 = gcode_path1.replace( "picture","gcode_file" );
                        stringFromJNI4(stl_path2,gcode_path2);//进行切片操作，接口需要传入stl和gcode路径
//                        Toast.makeText( getApplicationContext(),fileName2+"切片成功",Toast.LENGTH_LONG ).show()
                        Intent intent = new Intent();
                        intent.setClass(getApplicationContext(), DaYinJieMian.class);
                        Bundle mBundle = new Bundle();
                        mBundle.putString("Gcode", gcode_path2);//压入数据
                        mBundle.putString("stlpath",stl_path2);
                        intent.putExtras(mBundle);
                        startActivity(intent);
                        System.exit( 0 );
                    }
                };
                mHandler.post(mBackgroundRunnable);//将线程post到Handler中
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
        mHandler.removeCallbacks( mBackgroundRunnable );
    }

    public native String stringFromJNI4(String stl_path, String gcode_path);


}