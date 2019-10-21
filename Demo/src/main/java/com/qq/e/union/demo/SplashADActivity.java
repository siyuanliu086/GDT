package com.qq.e.union.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import com.qq.e.ads.splash.SplashAD;
import com.qq.e.comm.constants.LoadAdParams;

/**
 * @author tysche
 */

public class SplashADActivity extends Activity implements View.OnClickListener {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash_ad);
    ((EditText) findViewById(R.id.posId)).setText(PositionId.SPLASH_POS_ID);
    findViewById(R.id.splashADPreloadButton).setOnClickListener(this);
    findViewById(R.id.splashADDemoButton).setOnClickListener(this);
  }

  private String getPosID() {
    String posId = ((EditText) findViewById(R.id.posId)).getText().toString();
    return TextUtils.isEmpty(posId) ? PositionId.SPLASH_POS_ID : posId;
  }

  private boolean needLogo() {
    return ((CheckBox) findViewById(R.id.checkBox)).isChecked();
  }

  private boolean customSkipBtn(){
    return ((CheckBox)findViewById(R.id.checkCustomSkp)).isChecked();
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.splashADPreloadButton:
        SplashAD splashAD = new SplashAD(this, Constants.APPID, getPosID(), null);
        LoadAdParams params = new LoadAdParams();
        params.setLoginAppId("testAppId");
        params.setLoginOpenid("testOpenId");
        params.setUin("testUin");
        splashAD.setLoadAdParams(params);
        splashAD.preLoad();
        break;
      case R.id.splashADDemoButton:
        Intent intent = new Intent(SplashADActivity.this, SplashActivity.class);
        intent.putExtra("pos_id", getPosID());
        intent.putExtra("need_logo", needLogo());
        intent.putExtra("need_start_demo_list", false);
        intent.putExtra("custom_skip_btn", customSkipBtn());
        startActivity(intent);
        break;
    }
  }
}
