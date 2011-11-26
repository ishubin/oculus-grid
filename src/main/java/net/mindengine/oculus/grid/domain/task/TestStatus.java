package net.mindengine.oculus.grid.domain.task;

import net.mindengine.oculus.experior.test.descriptors.TestInformation;

public class TestStatus {
    
    private String name;
    private String mapping;
    private String description;
    private Long customId;
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
    
    public TestStatus(String name, String mapping, String description, Long customId, Integer status, Integer phase) {
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
    public void setCustomId(Long customId) {
        this.customId = customId;
    }
    public Long getCustomId() {
        return customId;
    }

    public void setTestRunId(Long testRunId) {
        this.testRunId = testRunId;
    }

    public Long getTestRunId() {
        return testRunId;
    }
}
