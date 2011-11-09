package net.mindengine.oculus.grid.domain;

import java.util.Date;

/**
 * Class is used to specify date ranges
 * 
 * @author Ivan Shubin
 * 
 */
public class DateRange {
	private Date dateBefore;
	private Date dateAfter;

	public Date getDateBefore() {
		return dateBefore;
	}

	public void setDateBefore(Date dateBefore) {
		this.dateBefore = dateBefore;
	}

	public Date getDateAfter() {
		return dateAfter;
	}

	public void setDateAfter(Date dateAfter) {
		this.dateAfter = dateAfter;
	}

}
