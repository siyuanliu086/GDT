package com.qq.e.union.demo;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.qq.e.ads.interstitial2.UnifiedInterstitialAD;
import com.qq.e.ads.interstitial2.UnifiedInterstitialADListener;
import com.qq.e.comm.util.AdError;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class UnifiedInterstitialADActivity extends Activity implements OnClickListener,
    UnifiedInterstitialADListener, CompoundButton.OnCheckedChangeListener {

  private static final String TAG = UnifiedInterstitialADActivity.class.getSimpleName();
  private UnifiedInterstitialAD iad;
  private String posId;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_unified_interstitial_ad);
    ((EditText) findViewById(R.id.posId)).setText(PositionId.UNIFIED_INTERSTITIAL_ID_LARGE_SMALL);
    this.findViewById(R.id.loadIAD).setOnClickListener(this);
    this.findViewById(R.id.showIAD).setOnClickListener(this);
    this.findViewById(R.id.showIADAsPPW).setOnClickListener(this);
    this.findViewById(R.id.closeIAD).setOnClickListener(this);
    ((CheckBox) this.findViewById(R.id.cbPos)).setOnCheckedChangeListener(this);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (iad != null) {
      iad.destroy();
    }
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.loadIAD:
        getIAD().loadAD();
        break;
      case R.id.showIAD:
        showAD();
        break;
      case R.id.showIADAsPPW:
        showAsPopup();
        break;
      case R.id.closeIAD:
        close();
        break;
      default:
        break;
    }
  }

  private UnifiedInterstitialAD getIAD() {
    String posId = getPosID();
    if (iad != null && this.posId.equals(posId)) {
      return iad;
    }
    this.posId = posId;
    if (this.iad != null) {
      iad.close();
      iad.destroy();
      iad = null;
    }
    if (iad == null) {
      iad = new UnifiedInterstitialAD(this, Constants.APPID, posId, this);
    }
    return iad;
  }

  private void showAD() {
    if (iad != null) {
      iad.show();
    } else {
      Toast.makeText(this, "请加载广告后再进行展示 ！ ", Toast.LENGTH_LONG).show();
    }
  }

  private void showAsPopup() {
    if (iad != null) {
      iad.showAsPopupWindow();
    } else {
      Toast.makeText(this, "请加载广告后再进行展示 ！ ", Toast.LENGTH_LONG).show();
    }
  }

  private void close() {
    if (iad != null) {
      iad.close();
    } else {
      Toast.makeText(this, "广告尚未加载 ！ ", Toast.LENGTH_LONG).show();
    }
  }

  private String getPosID() {
    String posId = ((EditText) findViewById(R.id.posId)).getText().toString();
    return TextUtils.isEmpty(posId) ? PositionId.UNIFIED_INTERSTITIAL_ID_LARGE_SMALL : posId;
  }

  @Override
  public void onADReceive() {
    Toast.makeText(this, "广告加载成功 ！ ", Toast.LENGTH_LONG).show();
    // onADReceive之后才可调用getECPM()
    Log.d(TAG, "eCPM = " + iad.getECPM() + " , eCPMLevel = " + iad.getECPMLevel());
  }

  @Override
  public void onNoAD(AdError error) {
    String msg = String.format(Locale.getDefault(), "onNoAD, error code: %d, error msg: %s",
        error.getErrorCode(), error.getErrorMsg());
    Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
  }

  @Override
  public void onADOpened() {
    Log.i(TAG, "onADOpened");
  }

  @Override
  public void onADExposure() {
    Log.i(TAG, "onADExposure");
  }

  @Override
  public void onADClicked() {
    Log.i(TAG, "onADClicked : " + (iad.getExt() != null? iad.getExt().get("clickUrl") : ""));
  }

  @Override
  public void onADLeftApplication() {
    Log.i(TAG, "onADLeftApplication");
  }

  @Override
  public void onADClosed() {
    Log.i(TAG, "onADClosed");
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    if (isChecked) {
      ((EditText) findViewById(R.id.posId)).setText(PositionId.UNIFIED_INTERSTITIAL_ID_LARGE_SMALL);
    } else {
      ((EditText) findViewById(R.id.posId)).setText(PositionId.UNIFIED_INTERSTITIAL_ID_ONLY_SMALL);
    }
  }
}
