package com.test.gradletask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Set;

import static android.content.ContentValues.TAG;

/**
 * 微信hook
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
@SuppressLint({ "ObsoleteSdkInt", "SetWorldReadable", "SetWorldWritable" })
public class WeiXinHook implements IXposedHookLoadPackage {
  private static String TAG="handleLoadPackage";
  @Override
  public void handleLoadPackage(final LoadPackageParam lp) throws Throwable {
    if (Build.VERSION.SDK_INT < 19 || Build.VERSION.SDK_INT > 25) {
      return;
    }
    final String pkg = "cn.com.open.mooc";
    Log.e("WeiXinHook", lp.packageName);
    if (pkg.equals(lp.packageName)) {
      //这里是为了解决app多dex进行hook的问题，Xposed默认是hook主dex
      XposedHelpers
          .findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
              Log.e("handleLoadPackage", "afterHookedMethod");
              final ClassLoader cl = ((Context) param.args[0]).getClassLoader();
              XposedHelpers.findAndHookMethod("cn.com.open.mooc.component.componentgoodsintro.ui.GoodsIntroActivity",
                  cl, "onCreate", Bundle.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                      //获取当前hook的activity
                      Activity thisObject = (Activity) param.thisObject;
                      Log.e("handleLoadPackage", thisObject.getClass().getName());
                      Intent intent = thisObject.getIntent();
                      Bundle bundle = intent.getExtras();
                      printBundle(bundle);
                      printFields(thisObject.getClass());

                    }
                  });
            }
          });
    }
  }


  public static void printBundle(Bundle bundle) {
    if (null!=bundle){
      for (String key : bundle.keySet()) {
        Log.e(TAG, "bundle.key: " + key + ", value: " + bundle.get(key));
      }
    }
  }
  public static void printTreeView(Activity activity) {
    View rootView = activity.getWindow().getDecorView();
    printTreeView(rootView);
  }
  public static void printTreeView(View rootView) {
    if (rootView instanceof ViewGroup) {
      ViewGroup parentView = (ViewGroup) rootView;
      for (int i = 0; i < parentView.getChildCount(); i++) {
        printTreeView(parentView.getChildAt(i));
      }
    } else {
      Log.d(TAG, "view: " + rootView.getId() + ", class: " + rootView.getClass());
      // any view if you want something different
      if (rootView instanceof EditText) {
        Log.d(TAG, "edit:" + rootView.getTag()
            + "， hint: " + ((EditText) rootView).getHint());
      } else if (rootView instanceof TextView) {
        Log.d(TAG, "text:" + ((TextView) rootView).getText().toString());
      }
    }
  }
  public static void printMethods(Class clazz) {
    for (Method method : clazz.getDeclaredMethods()) {
      Log.d(TAG, "" + method);
    }
  }
  public static void printFields(Class clazz) {
    for (Field field : clazz.getFields()) {
      Log.d(TAG, "" + field);
    }
  }


}

