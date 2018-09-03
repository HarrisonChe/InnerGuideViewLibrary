package lib.cbz.com.innerguideviewlibrary;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import java.util.LinkedHashMap;
import java.util.Map;

import lib.cbz.com.innerguideviewlib.InnerGuideView;

public class MainActivity extends Activity {

    private TextView tv1,tv2,tv3,tv4,tv5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv1=(TextView)findViewById(R.id.tv1);
        tv2=(TextView)findViewById(R.id.tv2);
        tv3=(TextView)findViewById(R.id.tv3);
        tv4=(TextView)findViewById(R.id.tv4);
        tv5=(TextView)findViewById(R.id.tv5);
        Map<View,String> map=new LinkedHashMap<>();
        map.put(tv1,"这是个人信息");
        map.put(tv2,"这是钱包");
        map.put(tv3,"这是设置");
        map.put(tv4,"点这里查看");
        map.put(tv5,"点这里退出");
        InnerGuideView igv=new InnerGuideView.Builder()
                .setColor(Color.argb(180,0,0,0))
                .setContext(MainActivity.this)
                .setViewsMap(map)
                .setBorderType(InnerGuideView.BorderType.oval)
                .setRoundRadius(10)
                .setTipTextColor(Color.RED)
                .setDash(false)
                .build();
        igv.show();
    }
}
