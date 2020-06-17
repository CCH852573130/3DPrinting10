package opengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Bundle;

import com.mukesh.drawingview.example.DaYinJieMian;

import java.io.IOException;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
public class GLRenderer  implements GLSurfaceView.Renderer {
    // private static final android.opengl.GLU GLU = ;
    private Model model;
    private Point mCenterPoint;
    private Point eye = new Point(0, 0, -3);
    private Point up = new Point(0, 1, 0);
    private Point center = new Point(0, 0, 0);
    private float mScalef = 1;
    private float mDegree = 0;
    public GLRenderer(Context context) {
        try {
            model = new STLReader().parserBinStlInSDCard(DaYinJieMian.previewpath);
            /*
            上面这句话是用来传文件的，请务必引起重视
             */
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void rotate(float degree) {
        mDegree = degree;
    }
    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {

        gl.glEnable(GL10.GL_DEPTH_TEST); // 启用深度缓存
        gl.glClearDepthf(1.0f); // 设置深度缓存值，由于范围是0-1，所以每一个像素都显示出来
        gl.glDepthFunc(GL10.GL_LEQUAL); // 设置深度缓存比较函数，只绘制模型中像素点的z值<=当前像素z值的部分
        gl.glShadeModel(GL10.GL_SMOOTH);// 设置阴影模式GL_SMOOTH，根据顶点的不同颜色，最终以渐变的形式填充图形
        //开启光
        openLight(gl);
        enableMaterial(gl);

        float r = model.getR();//确定缩放比例
        //r是半径，不是直径，因此用0.5/r可以算出放缩比例
        mScalef = 0.5f / r;
        mCenterPoint = model.getCentrePoint();
    }
    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {

        // 设置OpenGL场景的大小,(0,0)表示窗口内部视口的左下角，(width, height)指定了视口的大小
        gl.glViewport(0, 0, width, height);

        gl.glMatrixMode(GL10.GL_PROJECTION); // 设置投影矩阵，投影变换，这里是透视投影，还有正投影
        gl.glLoadIdentity(); // 设置矩阵为单位矩阵，相当于重置矩阵
        GLU.gluPerspective(gl, 45.0f, ((float) width) / height, 1f, 100f);// 设置透视范围
        //这里可以用另外一个函数
        //以下两句声明，以后所有的变换都是针对模型(即我们绘制的图形)
        gl.glMatrixMode(GL10.GL_MODELVIEW);//声明使用模型变换
        gl.glLoadIdentity();//将当前矩阵复位为单位矩阵
    }
    @Override
    public void onDrawFrame(GL10 gl) {
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);//清除屏幕和深度缓存
        gl.glLoadIdentity();// 重置当前的模型观察矩阵
        //眼睛对着原点看
        /**
         * 改变观察点
         * gl: GL10型变量
         * eyeX,eyeY,eyeZ: 观测点坐标（相机坐标）
         * centerX,centerY,centerZ：观察位置的坐标
         * upX,upY,upZ ：相机向上方向在世界坐标系中的方向（即保证看到的物体跟期望的不会颠倒）
         */
        GLU.gluLookAt(gl, eye.x, eye.y, eye.z, center.x,
                center.y, center.z, up.x, up.y, up.z);

        //为了能有立体感觉，通过改变mDegree值，让模型不断旋转
        gl.glRotatef(mDegree, 1, 1, 1);

        //将模型放缩到View刚好装下
        gl.glScalef(mScalef*2, mScalef*2, mScalef*2);
        //把模型移动到原点
        gl.glTranslatef(-mCenterPoint.x, -mCenterPoint.y,
                -mCenterPoint.z);
        //===================begin==============================//
        //允许给每个顶点设置法向量
        gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
        // 允许设置顶点
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        // 允许设置颜色
        //gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
        //设置法向量数据源
        gl.glNormalPointer(GL10.GL_FLOAT, 0, model.getVnormBuffer());
        // 设置三角形顶点数据源，size: 每个顶点有几个数值描述。必须是2，3 ，4 之一
        //type,每个顶点的坐标类型，取值：GL_BYTE,GL_SHORT, GL_FIXED, GL_FLOAT。
        //stride：数组中每个顶点间的间隔，步长（字节位移）。取值若为0，表示数组是连续的
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, model.getVertBuffer());
        // 绘制三角形
        //GL_TRIANGLES：每三个顶之间绘制三角形，之间不连接，最后一个为顶点的数量
        gl.glDrawArrays(GL10.GL_TRIANGLES, 0, model.getFacetCount() * 3);
        // 取消顶点设置
        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        //取消法向量设置
        gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
        // 取消颜色设置
        //gl.glDisableClientState(GL10.GL_COLOR_ARRAY);

    }
    //漫反射，镜面反射以及未照射到的部分
    float[] ambient = {0.9f, 0.9f, 0.9f, 1.0f};
    float[] diffuse = {0.5f, 0.5f, 0.5f, 1.0f};
    float[] specular = {1.0f, 1.0f, 1.0f, 1.0f};
    float[] lightPosition = {0.5f, 0.5f, 0.5f, 0.0f};

    public void openLight(GL10 gl) {

        gl.glEnable(GL10.GL_LIGHTING);
        gl.glEnable(GL10.GL_LIGHT0);
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, Util.floatToBuffer(ambient));
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, Util.floatToBuffer(diffuse));
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, Util.floatToBuffer(specular));
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, Util.floatToBuffer(lightPosition));


    }

    float[] materialAmb = {0.4f, 0.4f, 1.0f, 1.0f,};
    float[] materialDiff = {0.0f, 0.0f, 1.0f, 1.0f,};
    float[] materialSpec = {1.0f, 0.5f, 0.0f, 1.0f,};

    public void enableMaterial(GL10 gl) {

        //材料对环境光的反射情况
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, Util.floatToBuffer(materialAmb));
        //散射光的反射情况
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, Util.floatToBuffer(materialDiff));
        //镜面光的反射情况
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, Util.floatToBuffer(materialSpec));

    }
}