package com.xwtec.util.task;

import java.util.TimerTask;
import java.util.concurrent.Callable;

import android.content.Context;
import android.util.Log;

/**
 * 任务框架基础类
 * 
 * @author shifeng
 * 
 * @param <V>
 */
public abstract class Task<V> implements Callable<V>, Runnable {

	final String TAG = "==Task==";

	protected TaskListener listener;

	/**
	 * 任务是否被取消的标记位
	 */
	private boolean aborted = false;

	/**
	 * 是否超时
	 */
	private boolean timeout = false;

	private Context context = null;

	public TimerTask timeoutTask = null;

	public Task(Context context, TaskListener listener) {
		this.listener = listener;
		this.context = context;
	}

	public abstract V process() throws Exception;

	public void run() {
		try {
			startTimeoutTask();
			call();
		} catch (Exception e) {
			timeout();
			Log.e(TAG, "run().e=" + e.getMessage());
		} finally {
			stopTimeOutTask();
		}
	}

	public V call() throws Exception {
		V obj = null;
		try {
			listener.taskStarted(this);
			if (aborted || timeout) {
				throw new InterruptedException("cancel");
			}
			obj = process();
		} catch (Throwable ex) {
			Log.e(TAG, "call()" + ex.getMessage());
			if (listener != null) {
				listener.taskFailed(this, ex);
			}
			timeout();
			return null;
		}
		if (aborted || timeout) {
			throw new InterruptedException("cancel");
		}
		if (listener != null) {
			listener.taskCompleted(this, obj);
		}
		return obj;
	}

	/**
	 * 任务的cancel 操作
	 */
	public void cancel() {
		Log.i(TAG, "canel()");
		aborted = true;
		// 取消操作后需要停止超时定时器
		stopTimeOutTask();
	}

	/**
	 * @return 任务是否被取消
	 */
	public boolean isAborted() {
		return aborted;
	}

	/**
	 * @return 任务是否已经超时
	 */
	public boolean isTimeout() {
		return timeout;
	}

	public Context getContext() {
		return context;
	}

	/**
	 * 设置超时定时器
	 * 
	 * @param timeout
	 */
	public void setTimeOutTask(TimeOutTask timeout) {
		this.timeoutTask = timeout;
	}

	/**
	 * 启动超时定时器
	 */
	private void startTimeoutTask() {
		// if(null != timeoutTask)
		// {
		// Variable.Session.timer.schedule(timeoutTask, Variable.timeout);
		// }
	}

	/**
	 * 任务超时处理
	 */
	public void timeout() {
		Log.i(TAG, "timeout()");
		timeout = true;
	}

	/**
	 * 停止超时定时器
	 */
	private void stopTimeOutTask() {
		if (null != timeoutTask) {
			timeoutTask.cancel();
			timeoutTask = null;
		}
	}
}
