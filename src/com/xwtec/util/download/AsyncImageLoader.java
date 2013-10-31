package com.xwtec.util.download;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class AsyncImageLoader {

	private final static String TAG = AsyncImageLoader.class.getSimpleName();

	private HashMap<String, SoftReference<Drawable>> imageCache;

	public AsyncImageLoader() {
		imageCache = new HashMap<String, SoftReference<Drawable>>();
	}

	public Drawable loadDrawable(final String imageUrl,
			final ImageCallback imageCallback) {
		if (imageCache.containsKey(imageUrl)) {
			SoftReference<Drawable> softReference = imageCache.get(imageUrl);
			Drawable drawable = softReference.get();
			if (drawable != null) {
				return drawable;
			}
		}
		final Handler handler = new Handler() {
			public void handleMessage(Message message) {
				imageCallback.imageLoaded((byte[]) message.obj, imageUrl);
			}
		};
		new Thread() {
			@Override
			public void run() {
				byte[] drawable = loadImageFromUrl(imageUrl);
				if (null != drawable && drawable.length > 0) {
					Bitmap bitmap = BitmapFactory.decodeByteArray(drawable, 0,
							drawable.length);
					BitmapDrawable d = new BitmapDrawable(bitmap);
					imageCache.put(imageUrl, new SoftReference<Drawable>(d));
					Message message = handler.obtainMessage(0, drawable);
					handler.sendMessage(message);
				}
			}
		}.start();
		return null;
	}

	public static byte[] loadImageFromUrl(String url) {
		URL m;
		InputStream i = null;
		byte[] data = null;
		try {
			m = new URL(url);
			i = (InputStream) m.getContent();
			ByteArrayOutputStream bos = null;

			bos = new ByteArrayOutputStream();
			int ch = 0;
			byte[] d = new byte[1024 * 2];
			while ((ch = i.read(d)) != -1) {
				bos.write(d, 0, ch);
				data = bos.toByteArray();
			}
		} catch (MalformedURLException e1) {
			Log.e(TAG, "MalformedURLException = " + e1.getMessage());
		} catch (IOException e) {
			Log.e(TAG, "IOException = " + e.getMessage());
		}
		return data;
	}

	public interface ImageCallback {
		public void imageLoaded(byte[] imageDrawable, String imageUrl);
	}
}