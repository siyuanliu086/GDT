package com.qq.e.union.demo;

import android.app.Application;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.tencent.bugly.crashreport.CrashReport;

public class DemoApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    config(this);
  }

  void config(Context context) {
    try {
      CrashReport.initCrashReport(this, Constants.BuglyAppID, true);

      String packageName = context.getPackageName();
      //Get all activity classes in the AndroidManifest.xml
      PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
              packageName, PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA);
      if (packageInfo.activities != null) {
        for (ActivityInfo activity : packageInfo.activities) {
          Bundle metaData = activity.metaData;
          if (metaData != null && metaData.containsKey("id")
                  && metaData.containsKey("content") && metaData.containsKey("action")) {
            Log.e("gdt", activity.name);
            try {
              Class.forName(activity.name);
            } catch (ClassNotFoundException e) {
              continue;
            }
            String id = metaData.getString("id");
            String content = metaData.getString("content");
            String action = metaData.getString("action");
            DemoListActivity.register(action, id, content);
          }
        }
      }
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
  }
}
