package com.qq.e.union.demo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.MediaView;
import com.qq.e.ads.nativ.NativeADEventListener;
import com.qq.e.ads.nativ.NativeADMediaListener;
import com.qq.e.ads.nativ.NativeADUnifiedListener;
import com.qq.e.ads.nativ.NativeUnifiedAD;
import com.qq.e.ads.nativ.NativeUnifiedADData;
import com.qq.e.ads.nativ.widget.NativeAdContainer;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.util.AdError;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NativeADUnifiedDevRenderContainerActivity extends Activity implements NativeADUnifiedListener {

  private AQuery mAQuery;
  private View mVideoCoverContainer;
  private ImageView mVideoCover;
  private Button mDownloadButton;
  private NativeUnifiedADData mAdData;
  private static final int AD_COUNT = 1;
  private static final String TAG = NativeADUnifiedDevRenderContainerActivity.class.getSimpleName();

  // 与广告有关的变量，用来显示广告素材的UI
  private NativeUnifiedAD mAdManager;
  private MediaView mMediaView;
  private ImageView mImagePoster;
  private NativeAdContainer mADContainer;

  private View mButtonsContainer;
  private Button mPlayButton;
  private Button mPauseButton;
  private Button mStopButton;
  private CheckBox mMuteButton;

  private boolean mPlayMute = true;

  private boolean mVideoADShowing = false;
  private boolean mVideoADBinded = false;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_native_unified_ad_dev_render_container);
    initView();

    boolean nonOption = getIntent().getBooleanExtra(NativeADUnifiedActivity.NONE_OPTION, false);
    if(!nonOption){
      mPlayMute = getIntent().getBooleanExtra(NativeADUnifiedActivity.PLAY_MUTE,true);
    }

    mAdManager = new NativeUnifiedAD(this, Constants.APPID, getPosId(), this);
    mAdManager.setMaxVideoDuration(getMaxVideoDuration());

    /**
     * 如果广告位支持视频广告，强烈建议在请求广告前，调用下面两个方法，有助于提高视频广告的eCPM值 <br/>
     * 如果广告位仅支持图文广告，则无需调用
     */

    /**
     * 设置本次拉取的视频广告，从用户角度看到的视频播放策略<p/>
     *
     * "用户角度"特指用户看到的情况，并非SDK是否自动播放，与自动播放策略AutoPlayPolicy的取值并非一一对应 <br/>
     *
     * 例如开发者设置了VideoOption.AutoPlayPolicy.NEVER，表示从不自动播放 <br/>
     * 但满足某种条件(如晚上10点)时，开发者调用了startVideo播放视频，这在用户看来仍然是自动播放的
     */
    mAdManager.setVideoPlayPolicy(NativeADUnifiedSampleActivity.getVideoPlayPolicy(getIntent(), this)); // 本次拉回的视频广告，在用户看来是否为自动播放的

    /**
     * 设置在视频广告播放前，用户看到显示广告容器的渲染者是SDK还是开发者 <p/>
     *
     * 一般来说，用户看到的广告容器都是SDK渲染的，但存在下面这种特殊情况： <br/>
     *
     * 1. 开发者将广告拉回后，未调用bindMediaView，而是用自己的ImageView显示视频的封面图 <br/>
     * 2. 用户点击封面图后，打开一个新的页面，调用bindMediaView，此时才会用到SDK的容器 <br/>
     * 3. 这种情形下，用户先看到的广告容器就是开发者自己渲染的，其值为VideoADContainerRender.DEV
     * 4. 如果觉得抽象，可以参考NativeADUnifiedDevRenderContainerActivity的实现
     */
    mAdManager.setVideoADContainerRender(VideoOption.VideoADContainerRender.DEV); // 视频播放前，用户看到的广告容器是由开发者渲染的

    mAdManager.loadData(AD_COUNT);
  }

  private void initView() {
    mVideoCoverContainer = findViewById(R.id.video_cover_container);
    mVideoCover = findViewById(R.id.video_cover);

    mMediaView = findViewById(R.id.gdt_media_view);
    mImagePoster = findViewById(R.id.img_poster);
    mDownloadButton = findViewById(R.id.btn_download);
    mADContainer = findViewById(R.id.native_ad_container);
    mAQuery = new AQuery(findViewById(R.id.root));

    mButtonsContainer = findViewById(R.id.video_btns_container);
    mPlayButton = findViewById(R.id.btn_play);
    mPauseButton = findViewById(R.id.btn_pause);
    mStopButton = findViewById(R.id.btn_stop);
    mMuteButton = findViewById(R.id.btn_mute);
  }

  private String getPosId() {
    return getIntent().getStringExtra(Constants.POS_ID);
  }

  private int getMaxVideoDuration() {
    return getIntent().getIntExtra(Constants.MAX_VIDEO_DURATION, 0);
  }

  @Override
  public void onADLoaded(List<NativeUnifiedADData> ads) {
    if (ads != null && ads.size() > 0) {
      initAd(ads.get(0));
    }
  }

  private void initAd(NativeUnifiedADData ad) {
    mAdData = ad;
    if(mAdData.getAdPatternType() == AdPatternType.NATIVE_VIDEO){
      showVideoADCover();
    }else{
      showNoneVideoAD();
    }
    Log.d(TAG, String.format(Locale.getDefault(), "(pic_width,pic_height) = (%d , %d)",
        mAdData.getPictureWidth(), mAdData.getPictureHeight()));
    Log.d(TAG, "eCPM = " + mAdData.getECPM() + " , eCPMLevel = " + mAdData.getECPMLevel());
  }

  private void showVideoADCover(){
    mVideoADShowing = false;
    setContainersVisbility(false);

    mAQuery.id(R.id.video_cover).image(mAdData.getImgUrl(), false, true, 0, 0,
        new BitmapAjaxCallback() {
          @Override
          protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
            if (iv.getVisibility() == View.VISIBLE) {
              iv.setImageBitmap(bm);
              // 如果视频封面图所在容器是开发者自己提供的，必需调用onVideoADExposured进行封面图曝光，且曝光时的容器必需可见
              mAdData.onVideoADExposured(iv);
            }
          }
        });

    mVideoCover.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showVideoAD();
      }
    });
  }

  private void bindADContainer(){
    List<View> clickableViews = new ArrayList<>();
    clickableViews.add(mDownloadButton);
    mAdData.bindAdToView(this, mADContainer, null, clickableViews);
    mAdData.setNativeAdEventListener(new NativeADEventListener() {
      @Override
      public void onADExposed() {
        Log.d(TAG, "onADExposed: ");
      }

      @Override
      public void onADClicked() {
        Log.d(TAG, "onADClicked: " + " clickUrl: " + mAdData.ext.get("clickUrl"));
      }

      @Override
      public void onADError(AdError error) {
        Log.d(TAG, "onADError error code :" + error.getErrorCode()
            + "  error msg: " + error.getErrorMsg());
      }

      @Override
      public void onADStatusChanged() {
        Log.d(TAG, "onADStatusChanged: ");
        updateAdAction(mDownloadButton, mAdData);
      }
    });

    updateAdAction(mDownloadButton, mAdData);
  }

  private void setContainersVisbility(boolean showADContainer){
    mVideoCoverContainer.setVisibility(showADContainer ? View.GONE : View.VISIBLE);
    mADContainer.setVisibility(showADContainer ? View.VISIBLE : View.GONE);
  }

  private void showVideoAD(){
    setContainersVisbility(true);
    mVideoADShowing = true;

    // 广告对象只可绑定一次
    if(mVideoADBinded){
      return;
    }
    mVideoADBinded = true;

    bindADContainer();

    mAQuery.id(R.id.img_logo).image(mAdData.getIconUrl(), false, true);
    mAQuery.id(R.id.text_title).text(mAdData.getTitle());
    mAQuery.id(R.id.text_desc).text(mAdData.getDesc());

    mImagePoster.setVisibility(View.GONE);
    mMediaView.setVisibility(View.VISIBLE);

    VideoOption videoOption = getVideoOption(getIntent());

    mAdData.bindMediaView(mMediaView, videoOption, new NativeADMediaListener() {
      @Override
      public void onVideoInit() {
        Log.d(TAG, "onVideoInit: ");
      }

      @Override
      public void onVideoLoading() {
        Log.d(TAG, "onVideoLoading: ");
      }

      @Override
      public void onVideoReady() {
        Log.d(TAG, "onVideoReady: duration:" + mAdData.getVideoDuration());
      }

      @Override
      public void onVideoLoaded(int videoDuration) {
        Log.d(TAG, "onVideoLoaded: ");

      }

      @Override
      public void onVideoStart() {
        Log.d(TAG, "onVideoStart: duration:" + mAdData.getVideoDuration());
      }

      @Override
      public void onVideoPause() {
        Log.d(TAG, "onVideoPause: ");
      }

      @Override
      public void onVideoResume() {
        Log.d(TAG, "onVideoResume: ");
      }

      @Override
      public void onVideoCompleted() {
        Log.d(TAG, "onVideoCompleted: ");
      }

      @Override
      public void onVideoError(AdError error) {
        Log.d(TAG, "onVideoError: ");
      }

      @Override
      public void onVideoStop() {
        Log.d(TAG, "onVideoStop");
      }

      @Override
      public void onVideoClicked() {
        Log.d(TAG, "onVideoClicked");
      }
    });

    mButtonsContainer.setVisibility(View.VISIBLE);

    View.OnClickListener listener = new View.OnClickListener(){
      @Override
      public void onClick(View v) {
        if(v == mPlayButton){
          mAdData.startVideo();
        }else if(v == mPauseButton){
          mAdData.pauseVideo();
        }else if(v == mStopButton){
          mAdData.stopVideo();
        }
      }
    };
    mPlayButton.setOnClickListener(listener);
    mPauseButton.setOnClickListener(listener);
    mStopButton.setOnClickListener(listener);

    mMuteButton.setChecked(mPlayMute);
    mMuteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        mAdData.setVideoMute(isChecked);
      }
    });
  }

  @Nullable
  public static VideoOption getVideoOption(Intent intent) {
    if(intent == null){
      return null;
    }

    VideoOption videoOption = null;
    boolean noneOption = intent.getBooleanExtra(NativeADUnifiedActivity.NONE_OPTION, false);
    if (!noneOption) {
      VideoOption.Builder builder = new VideoOption.Builder();

      builder.setAutoPlayPolicy(intent.getIntExtra(NativeADUnifiedActivity.PLAY_NETWORK, VideoOption.AutoPlayPolicy.ALWAYS));
      builder.setAutoPlayMuted(intent.getBooleanExtra(NativeADUnifiedActivity.PLAY_MUTE, true));
      builder.setNeedCoverImage(intent.getBooleanExtra(NativeADUnifiedActivity.NEED_COVER, true));
      builder.setNeedProgressBar(intent.getBooleanExtra(NativeADUnifiedActivity.NEED_PROGRESS, true));
      builder.setEnableDetailPage(intent.getBooleanExtra(NativeADUnifiedActivity.ENABLE_DETAIL_PAGE, true));
      builder.setEnableUserControl(intent.getBooleanExtra(NativeADUnifiedActivity.ENABLE_USER_CONTROL, false));

      videoOption = builder.build();
    }
    return videoOption;
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (mAdData != null) {
      // 必须要在Actiivty.onResume()时通知到广告数据，以便重置广告恢复状态
      mAdData.resume();
    }
  }

  private void showNoneVideoAD() {
    setContainersVisbility(true);

    bindADContainer();
    int patternType = mAdData.getAdPatternType();
    if (patternType == AdPatternType.NATIVE_2IMAGE_2TEXT) {
      mAQuery.id(R.id.img_logo).image(mAdData.getIconUrl(), false, true);
      mAQuery.id(R.id.img_poster).image(mAdData.getImgUrl(), false, true, 0, 0,
          new BitmapAjaxCallback() {
            @Override
            protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
              if (iv.getVisibility() == View.VISIBLE) {
                iv.setImageBitmap(bm);
              }
            }
          });
      mAQuery.id(R.id.text_title).text(mAdData.getTitle());
      mAQuery.id(R.id.text_desc).text(mAdData.getDesc());
    } else if (patternType == AdPatternType.NATIVE_3IMAGE) {
      mAQuery.id(R.id.img_1).image(mAdData.getImgList().get(0), false, true);
      mAQuery.id(R.id.img_2).image(mAdData.getImgList().get(1), false, true);
      mAQuery.id(R.id.img_3).image(mAdData.getImgList().get(2), false, true);
      mAQuery.id(R.id.native_3img_title).text(mAdData.getTitle());
      mAQuery.id(R.id.native_3img_desc).text(mAdData.getDesc());
    } else if (patternType == AdPatternType.NATIVE_1IMAGE_2TEXT) {
      mAQuery.id(R.id.img_logo).image(mAdData.getImgUrl(), false, true);
      mAQuery.id(R.id.img_poster).clear();
      mAQuery.id(R.id.text_title).text(mAdData.getTitle());
      mAQuery.id(R.id.text_desc).text(mAdData.getDesc());
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (mAdData != null) {
      // 必须要在Actiivty.destroy()时通知到广告数据，以便释放内存
      mAdData.destroy();
    }
  }

  public static void updateAdAction(Button button, NativeUnifiedADData ad) {
    if (!ad.isAppAd()) {
      button.setText("浏览");
      return;
    }
    switch (ad.getAppStatus()) {
      case 0:
        button.setText("下载");
        break;
      case 1:
        button.setText("启动");
        break;
      case 2:
        button.setText("更新");
        break;
      case 4:
        button.setText(ad.getProgress() + "%");
        break;
      case 8:
        button.setText("安装");
        break;
      case 16:
        button.setText("下载失败，重新下载");
        break;
      default:
        button.setText("浏览");
        break;
    }
  }

  @Override
  public void onNoAD(AdError error) {
    Log.d(TAG, "onNoAd error code: " + error.getErrorCode()
        + ", error msg: " + error.getErrorMsg());
  }

  @Override
  public void onBackPressed() {
    if(mVideoADShowing){
      showVideoADCover();
    }else{
      super.onBackPressed();
    }
  }
}
