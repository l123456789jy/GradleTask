package com.test.gradletask;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

/**
 * 项目名称：GradleTask
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2018/3/12 10:46
 * 修改人：Administrator
 * 修改时间：2018/3/12 10:46
 * 修改备注：
 * 联系方式：906514731@qq.com
 */
public class Man implements IXposedHookLoadPackage {

  private static final String FILTER_PKGNAME = "com.congcong.dl.application";
  private static final String BAI_DU_PKGNAME = "com.congcong.dl.application.widget.BDCloudVideoView";
  private static final String AD_PKGNAME = "com.congcong.dl.application.cc.bar.AdvancedMediaController";
  private static final String LOG_PKGNAME = "android.util.Log";
  @Override
  public void handleLoadPackage(final LoadPackageParam loadPackageParam) throws Throwable {
    Log.e("handleLoadPackage", loadPackageParam.packageName);
    if (FILTER_PKGNAME.equals(loadPackageParam.packageName)) {

      //这里是为了解决app多dex进行hook的问题，Xposed默认是hook主dex
      XposedHelpers
          .findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
              Log.e("handleLoadPackage", "afterHookedMethod");
              final ClassLoader cl = ((Context) param.args[0]).getClassLoader();
              XposedHelpers.findAndHookMethod("com.congcong.dl.application.cc.AdvancedPlayActivity", cl, "onCreate", Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                  Log.e("handleLoadPackage", "onCreate");
                  //获取当前hook的activity
                  final Activity thisObject = (Activity) param.thisObject;
                  Uri data = thisObject.getIntent().getData();
                  Log.e("handleLoadPackage",data.toString());
                  final Class<?> aClass = cl.loadClass(AD_PKGNAME);
                  //hook AdvancedMediaController 中 startPositionTimer方法并且替换为空实现
                  XposedHelpers.findAndHookMethod(aClass, "startPositionTimer",
                      new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam methodHookParam)
                            throws Throwable {
                          Toast.makeText(thisObject,"hook,成功！",Toast.LENGTH_SHORT).show();
                          Log.e("handleLoadPackage", "replaceHookedMethod");
                          return null;
                        }
                      });
                }
              });
              //================================================
              final Class<?> aClass = cl.loadClass("com.congcong.dl.application.cc.ShowListActivityQ");
              XposedHelpers.findAndHookMethod(aClass, "getlogin",
                  new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam methodHookParam)
                        throws Throwable {
                      Toast.makeText(((Activity) methodHookParam.thisObject),"hook,成功！",Toast.LENGTH_SHORT).show();
                      //通过查看源码发现他标记用户的vip标识是vip=1是0不是，所以动态修改他的属性就行了
                      XposedHelpers.setObjectField(methodHookParam.thisObject,"vip","1");
                      Log.e("handleLoadPackage", "replaceHookedMethod");
                      return null;
                    }
                  });

            }
          });
    }


  }
}

