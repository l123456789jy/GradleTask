package com.test.gradletask;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.support.annotation.RequiresApi;
import android.util.Log;
import dalvik.system.DexClassLoader;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by fooree on 2018/1/19.
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
@SuppressLint({"ObsoleteSdkInt", "SetWorldReadable", "SetWorldWritable"})
public class DexHook implements IXposedHookLoadPackage {

  private static final String TAG = DexHook.class.getSimpleName();
  private static final File DUMP_PATH = new File("/data/local/tmp/dumpdex/");

  @Override
  public void handleLoadPackage(final LoadPackageParam lp) throws Throwable {
    if (Build.VERSION.SDK_INT < 19 || Build.VERSION.SDK_INT > 25) {
      return;
    }
    String pkg = "com.liaoliao.dl.application";
    if (lp.packageName.equals(pkg)) {

    }
  }
}

