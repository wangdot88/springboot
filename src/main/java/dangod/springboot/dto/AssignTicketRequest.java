package dangod.springboot.dto;

public class AssignTicketRequest {
    private Long agentId;
    private String note;
    
    // Getters and Setters
    public Long getAgentId() {
        return agentId;
    }
    
    public void setAgentId(Long agentId) {
        this.agentId = agentId;
    }
    
    public String getNote() {
        return note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
}