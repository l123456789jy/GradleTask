package com.test.gradletask;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.test.gradletask.db.DataBaseHelper;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

/**
 * 项目名称 西瓜电影hook
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2018/3/12 10:46
 * 修改人：Administrator
 * 修改时间：2018/3/12 10:46
 * 修改备注：
 * 联系方式：906514731@qq.com
 */
public class XiGuaMovieHook implements IXposedHookLoadPackage {

  private static final String FILTER_PKGNAME = "cn.vcinema.cinema";

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
              startManActivity(param.args[0], cl);

              XposedHelpers.findAndHookMethod("cn.vcinema.cinema.activity.renew.Renew461Activity",
                  cl, "onCreate",
                  Bundle.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                      Object fragment = param.thisObject;
                      ((Activity) fragment).finish();
                    }
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                      super.beforeHookedMethod(param);
                    }
                  });

              XposedHelpers.findAndHookMethod("cn.vcinema.cinema.activity.videoplay.HorizontalActivity",
                  cl, "onCreate",Bundle.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    }
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                      super.beforeHookedMethod(param);
                      Object fragment = param.thisObject;
                      Toast.makeText(((Activity) fragment),"注入成功",Toast.LENGTH_SHORT).show();
                      try {
                        Object getInstance = XposedHelpers.callStaticMethod(
                            cl.loadClass("cn.vcinema.cinema.utils.singleton.PumpkinGlobal"),
                            "getInstance");
                        int vipStatus = (int) XposedHelpers.getObjectField(getInstance, "vipStatus");
                        Log.e("user_vip_end_date_desc","修改之前的VIP状态"+vipStatus);
                        XposedHelpers.setObjectField(getInstance,"vipStatus",2);
                        int vipStatus2 = (int) XposedHelpers.getObjectField(getInstance, "vipStatus");
                        Log.e("user_vip_end_date_desc","修改的VIP状态"+vipStatus2);

                      /*  //操作数据库==================================
                        Cursor findBySQL = (Cursor) XposedHelpers.callStaticMethod(
                            cl.loadClass("org.litepal.LitePal"),
                            "findBySQL","select * from userinfo");
                        int user_id= findBySQL.getInt(findBySQL.getColumnIndex("user_id"));
                        Log.e("user_vip_end_date_desc","用户UID===="+user_id);
                        findBySQL.close();*/

                      }catch (Exception e){
                        Log.e("user_vip_end_date_desc",e.getMessage());
                      }
                    }
                  });

             /* try {
                DataBaseHelper dataBaseHelper = new DataBaseHelper((Context)param.args[0]);
                SQLiteDatabase readableDatabase = dataBaseHelper.getReadableDatabase();
                Cursor cursor =  readableDatabase.rawQuery("SELECT * FROM userinfo ", new String[]{});
                //存在数据才返回true
                if(cursor.moveToFirst())
                {
                  String personid = cursor.getString(cursor.getColumnIndex("user_vip_end_date_desc"));
                  Log.e("user_vip_end_date_desc", personid);
                }
                cursor.close();
                readableDatabase.close();
              }catch(Exception e) {
                Log.e("user_vip_end_date_desc", e.getMessage());
              }*/
            }
          });
    }
  }

  private void startManActivity(Object arg, ClassLoader cl) {
    Intent intent = new Intent();
    intent.setClassName(((Context) arg), "cn.vcinema.cinema.activity.main.MainActivity");
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    ((Context) arg).startActivity(intent);
  }
}

