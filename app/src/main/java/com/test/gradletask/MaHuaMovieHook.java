package com.test.gradletask;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

/**
 * 项目名称：麻花影视hook类
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2018/3/12 10:46
 * 修改人：Administrator
 * 修改时间：2018/3/12 10:46
 * 修改备注：
 * 联系方式：906514731@qq.com
 */
public class MaHuaMovieHook implements IXposedHookLoadPackage {

  private static final String FILTER_PKGNAME = "com.amahua.ompimdrt";

  @Override
  public void handleLoadPackage(final LoadPackageParam loadPackageParam) throws Throwable {
    Log.e("handleLoadPackage", loadPackageParam.packageName);
    if (FILTER_PKGNAME.equals(loadPackageParam.packageName)) {

      //这里是为了解决app多dex进行hook的问题，Xposed默认是hook主dex
      XposedHelpers
          .findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
              Log.e("handleLoadPackage", "init");
              final ClassLoader cl = ((Context) param.args[0]).getClassLoader();
                startManActivity(param.args[0],cl);

                XposedHelpers.findAndHookMethod("com.mh.movie.core.mvp.ui.fragment.MyFragment", cl, "onResume", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Object fragment = param.thisObject;
                    String fragmentName = fragment.getClass().getName();
                   Log.e("handleLoadPackage", "afterHookedMethod==="+fragmentName);


                    //用户属性
                    XposedHelpers.findAndHookMethod("com.mh.movie.core.mvp.ui.b", fragment.getClass().getClassLoader(), "a",
                            new XC_MethodHook() {
                                @Override
                                protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                                    Log.e("handleLoadPackage", "afterHookedMethod==="+param.getResult());
                                    XposedHelpers.callMethod(param.getResult(),"setMoney",1000000);
                                    XposedHelpers.callMethod(param.getResult(),"setLevel",1000000);
                                    XposedHelpers.callMethod(param.getResult(),"setCanCacheNum",1000000);
                                    XposedHelpers.callMethod(param.getResult(),"setRestCacheNum",1000000);

                                }
                                @Override
                                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                    super.beforeHookedMethod(param);

                                }
                            });

                 }
                  @Override
                  protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                      super.beforeHookedMethod(param);

                  }
              });



                //屏蔽vip弹窗
                XposedHelpers.findAndHookMethod("com.mh.movie.core.mvp.ui.activity.VipActivity", cl, "a",boolean.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                        Log.e("handleLoadPackage", "afterHookedMethod==="+param.args[0]);
                         View vd = (View) XposedHelpers.getObjectField(param.thisObject, "vipDialog");
                        vd.setVisibility(View.GONE);

                    }
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);

                    }
                });



            }
          });
    }


  }

    private void startManActivity(Object arg, ClassLoader cl) {
        Intent intent = new Intent();
        intent.setClassName(((Context) arg),"com.mh.movie.core.mvp.ui.activity.main.MainActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ((Context) arg).startActivity(intent);
    }
}

