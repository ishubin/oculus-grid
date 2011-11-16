package net.mindengine.oculus.grid.domain.agent;

public class AgentId {

    private Long id;
    private String token;
    public Long getId() {
        return id;
    }
    public String getToken() {
        return token;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public void setToken(String token) {
        this.token = token;
    }
}
