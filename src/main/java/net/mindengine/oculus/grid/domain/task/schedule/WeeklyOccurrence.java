package net.mindengine.oculus.grid.domain.task.schedule;

import java.util.Calendar;
import java.util.Date;

public class WeeklyOccurrence implements ScheduleOccurrence {
	private static final long serialVersionUID = -3837519400091918804L;

	/**
	 * Used to for specification days and hours when the task should be
	 * executed. The row specifies the day of week. The column specifies the
	 * hour.
	 */
	private Integer[][] occurrenceMatrix = new Integer[7][24];

	/**
	 * Used to check and prevent the double executing at the same occurrence
	 */
	private Date lastExecutionDate = null;
	
	public void setOccurrenceMatrix(Integer[][] occurrenceMatrix) {
		this.occurrenceMatrix = occurrenceMatrix;
	}

	public Integer[][] getOccurrenceMatrix() {
		return occurrenceMatrix;
	}

	
	public Date getClosestOccurrence(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		long closestDate = calendar.getTime().getTime();

		boolean bFound = false;

		// Counter is used to prevent infinite looping
		int counter = 0;
		while (!bFound) {
			hour++;
			closestDate += 3600000L;
			if (hour > 23) {
				hour = 0;
				day++;
				if (day > 6) {
					day = 0;
				}
			}

			if (occurrenceMatrix[day][hour] != null && occurrenceMatrix[day][hour] == 1) {
				return new Date(closestDate);
			}
			counter++;
			if (counter > 170)
				return null;
		}
		return null;
	}

	@Override
    public boolean shouldRun(Date currentDate) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentDate);
		int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		if(occurrenceMatrix[day][hour]==1){
			/*
			 * Checking whether the task was already executed for this occurrence
			 */
			if(lastExecutionDate!=null){
				calendar.setTime(lastExecutionDate);
				int lastDay = calendar.get(Calendar.DAY_OF_WEEK)-1;
				int lastHour = calendar.get(Calendar.HOUR_OF_DAY);
			    long timeDiff = currentDate.getTime() - lastExecutionDate.getTime();
			    
				if(lastDay==day && lastHour == hour && timeDiff < 3600000L){
					return false;
				}
			}
			lastExecutionDate = currentDate;
			return true;
		}
		return false;
    }
	
}
