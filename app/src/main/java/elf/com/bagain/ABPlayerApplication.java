package elf.com.bagain;


import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import elf.com.bagain.utils.FileUtils;


public class ABPlayerApplication extends Application{
	private static ABPlayerApplication mApplication;

	/** OPlayer SD卡缓存路径 */
	public static final String OPLAYER_CACHE_BASE = Environment.getExternalStorageDirectory() + "/oplayer";
	/** 视频截图缓冲路径 */
	public static final String OPLAYER_VIDEO_THUMB = OPLAYER_CACHE_BASE + "/thumb/";
	/** 首次扫描 */
	public static final String PREF_KEY_FIRST = "application_first";
	public static RefWatcher getRefWatcher(Context context) {
		ABPlayerApplication application = (ABPlayerApplication) context.getApplicationContext();
		return application.refWatcher;
	}

	private RefWatcher refWatcher;

	@Override
	public void onCreate() {
		super.onCreate();
		refWatcher = LeakCanary.install(this);
		mApplication = this;
		//Fresco.initialize(this);
		init();
	}

	private void init() {
		//创建缓存目录
		FileUtils.createIfNoExists(OPLAYER_CACHE_BASE);
		FileUtils.createIfNoExists(OPLAYER_VIDEO_THUMB);
	}

	public static ABPlayerApplication getApplication() {
		return mApplication;
	}

	public static Context getContext() {
		return mApplication;
	}

	/** 销毁 */
	public void destory() {
		mApplication = null;
	}

}
