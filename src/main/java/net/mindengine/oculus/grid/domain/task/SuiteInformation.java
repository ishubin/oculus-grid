package net.mindengine.oculus.grid.domain.task;

import java.util.List;

public class SuiteInformation {
    
    private List<TestStatus> tests;
    /**
     * Id of suite in Database
     */
    private Long suiteId;
    
    //TODO Gather statistics of passed/failed/warnings/total/finished
    
    public void changeTestStatus(Long customId, TestStatus testStatus) {
        int i=0;
        for(TestStatus ts : tests) {
            if(customId == ts.getCustomId()) {
                tests.set(i, testStatus);
                return;
            }
            i++;
        }
    }

    public void setTests(List<TestStatus> tests) {
        this.tests = tests;
    }

    public List<TestStatus> getTests() {
        return tests;
    }

    public void setSuiteId(Long suiteId) {
        this.suiteId = suiteId;
    }

    public Long getSuiteId() {
        return suiteId;
    }

}
