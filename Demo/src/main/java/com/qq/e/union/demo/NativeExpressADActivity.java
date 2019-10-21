package com.qq.e.union.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import static com.qq.e.union.demo.Constants.VIDEO_DURATION_SETTING_MAX;
import static com.qq.e.union.demo.Constants.VIDEO_DURATION_SETTING_MIN;

/**
 * Created by hechao on 2018/2/8.
 */

public class NativeExpressADActivity extends Activity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_native_express_ad);
    /**
     * 如果选择支持视频的模版样式，请使用{@link PositionId#NATIVE_EXPRESS_SUPPORT_VIDEO_POS_ID}
     */
    ((EditText) findViewById(R.id.posId)).setText(PositionId.NATIVE_EXPRESS_POS_ID);
  }

  /**
   * 如果选择支持视频的模版样式，请使用{@link PositionId#NATIVE_EXPRESS_SUPPORT_VIDEO_POS_ID}
   */
  private String getPosID() {
    String posId = ((EditText) findViewById(R.id.posId)).getText().toString();
    return TextUtils.isEmpty(posId) ? PositionId.NATIVE_EXPRESS_POS_ID : posId;
  }

  private int getMaxVideoDuration() {
    if (((CheckBox) findViewById(R.id.cbMaxVideoDuration)).isChecked()) {
      try {
        int rst = Integer.parseInt(((EditText) findViewById(R.id.etMaxVideoDuration)).getText()
            .toString());
        if (rst >= VIDEO_DURATION_SETTING_MIN && rst <= VIDEO_DURATION_SETTING_MAX) {
          return rst;
        } else {
          Toast.makeText(getApplicationContext(), "最大视频时长输入不在有效区间内!", Toast.LENGTH_LONG).show();
        }
      } catch (NumberFormatException e) {
        Toast.makeText(getApplicationContext(), "最大视频时长输入不是整数!", Toast.LENGTH_LONG).show();
      }
    }
    return 0;
  }

  public void onNormalViewClicked(View view) {
    Intent intent = new Intent();
    intent.setClass(this, NativeExpressDemoActivity.class);
    intent.putExtra(Constants.POS_ID, getPosID());
    intent.putExtra(Constants.MAX_VIDEO_DURATION, getMaxVideoDuration());
    startActivity(intent);
  }

  public void onRecyclerViewClicked(View view) {
    Intent intent = new Intent();
    intent.setClass(this, NativeExpressRecyclerViewActivity.class);
    intent.putExtra(Constants.POS_ID, getPosID());
    intent.putExtra(Constants.MAX_VIDEO_DURATION, getMaxVideoDuration());
    startActivity(intent);
  }
}
