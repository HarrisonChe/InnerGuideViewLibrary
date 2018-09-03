# InnerGuideViewLibrary
##Android内部引导页（支持Activity和Fragment）


###引用方法：  
第一步：在Project的build.gradle文件中添加:  
```java
	allprojects {  
		repositories {  
			...  
			maven { url 'https://jitpack.io' }  
		}  
	}  
  
第二步：在app的build.gradle文件中添加：  
	dependencies {  
	        implementation 'com.github.HarrisonChe:InnerGuideViewLibrary:1.0'  
	}  
```  
  
  
###使用方法：  
在需要引导的Activity的onCreate或者其他函数中写入代码  
```java
Map<View,String> map=new LinkedHashMap<>();   //定义需要高亮引导的view集合和对应的提示文字，按顺序加入  
        map.put(v1,"这是个人信息");  
        map.put(v2,"这是钱包");  
        map.put(v3,"这是设置");  
        map.put(v4,"点这里查看");  
        map.put(v5,"点这里退出");  
        InnerGuideView igv=new InnerGuideView.Builder()  
                .setColor(Color.argb(180,0,0,0))  //背景色(默认灰色透明)  
                .setContext(MainActivity.this)    //传入Activity的context，fragment中使用getActivity()即可  
                .setViewsMap(map)                 //需要高亮的view集合  
                .setBorderType(InnerGuideView.BorderType.ROUNDRECT)   //高亮view的形状  
                .setRoundRadius(10)             //圆角矩形的弧度  
                .setTipTextColor(Color.RED)     //提示文字的颜色  
                .setDash(true)                 //是否显示虚线边框  
                .build();  
        igv.show();            //显示引导页  
	```
