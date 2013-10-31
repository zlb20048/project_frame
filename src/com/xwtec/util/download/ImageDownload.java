package com.xwtec.util.download;

import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.xwtec.util.task.Task;
import com.xwtec.util.task.TaskExecutor;
import com.xwtec.util.task.TaskListener;
import com.xwtec.util.tool.Constant;

/**
 * 
 * @author shifeng
 * 
 */
public class ImageDownload implements TaskListener {

	private final static String TAG = ImageDownload.class.getSimpleName();

	private Context context;

	public ImageDownload(Context context) {
		this.context = context;
	}

	// protected ImageDownload() {
	// };

	public void getImg(String imgUrl, String name, Handler handler) {
		TaskExecutor executor = new TaskExecutor();
		ImageObj obj = new ImageObj();
		obj.setUrl(imgUrl);
		obj.setName(name);
		obj.setHandler(handler);
		// Log.e("===obj.getImg()====", img.getImg()+"");
		Task<?> task = new ImageTask(context, this, obj);
		executor.execute(task);
	}

	public void taskStarted(Task<?> task) {
		// Log.d(Constant.LOG_KEY+this.toString(), "ImageDownload taskStarted");
	}

	public void taskProgress(Task<?> task, long value, long max) {
		// Log.d(Constant.LOG_KEY+this.toString(),
		// "ImageDownload taskProgress");
	}

	public void taskCompleted(Task<?> task, Object obj) {
		Log.d(TAG, "ImageDownload taskCompleted");
		if (obj != null) {
			ImageObj img = (ImageObj) obj;
			byte[] data = img.getImg();
			if (data != null) {
				String name = img.getName();
				if (name != null) {
					try {
						saveToDefault(data, name);
					} catch (IOException e) {
						e.printStackTrace();
					}
					// 通知页面刷新图片
					Message msg = new Message();
					msg.what = Constant.DOWNLOAD_IMAGE_FINISH;
					msg.obj = img;
					Handler handler = img.getHandler();
					// 当Handler为空的时候不需要返回更新界面
					if (null != handler) {
						handler.sendMessage(msg);
					}
				}
			}
		}

	}

	/*
	 * 写入文件到默认路径
	 */
	public void saveToDefault(byte[] buf, String imgName) throws IOException {
		FileOutputStream fos = context.openFileOutput(imgName,
				Context.MODE_PRIVATE);
		fos.write(buf);
		fos.flush();
		fos.close();
	}

	public void taskFailed(Task<?> task, Throwable ex) {
		// Log.d(Constant.LOG_KEY+this.toString(), "ImageDownload taskFailed");
	}

}
