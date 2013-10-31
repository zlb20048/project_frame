package com.xwtec.util.task;

/**
 * 任务框架基础接口
 * @author shifeng
 *
 */
public interface TaskListener {
	
   public void taskStarted(Task<?> task);
   
   public void taskProgress(Task<?> task,long value,long max);
   
   public void taskCompleted(Task<?> task,Object obj) throws InterruptedException;
   
   public void taskFailed(Task<?> task,Throwable ex);
	
}
