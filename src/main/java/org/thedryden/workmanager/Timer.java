package org.thedryden.workmanager;

import java.text.NumberFormat;

/***
 * Used to time how long an operation takes at a nano second level. By default returned values are assumed to be a millisecond scale.
 * @author Matthew Dryden
 *
 */
public class Timer {
	public static final long NANO_IN_MILLISECONDS = 1_000_000;
	public static final long MILLISECONDS_IN_HOUR_ = 3_600L * 1_000L;
	public static final long MILLISECONDS_IN_MINUTES = 60L * 1_000L;
	
	private Long start = -1L;
	private Long end = -1L;
	
	/***
	 * Creates but does NOT start a new timer. You must call start on a timer before you can get a duration or stop a timer.
	 */
	public Timer() {}
	/***
	 * Starts this timer. Calling start a second time will reset the timer with no error. This returns itself so if you want to start the timer immediately you can call this right after new: Timer timer = new Timer().start();
	 * @return this - so it can be called right after new.
	 */
	public Timer start() {
		start = System.nanoTime();
		return this;
	}
	/***
	 * Stops the timer. This returns itself, so if you want you can immediately get the duration: Long duration = timer.stop().getDuration();
	 * @return this - for method chaining.
	 */
	public Timer stop() {
		end = System.nanoTime();
		return this;
	}
	/***
	 * This returns the number of elapsed nanoseconds. If timer is stooped this will return nanoseconds between when start and stop was called. If timer is not stopped this will return the nanoseconds between start and this function being called.
	 * @return The number of elapsed nanoseconds.
	 * @throws IllegalArgumentException will be thrown if start has not been called yet
	 */
	public Long getNanoDuration() throws IllegalArgumentException{
		if(start == -1)
			throw new IllegalArgumentException("You must call start before you can get a duration.");
		long duration = 0L;
		if(end == -1)
			duration = System.nanoTime() - start;
		else
			duration = end - start;
		return duration;
	}
	/***
	 * This returns the number of elapsed milliseconds. If timer is stooped this will return milliseconds between when start and stop was called. If timer is not stopped this will return the milliseconds between start and this function being called.
	 * @return The number of elapsed milliseconds.
	 * @throws IllegalArgumentException will be thrown if start has not been called yet
	 */	
	public Long getDuration() throws IllegalArgumentException{
		return getNanoDuration() / NANO_IN_MILLISECONDS;
	}
	/***
	 * This returns the number of elapsed seconds. If timer is stooped this will return seconds between when start and stop was called. If timer is not stopped this will return the seconds between start and this function being called.
	 * @return The number of elapsed seconds.
	 * @throws IllegalArgumentException will be thrown if start has not been called yet
	 */		
	public Long getSecondDuration() throws IllegalArgumentException{
		if(start == -1)
			throw new IllegalArgumentException("You must call start before you can get a duration.");
		long duration = 0;
		if(end == -1)
			duration = System.nanoTime() - start;
		else
			duration = end - start;
		return duration / NANO_IN_MILLISECONDS / 1_000;
	}

	@Override
	/***
	 * This will return the number milliseconds elapsed followed by the unit (milliseconds). For instance: 11652000 milliseconds.
	 */
	public String toString() throws IllegalArgumentException{
		if(start == -1)
			return "Timer has not been started yet";
		return new StringBuilder(NumberFormat.getInstance().format(getDuration()))
				.append(" milliseconds")
				.toString();
	}
	/***
	 * This will return the hours, minutes, seconds and milliseconds elapsed in this format: HH:mm:ss (SS milliseconds) such as: 03:14:12 (11652000 milliseconds)
	 * @return the hours, minutes, seconds and milliseconds elapsed in this format: HH:mm:ss (SS milliseconds) such as: 03:14:12 (11652000 milliseconds)
	 */
	public String toFancyString() {
		if(start == -1)
			return "Timer has not been started yet";
		long duration = getDuration();
		StringBuilder output = new StringBuilder();
		long temp = duration / MILLISECONDS_IN_HOUR_;
		if (temp < 10L)
			output.append("0");
		output.append(NumberFormat.getInstance().format(temp)).append(":");
		
		temp = (duration % MILLISECONDS_IN_HOUR_) / MILLISECONDS_IN_MINUTES;
		if (temp < 10L)
			output.append("0");
		output.append(temp).append(":");
		
		temp = ( duration % MILLISECONDS_IN_MINUTES ) / 1_000L;
		if (temp < 10L)
			output.append("0");
		output.append(temp)
			.append(" (").append(toString()).append(")");
		
		
		return output.toString();
	}
}
