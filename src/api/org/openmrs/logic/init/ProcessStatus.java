package org.openmrs.logic.init;

/**
 * Interface of maintain basic process status.
 *
 */
public interface ProcessStatus {

	public static final int STATUS_OFF = 0; // This means the process is not running.
	public static final int STATUS_ON = 1; // This means the process is running.

	public abstract void setStatus(int status);

	public abstract int getStatus();

	public abstract long getTimeElapsed();

	public abstract void setTimeElapsed(long timeElapsed);

	public abstract boolean isRunning();

}