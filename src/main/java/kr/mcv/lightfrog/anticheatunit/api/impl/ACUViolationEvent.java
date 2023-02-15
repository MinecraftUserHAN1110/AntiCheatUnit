package kr.mcv.lightfrog.anticheatunit.api.impl;

import kr.mcv.lightfrog.anticheatunit.api.ACUEvent;
import kr.mcv.lightfrog.anticheatunit.api.EventType;

public class ACUViolationEvent extends ACUEvent {
    public ACUViolationEvent() {
        super("Violation");
    }

    public boolean cancelled;

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    private EventType type;

    public EventType getType() { return type; }
    public void setType(EventType type) { this.type = type; }

    public boolean isPre() {
        return type == EventType.PRE;
    }

    public boolean isPost() {
        return type == EventType.POST;
    }
}
