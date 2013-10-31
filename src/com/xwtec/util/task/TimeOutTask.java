package com.xwtec.util.task;

import java.util.TimerTask;

import android.util.Log;

public class TimeOutTask extends TimerTask {
	
	private Task<?>task;
	
	public TimeOutTask(Task<?>task)
	{
		this.task = task;
	}

	@Override
	public void run() {
		Log.i("==TimeOutTask==", "run");
		task.timeout();
	}

}
