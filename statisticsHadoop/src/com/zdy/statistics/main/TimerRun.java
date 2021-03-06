package com.zdy.statistics.main;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.zdy.statistics.analysis.common.MysqlConnect;

public class TimerRun {

	private Logger logger = Logger.getLogger(TimerRun.class);
	
	//线程池
	ScheduledExecutorService scheduledService = Executors.newScheduledThreadPool(30);
	//定时调度
	public void runScheduled(long initialDelay,long period,TimeUnit timeUnit,final Map<String,String[]> classMap,final Map<String,Object[]> argesMap){

		for(Entry<String, String[]> entry : classMap.entrySet()){
			final String className = entry.getKey();//class name
			final String[] methodNames = entry.getValue();//called method name;
			Arrays.sort(methodNames);
			
			scheduledService.scheduleAtFixedRate(new Runnable() {
				
				@SuppressWarnings("rawtypes")
				@Override
				public void run() {
					try {
						//得到任务类
						Class clazz = Class.forName(className);
						Method[] methods = clazz.getMethods();
						//判断调用的方法
						
						for (Method method : methods) {
							
							if(Arrays.binarySearch(methodNames, method.getName()) >= 0){
								Object[] arges = argesMap.get(className);
								if(arges == null){
									arges = new Object[0];
								}
								Object clas = clazz.newInstance();
								method.invoke(clas,arges);
								
								logger.info(className+" -- "+method.getName()+" : 执行");
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, initialDelay, period, timeUnit);
			
		}
	}
	
	public void startTimer(){
		//------ 1.启动隔天查询定时器 -------------------------------------------------------------------------------------//
		Map<String,String[]> analysisClasses = new HashMap<String, String[]>();
		analysisClasses.put("com.zdy.statistics.analysis.register.Register", new String[]{"insertResult"});//
		analysisClasses.put("com.zdy.statistics.analysis.firstConsume.FirstConsume", new String[]{"insertResult"});//
		analysisClasses.put("com.zdy.statistics.analysis.gameJoin.GameJoin", new String[]{"insertResult"});//
		analysisClasses.put("com.zdy.statistics.analysis.huanle.AnalysisHuanLe", new String[]{"insertResult"});//
		analysisClasses.put("com.zdy.statistics.analysis.level.Level", new String[]{"insertResult"});//
		analysisClasses.put("com.zdy.statistics.analysis.login.Login", new String[]{"insertResult"});//
		analysisClasses.put("com.zdy.statistics.analysis.recharge.AnalysisRecharge", new String[]{"insertResult"});//
		analysisClasses.put("com.zdy.statistics.analysis.shop.AnalysisShop", new String[]{"execShopAnalysis"});//
		
		String[] userInfoMethods = {"userBasicInfo","analysisLevel","analysisHuanle","ananlysisRecharge","analysisGameJoin"};
		analysisClasses.put("com.zdy.statistics.analysis.userInfo.UserInfo", userInfoMethods);
		
		analysisClasses.put("com.zdy.statistics.analysis.remainder.Remainder", new String[]{"insertResult"});//
		
		Map<String,Object[]> argesMap = new HashMap<String, Object[]>();
		argesMap.put("com.zdy.statistics.analysis.register.Register", null);
		argesMap.put("com.zdy.statistics.analysis.firstConsume.FirstConsume", null);
		argesMap.put("com.zdy.com.zdy.statistics.analysis.gameJoin.GameJoin", null);
		argesMap.put("com.zdy.statistics.analysis.huanle.AnalysisHuanLe", null);
		argesMap.put("com.zdy.statistics.analysis.level.Level", null);
		argesMap.put("com.zdy.statistics.analysis.login.Login", null);
		argesMap.put("com.zdy.statistics.analysis.recharge.AnalysisRecharge", null);
		argesMap.put("com.zdy.statistics.analysis.shop.AnalysisShop", null);
		argesMap.put("com.zdy.statistics.analysis.userInfo.UserInfo", null);
		argesMap.put("com.zdy.statistics.analysis.remainder.Remainder", null);
		
		Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)+1);
		calendar.set(Calendar.HOUR_OF_DAY, 2);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		
		long initialDelay = calendar.getTimeInMillis() - new Date().getTime();
		long period = 1000*60*60*24;
		TimeUnit timeUnit = TimeUnit.MILLISECONDS;
		
		runScheduled(initialDelay, period, timeUnit, analysisClasses, argesMap);
		logger.info("启动隔天查询定时器");
		
		
		//------2.启动一小时定时器-------------------------------------------------------------------------------------//
		Map<String,String[]> analysisClassesHour = new HashMap<String, String[]>();
		analysisClassesHour.put("com.zdy.statistics.analysis.register.RegisterSummit", new String[]{"insertResult"});
		
		Map<String,Object[]> argesMapHour = new HashMap<String, Object[]>();
		argesMapHour.put("com.zdy.statistics.analysis.register.RegisterSummit", null);
		
		Calendar calendarHour = Calendar.getInstance(TimeZone.getDefault());
		calendarHour.set(Calendar.HOUR_OF_DAY, calendarHour.get(Calendar.HOUR_OF_DAY)+1);
		calendarHour.set(Calendar.MINUTE, 0);
		calendarHour.set(Calendar.SECOND, 0);
		
		long initialDelayHour = calendarHour.getTimeInMillis() - new Date().getTime();
		long periodHour = 1000*60*60;
		TimeUnit timeUnitHour = TimeUnit.MILLISECONDS;
		
		runScheduled(initialDelayHour, periodHour, timeUnitHour, analysisClassesHour, argesMapHour);
		logger.info("启动一小时定时器");
		
		//------ 4.启动每十五分钟定时器 -------------------------------------------------------------------------------------//
		Map<String,String[]> analysisClassesFifteen = new HashMap<String, String[]>();
		analysisClassesFifteen.put("com.zdy.statistics.analysis.login.LoginSummit", new String[]{"insertResult"});
		
		Map<String,Object[]> argesMapFifteen = new HashMap<String, Object[]>();
		argesMapFifteen.put("com.zdy.statistics.analysis.login.LoginSummit", null);
		
		Calendar calendarFifteen = Calendar.getInstance(TimeZone.getDefault());
		calendarFifteen.set(Calendar.MINUTE, 15*(calendarFifteen.get(Calendar.MINUTE)/15+1));
		calendarFifteen.set(Calendar.SECOND, 0);
		
		long initialDelayFifteen = calendarFifteen.getTimeInMillis() - new Date().getTime();
		long periodFifteen = 1000*60*15;
		TimeUnit timeUnitFifteen = TimeUnit.MILLISECONDS;
		
		runScheduled(initialDelayFifteen, periodFifteen, timeUnitFifteen, analysisClassesFifteen, argesMapFifteen);
		logger.info("启动每十五分钟定时器");
			
		//------ 5.启动每五分钟定时 -------------------------------------------------------------------------------------//
		Map<String,String[]> analysisClassesFive = new HashMap<String, String[]>();
		analysisClassesFive.put("com.zdy.statistics.analysis.online.OnlineAnalysis", new String[]{"analysisOnlineCount"});
		
		Map<String,Object[]> argesMapFive = new HashMap<String, Object[]>();
		argesMapFive.put("com.zdy.statistics.analysis.online.OnlineAnalysis", null);
		
		Calendar calendarFive = Calendar.getInstance(TimeZone.getDefault());
		calendarFive.set(Calendar.MINUTE, 5*((calendarFive.get(Calendar.MINUTE)/5+1)));
		calendarFive.set(Calendar.SECOND, 0);
		
		long initialDelayFive = calendarFive.getTimeInMillis() - new Date().getTime();
		long periodFiven = 1000*60*5;
		TimeUnit timeUnitFive = TimeUnit.MILLISECONDS;
		
		runScheduled(initialDelayFive, periodFiven, timeUnitFive, analysisClassesFive, argesMapFive);
		logger.info("启动每五分钟定时");
		
		//------ 6.每隔五秒总 -------------------------------------------------------------------------------------//
		Map<String,String[]> analysisClassesFiveSecond = new HashMap<String, String[]>();
		analysisClassesFiveSecond.put("com.zdy.statistics.analysis.online.OnlineAnalysis", new String[]{"analysis"});
		analysisClassesFiveSecond.put("com.zdy.statistics.analysis.register.RegisterSummit", new String[]{"insertUserInfo"});
		
		Map<String,Object[]> argesMapFiveSecond = new HashMap<String, Object[]>();
		argesMapFiveSecond.put("com.zdy.statistics.analysis.online.OnlineAnalysis", null);
		argesMapFiveSecond.put("com.zdy.statistics.analysis.register.RegisterSummit", null);
		
		Calendar calendarFiveSecond = Calendar.getInstance(TimeZone.getDefault());
		calendarFiveSecond.set(Calendar.SECOND, 5*(calendarFiveSecond.get(Calendar.SECOND)/5+1));
		
		long initialDelayFiveSecond = calendarFiveSecond.getTimeInMillis() - new Date().getTime();
		long periodFiveSecond = 1000*5;
		TimeUnit timeUnitFiveSecond = TimeUnit.MILLISECONDS;
		
		runScheduled(initialDelayFiveSecond, periodFiveSecond, timeUnitFiveSecond, analysisClassesFiveSecond, argesMapFiveSecond);
		logger.info("启动每五秒钟定时");
		
		//------ 7.在线用户监测线程 --------------------------------------------------------------------------------//
		Map<String,String[]> onlineMoniter = new HashMap<String, String[]>();
		onlineMoniter.put("com.zdy.statistics.analysis.online.OnlineMoniter", new String[]{"moniterOnlineMap"});
		
		Map<String,Object[]> argesOnlineMoniter = new HashMap<String, Object[]>();
		argesOnlineMoniter.put("com.zdy.statistics.analysis.online.OnlineMoniter", null);
		
		long initialDelayMoniter = 1000*60*30;
		long periodMoniter = 1000*60*30;
		TimeUnit timeUnitMoniter = TimeUnit.MILLISECONDS;
		
		runScheduled(initialDelayMoniter, periodMoniter, timeUnitMoniter, onlineMoniter, argesOnlineMoniter);
		logger.info("启动六小时在线监测定时器");
	}
	
	public static void main(String[] args) {
		TimerRun timerRun = new TimerRun();
		timerRun.startTimer();
	}
}
