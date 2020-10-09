package com.fajar.livestreaming.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SchedulerUtil {
	private static final Map<String , SchedulerUtil.SchedulerCallback> SCHEDULER_CALLBACKS = new HashMap<>();
	
	public static void registerScheduler(SchedulerCallback schedulerCallback ) {
		
		if(getScheduler(schedulerCallback.getId()) != null) {
			getScheduler(schedulerCallback.getId()).stop();
			removeScheduler(schedulerCallback.getId());
		}
		
		ThreadUtil.run(new Runnable() {
			int counterTime = 0;
			int maxTime = schedulerCallback.getMaxTime();
			long delta = 1000; //1s
			
			@Override
			public void run() {
				
				log.info("Start Scheduler, max: {}", maxTime);
				
				long timeMillis = System.currentTimeMillis();
				
				while(counterTime < maxTime && schedulerCallback.isRunning()) {
					long currentTime = new Date().getTime();
					if(currentTime - timeMillis >= delta) {
						timeMillis = currentTime;
						counterTime++;
						
						schedulerCallback.action(counterTime);
					} 
				}
				
				log.info("END Scheduler");
				schedulerCallback.end(getStoppedRecordingCause(), counterTime);
				
				removeScheduler(schedulerCallback.getId());
			}
			
			private String getStoppedRecordingCause() {
				if(counterTime < maxTime) {
					return "Stopped by user";
				}
				return "Recording time exceeds max time: "+maxTime+" seconds";
			}
		});
		
		SCHEDULER_CALLBACKS.put(schedulerCallback.getId(), schedulerCallback);
		
	}
	
	public static interface SchedulerCallback {
		public void action(int counter);
		public int getMaxTime();
		public String getId();
		public void stop();
		public boolean isRunning();
		
		default void end(String cause, int counter) { log.info("Recording ended, cause: {}", cause); }
	}

	public static SchedulerCallback getScheduler(String schedulerId) {
		 
		return SCHEDULER_CALLBACKS.get(schedulerId);
	}

	public static void removeScheduler(String id) {
		try {
			SCHEDULER_CALLBACKS.remove(id);
		}catch (Exception e) {
			 
		}
	}

}
