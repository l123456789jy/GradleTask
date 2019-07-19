package com.test.gradletask.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 项目名称：GradleTask
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2019/7/18 15:23
 * 修改人：Administrator
 * 修改时间：2019/7/18 15:23
 * 修改备注：
 * 联系方式：906514731@qq.com
 */
public class DataBaseHelper extends SQLiteOpenHelper {
  private static final String DATABASE_NAME = "vcinemaph4.db";

  public DataBaseHelper(Context context) {
    super(context, DATABASE_NAME, null, 15);
  }

  public DataBaseHelper(Context context, String str, SQLiteDatabase.CursorFactory cursorFactory, int i) {
    super(context, str, cursorFactory, i);
  }

  @Override public void onCreate(SQLiteDatabase sQLiteDatabase) {

  }

  @Override public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {

  }
}
