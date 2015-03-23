package com.zdy.statistics.main;

import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TimerRun {

	public void runTaskByDay(final Map<String,String> analysisClassInfo){
		
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)+1);
		calendar.set(Calendar.HOUR_OF_DAY, 2);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		System.out.println(calendar.getTime());
		
		ScheduledExecutorService scheduledService = Executors.newScheduledThreadPool(20);
		
		scheduledService.scheduleAtFixedRate(new Runnable() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void run() {
				try {
//					Class clazz = Class.forName(className);
//					Method method = clazz.getMethod(methodName, clazzsArgesType);
//					method.invoke(clazz.newInstance(), arges);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, calendar.getTimeInMillis() - new Date().getTime(), (long) (1000*60*60*24), TimeUnit.MILLISECONDS);
		
	}
	
	public void runTaskByHour(){
		
	}
	
	public void runTaskByMinute(){
		
	}
	
	
	public void startTimer(){
		/*
		 * 1.启动隔天查询定时器
		 */
		
		
		/*
		 * 2.启动一小时定时器
		 */
		
		/*
		 * 3.启动每五分钟定时器
		 */
	}
	
	public static void main(String[] args) {
		TimerRun timerRun = new TimerRun();
//		timerRun.runTaskByDay();
		
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)+1);
		calendar.set(Calendar.HOUR_OF_DAY, 2);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
//		System.out.println(calendar.get(Calendar.DAY_OF_MONTH));
		System.out.println(calendar.getTime());
	}
}
