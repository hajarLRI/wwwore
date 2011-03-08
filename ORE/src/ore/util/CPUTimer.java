package ore.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;

public class CPUTimer extends Thread {

	private long sleepTime = 0;
	private boolean stop = false;
	private int events = 0;
	private int total = 0;
	private int overallTotal = 0;
	private int overallEvents = 0;

	public CPUTimer(long sleepTime) {
		this.sleepTime = sleepTime;
	}

	public void stopTimer() {
		this.stop = true;
	}

	public synchronized int getTotal() {
		if(events == 0) {
			return 0;
		}
		int result = overallTotal/overallEvents;
		return result;
	}
	
	public synchronized int getRecent() {
		if(events == 0) {
			return 0;
		}
		int result = total/events;
		events = 0;
		total = 0;
		return result;
	}

	@Override
	public void run() {
		try {
			while(!stop) {
				Thread.sleep(sleepTime);
				synchronized(this) {
					total += getReading();
					overallTotal += total;
					events++;
					overallEvents++;
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private int getReading() throws IOException {
		Process proc = Runtime.getRuntime().exec("wmic /node:localhost path Win32_Processor where DeviceID='CPU0' get LoadPercentage");
		final InputStream input = proc.getInputStream();
		final InputStream error = proc.getErrorStream();
		new StreamCleaner(error).start();
		BufferedReader br = new BufferedReader(new InputStreamReader(input));
		br.readLine();
		br.readLine();
		String line = br.readLine().trim();
		int measure = 0;
		if((line != null) && (!line.equals(""))) {
			measure = Integer.parseInt(line);
		}
		return measure;
	}

	public static void main(String[] args) throws Exception {
		CPUTimer t = new CPUTimer(100);
		t.start();
		while(true) {
			System.out.println("Overall: " + t.getTotal() + ", Recent: " + t.getRecent());
			Thread.sleep(1000);
		}
	}
}
