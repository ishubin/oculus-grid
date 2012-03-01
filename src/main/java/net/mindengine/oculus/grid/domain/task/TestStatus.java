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
* along with Oculus Experior.  If not, see <http://www.gnu.org/licenses/>.
******************************************************************************/
package net.mindengine.oculus.grid.domain.task;

import net.mindengine.oculus.experior.test.descriptors.TestInformation;

public class TestStatus {
    
    private String name;
    private String mapping;
    private String description;
    private String customId;
    private Long testRunId;
    
    /**
     * See {@link TestInformation} for constants
     */
    private Integer status;
    /**
     * See {@link TestInformation} for constants
     */
    private Integer phase;
    
    public TestStatus() {
    }
    
    public TestStatus(String name, String mapping, String description, String customId, Integer status, Integer phase) {
        super();
        this.name = name;
        this.mapping = mapping;
        this.description = description;
        this.customId = customId;
        this.status = status;
        this.phase = phase;
    }

    public String getName() {
        return name;
    }
    public String getMapping() {
        return mapping;
    }
    public Integer getStatus() {
        return status;
    }
    public Integer getPhase() {
        return phase;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setMapping(String mapping) {
        this.mapping = mapping;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public void setPhase(Integer phase) {
        this.phase = phase;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getDescription() {
        return description;
    }
    public void setCustomId(String customId) {
        this.customId = customId;
    }
    public String getCustomId() {
        return customId;
    }

    public void setTestRunId(Long testRunId) {
        this.testRunId = testRunId;
    }

    public Long getTestRunId() {
        return testRunId;
    }
}
