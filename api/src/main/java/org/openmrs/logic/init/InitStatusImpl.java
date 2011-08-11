package org.openmrs.logic.init;

/**
 * This class is used to hold default logic rule registration process status. 
 *
 */
public class InitStatusImpl implements ProcessStatus {

	private int status;
	private long timeElapsed;
	
	/* (non-Javadoc)
	 * @see org.openmrs.logic.init.InitStatus#setStatus(int)
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	/* (non-Javadoc)
	 * @see org.openmrs.logic.init.InitStatus#getStatus()
	 */
	public int getStatus() {
		return status;
	}
	/* (non-Javadoc)
	 * @see org.openmrs.logic.init.InitStatus#getTimeElapsed()
	 */
	public long getTimeElapsed() {
		return timeElapsed;
	}
	/* (non-Javadoc)
	 * @see org.openmrs.logic.init.InitStatus#setTimeElapsed(long)
	 */
	public void setTimeElapsed(long timeElapsed) {
		this.timeElapsed = timeElapsed;
	}
	/* (non-Javadoc)
	 * @see org.openmrs.logic.init.InitStatus#isRunning()
	 */
	public boolean isRunning() {
		return status == STATUS_ON ? true : false;
	}

	
}
