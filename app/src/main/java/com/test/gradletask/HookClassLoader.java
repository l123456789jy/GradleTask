package com.test.gradletask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import java.lang.reflect.Method;
import java.util.Arrays;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

/**
 * 项目名称：GradleTask
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2018/11/29 13:15
 * 修改人：Administrator
 * 修改时间：2018/11/29 13:15
 * 修改备注：
 * 联系方式：906514731@qq.com
 */
public class HookClassLoader implements IXposedHookLoadPackage {
  @Override
  public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpp) throws Throwable {
    Log.e("handleLoadPackage", lpp.packageName );
    if (!"cn.com.open.mooc".equals(lpp.packageName)) return;

    // 第一步：Hook方法ClassLoader#loadClass(String)
    findAndHookMethod(ClassLoader.class, "loadClass", String.class, new XC_MethodHook() {
      @Override
      protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        if (param.hasThrowable()) {
          return;
        }
        Class<?> cls = (Class<?>) param.getResult();
        String name = cls.getName();
        Log.e("HookClassLoader", name );
        if ("cn.com.open.mooc.component.componentgoodsintro.ui.GoodsIntroActivity".equals(name)) {
          //printMethods(cls);
          // 所有的类都是通过loadClass方法加载的
          // 所以这里通过判断全限定类名，查找到目标类
          // 第二步：Hook目标方法
          findAndHookMethod(cls, "onStart",  new XC_MethodHook() {
            @SuppressLint("LongLogTag") @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

            }
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
              Activity thisObject = (Activity) param.thisObject;
              Intent intent = thisObject.getIntent();
              Bundle bundle = intent.getExtras();
              printBundle(bundle);

            }
          });
        }
      }
    });
  }

  public static void printMethods(Class clazz) {
    for (Method method : clazz.getDeclaredMethods()) {
      Log.e("FooxMainbeforeHooked", "" + method);
    }
  }


  public static void printBundle(Bundle bundle) {
    if (null!=bundle){
      for (String key : bundle.keySet()) {
        Log.e("FooxMainbeforeHooked", "bundle.key: " + key + ", value: " + bundle.get(key));
      }
    }
  }
}
