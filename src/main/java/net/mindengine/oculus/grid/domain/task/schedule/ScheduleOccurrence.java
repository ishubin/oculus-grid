package net.mindengine.oculus.grid.domain.task.schedule;

import java.io.Serializable;
import java.util.Date;

/**
 * This interface should be implemented by different scheduler occurrences and
 * will be set to the specified task and then this occurrence will be used in
 * scheduler for date and time verification
 * 
 * @author Ivan Shubin
 * 
 */
public interface ScheduleOccurrence extends Serializable {
	/**
	 * Checks whether the occurrence matches the current date
	 * 
	 * @param currentDate
	 *            The current date
	 * 
	 * @return true if the occurrence should be handled
	 */
	public boolean shouldRun(Date currentDate);
	
	/**
	 * Used to fetch the closest date when the occurrence is going to happen
	 * @param date
	 * @return
	 */
	public Date getClosestOccurrence(Date date);
}
