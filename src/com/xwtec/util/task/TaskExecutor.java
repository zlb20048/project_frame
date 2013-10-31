package com.xwtec.util.task;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.util.Log;

/**
 * 任务框架基础类
 * @author shifeng
 *
 */
public class TaskExecutor {
	
	private static final int CORE_POOL_SIZE = 1;
	private static final int MAX_POOL_SIZE = 10;
	private static final int KEEP_ALIVE = 100;
	
	private ThreadPoolExecutor executor;
	
	private BlockingQueue<Runnable> queue = new PriorityBlockingQueue<Runnable>(10);
	
	public TaskExecutor() {
		executor = new ThreadPoolExecutor(CORE_POOL_SIZE,MAX_POOL_SIZE,KEEP_ALIVE,TimeUnit.SECONDS,queue);
	}
	
	public TaskExecutor(int max_pool)
	{
		executor = new ThreadPoolExecutor(4, max_pool, KEEP_ALIVE, TimeUnit.SECONDS,queue);
	}
	
	public void execute(Task<?> task) {
		Log.e("==TaskExecutor==", "execute");
		try
		{
			executor.execute(task);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void shutsown() {
		executor.shutdown();
	}
	
	public void abortAll(Context context)
	{
		Iterator<Runnable>it = queue.iterator();
		while(it.hasNext())
		{
			Runnable run = it.next();
			if(run instanceof Task<?>)
			{
				Task<?> task = (Task<?>)run;
				if(context != null && context.equals(task.getContext()))
				{
					task.cancel();
					queue.remove(run);
				}
				else if(context == null)
				{
					task.cancel();
					queue.remove(run);
				}
			}
		}
	}
	

}
