package lib.cbz.com.innerguideviewlib;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposePathEffect;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by cbz on 2018/8/2 0002.
 */

public class InnerGuideView extends View {
    private View focusView;  //高亮view
    private int color=Color.argb(180,0,0,0);       //背景色(灰色)
    private Context context;
    private String tipText;     //提示文字
    private int xy[]=new int[2];        //view左上角坐标
    private int contentXY[]=new int[2];     //contentView左上角坐标
    private ViewGroup content;      //contentView
    private Map<View,String> viewMap=new LinkedHashMap<>();     //需要高亮的所有View
    private Iterator<View> iteratorView;
    private Iterator<String> iteratorText;
    private int tipTextColor=Color.WHITE;
    private int borderType=0;
    private boolean isDash=true;    //是否显示虚线框
    private int roundRadius=10;     //圆角矩形弧度
    private InnerGuideView(Context context, int color, Map viewMap, int borderType, int tipTextColor, int roundRadius, boolean isDash) {
        super(context);
        this.color=color;
        this.context=context;
        this.viewMap=viewMap;
        this.borderType=borderType;
        this.tipTextColor=tipTextColor;
        this.roundRadius=roundRadius;
        this.isDash=isDash;
        content=(ViewGroup) (((Activity)context).findViewById(android.R.id.content));       //获取contentView
        content.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {     //view绘制完成后的监听，获取坐标
            @Override
            public void onGlobalLayout() {
                content.getLocationOnScreen(contentXY);
                invalidate();
            }
        });

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);//关闭硬件加速，防止出现黑色
        if(viewMap!=null&&viewMap.size()>0)     //遍历所有高亮View，获取坐标从而依次高亮显示以及文字
        {
            Set viewSet=viewMap.keySet();
            iteratorView=viewSet.iterator();
            Collection<String> cll=viewMap.values();
            iteratorText=cll.iterator();
            focusView=iteratorView.next();
            tipText=iteratorText.next();
            focusView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {
                    Log.e("igv",
                            focusView.getMeasuredWidth() + "=="
                                    + focusView.getMeasuredWidth());
                    focusView.getLocationOnScreen(xy);
                    invalidate();
                }
            });
        }
        setOnClickListener(new OnClickListener() {      //点击依次显示，最后关闭引导View
            @Override
            public void onClick(View v) {
                if(iteratorView!=null&&iteratorView.hasNext())
                {
                    focusView=iteratorView.next();
                    tipText=iteratorText.next();
                    focusView.getLocationOnScreen(xy);
                    invalidate();
                }
                else
                {
                    InnerGuideView.this.setVisibility(View.GONE);
                    setLight((Activity)InnerGuideView.this.context,-1);
                }
            }
        });

    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(color);
        xy[0]-=contentXY[0];        //计算高亮View在contentView中的坐标，即灰色背景中的坐标
        xy[1]-=contentXY[1];
        Paint paint=new Paint();
        paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.CLEAR));      //图像混合模式为clear，显示透明图层
        RectF rectF=new RectF(xy[0]-10,xy[1]-10,xy[0]+focusView.getWidth()+10,xy[1]+focusView.getHeight()+10);      //矩形高亮形状
        RectF rectF2=new RectF(xy[0]-focusView.getWidth()/2*0.414f,xy[1]-focusView.getHeight()/2*0.414f,xy[0]+focusView.getWidth()*1.207f,xy[1]+focusView.getHeight()*1.207f);      //外接椭圆高亮形状，具体计算见我的博客
        switch (borderType)     //判断高亮形状
        {
            case BorderType.RECT:
                canvas.drawRect(rectF,paint);
                break;
            case BorderType.oval:

                canvas.drawOval(rectF2,paint);
                break;
            case BorderType.ROUNDRECT:
                canvas.drawRoundRect(rectF,roundRadius,roundRadius,paint);
                break;
        }
        int dx=18;//矩形框边距
        if(isDash)      //是否显示虚线框
        {
            //创建虚线画笔
            Paint paintDash = new Paint(Paint.ANTI_ALIAS_FLAG);
            paintDash.setColor(Color.WHITE);
            paintDash.setStyle(Paint.Style.STROKE);
            paintDash.setStrokeWidth(2);

            //创建路径
            Path path = new Path();
            //绘制一个矩形虚线框
            RectF dashRect=new RectF(xy[0]-dx,xy[1]-dx,xy[0]+focusView.getWidth()+dx,xy[1]+focusView.getHeight()+dx);
            path.addRect(dashRect, Path.Direction.CW);
            PathEffect pathEffect = new DashPathEffect(new float[]{10f, 10f}, 0);       //虚线路径
            CornerPathEffect cornerPathEffect=new CornerPathEffect(roundRadius);        //弧形路径
            ComposePathEffect sumPathEffect=new ComposePathEffect(pathEffect,cornerPathEffect);     //组合路径
            if(borderType== BorderType.ROUNDRECT)        //根据不同的形状绘制不同的虚线框
            {
                paintDash.setPathEffect(sumPathEffect);
            }
            if(borderType== BorderType.RECT)
            {
                paintDash.setPathEffect(pathEffect);
            }
            if (borderType== BorderType.oval)
            {
                RectF rectF3=new RectF(xy[0]-focusView.getWidth()/2*0.414f-8,xy[1]-focusView.getHeight()/2*0.414f-8,xy[0]+focusView.getWidth()*1.207f+8,xy[1]+focusView.getHeight()*1.207f+8);
                path.reset();
                path.addOval(rectF3, Path.Direction.CW);
                paintDash.setPathEffect(pathEffect);
            }
            canvas.drawPath(path, paintDash);
        }



        //绘制指向的路径和文字
        boolean isTextLeft=false;
        boolean isTextTop=false;
        int interval=100;
        float startPosition[]=new float[2];     //曲线起点
        float textPosition[]=new float[2];      //文字坐标，即曲线终点
        int middleW=getWidth()/2;               //灰色背景中间线横坐标
        int middleH=getHeight()/2;              //灰色背景中间线纵坐标
        int bottom=xy[1]+focusView.getHeight();     //获取View的上下左右坐标值，便于定位文字位置
        int top=xy[1];
        int left=xy[0];
        int right=xy[0]+focusView.getWidth();
        if(bottom<middleH)          //指向的曲线统一起点为高亮view的上边中心或者下边中心，根据高亮View在屏幕中的相对位置来确定文字的上下
        {
            textPosition[1]=bottom+interval;
            startPosition[0]=left+(right-left)/2;
            if(borderType== BorderType.oval)
            {
                startPosition[1]=bottom+focusView.getHeight()/2*0.414f+8;
            }
            else
            {
                startPosition[1]=bottom+dx;
            }

        }
        else if(top>middleH)
        {
            textPosition[1]=top-interval;
            startPosition[0]=left+(right-left)/2;
            if(borderType== BorderType.oval)
            {
                startPosition[1]=top-focusView.getHeight()/2*0.414f-8;
            }
            else
            {
                startPosition[1]=top-dx;
            }

            isTextTop=true;
        }
        else if(middleH-top<=bottom-middleH)
        {
            textPosition[1]=top<interval?middleH-2*interval:top-interval;
            startPosition[0]=left+(right-left)/2;
            if(borderType== BorderType.oval)
            {
                startPosition[1]=top-focusView.getHeight()/2*0.414f-8;
            }
            else
            {
                startPosition[1]=top-dx;
            }

            isTextTop=true;
        }
        else
        {
            textPosition[1]=getHeight()-bottom<interval?middleH+2*interval:bottom+interval;
            startPosition[0]=left+(right-left)/2;
            if(borderType== BorderType.oval)
            {
                startPosition[1]=bottom+focusView.getHeight()/2*0.414f+8;
            }
            else
            {
                startPosition[1]=bottom+dx;
            }

        }
        if(left>middleW)        //同理，根据高亮View的左右相对位置确定文字的左右
        {
            textPosition[0]=left-interval;
            isTextLeft=true;
        }
        else if(right<middleW)
        {
            textPosition[0]=right+interval;
        }
        else if(middleW-left<=right-middleW)
        {
            textPosition[0]=left<interval?middleW-2*interval:left-interval;
            isTextLeft=true;
        }
        else
        {
            textPosition[0]=getWidth()-right<interval?middleW+2*interval:right+interval;
        }

        float mPoint[]=new float[2];        //定义一个中间点，根据起点和终点的斜率值以及文字的上下左右来确定拐点的位置
        int floatdp=20;
        if((textPosition[1]-startPosition[1])*(textPosition[0]-startPosition[0])>0)
        {
            mPoint[0]=(textPosition[0]-startPosition[0])<0?textPosition[0]+(startPosition[0]-textPosition[0])/2:startPosition[0]+(textPosition[0]-startPosition[0])/2;
            mPoint[1]=(textPosition[1]-startPosition[1])<0?textPosition[1]+(startPosition[1]-textPosition[1])/2-floatdp:startPosition[1]+(textPosition[1]-startPosition[1])/2+floatdp;
        }
        else
        {
            mPoint[0]=(textPosition[0]-startPosition[0])<0?textPosition[0]+(startPosition[0]-textPosition[0])/2:startPosition[0]+(textPosition[0]-startPosition[0])/2;
            mPoint[1]=(textPosition[1]-startPosition[1])<0?textPosition[1]+(startPosition[1]-textPosition[1])/2-floatdp:startPosition[1]+(textPosition[1]-startPosition[1])/2+floatdp;
        }
        Paint paintArrow=new Paint();       //开始绘制三点构成的曲线
        paintArrow.setColor(Color.WHITE);
        paintArrow.setAntiAlias(true);
        paintArrow.setStyle(Paint.Style.STROKE);
        paintArrow.setStrokeWidth(2);
        Path pathArrow=new Path();
        pathArrow.moveTo(startPosition[0],startPosition[1]);
        pathArrow.lineTo(mPoint[0],mPoint[1]);
        pathArrow.lineTo(textPosition[0],textPosition[1]);
        paintArrow.setPathEffect(new CornerPathEffect(50));
        canvas.drawPath(pathArrow,paintArrow);




        //绘制文本内容
        Paint paint1=new Paint();
        paint1.setColor(tipTextColor);
        paint1.setTextSize(40);
        Rect rectText=new Rect();
        paint1.getTextBounds(tipText,0,tipText.length(),rectText);
        if(isTextLeft)      //确定文字左右
        {
            canvas.drawText(tipText,textPosition[0]-rectText.width(),isTextTop?textPosition[1]:textPosition[1]+rectText.height(),paint1);
        }
        else
        {
            canvas.drawText(tipText,textPosition[0],textPosition[1],paint1);
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void show()      //显示引导View
    {
        View view=content.getChildAt(0);        //获取当前页面的根布局
        content.removeView(view);               //从contentView中移除
        RelativeLayout relativeLayout=new RelativeLayout(context);      //定义一个相对布局，加入根布局和引导View，引导view置于根布局上方
        relativeLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        relativeLayout.addView(view);
        relativeLayout.addView(this,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        content.addView(relativeLayout);            //重新加入contentView显示出来
        setLight((Activity)this.context,255);       //高亮屏幕
    }





    public static class Builder     //引导View的属性构建
    {
        private int color=Color.argb(200,0,0,0);
        private Context context;
        private Map<View,String> viewMap=new HashMap<>();
        private int tipTextColor=Color.WHITE;
        private int borderType=0;
        private int roundRadius=10;
        private boolean isDash=true;

        public Builder setTipTextColor(int tipTextColor) {
            this.tipTextColor = tipTextColor;
            return this;
        }

        public Builder setBorderType(int borderType) {
            this.borderType = borderType;
            return this;
        }



        public Builder setViewsMap(Map viewsMap)
        {
            this.viewMap=viewsMap;
            return this;
        }

        public Builder setColor(int color) {
            this.color = color;
            return this;
        }


        public Builder setContext(Context context) {
            this.context = context;
            return this;
        }

        public Builder setRoundRadius(int roundRadius) {
            this.roundRadius = roundRadius;
            return this;
        }

        public Builder setDash(boolean dash) {
            isDash = dash;
            return this;
        }

        public InnerGuideView build()
        {
            return new InnerGuideView(context,color,viewMap,borderType,tipTextColor,roundRadius,isDash);
        }
    }

    public class BorderType     //高亮形状
    {
        public static final int RECT=0;
        public static final int oval=1;
        public static final int ROUNDRECT=2;
    }
    private void setLight(Activity context, int brightness) {       //高亮屏幕
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.screenBrightness = Float.valueOf(brightness) * (1f / 255f);
        context.getWindow().setAttributes(lp);
    }
}
