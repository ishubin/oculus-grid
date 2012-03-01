/*******************************************************************************
* 2012 Ivan Shubin http://mindengine.net
* 
* This file is part of MindEngine.net Oculus Grid.
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with Oculus Grid.  If not, see <http://www.gnu.org/licenses/>.
******************************************************************************/
package net.mindengine.oculus.grid.domain.task;

public class SuiteStatistic {
    
    private Integer passed = 0;
    private Integer failed = 0;
    private Integer warning = 0;
    private Integer total = 0;
    private Integer finished = 0;
    private Integer postponed = 0;
    public Integer getPassed() {
        return passed;
    }
    public Integer getFailed() {
        return failed;
    }
    public Integer getWarning() {
        return warning;
    }
    public Integer getTotal() {
        return total;
    }
    public Integer getFinished() {
        return finished;
    }
    public void setPassed(Integer passed) {
        this.passed = passed;
    }
    public void setFailed(Integer failed) {
        this.failed = failed;
    }
    public void setWarning(Integer warning) {
        this.warning = warning;
    }
    public void setTotal(Integer total) {
        this.total = total;
    }
    public void setFinished(Integer finished) {
        this.finished = finished;
    }
    public void setPostponed(Integer postponed) {
        this.postponed = postponed;
    }
    public Integer getPostponed() {
        return postponed;
    }
}
