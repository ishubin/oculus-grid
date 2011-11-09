package net.mindengine.oculus.grid.domain.task.schedule;

import java.util.Date;

public class DateOccurrence implements ScheduleOccurrence {
	/**
     * 
     */
	private static final long serialVersionUID = 2479936201431103167L;
	private Date date;
	private boolean wasExecutedAlready = false;

	public void setDate(Date date) {
		this.date = date;
	}

	public Date getDate() {
		return date;
	}

	@Override
    public boolean shouldRun(Date currentDate) {
	    if(!wasExecutedAlready && currentDate.after(date)){
	    	wasExecutedAlready = true;
	    	return true;
	    }
	    return false;
    }

	@Override
    public Date getClosestOccurrence(Date currentDate) {
	    if(!wasExecutedAlready && currentDate.before(date)){
	    	return date;
	    }
		return null;
    }
}
