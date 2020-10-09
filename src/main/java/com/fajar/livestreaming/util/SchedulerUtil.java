package com.fajar.livestreaming.util;

import java.util.Date;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SchedulerUtil {
	
	public static void registerScheduler(SchedulerCallback schedulerCallback ) {
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
				schedulerCallback.end(getStoppedRecordingCause());
			}
			
			private String getStoppedRecordingCause() {
				if(counterTime < maxTime) {
					return "Stopped by user";
				}
				return "Recording time exceeds max time: "+maxTime+" seconds";
			}
		});
	}
	
	public static interface SchedulerCallback {
		public void action(int counter);
		public int getMaxTime();
		public String getId();
		public void stop();
		public boolean isRunning();
		
		default void end(String cause) { log.info("Recording ended"); }
	}

}
