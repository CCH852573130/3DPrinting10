package com.mukesh.drawingview.example;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class MoXingKuShouYe extends AppCompatActivity {
    private GridView mGv;
    private static final String FILE_NAME[] = {
            "人物角色.png",
            "动物植物.png",
            "工具配件.png","教育学习.png"
    };
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
        setContentView( R.layout.activity_mo_xing_ku_shou_ye );
        String sdpath = Environment.getExternalStorageDirectory() + "/classify";//获得SD卡中图片的路径
        File testFolder1 = new File( sdpath);
        if(testFolder1.exists() && testFolder1.isDirectory() ) {

        } else if(!testFolder1.exists()) {
            testFolder1.mkdir();

        }
        for (int n =0; n < FILE_NAME.length; n++) {
            File modelFile = new File(testFolder1, FILE_NAME[n]);
            if (!modelFile.exists()) {
                copyAssetFilesToSDCard(modelFile, FILE_NAME[n]);
            }
        }
        getFiles( sdpath );//调用getFiles()方法获取SD卡上的全部图片
        if (imagePath.size() < 1) {//如果不存在文件图片
            return;
        }
        mGv = (GridView) findViewById( R.id.gv1 );
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
                    LayoutInflater mLayoutInflater = LayoutInflater.from( MoXingKuShouYe.this );
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
                String fPath = imagePath.get(position).trim();
                String fileName = "com.mukesh.drawingview.example." + fPath.substring(fPath.lastIndexOf("/")+1,fPath.lastIndexOf("."));
                Intent intent = new Intent();
                intent.setClassName(getApplicationContext(), fileName);
                startActivity(intent);
            }
        } );
    }
}
