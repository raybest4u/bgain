/*
 * Copyright (C) 2013 yixia.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package elf.com.bagain;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import elf.com.bagain.utils.DeviceUtils;
import elf.com.bagain.utils.HttpUtil;
import elf.com.bagain.utils.Logger;
import elf.com.bagain.utils.XLog;
import elf.com.bagain.view.PlayerService;
import io.vov.vitamio.LibsChecker;
import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.MediaPlayer.OnBufferingUpdateListener;
import io.vov.vitamio.MediaPlayer.OnInfoListener;
import io.vov.vitamio.utils.Log;
import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

public class VideoViewBuffer extends Activity implements OnInfoListener, OnBufferingUpdateListener {

  /**
   * TODO: Set the path variable to a streaming video URL or a local media file
   * path.
   */
  private String path = "http://cn-gdfs11-dx.acgvideo.com/vg3/c/96/5296428.mp4?expires=1449744300&ssig=azFRk1yIsCvVnGynZY0qFw&oi=1947629244&internal=1&rate=0";
  private Uri uri;
  private VideoView mVideoView;
  private ProgressBar pb;
  private TextView downloadRateView, loadRateView;

  @Override
  public void onCreate(Bundle icicle) {
    super.onCreate(icicle);
    if (!LibsChecker.checkVitamioLibs(this))
      return;

    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
    setContentView(R.layout.videobuffer);
    mVideoView = (VideoView) findViewById(R.id.buffer);
    pb = (ProgressBar) findViewById(R.id.probar);

    downloadRateView = (TextView) findViewById(R.id.download_rate);
    loadRateView = (TextView) findViewById(R.id.load_rate);

    parseIntent(getIntent());
    new VideoViewInitTask().execute();
   /* if (path == "") {
      // Tell the user to provide a media file URL/path.
      Toast.makeText(
          VideoViewBuffer.this,
          "Please edit VideoBuffer Activity, and set path"
              + " variable to your media file URL/path", Toast.LENGTH_LONG).show();
      return;
    } else {
      *//*
       * Alternatively,for streaming media you can use
       * mVideoView.setVideoURI(Uri.parse(URLstring));
       *//*
      uri = Uri.parse(path);
      mVideoView.setVideoURI(uri);
      mVideoView.setMediaController(new MediaController(this));
      mVideoView.requestFocus();
      mVideoView.setOnInfoListener(this);
      mVideoView.setOnBufferingUpdateListener(this);
      mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
          // optional need Vitamio 4.0
          mediaPlayer.setPlaybackSpeed(1.0f);
        }
      });
    }*/

  }

  @Override
  public boolean onInfo(MediaPlayer mp, int what, int extra) {
    switch (what) {
    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
      if (mVideoView.isPlaying()) {
        mVideoView.pause();
        pb.setVisibility(View.VISIBLE);
        downloadRateView.setText("");
        loadRateView.setText("");
        downloadRateView.setVisibility(View.VISIBLE);
        loadRateView.setVisibility(View.VISIBLE);

      }
      break;
    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
      mVideoView.start();
      pb.setVisibility(View.GONE);
      downloadRateView.setVisibility(View.GONE);
      loadRateView.setVisibility(View.GONE);
      break;
    case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
      downloadRateView.setText("" + extra + "kb/s" + "  ");
      break;
    }
    return true;
  }

  @Override
  public void onBufferingUpdate(MediaPlayer mp, int percent) {
    loadRateView.setText(percent + "%");
  }
  @Override
  protected void onDestroy() {
    mVideoView.stopPlayback();
    /*if (DeviceUtils.hasICS()) {
      android.os.Process.killProcess(android.os.Process.myPid());
    }*/
    super.onDestroy();
  }
  private String danmakuPath;
  private String av;
  private String page;
  private boolean mNeedLock;
  private String mDisplayName;
  private boolean mFromStart;
  private boolean mSaveUri;
  private float mStartPos;
  private int mLoopCount;
  private int mParentId;
  private String mSubPath;
  private boolean mSubShown;
  private boolean mIsHWCodec;
  private void parseIntent(Intent i) {
    av = i.getStringExtra("av");
    page = i.getStringExtra("page");
    Logger.d("----->" + av + "/" + page);

    mNeedLock = i.getBooleanExtra("lockScreen", false);
    mDisplayName = i.getStringExtra("displayName");
    mFromStart = i.getBooleanExtra("fromStart", false);
    mSaveUri = i.getBooleanExtra("saveUri", true);
    mStartPos = i.getFloatExtra("startPosition", -1.0f);
    mLoopCount = i.getIntExtra("loopCount", 1);
    mParentId = i.getIntExtra("parentId", 0);
    mSubPath = i.getStringExtra("subPath");
    mSubShown = i.getBooleanExtra("subShown", true);
    mIsHWCodec = i.getBooleanExtra("hwCodec", false);
    Log.i("L: %b, N: %s, S: %b, P: %f, LP: %d", mNeedLock, mDisplayName,
            mFromStart, mStartPos, mLoopCount);
  }
  private class VideoViewInitTask extends AsyncTask<String, Void, Integer> {

    @Override
    protected Integer doInBackground(String... arg0) {
      // TODO Auto-generated method stub
      Log.d("TAG", "开始解析视频地址");
      try {
        String jsonStr = HttpUtil.getHtmlString("http://www.bilibili.com/m/html5?aid=" + av + "&page=" + page);

        XLog.d(jsonStr);
        JSONObject videopathjson = new JSONObject(jsonStr);
        Log.d("QAQ--->","===>"+videopathjson.getString("src").toString());
        danmakuPath = videopathjson.getString("cid").toString();
        uri = Uri.parse(videopathjson.getString("src").toString());
      } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      Log.d("TAG", "开始加载弹幕");
      //mParser = createParser(danmakuPath);
      return null;
    }

    @Override
    protected void onPostExecute(Integer result) {
      // TODO Auto-generated method stub
      super.onPostExecute(result);

      Log.d("TAG", "加载弹幕成功");
      mVideoView.setVideoURI(uri);
      mVideoView.setMediaController(new MediaController(VideoViewBuffer.this));
      mVideoView.requestFocus();
      mVideoView.setOnInfoListener(VideoViewBuffer.this);
      mVideoView.setOnBufferingUpdateListener(VideoViewBuffer.this);
      mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
          // optional need Vitamio 4.0
          mediaPlayer.setPlaybackSpeed(1.0f);
        }
      });
    }
  }
}
