package net.mindengine.oculus.grid.domain.agent;

public class AgentTag {

    public static final String STRING="string".intern();
    public static final String LIST="list".intern();
    
    private String name;
    private String type;
    private String value;
    private String[] values;
    public String[] getValues() {
        return values;
    }
    public void setValues(String[] values) {
        this.values = values;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
