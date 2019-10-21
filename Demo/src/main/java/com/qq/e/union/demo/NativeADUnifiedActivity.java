package com.qq.e.union.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import static com.qq.e.union.demo.Constants.VIDEO_DURATION_SETTING_MAX;
import static com.qq.e.union.demo.Constants.VIDEO_DURATION_SETTING_MIN;


public class NativeADUnifiedActivity extends Activity {

  private Spinner mPlayNetworkSpinner;
  private CheckBox mVideoOptionCheckBox;

  private CheckBox mMuteCheckBox;
  private CheckBox mCoverCheckBox;
  private CheckBox mProgressCheckBox;
  private CheckBox mDetailCheckBox;
  private CheckBox mControlCheckBox;

  public static final String PLAY_MUTE = "mute";
  public static final String PLAY_NETWORK = "network";
  public static final String NEED_COVER = "need_cover";
  public static final String NEED_PROGRESS = "need_progress";
  public static final String ENABLE_DETAIL_PAGE = "enable_detail_page";
  public static final String ENABLE_USER_CONTROL = "enable_user_control";
  public static final String NONE_OPTION = "none_option";

  private static final String TAG = NativeADUnifiedActivity.class.getSimpleName();

  private boolean mNoneOption = false;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_native_unified_ad);
    ((EditText) findViewById(R.id.posId)).setText(PositionId.NATIVE_UNIFIED_POS_ID);

    mVideoOptionCheckBox = findViewById(R.id.cb_none_video_option);
    mVideoOptionCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(TAG, "onCheckedChanged: isChecked:" + isChecked);
        mNoneOption = isChecked;
        boolean enable = !isChecked;
        mPlayNetworkSpinner.setEnabled(enable);
        mMuteCheckBox.setEnabled(enable);
        mCoverCheckBox.setEnabled(enable);
        mProgressCheckBox.setEnabled(enable);
        mDetailCheckBox.setEnabled(enable);
        mControlCheckBox.setEnabled(enable);
      }
    });

    mPlayNetworkSpinner = findViewById(R.id.spinner_network);
    mPlayNetworkSpinner.setSelection(1); // 默认任何网络下都自动播放

    mMuteCheckBox = findViewById(R.id.btn_mute);
    mCoverCheckBox = findViewById(R.id.btn_cover);
    mProgressCheckBox = findViewById(R.id.btn_progress);
    mDetailCheckBox = findViewById(R.id.btn_detail);
    mControlCheckBox = findViewById(R.id.btn_control);
  }
  
  private String getPosID() {
    String posId = ((EditText) findViewById(R.id.posId)).getText().toString();
    return TextUtils.isEmpty(posId) ? PositionId.NATIVE_UNIFIED_POS_ID : posId;
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
    startActivity(getIntent(NativeADUnifiedSampleActivity.class));
  }

  public void onRecyclerViewClicked(View view) {
    startActivity(getIntent(NativeADUnifiedRecyclerViewActivity.class));
  }

  public void onListViewClick(View view) {
    startActivity(getIntent(NativeADUnifiedListViewActivity.class));
  }

  public void onPreMovieClick(View view){
    startActivity(getIntent(NativeADUnifiedPreMovieActivity.class));
  }

  public void onFullScreenClick(View view) {
    startActivity(getIntent(NativeADUnifiedFullScreenActivity.class));
  }

  public void onFullScreenFeedClick(View view){
    startActivity(getIntent(NativeADUnifiedFullScreenFeedActivity.class));
  }

  public void onDevRenderContainerClick(View view){
    startActivity(getIntent(NativeADUnifiedDevRenderContainerActivity.class));
  }

  private Intent getIntent(Class cls){
    Intent intent = new Intent();
    intent.setClass(this, cls);
    intent.putExtra(Constants.POS_ID, getPosID());
    intent.putExtra(Constants.MAX_VIDEO_DURATION, getMaxVideoDuration());
    intent.putExtra(NONE_OPTION, mNoneOption);
    intent.putExtra(PLAY_NETWORK, mPlayNetworkSpinner.getSelectedItemPosition());
    intent.putExtra(PLAY_MUTE, mMuteCheckBox.isChecked());
    intent.putExtra(NEED_COVER, mCoverCheckBox.isChecked());
    intent.putExtra(NEED_PROGRESS, mProgressCheckBox.isChecked());
    intent.putExtra(ENABLE_DETAIL_PAGE, mDetailCheckBox.isChecked());
    intent.putExtra(ENABLE_USER_CONTROL, mControlCheckBox.isChecked());
    return intent;
  }
}
