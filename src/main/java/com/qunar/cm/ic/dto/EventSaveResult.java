package com.qunar.cm.ic.dto;

/**
 * Created by dandan.sha on 2018/09/13.
 */
public class EventSaveResult extends MessageResponse {
    private EventResult event;

    public EventSaveResult(EventResult event) {
        super("success");
        this.event = event;
    }

    public EventResult getEvent() {
        return event;
    }

    public void setEvent(EventResult event) {
        this.event = event;
    }
}
