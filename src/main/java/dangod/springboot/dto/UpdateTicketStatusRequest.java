package dangod.springboot.dto;

import dangod.springboot.entity.Ticket.Status;

public class UpdateTicketStatusRequest {
    private Status status;
    private String note;
    
    // Getters and Setters
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    public String getNote() {
        return note;
    }
    
    public void setNote(String note) {
        this.note = note;
    }
}